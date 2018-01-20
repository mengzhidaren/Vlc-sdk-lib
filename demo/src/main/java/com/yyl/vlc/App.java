package com.yyl.vlc;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.danikula.videocache.HttpProxyCacheServer;

import org.videolan.vlc.util.VLCInstance;

/**
 * Created by yyl on 2017/11/30.
 */

public class App extends Application {



    private HttpProxyCacheServer proxy;

    public static HttpProxyCacheServer getProxy(Context context) {
        App app = (App) context.getApplicationContext();
        return app.proxy == null ? (app.proxy = app.newProxy()) : app.proxy;
    }

    /**
     * 注意被app 安全软件禁网后 127.0.0.1 的getLocalPort 会被限制权限 而闪退
     *
     */
    private HttpProxyCacheServer newProxy() {
        return new HttpProxyCacheServer(this);
    }
}
