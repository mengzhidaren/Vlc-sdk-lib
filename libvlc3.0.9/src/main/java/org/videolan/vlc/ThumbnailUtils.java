package org.videolan.vlc;

import android.graphics.Bitmap;

import org.videolan.libvlc.Media;
import org.videolan.libvlc.util.VLCUtil;

import java.nio.ByteBuffer;

/**
 * @author https://github.com/mengzhidaren
 */

public class ThumbnailUtils {

    public static Bitmap getThumbnail(Media media, int width, int height) {
        byte[] b = VLCUtil.getThumbnail(media, width, height);
        if (b != null) {
            // Create the bitmap
            Bitmap thumbnail = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            thumbnail.copyPixelsFromBuffer(ByteBuffer.wrap(b));
            return thumbnail;
        }
        return null;
    }

}
