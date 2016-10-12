package videolist.yyl.com.vlc_sdk_lib;


import android.content.res.Configuration;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.vlc.util.VLCInstance;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A fragment with a yyl +1 button.
 * create an instance of this fragment.
 */
public class VideoFragment extends Fragment implements IVLCVout.Callback {
    String tag = "yyl";
    @Bind(R.id.textureView)
    TextureView textureView;


    LibVLC libVLC;
    MediaPlayer mediaPlayer;
    String path = "http://img1.peiyinxiu.com/2014121211339c64b7fb09742e2c.mp4";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        libVLC = VLCInstance.get(getContext());
        mediaPlayer = new MediaPlayer(libVLC);
        Media media = new Media(libVLC, Uri.parse(path));
        media.setHWDecoderEnabled(false, false);
        media.parseAsync(Media.Parse.FetchNetwork, 10 * 1000);
        mediaPlayer.setMedia(media);
        mediaPlayer.setEventListener(new MediaPlayer.EventListener() {
            @Override
            public void onEvent(MediaPlayer.Event event) {
                switch (event.type) {
                    case MediaPlayer.Event.Opening:
                        Log.i(tag, "Event Opening");
                        break;
                    case MediaPlayer.Event.Buffering:
                        Log.i(tag, "Event Buffering=" + event.getBuffering());
                        break;
                    case MediaPlayer.Event.Stopped:
                        Log.i(tag, "Event Stopped");
                        break;

                }
            }
        });
        mediaPlayer.getVLCVout().addCallback(this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(tag, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_video, container, false);
        ButterKnife.bind(this, view);
        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                mediaPlayer.getVLCVout().setVideoSurface(surface);
                mediaPlayer.getVLCVout().attachViews();
                mediaPlayer.play();
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                mediaPlayer.getVLCVout().detachViews();
                mediaPlayer.stop();
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onNewLayout(IVLCVout vlcVout, int width, int height, int visibleWidth, int visibleHeight, int sarNum, int sarDen) {
        adjustAspectRatio(width, height, textureView);
    }

    @Override
    public void onSurfacesCreated(IVLCVout vlcVout) {

    }

    @Override
    public void onSurfacesDestroyed(IVLCVout vlcVout) {

    }

    private void adjustAspectRatio(int videoWidth, int videoHeight, TextureView textureView) {
        if (videoWidth * videoHeight == 0) {
            return;
        }
        int viewWidth = textureView.getWidth();
        int viewHeight = textureView.getHeight();
        double aspectRatio = (double) videoHeight / (double) videoWidth;

        int newWidth, newHeight;
        if (viewHeight > (int) (viewWidth * aspectRatio)) {
            // limited by narrow width; restrict height
            newWidth = viewWidth;
            newHeight = (int) (viewWidth * aspectRatio);
        } else {
            // limited by short height; restrict width
            newWidth = (int) (viewHeight / aspectRatio);
            newHeight = viewHeight;
        }
        int xoff = (viewWidth - newWidth) / 2;
        int yoff = (viewHeight - newHeight) / 2;
        Log.i(tag, "video=" + videoWidth + "x" + videoHeight + " view="
                + viewWidth + "x" + viewHeight + " newView=" + newWidth + "x"
                + newHeight + " off=" + xoff + "," + yoff);

        Matrix txform = new Matrix();
        textureView.getTransform(txform);
        txform.setScale((float) newWidth / viewWidth, (float) newHeight
                / viewHeight);
        // txform.postRotate(10); // just for fun
        txform.postTranslate(xoff, yoff);
        textureView.setTransform(txform);
    }
}
