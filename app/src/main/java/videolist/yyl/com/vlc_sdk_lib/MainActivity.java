package videolist.yyl.com.vlc_sdk_lib;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.videolan.vlc.util.VLCInstance;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    String tag = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (VLCInstance.testCompatibleCPU(this)) {
            Log.i(tag, "支持 x86  armv7");
            setContentView(R.layout.activity_main);
            // Example of a call to a native method
            TextView tv = (TextView) findViewById(R.id.sample_text);
            tv.setText(stringFromJNI());
        } else {
            Log.i(tag, "什么破手机  不支持");
        }


    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}
