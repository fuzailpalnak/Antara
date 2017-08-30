package in.antara.antara;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;

import java.io.IOException;

import in.antara.antara.camera.CameraPreview;
import in.antara.antara.position.PositionView;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private Camera camera;
    private CameraPreview cameraPreview;
    private PositionView positionView;
    private FrameLayout frameLayout;
    private Button clickButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(LOG_TAG, "Antara");

        if (!OpenCVLoader.initDebug()) {
            Log.d(LOG_TAG, "OpenCV not loaded");
            // Toast.makeText(this, "OpenCV not loaded", Toast.LENGTH_SHORT).show();
        } else {
            Log.d(LOG_TAG, "OpenCV loaded");
            // Toast.makeText(this, "OpenCV loaded", Toast.LENGTH_SHORT).show();
        }

        if(checkCameraHardware(getApplicationContext())) {
            camera = getCameraInstance();
            if (camera == null) {
                Log.e(LOG_TAG, "Failed to create camera instance");
                Toast.makeText(getApplicationContext(), "Failed to create camera instance", Toast.LENGTH_SHORT);
                exit();
            }
            cameraPreview = new CameraPreview(this, camera);
            frameLayout = (FrameLayout) findViewById(R.id.camera_preview);
            frameLayout.addView(cameraPreview);

            positionView = (PositionView) findViewById(R.id.position_view);
            clickButton = (Button)  findViewById(R.id.captureFront);

            updateHeights(clickButton, cameraPreview, positionView);
        } else {
            Log.e(LOG_TAG, "Failed to access camera");
            Toast.makeText(getApplicationContext(), "Failed to access camera", Toast.LENGTH_SHORT);
            exit();
        }
    }

    private void exit() {
        finish();
        System.exit(0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (camera != null) {
            try {
                camera.reconnect();
            } catch (IOException e) {
                Log.e(LOG_TAG, e.getMessage());
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // camera.release();
    }

    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            Log.d(LOG_TAG, "Camera available on device");
            return true;
        } else {
            Log.d(LOG_TAG, "Camera not available on device");
            return false;
        }
    }

    public Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
            c.setDisplayOrientation(90);
        } catch (Exception e){
            Log.e(LOG_TAG, e.getMessage());
        }
        return c; // returns null if camera is unavailable
    }

    public void takePicture(View view) {
        Log.d(LOG_TAG, "Take picture clicked");
        cameraPreview.takePicture();
    }

    private void updateHeights(Button clickButton, CameraPreview cameraPreview,
                               PositionView positionView) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displayMetrics);
        int heightPixels = displayMetrics.heightPixels;
        int widthPixels = displayMetrics.widthPixels;

        clickButton.setHeight((int) (0.2 * heightPixels));
        cameraPreview.getHolder().setFixedSize(widthPixels, (int)(0.4 * heightPixels));
        positionView.getHolder().setFixedSize(widthPixels, (int)(0.4 * heightPixels));
    }
}
