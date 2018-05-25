package org.videolan.vlc.listener;

import android.support.annotation.AnyThread;
import android.support.annotation.MainThread;

/**
 * Created by yyl on 2016/3/25/025.
 */
public interface MediaListenerEvent {

    int EVENT_ERROR = 0;
    int EVENT_ERROR_PATH = 2;
    int EVENT_BUFFING = 1;

    @AnyThread
    void eventBuffing(final int event, final float buffing);//子线程

    @AnyThread
    void eventStop(final boolean isPlayError);//子线程

    @AnyThread
    void eventError(final int event, final boolean show);//子线程

    @AnyThread
    void eventPlay(final boolean isPlaying);//子线程


    /**
     * 初始化开始加载  当前线程为调用者线程
     *
     * @param openClose true加载视频中  false回到初始化  比如显示封面图
     */
    @MainThread
    void eventPlayInit(boolean openClose);//调用者线程
}
