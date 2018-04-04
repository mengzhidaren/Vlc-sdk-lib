package org.videolan.vlc.listener;

import android.widget.MediaController;


/**
 * Created by yyl on 2016/2/16/016.
 */
public interface MediaPlayerControl extends MediaController.MediaPlayerControl {


    boolean isPrepare();

    boolean canControl();

    /**
     *
     */
    void startPlay();

    void setPath(String path);

    void seekTo(long pos);

    /**
     * speed  0.25--4.0
     */
    boolean setPlaybackSpeedMedia(float speed);

    float getPlaybackSpeed();

    void setLoop(boolean isLoop);

    boolean isLoop();


    /**
     * 镜面
     */
    void setMirror(boolean mirror);

    boolean getMirror();
}
