package com.yyl.vlc.vlc;

import android.util.Log;
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
    TextView logInfo;
    String tag = "MediaControl";

    public MediaControl(VlcVideoView mediaPlayer, TextView logInfo) {
        this.mediaPlayer = mediaPlayer;
        this.logInfo = logInfo;
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


    long time;

    @Override
    public void eventBuffing(int event, float buffing) {
    }

    @Override
    public void eventPlayInit(boolean openClose) {
        if (openClose) {
            Log.i(tag, "打开时间  00000");
            time = System.currentTimeMillis();
        }
        logInfo.setText("加载");
    }

    @Override
    public void eventStop(boolean isPlayError) {
        logInfo.setText("Stop" + (isPlayError ? "  播放已停止   有错误" : ""));

    }

    @Override
    public void eventError(int error, boolean show) {
        logInfo.setText("地址 出错了 error=" + error);
    }

    @Override
    public void eventPlay(boolean isPlaying) {
        if (isPlaying) {
            controller.show();
            Log.i(tag, "打开时间是 time=" + (System.currentTimeMillis() - time));
            logInfo.setText("播放");
        }else{
            logInfo.setText("暂停");
        }

    }

}
