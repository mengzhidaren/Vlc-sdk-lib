package com.yyl.vlc;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.yyl.vlc.ijk.IjkPlayerActivity;
import com.yyl.vlc.vlc.VlcPlayerActivity;

import org.videolan.vlc.util.VLCInstance;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class MainActivity extends AppCompatActivity {

    String tag = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //加载库文件
        if (VLCInstance.testCompatibleCPU(this)) {
            new IjkMediaPlayer();
            Log.i(tag, "支持 x86  armv7");
            setContentView(R.layout.activity_main);
        } else {
            Log.i(tag, "不支持");
        }
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
}
