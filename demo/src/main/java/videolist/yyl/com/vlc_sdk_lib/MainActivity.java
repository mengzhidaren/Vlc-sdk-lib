package videolist.yyl.com.vlc_sdk_lib;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.videolan.vlc.util.VLCInstance;

public class MainActivity extends AppCompatActivity {

    String tag = "MainActivity";

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == ActivityInfo.SCREEN_ORIENTATION_USER) {
            getSupportActionBar().hide();
        } else {
            getSupportActionBar().show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (VLCInstance.testCompatibleCPU(this)) {
            Log.i(tag, "支持 x86  armv7");
            setContentView(R.layout.activity_main);
        } else {
            Log.i(tag, "什么破手机  不支持");
        }


    }

}
