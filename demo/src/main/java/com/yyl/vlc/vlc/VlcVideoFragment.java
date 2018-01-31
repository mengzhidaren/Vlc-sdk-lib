package com.yyl.vlc.vlc;


import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.yyl.vlc.MainActivity;
import com.yyl.vlc.R;

import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.vlc.VlcVideoView;
import org.videolan.vlc.util.VLCInstance;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * 有问题留言给我 https://github.com/mengzhidaren
 */
public class VlcVideoFragment extends Fragment implements View.OnClickListener {
    private VlcVideoView vlcVideoView;
    private TextView logInfo;
    private TextureView surface;//字幕画布
    //本地字幕文件
    private File alaveFile = new File(Environment.getExternalStorageDirectory(), "test2.srt");
    //    private File alaveFile = new File(Environment.getExternalStorageDirectory(), "test.ass");
    //网络字幕
    private String uri = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video, container, false);
        vlcVideoView = view.findViewById(R.id.player);
        surface = view.findViewById(R.id.surface);
        logInfo = view.findViewById(R.id.info);
        view.findViewById(R.id.change).setOnClickListener(this);
        view.findViewById(R.id.changeSlave).setOnClickListener(this);
        view.findViewById(R.id.change2).setOnClickListener(this);
        vlcVideoView.setMediaListenerEvent(new MediaControl(vlcVideoView, logInfo));
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        startPlay0();
    }

    //大多数情况
    private void startPlay() {
        vlcVideoView.startPlay(MainActivity.getUrl(getContext()));
    }


    //加载字幕方法
    //这里仅供参考
    // 更多方法参考官方APP
    private void startPlay0() {
        surface.setVisibility(View.VISIBLE);
        vlcVideoView.setSurfaceSubtitlesView(surface);
        vlcVideoView.setAddSlave(alaveFile.getAbsolutePath());
        //    vlcVideoView.setAddSlave("android:resource://"+getActivity().getPackageName()+"/"+R.raw.test2);
        vlcVideoView.startPlay(MainActivity.getUrl(getContext()));
    }

    //打开硬件加速
    private void startPlay1() {
        Media media = new Media(VLCInstance.get(getContext()), Uri.parse(MainActivity.getUrl(getContext())));
        media.setHWDecoderEnabled(true, false);
        vlcVideoView.setMedia(media);
        vlcVideoView.startPlay(null);
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
                MediaPlayer mediaPlayer = vlcVideoView.getMediaPlayer();
                MediaPlayer.TrackDescription[] spuTracks = mediaPlayer.getSpuTracks();
                if (spuTracks != null && spuTracks.length > 1) {
                    Log.i("yyl spu=" + mediaPlayer.getSpuTrack(), "spuTracks=" + Arrays.toString(spuTracks));
                    mediaPlayer.setSpuTrack(isOpenSlave ? spuTracks[0].id : spuTracks[1].id);//关闭字幕
                }
                isOpenSlave = !isFullscreen;
                break;
            case R.id.change2:
                if (!vlcVideoView.isPrepare()) return;
                MediaPlayer mediaPlayer2 = vlcVideoView.getMediaPlayer();
               // mediaPlayer2.setAspectRatio("16:9");
             //   mediaPlayer2.setScale(4/3);
                break;
        }
    }

    boolean isOpenSlave = true;

    //自定义 源文件
    private void startPlay2() {
        ArrayList<String> options = new ArrayList<>();
//        options.add("--text-renderer");
//        options.add("UTF-8");//
        //   options.add("--subsdec-encoding=UTF-8");// 字幕字体编码
        // options.add("--no-osd");//
        options.add("-vvv");//显示全部调试日志
        LibVLC libVLC = new LibVLC(getContext(), options);
        Media media = new Media(libVLC, Uri.parse(MainActivity.getUrl(getContext())));
        vlcVideoView.setMediaPlayer(new MediaPlayer(libVLC));
        vlcVideoView.setMedia(media);
        surface.setVisibility(View.VISIBLE);
        vlcVideoView.setSurfaceSubtitlesView(surface);
        //字幕
        vlcVideoView.setAddSlave(alaveFile.getAbsolutePath());
        vlcVideoView.startPlay(null);
    }
}
