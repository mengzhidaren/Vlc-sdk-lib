package org.videolan.vlc.listener;

/**
 * Created by yuyunlong on 2016/8/30/030.
 */
public interface VideoSizeChange {
    void onVideoSizeChanged(int width, int height, int visibleWidth, int visibleHeight, int sarNum, int sarDen);
}
