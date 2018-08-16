package org.videolan.vlc;


import android.content.Context;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;

import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.vlc.listener.MediaListenerEvent;
import org.videolan.vlc.listener.MediaPlayerControl;
import org.videolan.vlc.listener.VideoSizeChange;
import org.videolan.vlc.util.LogUtils;
import org.videolan.vlc.util.VLCInstance;

/**
 * 这个类实现了大部分播放器功能
 * 只是提供参考的实例代码
 * <p>
 * 如果有bug请在github留言
 *
 * @author https://github.com/mengzhidaren
 */
public class VlcVideoView extends TextureView implements MediaPlayerControl, VideoSizeChange {
    private VlcPlayer videoMediaLogic;
    private final String tag = "VlcVideoView";

    public VlcVideoView(Context context) {
        this(context, null);
    }

    public VlcVideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VlcVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (isInEditMode()) {
            return;
        }
        videoMediaLogic = getVideoLogic(context);
        videoMediaLogic.setVideoSizeChange(this);
        setSurfaceTextureListener(videoSurfaceListener);
    }

    //这里改为子类继承   方便修改libVLC参数
    public VlcPlayer getVideoLogic(Context context) {
        return new VlcPlayer(VLCInstance.get(context));
    }

    public void setLibVLC(LibVLC libVLC) {
        videoMediaLogic.onDestory();
        videoMediaLogic=new VlcPlayer(libVLC);
        videoMediaLogic.setVideoSizeChange(this);
    }

    public void setMediaListenerEvent(MediaListenerEvent mediaListenerEvent) {
        videoMediaLogic.setMediaListenerEvent(mediaListenerEvent);
    }

    @Override
    public boolean canControl() {//直播流不要用这个判断
        return videoMediaLogic.canControl();
    }

    @Override
    public void startPlay() {
        videoMediaLogic.startPlay();
    }

    @Override
    public void setPath(String path) {
        videoMediaLogic.setPath(path);
    }

    @Override
    public void seekTo(long pos) {
        videoMediaLogic.seekTo(pos);
    }

    /**
     * 关闭停止播放器时用
     */
    public void onStop() {
        videoMediaLogic.onStop();
    }

    /**
     * 退出界面时回收
     */
    public void onDestory() {
        if (videoMediaLogic != null)
            videoMediaLogic.onDestory();
        LogUtils.i(tag, "onDestory");
    }

    @Override
    public boolean isPrepare() {
        return videoMediaLogic.isPrepare();
    }


    @Override
    public void start() {
        videoMediaLogic.start();
    }

    @Override
    public void pause() {
        videoMediaLogic.pause();
    }

    @Override
    public int getDuration() {
        return videoMediaLogic.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        return videoMediaLogic.getCurrentPosition();
    }

    @Override
    public void seekTo(int pos) {
        videoMediaLogic.seekTo(pos);
    }

    @Override
    public boolean isPlaying() {
        return videoMediaLogic.isPlaying();
    }


    @Override
    public void setMirror(boolean mirror) {
        this.mirror = mirror;
        if (mirror) {
            setScaleX(-1f);
        } else {
            setScaleX(1f);
        }
    }

    private boolean mirror = false;

    @Override
    public boolean getMirror() {
        return mirror;
    }


    @Override
    public int getBufferPercentage() {
        return videoMediaLogic.getBufferPercentage();
    }


    @Override
    public boolean setPlaybackSpeedMedia(float speed) {
        return videoMediaLogic.setPlaybackSpeedMedia(speed);
    }

    @Override
    public float getPlaybackSpeed() {
        return videoMediaLogic.getPlaybackSpeed();
    }


    @Override
    public void setLoop(boolean isLoop) {
        videoMediaLogic.setLoop(isLoop);
    }

    @Override
    public boolean isLoop() {
        return videoMediaLogic.isLoop();
    }


