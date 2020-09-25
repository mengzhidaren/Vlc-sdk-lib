package com.yyl.vlc.vlc;


import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.vlc.lib.RecordEvent;
import com.vlc.lib.VlcVideoView;
import com.vlc.lib.listener.util.VLCOptions;
import com.yyl.vlc.MainActivity;
import com.yyl.vlc.R;

import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;

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

    String tag = "VlcVideoFragment";
    //本地字幕文件
    private File alaveFile = new File(Environment.getExternalStorageDirectory(), "test2.srt");
    //网络字幕
    private String uri = "";

    private RecordEvent recordEvent = new RecordEvent();
    //录像
    private File recordFile = new File(Environment.getExternalStorageDirectory(), "yyl");
    private File videoFile = new File(Environment.getExternalStorageDirectory(), "yyl" + File.separatorChar + "yyl.mp4");


    //vlc截图文件地址
    private File takeSnapshotFile = new File(Environment.getExternalStorageDirectory(), "yyl.png");
    String directory = recordFile.getAbsolutePath();

    boolean show1 = true;
    FrameLayout frameLayout1;
    FrameLayout frameLayout2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video, container, false);
        vlcVideoView = view.findViewById(R.id.player);
        logInfo = view.findViewById(R.id.info);
        thumbnail = view.findViewById(R.id.thumbnail);
        frameLayout1 = view.findViewById(R.id.layout1);
        frameLayout2 = view.findViewById(R.id.layout2);
        recordFile.mkdirs();
        thumbnail.setOnClickListener(this);
        view.findViewById(R.id.change).setOnClickListener(this);
        view.findViewById(R.id.changeSlave).setOnClickListener(this);
        view.findViewById(R.id.recordStart).setOnClickListener(this);
        view.findViewById(R.id.recordStop).setOnClickListener(this);
        view.findViewById(R.id.snapShot).setOnClickListener(this);
        view.findViewById(R.id.change_Parent).setOnClickListener(v -> {
            Log.i(tag, "removeAllViews");
            ((ViewGroup) vlcVideoView.getParent()).removeAllViews();
            //切换屏幕后  暂时用seek清空cache
            vlcVideoView.setClearVideoTrackCache(true);
            if (show1) {
                frameLayout2.addView(vlcVideoView);
            } else {
                frameLayout1.addView(vlcVideoView);
            }
            show1 = !show1;
        });

        view.findViewById(R.id.test1).setOnClickListener(v -> {
            startPlay1();
        });

        view.findViewById(R.id.test2).setOnClickListener(v -> {
            startPlay2();
        });

        vlcVideoView.setMediaListenerEvent(new MediaControl(vlcVideoView, logInfo));
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        startPlay1();
    }


    //加载字幕方法
    //这里仅供参考
    // 更多方法参考官方APP
    private void startPlay0() {
        vlcVideoView.setPath("");
        vlcVideoView.setAddSlave(alaveFile.getAbsolutePath());
        vlcVideoView.startPlay();
    }

    //自定义 源文件
    private void startPlay1() {
        vlcVideoView.setPath(MainActivity.path);
        vlcVideoView.startPlay();
    }

    //打开硬件加速
    private void startPlay2() {
        ArrayList<String> libOptions = VLCOptions.getLibOptions(getContext());

        LibVLC libVLC = new LibVLC(getContext(), libOptions);
        Media media = new Media(libVLC, Uri.parse(MainActivity.path));
        media.setHWDecoderEnabled(true, true);
        vlcVideoView.setMedia(media);
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
        vlcVideoView.setMediaPlayer(mediaPlayer);
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
//                vlcVideoView.onStop();
//                vlcVideoView.startPlay();
                break;
            case R.id.recordStart:
                if (vlcVideoView.isPrepare()) {
                    vlcVideoView.getMediaPlayer().record(directory);
//                    recordEvent.startRecord(vlcVideoView.getMediaPlayer(), directory, "yyl.mp4");
                }
                break;
            case R.id.recordStop:
                if (vlcVideoView.isPrepare()) {
                    vlcVideoView.getMediaPlayer().record(null);
//                    recordEvent.stopRecord(vlcVideoView.getMediaPlayer());
                }

                break;
            case R.id.snapShot:
                if (vlcVideoView.isPrepare()) {
                    Media.VideoTrack videoTrack = vlcVideoView.getVideoTrack();
                    if (videoTrack != null) {
//                        vlcVideoView.getMediaPlayer().updateVideoSurfaces();
                        //原图
                        recordEvent.takeSnapshot(vlcVideoView.getMediaPlayer(), takeSnapshotFile.getAbsolutePath(), 0, 0);
                        //原图的一半
                        //    recordEvent.takeSnapshot(vlcVideoView.getMediaPlayer(), takeSnapshotFile.getAbsolutePath(), videoTrack.width / 2, 0);
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
