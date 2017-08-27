package in.antara.antara;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(LOG_TAG, "Antara");

        if (!OpenCVLoader.initDebug()) {
            Log.d(LOG_TAG, "OpenCV not loaded");
            Toast.makeText(this, "OpenCV not loaded", Toast.LENGTH_SHORT).show();
        } else {
            Log.d(LOG_TAG, "OpenCV loaded");
            Toast.makeText(this, "OpenCV loaded", Toast.LENGTH_SHORT).show();
        }
    }
}
