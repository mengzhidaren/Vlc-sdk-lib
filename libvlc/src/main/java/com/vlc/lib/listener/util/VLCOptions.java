/*****************************************************************************
 * VLCOptions.java
 *****************************************************************************
 * Copyright © 2015 VLC authors and VideoLAN
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston MA 02110-1301, USA.
 *****************************************************************************/

package com.vlc.lib.listener.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ConfigurationInfo;
import android.media.AudioManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.util.AndroidUtil;
import org.videolan.libvlc.util.HWDecoderUtil;
import org.videolan.libvlc.util.VLCUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import static android.content.Context.ACTIVITY_SERVICE;
import static android.os.Build.VERSION_CODES.KITKAT;

//  <string-array name="deblocking_values" translatable="false">
//        <item>-1</item>
//        <item>0</item>
//        <item>1</item>
//        <item>3</item>
//        <item>4</item>
//    </string-array>
//          <string name="deblocking_always">完全去块 (最慢)</string>
//    <string name="deblocking_nonref">中度去块</string>
//    <string name="deblocking_nonkey">低级去块</string>
//    <string name="deblocking_all">不去块 (最快)</string>
public class VLCOptions {
    private static final String TAG = "VLCConfig";

    public static final int AOUT_AUDIOTRACK = 0;
    public static final int AOUT_OPENSLES = 1;

    @SuppressWarnings("unused")
    public static final int HW_ACCELERATION_AUTOMATIC = -1;
    public static final int HW_ACCELERATION_DISABLED = 0;
    public static final int HW_ACCELERATION_DECODING = 1;
    public static final int HW_ACCELERATION_FULL = 2;

    private static int AUDIOTRACK_SESSION_ID = 0;

    private static boolean isSupportsOpenGL(Context context) {
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x2000;
        boolean isEmulator = Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1
                && (Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86"));
        return supportsEs2 || isEmulator;
    }

    // TODO should return List<String>
    public static ArrayList<String> getLibOptions(Context context) {
        /* generate an audio session id so as to share audio output with external equalizer */
        if (Build.VERSION.SDK_INT >= 21 && AUDIOTRACK_SESSION_ID == 0) {
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            AUDIOTRACK_SESSION_ID = audioManager.generateAudioSessionId();
        }

        ArrayList<String> options = new ArrayList<String>(50);

        final boolean timeStrechingDefault = Build.VERSION.SDK_INT > KITKAT;
        //启用音频的时间伸缩      可加速与减慢音频播放而不改变音调 (需要较快设备支持)。
        final String subtitlesEncoding = "";
        final boolean frameSkip = false;//启用跳帧     加速解码，但可能降低画质
        String chroma = "RV16";//RGB 32 位: 默认色度 RGB 16 位: 性能更佳，但画质下降 YUV: 性能最佳，但并非所有设备可用。仅限 Android 2.3 及更高版本。
        final boolean verboseMode = true;

//缓冲网络媒体的时间量 (单位为毫秒)。硬件解码时无效。留空可重置。
        int networkCaching =1500;
        final String freetypeRelFontsize = "16";
        final boolean freetypeBold = false;
        final String freetypeColor = "16777215";
        final boolean freetypeBackground = false;
        final boolean opengl = isSupportsOpenGL(context);

        /* CPU intensive plugin, setting for slow devices */
        options.add(timeStrechingDefault ? "--audio-time-stretch" : "--no-audio-time-stretch");

        options.add("--avcodec-skiploopfilter");
        options.add("" + getDeblocking(-1));//这里太大了消耗性能   太小了会花屏
//        options.add("0");//这里太大了消耗性能   太小了会花屏


        options.add("--avcodec-skip-frame");
        options.add(frameSkip ? "2" : "0");
        options.add("--avcodec-skip-idct");
        options.add(frameSkip ? "2" : "0");
        options.add("--subsdec-encoding");
        options.add(subtitlesEncoding);//chi是中国文字
        options.add("--stats");
        /* XXX: why can't the default be fine ? #7792 */
        if (networkCaching > 0)
            options.add("--network-caching=" + networkCaching);
        options.add("--android-display-chroma");
        if (chroma.equals("YV12")) {
            chroma = "";
        } else {
            chroma = opengl ? "YUV" : chroma;
        }
        options.add(chroma);
        options.add("--audio-resampler");
        options.add("soxr");
        options.add("--audiotrack-session-id=" + AUDIOTRACK_SESSION_ID);

        options.add("--freetype-rel-fontsize=" + freetypeRelFontsize);
        if (freetypeBold)
            options.add("--freetype-bold");
        options.add("--freetype-color=" + freetypeColor);
        if (freetypeBackground)
            options.add("--freetype-background-opacity=128");
        else
            options.add("--freetype-background-opacity=0");
//        if (opengl == 1)
//            options.add("--vout=gles2,none");
//        else if (opengl == 0)
//            options.add("--vout=android_display,none");
//        else
//            options.add("--vout=android_display");
        if (opengl) {
            options.add("--vout=gles2,none");//OpenGL ES 2.0
        } else {
            options.add("--vout=android_display,none");
        }

        /* Configure keystore */
        options.add("--keystore");
        if (AndroidUtil.isMarshMallowOrLater)
            options.add("file_crypt,none");
        else
            options.add("file_plaintext,none");
        options.add("--keystore-file");
        options.add(new File(context.getDir("keystore", Context.MODE_PRIVATE), "file").getAbsolutePath());



        //Chromecast
        options.add("-v");
        options.add("--no-sout-chromecast-audio-passthrough");
        options.add("--sout-chromecast-conversion-quality=2");
        options.add("--sout-keep");
        //加入自已的配置
        options.add("--smb-force-v1");
        LogUtils.i("options=" + Arrays.toString(options.toArray()));
        return options;
    }

    public static boolean isAudioDigitalOutputEnabled(SharedPreferences pref) {
        return pref.getBoolean("audio_digital_output", false);
    }

    public static void setAudioDigitalOutputEnabled(SharedPreferences pref, boolean enabled) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("audio_digital_output", enabled);
        editor.apply();
    }

