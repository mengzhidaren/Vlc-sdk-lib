package videolist.yyl.com.vlc_sdk_lib;


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

import org.videolan.libvlc.Media;
import org.videolan.vlc.VlcVideoView;
import org.videolan.vlc.util.VLCInstance;


/**
 */
public class VideoFragment extends Fragment implements View.OnClickListener {
   // String path = "http://img1.peiyinxiu.com/2014121211339c64b7fb09742e2c.mp4";
   // String path = "rtsp://video.fjtu.com.cn/vs01/flws/flws_01.rm";
    String path = "rtsp://139.199.159.71:554/easypusher_rtsp.sdp";
    //  @Bind(R.id.player)
    VlcVideoView vlcVideoView;
    //  @Bind(R.id.info)
    TextView logInfo;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video, container, false);
        //为啥这么多人用不会用butterknife 要我注掉
        // ButterKnife.bind(this, view);
        vlcVideoView = (VlcVideoView) view.findViewById(R.id.player);
        logInfo = (TextView) view.findViewById(R.id.info);
        view.findViewById(R.id.change).setOnClickListener(this);
//        Media media = new Media(VLCInstance.get(getContext()), Uri.parse(path));
//        media.setHWDecoderEnabled(false, false);
//        vlcVideoView.setMedia(media);

        vlcVideoView.setMediaListenerEvent(new MediaControl(vlcVideoView, logInfo));
        vlcVideoView.startPlay(path);
        return view;
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
