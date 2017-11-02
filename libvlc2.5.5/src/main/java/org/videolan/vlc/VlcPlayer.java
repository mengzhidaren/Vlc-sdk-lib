package org.videolan.vlc;


import android.content.Context;
import android.graphics.SurfaceTexture;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.TextUtils;


import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.vlc.listener.MediaListenerEvent;
import org.videolan.vlc.listener.MediaPlayerControl;
import org.videolan.vlc.listener.VideoSizeChange;
import org.videolan.vlc.util.LogUtils;
import org.videolan.vlc.util.VLCInstance;
import org.videolan.vlc.util.VLCOptions;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by yyl on 2017/11/02
 */
public class VlcPlayer implements MediaPlayerControl, Handler.Callback, IVLCVout.OnNewVideoLayoutListener, IVLCVout.Callback {
    private final String tag = "VlcPlayer";
    private final Handler theadsHandler;
    private final Handler mainHandler;
    private final Context mContext;
    private SurfaceTexture surface;
    private static boolean isInstance;//是否单例播放   默认开
    private static boolean isSaveState;//跳转界面时 保存实例
    private static final int INIT_START = 0x0008;
    private static final int INIT_STOP = 0x0009;

    private final Lock lock = new ReentrantLock();
    private static final HandlerThread sThread = new HandlerThread("VlcPlayThread");

    static {
        sThread.start();
    }

    private static MediaPlayer staticMediaPlayer;

    private static MediaPlayer getMediaPlayer(Context context) {
        if (isInstance) {
            if (staticMediaPlayer == null) {
                staticMediaPlayer = new MediaPlayer(VLCInstance.get(context));
            }
        } else {
            return new MediaPlayer(VLCInstance.get(context));
        }
        return staticMediaPlayer;
    }

    public static void setInstance(boolean isInstance) {
        VlcPlayer.isInstance = isInstance;
    }


    @Override
    public boolean handleMessage(Message msg) {
        lock.lock();
        try {
            switch (msg.what) {
                case INIT_START:
                    if (isInitStart)
                        opendVideo();
                    break;
                case INIT_STOP:
                    release();
                    break;
            }
        } finally {
            lock.unlock();
        }
        return true;
    }

    private boolean isInitStart;
    /**
     * 初始化全部参数
     */
    private boolean isInitPlay;
    /**
     * surface 线程异步状态中
     */
    private boolean isSufaceDelayerPlay;//布局延迟加载播放
    private boolean canSeek;//能否快进
    private boolean canPause;//能否快进
    private boolean canReadInfo;//能否拿到信息
    private boolean isPlayError;
    private boolean isAttached;
    private boolean isAttachedSurface;
    private boolean othereMedia;
    private boolean isLoop = true;


    public VlcPlayer(Context context) {
        mContext = context;
        theadsHandler = new Handler(sThread.getLooper(), this);
        mainHandler = new Handler(Looper.getMainLooper());
    }

    public void setMediaPlayer(LibVLC libVLC) {
        mMediaPlayer = new MediaPlayer(libVLC);
    }

    public void setMedia(Media media) {
        othereMedia = true;
        if (mMediaPlayer == null) mMediaPlayer = getMediaPlayer(mContext);
        mMediaPlayer.setMedia(media);
    }

    /**
     * 解析器
     */
    private LibVLC libVLC;
    private MediaPlayer mMediaPlayer;
    private String path;


    /**
     * surface线程  可能有延迟
     */
    public void setSurface(SurfaceTexture surface) {
        this.surface = surface;
        isAttached = true;
        if (isSufaceDelayerPlay) {
            isSufaceDelayerPlay = false;
            startPlay(path);
        }
    }

    public void onSurfaceTextureDestroyed() {
        isAttached = false;
        isSufaceDelayerPlay = false;
        if (isAttachedSurface) {
            isAttachedSurface = false;
            pause();
            mMediaPlayer.getVLCVout().detachViews();
        }
    }


    @Override
    public void setLoop(boolean isLoop) {
        this.isLoop = isLoop;
    }

