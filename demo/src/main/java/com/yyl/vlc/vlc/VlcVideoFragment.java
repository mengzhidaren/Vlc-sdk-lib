package com.yyl.vlc.vlc;


import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.yyl.vlc.MediaControl;
import com.yyl.vlc.R;

import org.videolan.libvlc.Media;
import org.videolan.vlc.VlcVideoView;
import org.videolan.vlc.util.VLCInstance;


/**
 * d
 */
public class VlcVideoFragment extends Fragment implements View.OnClickListener {
    public static final String path = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";
    //public static final String path = "rtsp://video.fjtu.com.cn/vs01/flws/flws_01.rm";
    VlcVideoView vlcVideoView;
    TextView logInfo;

    String tag = "VlcVideoFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video, container, false);
        vlcVideoView = (VlcVideoView) view.findViewById(R.id.player);
        logInfo = (TextView) view.findViewById(R.id.info);
        view.findViewById(R.id.change).setOnClickListener(this);
//        Media media = new Media(VLCInstance.get(getContext()), Uri.parse(path));
//        media.setHWDecoderEnabled(false, false);
//        vlcVideoView.setMedia(media);

        vlcVideoView.setMediaListenerEvent(new MediaControl(vlcVideoView, logInfo));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.i(tag, "---------   start   ----------------");
                vlcVideoView.startPlay(path);
            }
        }, 1000);

        return view;
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
