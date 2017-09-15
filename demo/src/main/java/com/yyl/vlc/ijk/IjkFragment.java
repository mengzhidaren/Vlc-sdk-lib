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
 * A simple {@link Fragment} subclass.
 */
public class IjkFragment extends Fragment {
    IjkPlayer ijkPlayer;
    String tag = "IjkFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ijk, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ijkPlayer = (IjkPlayer) view.findViewById(R.id.ijkplayer);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.i(tag, "---------   start   ----------------");
                ijkPlayer.setPath(MainActivity.path);
            }
        }, 1000);

        view.findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        view.findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

    }

    @Override
    public void onDetach() {
        super.onDetach();
        ijkPlayer.release();
    }
}