    @Override
    public boolean isLoop() {
        return isLoop;
    }

    public void onStop() {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mediaListenerEvent != null)
                    mediaListenerEvent.eventPlayInit(false);
            }
        });
        theadsHandler.obtainMessage(INIT_STOP).sendToTarget();
    }


    private void opendVideo() {
        if (TextUtils.isEmpty(path) || mContext == null || !isAttached) {
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mediaListenerEvent != null)
                        mediaListenerEvent.eventError(MediaListenerEvent.EVENT_ERROR, true);
                }
            });
            return;
        }
        if (libVLC == null) {
            libVLC = VLCInstance.get(mContext);
        }
        if (mMediaPlayer == null) {
            mMediaPlayer = getMediaPlayer(mContext);
            mMediaPlayer.setAudioOutput(VLCOptions.getAout(PreferenceManager.getDefaultSharedPreferences(mContext)));
            mMediaPlayer.setEqualizer(VLCOptions.getEqualizer(mContext));
        }
        if (!isAttachedSurface && mMediaPlayer.getVLCVout().areViewsAttached()) {
            mMediaPlayer.getVLCVout().detachViews();
        }
        if (!othereMedia) {
            loadMedia();
        }
        if (!mMediaPlayer.getVLCVout().areViewsAttached() && isAttached && surface != null) {
            isAttachedSurface = true;
            mMediaPlayer.getVLCVout().setVideoSurface(surface);
            mMediaPlayer.getVLCVout().addCallback(this);
            mMediaPlayer.getVLCVout().attachViews(this);
            mMediaPlayer.setVideoTitleDisplay(MediaPlayer.Position.Disable, 0);
            LogUtils.i(tag, "setVideoSurface   attachViews");
        }
        mMediaPlayer.setEventListener(new MediaPlayer.EventListener() {
            @Override
            public void onEvent(MediaPlayer.Event event) {
                onEventNative(event);
            }
        });
        isInitPlay = true;
        othereMedia = false;
        isSufaceDelayerPlay = false;
        canSeek = false;
        canPause = false;
        isPlayError = false;
        LogUtils.i(tag, "isAttached=" + isAttached + " isInitStart=" + isInitStart);
        if (isAttached && isInitStart && isAttachedSurface) {
            mMediaPlayer.play();
        }
    }

    /**
     * //http://www.baidu
     * //rtmp://58.61.150.198/live/Livestream
     * // ftp://www.baidu
     * // sdcard/mp4.mp4
     */
    private void loadMedia() {
        if (isSaveState) {
            isSaveState = false;
            Media media = mMediaPlayer.getMedia();
            if (media != null && !media.isReleased()) {
                canSeek = true;
                canPause = true;
                canReadInfo = true;
                return;
            }
        }
        final Media media;
        if (path.contains("://")) {
            media = new Media(libVLC, Uri.parse(path));
            //    media.parseAsync(Media.Parse.FetchNetwork, 5 * 1000);
        } else {
            media = new Media(libVLC, path);
            //    media.parseAsync(Media.Parse.FetchLocal);
        }
        media.setHWDecoderEnabled(false, false);
        media.setEventListener(mMediaListener);
        mMediaPlayer.setMedia(media);
        media.release();
    }


    private final Media.EventListener mMediaListener = new Media.EventListener() {
        @Override
        public void onEvent(Media.Event event) {
            switch (event.type) {
                case Media.Event.MetaChanged:
                    LogUtils.i(tag, "Media.Event.MetaChanged:  =" + event.getMetaId());
                    break;
                case Media.Event.ParsedChanged:
                    LogUtils.i(tag, "Media.Event.ParsedChanged  =" + event.getMetaId());
                    break;
                case Media.Event.StateChanged:
                    LogUtils.i(tag, "StateChanged   =" + event.getMetaId());
                    break;
                default:
                    LogUtils.i(tag, "Media.Event.type=" + event.type + "   eventgetParsedStatus=" + event.getParsedStatus());
                    break;

            }
        }
    };


    public void saveState() {
        if (isInitPlay) {
            isSaveState = true;
            onStop();
        }
    }

    @Override
    public void startPlay(String path) {
        this.path = path;
        isInitStart = true;
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mediaListenerEvent != null)
                    mediaListenerEvent.eventPlayInit(true);
            }
        });
        if (isAttached) {
            theadsHandler.obtainMessage(INIT_START).sendToTarget();
        } else {
            isSufaceDelayerPlay = true;
        }
    }

    private void reStartPlay() {
        canReadInfo = false;
        canSeek = false;
        canPause = false;
        if (isAttached && isLoop && isPrepare()) {
            LogUtils.i(tag, "reStartPlay setMedia");
            mMediaPlayer.setMedia(mMediaPlayer.getMedia());
            if (isAttached)
                mMediaPlayer.play();
        } else {
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mediaListenerEvent != null)
                        mediaListenerEvent.eventStop(isPlayError);
                }
            });
        }
    }


    private void release() {
        LogUtils.i(tag, "release");
        canSeek = false;
        canPause = false;
        if (mMediaPlayer != null && isInitPlay) {
            isInitPlay = false;
            if (isAttachedSurface) {
                isAttachedSurface = false;
                mMediaPlayer.getVLCVout().detachViews();
            }
            if (isSaveState) {
                mMediaPlayer.pause();
            } else {
                final Media media = mMediaPlayer.getMedia();
                if (media != null) {
                    media.setEventListener(null);
                    mMediaPlayer.stop();
                    LogUtils.i(tag, "release setMedia null");
                    mMediaPlayer.setMedia(null);
                    media.release();
                    isSaveState = false;
                }
            }
            LogUtils.i(tag, "release over");
        }
        isInitStart = false;
    }

    public void onDestory() {
        videoSizeChange = null;
        release();
        if (mMediaPlayer != null) {
            if (!mMediaPlayer.isReleased()) {
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
        }
    }

    private void onEventNative(final MediaPlayer.Event event) {
        switch (event.type) {
            case MediaPlayer.Event.Stopped:
                LogUtils.i(tag, "Stopped  isLoop=" + isLoop + "  ");
                reStartPlay();
                break;
            case MediaPlayer.Event.EndReached:
                LogUtils.i(tag, "EndReached");
                break;
            case MediaPlayer.Event.EncounteredError:
                isPlayError = true;
                canReadInfo = false;
                LogUtils.i(tag, "EncounteredError");
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mediaListenerEvent != null)
                            mediaListenerEvent.eventError(MediaListenerEvent.EVENT_ERROR, true);
                    }
                });
                break;
            case MediaPlayer.Event.Opening:
                LogUtils.i(tag, "Opening");
                canReadInfo = true;
                speed = 1f;
                mMediaPlayer.setRate(1f);
                break;
            case MediaPlayer.Event.Playing:
                LogUtils.i(tag, "Playing");
                canReadInfo = true;
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mediaListenerEvent != null)
                            mediaListenerEvent.eventPlay(true);
                    }
                });
                if (!isAttached || !mMediaPlayer.getVLCVout().areViewsAttached()) {//禁止双线程后台运行
                    LogUtils.i(tag, "---多线程出错----没有surface  禁止双线程后台运行");
                    if (mMediaPlayer != null) {
                        mMediaPlayer.pause();
                    }
                }
                break;
            case MediaPlayer.Event.Paused:
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mediaListenerEvent != null)
                            mediaListenerEvent.eventPlay(false);
                    }
                });
                LogUtils.i(tag, "Paused");

                break;
            case MediaPlayer.Event.TimeChanged://TimeChanged   15501
                // LogUtils.i(tag, "TimeChanged" + event.getTimeChanged());
