package org.videolan.vlc.listener;

/**
 * Created by yuyunlong on 2016/8/30/030.
 */
public interface VideoSizeChange {
    /**
     * @param width
     * @param height
     * @param rotation 视频角度 对应ffmpeg中的rotation 参数
     */
    void onVideoSizeChanged(int width, int height, boolean rotation);
}
