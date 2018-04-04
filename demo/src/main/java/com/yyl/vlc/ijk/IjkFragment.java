package com.yyl.vlc.ijk;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yyl.vlc.MainActivity;
import com.yyl.vlc.R;


/**
 */
public class IjkFragment extends Fragment {
    IjkPlayer ijkPlayer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ijk, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ijkPlayer = (IjkPlayer) view.findViewById(R.id.ijkplayer);
        ijkPlayer.setPath(MainActivity.getUrl(getContext()));
    }

    @Override
    public void onResume() {
        super.onResume();
        ijkPlayer.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        ijkPlayer.onPause();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        ijkPlayer.release();
    }
}
