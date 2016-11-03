package org.videolan.vlc.listener;

/**
 * Created by yyl on 2016/3/25/025.
 */
public interface MediaListenerEvent {

    void eventBuffing(float buffing, boolean show);

    /**
     * 初始化开始加载
     *
     * @param openClose true加载视频中  false回到初始化  比如显示封面图
     */
    void eventPlayInit(boolean openClose);

    void eventStop(boolean isPlayError);

    void eventError(int error, boolean show);

    void eventPlay(boolean isPlaying);
}
