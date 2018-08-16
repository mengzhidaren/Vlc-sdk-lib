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
    @Deprecated//这是临时的方法 文件名是宏定义的 还没有改文件名的方法  后期必改
    public boolean startRecord(MediaPlayer mediaPlayer, String fileDirectory) {
        if (mediaPlayer != null) {
            new File(fileDirectory).mkdirs();
            return startRecord(mediaPlayer, fileDirectory, "");
        }
        return false;
    }

    // TMD文件名是宏定义的  难道只能修改源代码    有知道的请留言
    private native boolean startRecord(MediaPlayer mediaPlayer, String fileDirectory, String fileName);

    public native boolean stopRecord(MediaPlayer mediaPlayer);

    public native boolean isRecording(MediaPlayer mediaPlayer);

    public native boolean isSuportRecord(MediaPlayer mediaPlayer);


    /**
     * * Take a snapshot of the current video window.
     * <p>
     * If i_width AND i_height is 0, original size is used.
     * If i_width XOR i_height is 0, original aspect-ratio is preserved.
     * <p>
     * \param p_mi media player instance
     * \param num number of video output (typically 0 for the first/only one)
     * \param psz_filepath the path of a file or a folder to save the screenshot into
     * \param i_width the snapshot's width
     * \param i_height the snapshot's height
     * \return 0 on success, -1 if the video was not found
     */
    public native int takeSnapshot(MediaPlayer mediaPlayer, String path, int width, int height);


//      public native boolean setMediaPlayerInit(MediaPlayer mediaPlayer);

}