//    @Override
//    protected void onAttachedToWindow() {
//        super.onAttachedToWindow();
//        if (isInEditMode()) {
//            return;
//        }
//        setKeepScreenOn(true);// 上层决定开关
//    }
//
//    @Override
//    protected void onDetachedFromWindow() {
//        super.onDetachedFromWindow();
//        if (isInEditMode()) {
//            return;
//        }
//        setKeepScreenOn(false);
//    }

    /**
     * 这里计算方法是以16:9为基础
     * 在当前view中显示video的最大videoWidth或者video的最大videoHeight
     * 只供参考 跟距产品需求适配
     *
     * @param videoWidth
     * @param videoHeight
     */
    public void adjustAspectRatio(int videoWidth, int videoHeight) {
        if (videoWidth * videoHeight == 0) {
            return;
        }
        int viewWidth = getWidth();
        int viewHeight = getHeight();
        double videoRatio = (double) viewWidth / (double) viewHeight;//显示比例 (小屏16：9 大屏厂商手机比例  真乱)
        double aspectRatio = (double) videoWidth / (double) videoHeight;//视频比例
        int newWidth, newHeight;
        if (videoWidth > videoHeight) {//正常比例16：9
            if (videoRatio > aspectRatio) {//16:9>16:10
                newWidth = (int) (viewHeight * aspectRatio);
                newHeight = viewHeight;
            } else {//16:9<16:8
                newWidth = viewWidth;
                newHeight = (int) (viewWidth / aspectRatio);
            }
        } else {//非正常可能是 90度
            //16:9>1:9
            newWidth = (int) (viewHeight * aspectRatio);
            newHeight = viewHeight;
        }
        float xoff = (viewWidth - newWidth) / 2f;
        float yoff = (viewHeight - newHeight) / 2f;
        Matrix txform = new Matrix();
        getTransform(txform);
        txform.setScale((float) newWidth / viewWidth, (float) newHeight
                / viewHeight);
        // txform.postRotate(10); // just for fun
        txform.postTranslate(xoff, yoff);
        setTransform(txform);
        if (rotation == 180) {
            setRotation(180);
        } else {
            setRotation(0);
        }
        LogUtils.i(tag, "onVideoSizeChanged   newVideo=" + newWidth + "x" + newHeight);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed && mVideoWidth * mVideoHeight > 0) {
            post(new Runnable() {
                @Override
                public void run() {
                    adjustAspectRatio(mVideoWidth, mVideoHeight);
                }
            });
        }
    }


    private int mVideoWidth;
    private int mVideoHeight;
    private int rotation = 0;

//    public int getVideoRotation() {
//        return rotation;
//    }
//
//    public int getVideoWidth() {
//        return mVideoWidth;
//    }
//
//    public int getVideoHeight() {
//        return mVideoHeight;
//    }

    public Media.VideoTrack getVideoTrack() {
        return videoMediaLogic.getVideoTrack();
    }

    /**
     * 如果设备支持opengl这里就不会调用
     *
     * @param width w
     * @param height h
     * @param visibleWidth w
     * @param visibleHeight h
     */
    @Override
    public void onVideoSizeChanged(int width, int height, int visibleWidth, int visibleHeight) {
        LogUtils.i(tag, "onVideoSizeChanged   video=" + width + "x" + height + " visible="
                + visibleWidth + "x" + visibleHeight);
        if (width * height == 0) return;
        this.mVideoWidth = visibleWidth;
        this.mVideoHeight = visibleHeight;
        post(new Runnable() {
            @Override
            public void run() {
                adjustAspectRatio(mVideoWidth, mVideoHeight);
            }
        });
    }


    //设置字幕
    public void setAddSlave(String addSlave) {
        videoMediaLogic.setAddSlave(addSlave);
    }

    //定制播放器
    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        videoMediaLogic.setMediaPlayer(mediaPlayer);
    }

    //定制media
    public void setMedia(MediaPlayer mediaPlayer) {
        videoMediaLogic.setMedia(mediaPlayer);
    }

    public MediaPlayer getMediaPlayer() {
        return videoMediaLogic.getMediaPlayer();
    }

    @Override
    public boolean canPause() {
        return videoMediaLogic.canPause();
    }

    @Override
    public boolean canSeekBackward() {
        return videoMediaLogic.canSeekBackward();
    }

    @Override
    public boolean canSeekForward() {
        return videoMediaLogic.canSeekForward();
    }

    @Override
    public int getAudioSessionId() {
        return videoMediaLogic.getAudioSessionId();
    }


    private TextureView.SurfaceTextureListener videoSurfaceListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            videoMediaLogic.setWindowSize(width, height);
            videoMediaLogic.setSurface(new Surface(surface), null);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            videoMediaLogic.setWindowSize(width, height);
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            videoMediaLogic.onSurfaceTextureDestroyedUI();
            return true;//回收掉Surface
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };

}