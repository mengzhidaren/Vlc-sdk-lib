package com.yyl.vlc.vlc;


import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.yyl.vlc.MainActivity;
import com.yyl.vlc.R;

import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.vlc.VlcVideoView;
import org.videolan.vlc.util.VLCInstance;
import org.videolan.vlc.util.VLCOptions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;


/**
 * 有问题留言给我 https://github.com/mengzhidaren
 */
public class VlcVideoFragment extends Fragment implements View.OnClickListener {
    private VlcVideoView vlcVideoView;
    private TextView logInfo;
    private ImageView thumbnail;


    //本地字幕文件
    private File alaveFile = new File(Environment.getExternalStorageDirectory(), "test2.srt");
    //网络字幕
    private String uri = "";


    //录像
//    private File recordFile = new File(Environment.getExternalStorageDirectory(), "recordFile.mp4");


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video, container, false);
        vlcVideoView = view.findViewById(R.id.player);
        logInfo = view.findViewById(R.id.info);
        thumbnail = view.findViewById(R.id.thumbnail);

        thumbnail.setOnClickListener(this);
        view.findViewById(R.id.change).setOnClickListener(this);
        view.findViewById(R.id.changeSlave).setOnClickListener(this);
        view.findViewById(R.id.recordStart).setOnClickListener(this);
        view.findViewById(R.id.recordStop).setOnClickListener(this);


        vlcVideoView.setMediaListenerEvent(new MediaControl(vlcVideoView, logInfo));
        vlcVideoView.setPath(MainActivity.getUrl(getContext()));
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        startPlay();
    }

    //大多数情况
    private void startPlay() {
        vlcVideoView.startPlay();
    }

    //直播测试
    private void startPlay11() {
        vlcVideoView.setPath("rtsp://video.fjtu.com.cn/vs01/flws/flws_01.rm");
        vlcVideoView.startPlay();
    }

    //加载字幕方法
    //这里仅供参考
    // 更多方法参考官方APP
    private void startPlay0() {
        vlcVideoView.setAddSlave(alaveFile.getAbsolutePath());
        //    vlcVideoView.setAddSlave("android:resource://"+getActivity().getPackageName()+"/"+R.raw.test2);
        vlcVideoView.startPlay();
    }

    //打开硬件加速
    private void startPlay1() {
        LibVLC libVLC = VLCInstance.get(getContext());
        Media media = new Media(libVLC, Uri.parse(MainActivity.getUrl(getContext())));
        media.setHWDecoderEnabled(true, false);
        vlcVideoView.setMedia(new MediaPlayer(libVLC));
        vlcVideoView.startPlay();
    }

    //自定义 源文件
//    private void startPlay2() {
//        ArrayList<String> libOptions = VLCOptions.getLibOptions(getContext());
//        libOptions.add("--record-path");
//        libOptions.add(recordFile.getAbsolutePath());
//        LibVLC libVLC = new LibVLC(getContext(), libOptions);
//
//        Media media = new Media(libVLC, Uri.parse(MainActivity.getUrl(getContext())));
//        vlcVideoView.setMedia(new MediaPlayer(media));
//        vlcVideoView.startPlay();
//    }

    //自定义 源文件
    private void startPlay3() {
//        ArrayList<String> libOptions = new ArrayList<>();
        ArrayList<String> libOptions = VLCOptions.getLibOptions(getContext());

        //并不通用 所有环境
        libOptions.add("--rtsp-caching=0");
        libOptions.add("--network-caching=0"); // probably overwritten in org\videolan\libvlc\Media.java
        libOptions.add("--file-caching=0"); // probably overwritten in org\videolan\libvlc\Media.java
        libOptions.add("--rtsp-timeout=5");
        libOptions.add("--udp-timeout=5");
        libOptions.add("--clock-synchro=0");
        libOptions.add("--clock-jitter=0");
        libOptions.add("--network-synchronisation");
        libOptions.add("--no-drop-late-frames");
        libOptions.add("--quiet-synchro");
        libOptions.add("--sout-ts-dts-delay=100");
        libOptions.add("--sout-ps-dts-delay=100");
        LibVLC libVLC = new LibVLC(getContext(), libOptions);

        Media media = new Media(libVLC, Uri.parse("rtsp://video.fjtu.com.cn/vs01/flws/flws_01.rm"));



        vlcVideoView.setMedia(new MediaPlayer(media));
        vlcVideoView.startPlay();
    }

    @Override
    public void onResume() {
        super.onResume();
        vlcVideoView.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        vlcVideoView.pause();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //手动清空字幕
        vlcVideoView.setAddSlave(null);
        vlcVideoView.onStop();
    }

    public boolean isFullscreen;

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.change:
                isFullscreen = !isFullscreen;
                if (isFullscreen) {
                    getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                } else {
                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
                break;
            case R.id.changeSlave:
                if (!vlcVideoView.isPrepare()) return;
                Media media11 = vlcVideoView.getMediaPlayer().getMedia();
                media11.clearSlaves();
                break;
            case R.id.recordStart:
                // if (!vlcVideoView.isPrepare()) return;
                //  Media media = vlcVideoView.getMediaPlayer().getMedia();

//                var_SetString( p_input, "input-record-path", psz_filepath );
//                var_SetString( p_input, "sout-record-dst-prefix", psz_filename );
//                var_ToggleBool( p_input, "record");
                break;
            case R.id.recordStop:
                //     if (!vlcVideoView.isPrepare()) return;
                //       Media media2 = vlcVideoView.getMediaPlayer().getMedia();


                break;
            case R.id.thumbnail:
                thumbnail.setImageBitmap(vlcVideoView.getBitmap());
                //这个就是截图 保存Bitmap就行了 懒的写 老有人问
//                Bitmap bitmap = vlcVideoView.getBitmap();
//                saveBitmap("", bitmap);

                //如果要截封面图用MainActivity里的方法
                break;


        }
    }


    public static boolean saveBitmap(String savePath, Bitmap mBitmap) {
        try {
            File filePic = new File(savePath);
            if (!filePic.exists()) {
                filePic.getParentFile().mkdirs();
                filePic.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(filePic);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

}
