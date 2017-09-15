package org.videolan.vlc;

import android.graphics.Bitmap;
import android.util.Log;

import org.videolan.libvlc.Media;
import org.videolan.libvlc.util.VLCUtil;
import org.videolan.vlc.util.LogUtils;

import java.nio.ByteBuffer;

/**
 * Created by yuyunlong on 2017/9/13/013.
 */

public class ThumbnailUtils {


    public static Bitmap getThumbnail(Media media, int width, int height) {
        byte[] b = VLCUtil.getThumbnail(media, width, height);
        LogUtils.i("ThumbnailUtils", "getThumbnail=" + (b == null));
        if (b != null) {
            // Create the bitmap
            Bitmap thumbnail = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            thumbnail.copyPixelsFromBuffer(ByteBuffer.wrap(b));
            return thumbnail;
        }
        return null;
    }

}
