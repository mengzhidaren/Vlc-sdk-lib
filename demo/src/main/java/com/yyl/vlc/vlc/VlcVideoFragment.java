package com.yyl.vlc.vlc;


import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
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

import java.util.ArrayList;


/**
 * 有问题留言给我 https://github.com/mengzhidaren
 */
public class VlcVideoFragment extends Fragment implements View.OnClickListener {
    private VlcVideoView vlcVideoView;
    private TextView logInfo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video, container, false);
        vlcVideoView = view.findViewById(R.id.player);
        logInfo = view.findViewById(R.id.info);
        view.findViewById(R.id.change).setOnClickListener(this);
        vlcVideoView.setMediaListenerEvent(new MediaControl(vlcVideoView, logInfo));
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        vlcVideoView.startPlay(MainActivity.getUrl(getContext()));


//        Media media = new Media(VLCInstance.get(getContext()), Uri.parse(MainActivity.getUrl(getContext())));
//        media.setHWDecoderEnabled(true, false);//打开硬件加速
//        vlcVideoView.startPlay(null);
//-----------------------------日志-------------------------------------
//        ArrayList<String> options = new ArrayList<>();
//        options.add("-vvv");//显示全部调试日志
//        LibVLC libVLC = new LibVLC(getContext(), options);
//        Media media = new Media(libVLC, Uri.parse(MainActivity.getUrl(getContext())));
////        MediaPlayer mediaPlayer=new MediaPlayer(media);
////        vlcVideoView.setMediaPlayer(mediaPlayer);
//        vlcVideoView.setMedia(media);
//        vlcVideoView.startPlay(null);

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
        }
    }
}
