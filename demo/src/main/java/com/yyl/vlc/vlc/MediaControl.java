package com.yyl.vlc.vlc;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import android.widget.TextView;

import org.videolan.vlc.VlcVideoView;
import org.videolan.vlc.listener.MediaListenerEvent;


/**
 * Created by yyl on 2016/11/3/003.
 */

public class MediaControl implements MediaListenerEvent {
    private final MediaController controller;
    private TextView logInfo;
    private String tag = "MediaControl";
    private long time;
    private Handler mainThread = new Handler();

    public MediaControl(VlcVideoView mediaPlayer, TextView logInfo) {
        this.logInfo = logInfo;
        controller = new MediaController(mediaPlayer.getContext());
        controller.setMediaPlayer(mediaPlayer);
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
    public void eventBuffing(int event, float buffing) {

    }

    @Override
    public void eventPlayInit(boolean openClose) {
        if (openClose) {
            time = System.currentTimeMillis();
        }
        logInfo.setText("加载");
    }

    @Override
    public void eventStop(final boolean isPlayError) {
        mainThread.post(new Runnable() {
            @Override
            public void run() {
                logInfo.setText("Stop" + (isPlayError ? "  播放已停止   有错误" : ""));
            }
        });
    }

    @Override
    public void eventError(final int error, final boolean show) {
        mainThread.post(new Runnable() {
            @Override
            public void run() {
                logInfo.setText("地址 出错了 error=" + error);
            }
        });

    }

    @Override
    public void eventPlay(final boolean isPlaying) {
        mainThread.post(new Runnable() {
            @Override
            public void run() {
                if (isPlaying) {
                    controller.show();
                    Log.i(tag, "加载耗时  time=" + (System.currentTimeMillis() - time));
                    logInfo.setText("播放中");
                } else {
                    logInfo.setText("暂停中");
                }
            }
        });


    }

}
