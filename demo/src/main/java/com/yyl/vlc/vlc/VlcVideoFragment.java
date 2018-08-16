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
import org.videolan.vlc.RecordEvent;
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

    private RecordEvent recordEvent = new RecordEvent();
    //录像
    private File recordFile = new File(Environment.getExternalStorageDirectory(), "yyl");


    //vlc截图文件地址
    private File takeSnapshotFile = new File(Environment.getExternalStorageDirectory(), "yyl.png");
    String directory = recordFile.getAbsolutePath();

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
        view.findViewById(R.id.snapShot).setOnClickListener(this);
        recordFile.mkdirs();

        vlcVideoView.setMediaListenerEvent(new MediaControl(vlcVideoView, logInfo));
        vlcVideoView.setPath(MainActivity.getUrl(getContext()));
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        startPlay();
    }

    //more state
    private void startPlay() {
        vlcVideoView.startPlay();
    }

    //加载字幕方法
    //这里仅供参考
    // 更多方法参考官方APP
    private void startPlay0() {
        vlcVideoView.setAddSlave(alaveFile.getAbsolutePath());
        vlcVideoView.startPlay();
    }

    //打开硬件加速
    private void startPlay1() {
        LibVLC libVLC = VLCInstance.get(getContext());

        Media media = new Media(libVLC, Uri.parse(MainActivity.getUrl(getContext())));
      //  VLCOptions.setMediaOptions(media,getActivity(),VLCOptions.HW_ACCELERATION_AUTOMATIC);
//        media.setHWDecoderEnabled(true, true);
        media.setHWDecoderEnabled(false, false);
        vlcVideoView.setMedia(new MediaPlayer(libVLC));
        vlcVideoView.startPlay();
    }

    //    自定义 源文件
    private void startPlay2() {
        ArrayList<String> libOptions = VLCOptions.getLibOptions(getContext());

        LibVLC libVLC = new LibVLC(getContext(), libOptions);
        Media media = new Media(libVLC, Uri.parse(MainActivity.getUrl(getContext())));
        media.setHWDecoderEnabled(false, false);
        vlcVideoView.setMedia(new MediaPlayer(media));
        vlcVideoView.startPlay();
    }

    //直播测试 自定义 源文件
    private void startPlay3() {
//        ArrayList<String> libOptions = new ArrayList<>();
        ArrayList<String> libOptions = VLCOptions.getLibOptions(getContext());

        LibVLC libVLC = new LibVLC(getContext(), libOptions);
        Media media = new Media(libVLC, Uri.parse("rtsp://video.fjtu.com.cn/vs01/flws/flws_01.rm"));
        media.setHWDecoderEnabled(false, false);
        //   media.addOption(":sout-record-dst-prefix=yylpre.mp4");
//        media.addOption(":network-caching=1000");
//        media.addOption(":rtsp-frame-buffer-size=100000");
        //   media.addOption(":rtsp-tcp");

        MediaPlayer mediaPlayer = new MediaPlayer(media);
        vlcVideoView.setMedia(mediaPlayer);
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
//                if (!vlcVideoView.isPrepare()) return;
//                Media media11 = vlcVideoView.getMediaPlayer().getMedia();
//                media11.clearSlaves();
                vlcVideoView.onStop();
                vlcVideoView.startPlay();
                break;
            case R.id.recordStart:
                String std2 = "standard{access=file,mux=mp4,dst='" + directory + "/yyl.mp4'}";
                if (vlcVideoView.isPrepare()) {
                    recordEvent.startRecord(vlcVideoView.getMediaPlayer(),directory);
                }
                break;
            case R.id.recordStop:
                if (vlcVideoView.isPrepare()) {
                    recordEvent.stopRecord(vlcVideoView.getMediaPlayer());
                }

                break;
            case R.id.snapShot:
                if (vlcVideoView.isPrepare()) {
                    Media.VideoTrack videoTrack = vlcVideoView.getVideoTrack();
                    if (videoTrack != null) {
                        //原图
                       // recordEvent.takeSnapshot(vlcVideoView.getMediaPlayer(), takeSnapshotFile.getAbsolutePath(), 0, 0);
                        //原图的一半
                        recordEvent.takeSnapshot(vlcVideoView.getMediaPlayer(), takeSnapshotFile.getAbsolutePath(), videoTrack.width / 2, 0);
                    }
                }


                //                这个就是截图 保存Bitmap就行了
                //   thumbnail.setImageBitmap(vlcVideoView.getBitmap());
//                Bitmap bitmap = vlcVideoView.getBitmap();
//                saveBitmap("", bitmap);
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
