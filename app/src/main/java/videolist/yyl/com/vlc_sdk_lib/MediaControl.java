package videolist.yyl.com.vlc_sdk_lib;

import android.view.MotionEvent;
import android.view.View;
import android.widget.MediaController;
import android.widget.TextView;

import org.videolan.vlc.VlcVideoView;
import org.videolan.vlc.listener.MediaListenerEvent;
import org.videolan.vlc.listener.MediaPlayerControl;

/**
 * Created by yyl on 2016/11/3/003.
 */

public class MediaControl implements MediaController.MediaPlayerControl, MediaListenerEvent {
    MediaPlayerControl mediaPlayer;
    final MediaController controller;
    TextView info;

    public MediaControl(VlcVideoView mediaPlayer, TextView info) {
        this.mediaPlayer = mediaPlayer;
        this.info = info;
        controller = new MediaController(mediaPlayer.getContext());
        controller.setMediaPlayer(this);
        controller.setAnchorView(mediaPlayer);
        mediaPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!controller.isShowing())
                    controller.show();
                else
                    controller.hide();
            }
        });
    }


    @Override
    public void start() {
        mediaPlayer.start();
    }

    @Override
    public void pause() {
        mediaPlayer.pause();
    }

    @Override
    public int getDuration() {
        return (int) mediaPlayer.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        return (int) mediaPlayer.getCurrentPosition();
    }

    @Override
    public void seekTo(int pos) {
        mediaPlayer.seekTo(pos);
    }

    @Override
    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return mediaPlayer.getBufferPercentage();
    }

    @Override
    public boolean canPause() {
        return mediaPlayer.isPrepare();
    }

    @Override
    public boolean canSeekBackward() {//快退
        return true;
    }

    @Override
    public boolean canSeekForward() {//快进
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    @Override
    public void eventBuffing(float buffing, boolean show) {
        if (show)
            info.setText("buffing=" + buffing);
        else
            info.setText("播放中");
    }

    @Override
    public void eventPlayInit(boolean openClose) {
        info.setText("加载中");
    }

    @Override
    public void eventStop(boolean isPlayError) {
        info.setText("Stop" + (isPlayError ? "  播放已停止   有错误" : ""));

    }

    @Override
    public void eventError(int error, boolean show) {
        info.setText("地址 出错了 error=" + error);
    }

    @Override
    public void eventPlay(boolean isPlaying) {
        controller.show();
    }

}
