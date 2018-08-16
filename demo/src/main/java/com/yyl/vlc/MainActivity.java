package com.yyl.vlc;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.danikula.videocache.CacheListener;
import com.danikula.videocache.HttpProxyCacheServer;
import com.yyl.vlc.ijk.IjkPlayerActivity;
import com.yyl.vlc.vlc.VlcPlayerActivity;

import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.util.Dumper;
import org.videolan.libvlc.util.VLCUtil;
import org.videolan.vlc.RecordEvent;
import org.videolan.vlc.ThumbnailUtils;
import org.videolan.vlc.util.LogUtils;
import org.videolan.vlc.util.VLCInstance;

import java.io.File;
import java.util.ArrayList;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class MainActivity extends AppCompatActivity {
    private static final String path = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";
//    private static final String path = "http://haobashi.me/20180525T003000_0_10341127_0.ts";
//    private static final String path = "http://ivi.bupt.edu.cn/hls/cctv1hd.m3u8";
//    private static final String path = "https://www.wowza.com/downloads/images/sample.mp4";
//     public static final String path = "http://192.168.1.27/demo2.mp4";
    //public static final String path = "rtsp://video.fjtu.com.cn/vs01/flws/flws_01.rm";
    private final String tag = "MainActivity";
    private ImageView thumbnail;
    private Context context;
    public static boolean testNetWork = false;

    public static String getUrl(Context context) {
        if (testNetWork) {
            return path;
        } else {
            return App.getProxy(context).getProxyUrl(path, true);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        thumbnail = findViewById(R.id.thumbnail);
        init();
        test();
    }


    private void init() {
        //加载库文件
        if (VLCInstance.testCompatibleCPU(this)) {
            Log.i(tag, "support   cpu");
        } else {
            Log.i(tag, "not support  cpu");
        }
    }

    private void test() {
        App.getProxy(context).registerCacheListener(new CacheListener() {
            @Override
            public void onCacheAvailable(File cacheFile, String url, int percentsAvailable) {
                Log.i(tag, "support   percentsAvailable=" + percentsAvailable);
            }
        }, path);
    }

    public void myClick1(View view) {
        startActivity(new Intent(this, IjkPlayerActivity.class));
    }

    public void myClick2(View view) {
        startActivity(new Intent(this, VlcPlayerActivity.class));
    }

    public void myClick3(View view) {
        startActivity(new Intent(this, TestActivity.class));
    }


    /**
     * 不推荐用vlc截图 不稳定 可以用TextureView的 surface cache中截取
     *
     * @param view
     */
    public void myClick4(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<String> options = new ArrayList<String>(10);
                options.add("-vvv");
                LibVLC libVLC = new LibVLC(context, options);
                Media media = new Media(libVLC, Uri.parse(path));//网络截图效率不高
                //   media.addOption("--vout=android_display,none");
                //   media.addOption("--no-audio");
                //   media.addOption("--start-time=20");
                media.setHWDecoderEnabled(false, false);//软解码
                media.parse(Media.Parse.ParseNetwork);
                Log.i("yyl", "getTrackCount=" + media.getTrackCount());
                Media.VideoTrack track = (Media.VideoTrack) media.getTrack(Media.Track.Type.Video);//获取video轨道1的信息 可用ffmpeg查看
                if (track == null) return;
                Log.i("yyl", "track=" + track.toString());
                final Bitmap bitmap = ThumbnailUtils.getThumbnail(media, track.width / 2, track.height / 2);
                Log.i("yyl", "bitmap=" + bitmap);
                thumbnail.post(new Runnable() {
                    @Override
                    public void run() {
                        if (bitmap != null)
                            thumbnail.setImageBitmap(bitmap);
                    }
                });

            }
        }).start();

    }


    public void myClick5(View view) {

    }

    public void myClick6(View view) {
    }

    public void myClick7(View view) {
    }
}
