//
// Created by yyl on 2018/5/28.
//

#include "vlc/vlc.h"

/**
 * 最简单的基于libVLC的推流器
 * Simplest libVLC Streamer
 *
 * 雷霄骅 Lei Xiaohua
 * leixiaohua1020@126.com
 * 中国传媒大学/数字电视技术
 * Communication University of China / Digital TV Technology
 * http://blog.csdn.net/leixiaohua1020
 *
 * 本程序是一个最简单的基于libVLC的推流器。
 * 适合初学者学习libVLC。
 *
 * This example is the simplest Streamer based on libVLC.
 * Suitable for the beginner of libVLC.
 */
int main(int argc, char **argv) {
    libvlc_instance_t *vlc;
    const char *url;
    //Send File
    //Transcode it. Video codec use x264. Audio codec use mpga.
    //Mux it to mpegts format.
    //And stream it to udp://233.233.233.233:6666
    /*
    const char *sout = "#transcode{vcodec=h264,fps=25,venc=x264{preset=ultrafast,"\
        "profile=main,tune=zerolatency},vb=512,scale=0.5,"                        \
        "acodec=mpa,aenc=ffmpeg,ab=64,channels=2}"                                \
        ":standard{access=udp,mux=ts,dst=233.233.233.233:6666}";
    */
    //Send and playing at same time
    const char *sout = "#transcode{vcodec=h264,fps=25,venc=x264{preset=ultrafast,"\
        "profile=baseline,tune=zerolatency},vb=512,"                              \
        "acodec=mpga,ab=64,channels=2}"                                           \
        ":duplicate{dst=display,dst=standard{access=udp,mux=ts,dst=233.233.233.233:6666}}";
    const char *media_name = "Lei's test";

    //Screen Capture
    //url = "screen://";

    url = "cuc_ieschool.flv";

    vlc = libvlc_new(0, NULL);
    libvlc_vlm_add_broadcast(vlc, media_name, url, sout, 0, NULL, true, false);
    libvlc_vlm_play_media(vlc, media_name);

    //play 30s
   // _sleep(30000);

    libvlc_vlm_stop_media(vlc, media_name);
    libvlc_vlm_release(vlc);
    return 0;
}