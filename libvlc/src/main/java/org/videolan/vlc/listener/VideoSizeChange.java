package org.videolan.vlc.listener;

/**
 * 如果设备支持 Es2 这里就不会调用
 * Created by yuyunlong on 2016/8/30/030.
 */
public interface VideoSizeChange {
    void onVideoSizeChanged(int width, int height, int visibleWidth, int visibleHeight);
}
