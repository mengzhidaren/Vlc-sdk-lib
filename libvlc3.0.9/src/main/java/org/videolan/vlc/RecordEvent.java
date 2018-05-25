package org.videolan.vlc;


import org.videolan.libvlc.MediaPlayer;

import java.io.File;

/**
 * vlc input_file --sout="#transcode{vfilter=adjust{gamma=1.5},vcodec=theo,vb=2000,scale=0.67,acodec=vorb,ab=128,channels=2}:standard{access=file,mux=ogg,dst="output_file.ogg"}"
 *
 * @author Created by yyl on 2018/5/23.
 * https://github.com/mengzhidaren
 */
public class RecordEvent {
    private static volatile boolean isSport = false;

    public RecordEvent() {
        loadLibrariesOnce();
    }

    private static void loadLibrariesOnce() {
        if (!isSport) {
            synchronized (RecordEvent.class) {
                if (!isSport) {
                    try {
                        System.loadLibrary("yylRecord");
                        isSport = true;
                    } catch (UnsatisfiedLinkError error) {
                        error.printStackTrace();
                    } catch (SecurityException error) {
                        error.printStackTrace();
                    }
                }
            }
        }
    }

    public static boolean isSport() {
        loadLibrariesOnce();
        return isSport;
    }

    /**
     * @param mediaPlayer
     * @param fileDirectory 录像目录   这是文件夹 不是文件
     * @return 是否录制成功
     */
    @Deprecated//这是临时的方法 因为还没找到改文件名的方法  后期必改
    public boolean startRecord(MediaPlayer mediaPlayer, String fileDirectory) {
        if (mediaPlayer != null) {
            new File(fileDirectory).mkdirs();
            return startRecord(mediaPlayer, fileDirectory, "");
        }
        return false;
    }

    // 还没找到改文件名的方法  先用 private  后期在开放
    private native boolean startRecord(MediaPlayer mediaPlayer, String fileDirectory, String fileName);

    public native boolean stopRecord(MediaPlayer mediaPlayer);

    public native boolean isRecording(MediaPlayer mediaPlayer);

    public native boolean isSuportRecord(MediaPlayer mediaPlayer);


    /**
     * @param mediaPlayer 播放器
     * @param path        path 可以是文件也可以是文件夹
     * @param width       图片
     * @param height      图片
     * @return 自已试吧
     */
    public native int takeSnapshot(MediaPlayer mediaPlayer, String path, int width, int height);


//      public native boolean setMediaPlayerInit(MediaPlayer mediaPlayer);

}