    public static String getAout(SharedPreferences pref) {
        int aout = -1;
        try {
            aout = Integer.parseInt(pref.getString("aout", "-1"));
        } catch (NumberFormatException ignored) {
        }
        final HWDecoderUtil.AudioOutput hwaout = HWDecoderUtil.getAudioOutputFromDevice();
        if (hwaout == HWDecoderUtil.AudioOutput.AUDIOTRACK || hwaout == HWDecoderUtil.AudioOutput.OPENSLES)
            aout = hwaout == HWDecoderUtil.AudioOutput.OPENSLES ? AOUT_OPENSLES : AOUT_AUDIOTRACK;

        return aout == AOUT_OPENSLES ? "opensles_android" : null /* audiotrack is the default */;
    }

    private static int getDeblocking(int deblocking) {
        int ret = deblocking;
        if (deblocking < 0) {
            /**
             * Set some reasonable sDeblocking defaults:
             *
             * Skip all (4) for armv6 and MIPS by default
             * Skip non-ref (1) for all armv7 more than 1.2 Ghz and more than 2 cores
             * Skip non-key (3) for all devices that don't meet anything above
             */
            VLCUtil.MachineSpecs m = VLCUtil.getMachineSpecs();
            if (m == null)
                return ret;
            if ((m.hasArmV6 && !(m.hasArmV7)) || m.hasMips)
                ret = 4;
            else if (m.frequency >= 1200 && m.processors > 2)
                ret = 1;
            else if (m.bogoMIPS >= 1200 && m.processors > 2) {
                ret = 1;
                Log.d(TAG, "Used bogoMIPS due to lack of frequency info");
            } else
                ret = 3;
        } else if (deblocking > 4) { // sanity check
            ret = 3;
        }
        return ret;
    }

    private static String getResampler() {
        final VLCUtil.MachineSpecs m = VLCUtil.getMachineSpecs();
        return (m == null || m.processors > 2) ? "soxr" : "ugly";
    }

//    public static void setMediaOptions(Media media, Context context, int flags) {
//        boolean noHardwareAcceleration = (flags & MediaWrapper.MEDIA_NO_HWACCEL) != 0;
//        boolean noVideo = (flags & MediaWrapper.MEDIA_VIDEO) == 0;
//        final boolean paused = (flags & MediaWrapper.MEDIA_PAUSED) != 0;
//        int hardwareAcceleration = HW_ACCELERATION_DISABLED;
//        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
//
//        if (!noHardwareAcceleration) {
//            try {
//                hardwareAcceleration = Integer.parseInt(prefs.getString("hardware_acceleration", "-1"));
//            } catch (NumberFormatException ignored) {
//            }
//        }
//        if (hardwareAcceleration == HW_ACCELERATION_DISABLED)
//            media.setHWDecoderEnabled(false, false);
//        else if (hardwareAcceleration == HW_ACCELERATION_FULL || hardwareAcceleration == HW_ACCELERATION_DECODING) {
//            media.setHWDecoderEnabled(true, true);
//            if (hardwareAcceleration == HW_ACCELERATION_DECODING) {
//                media.addOption(":no-mediacodec-dr");
//                media.addOption(":no-omxil-dr");
//            }
//        } /* else automatic: use default options */
//
//        if (noVideo) media.addOption(":no-video");
//        if (paused) media.addOption(":start-paused");
//        if (!prefs.getBoolean("subtitles_autoload", true)) media.addOption(":sub-language=none");
//        if (prefs.getBoolean("media_fast_seek", true)) media.addOption(":input-fast-seek");
//
////        if (RendererDelegate.INSTANCE.getSelectedRenderer() != null) {
////            media.addOption(":sout-chromecast-audio-passthrough="+prefs.getBoolean("casting_passthrough", true));
////            media.addOption(":sout-chromecast-conversion-quality="+prefs.getString("casting_quality", "2"));
////        }
//    }

}