//                if (isABLoop && isAttached && canSeek && abTimeEnd > 0) {
//                    if (event.getTimeChanged() > abTimeEnd) {
//                               seekTo(abTimeStart);
//                    }
//                }
                break;
            case MediaPlayer.Event.PositionChanged://PositionChanged   0.061593015
                // LogUtils.i(tag, "PositionChanged" + event.getPositionChanged());
                break;
            case MediaPlayer.Event.Vout:
                LogUtils.i(tag, "Vout" + event.getVoutCount());
                break;
            case MediaPlayer.Event.ESAdded:
                LogUtils.i(tag, "ESAdded");
                break;
            case MediaPlayer.Event.ESDeleted:
                LogUtils.i(tag, "ESDeleted");
                break;
            case MediaPlayer.Event.SeekableChanged:
                canSeek = event.getSeekable();
                LogUtils.i(tag, "SeekableChanged=" + canSeek);
                break;
            case MediaPlayer.Event.PausableChanged:
                canPause = event.getPausable();
                LogUtils.i(tag, "PausableChanged=" + canPause);
                break;
            case MediaPlayer.Event.Buffering:
                LogUtils.i(tag, "MediaPlayer.Event.Buffering" + event.getBuffering());
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mediaListenerEvent != null)
                            mediaListenerEvent.eventBuffing(MediaListenerEvent.EVENT_BUFFING, event.getBuffering());
                    }
                });
                break;
            case MediaPlayer.Event.MediaChanged:
                LogUtils.i(tag, "MediaChanged=" + event.getEsChangedType());
                break;
            default:
                LogUtils.i(tag, "event.type=" + event.type);
                break;
        }
    }


    @Override
    public boolean isPrepare() {
        return mMediaPlayer != null && isInitPlay && !isPlayError;
    }

    @Override
    public boolean canControl() {
        return canPause && canReadInfo && canSeek;
    }

    @Override
    public void start() {
        LogUtils.i(tag, "start");
        if (isPrepare())
            mMediaPlayer.play();
    }

    @Override
    public void pause() {
        if (isPrepare() && canPause) {
            mMediaPlayer.pause();
        }
    }


    @Override
    public long getDuration() {
        if (isPrepare() && canReadInfo) {
            return mMediaPlayer.getLength();
        }
        return 0;
    }

    @Override
    public long getCurrentPosition() {
        if (isPrepare() && canReadInfo)
            return (long) (getDuration() * mMediaPlayer.getPosition());
        return 0;
    }

    private boolean isSeeking;

    @Override
    public void seekTo(long pos) {
        if (isPrepare() && canSeek && !isSeeking) {
            isSeeking = true;
            mMediaPlayer.setTime(pos);
            isSeeking = false;
        }
    }


    @Override
    public boolean isPlaying() {
        if (isPrepare()) {
            return mMediaPlayer.isPlaying();
        }
        return false;
    }

    @Override
    public void setMirror(boolean mirror) {
    }

    @Override
    public boolean getMirror() {
        return false;
    }

    /**
     * @return 没空做了
     */
    @Override
    public int getBufferPercentage() {
        return 0;
    }

    private float speed = 1f;

    @Override
    public boolean setPlaybackSpeedMedia(float speed) {
        if (isPrepare() && canSeek) {
            this.speed = speed;
            mMediaPlayer.setRate(speed);
            seekTo(getCurrentPosition());
        }
        return true;
    }

    @Override
    public float getPlaybackSpeed() {
        if (isPrepare() && canSeek)
            return mMediaPlayer.getRate();
        return speed;
    }


    private MediaListenerEvent mediaListenerEvent;

    public void setMediaListenerEvent(MediaListenerEvent mediaListenerEvent) {
        this.mediaListenerEvent = mediaListenerEvent;
    }


    public void setVideoSizeChange(VideoSizeChange videoSizeChange) {
        this.videoSizeChange = videoSizeChange;
    }

    private VideoSizeChange videoSizeChange;

    @Override
    public void onNewVideoLayout(IVLCVout vlcVout, int width, int height, int visibleWidth, int visibleHeight, int sarNum, int sarDen) {
        if (videoSizeChange != null) {
            videoSizeChange.onVideoSizeChanged(width, height, visibleWidth, visibleHeight, sarNum, sarDen);
        }
    }


    @Override
    public void onSurfacesCreated(IVLCVout vlcVout) {

    }

    @Override
    public void onSurfacesDestroyed(IVLCVout vlcVout) {

    }
}
