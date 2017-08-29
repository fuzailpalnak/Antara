package in.antara.antara.camera;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import in.antara.antara.AntaraApplication;

/**
 * Created by udar on 8/29/2017.
 */

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private static final String LOG_TAG = CameraPreview.class.getSimpleName();

    private SurfaceHolder surfaceHolder;
    private Camera camera;
    private PictureTakenCallback pictureTakenCallback;


    public CameraPreview(Context context, Camera camera) {
        super(context);
        init(context, camera);
    }

    public CameraPreview(Context context, AttributeSet attrs, Camera camera) {
        super(context, attrs);
        init(context, camera);
    }

    public CameraPreview(Context context, AttributeSet attrs, int defStyleAttr, Camera camera) {
        super(context, attrs, defStyleAttr);
        init(context, camera);
    }

    private void init(Context context, Camera camera) {
        this.camera = camera;
        getHolder().addCallback(this);
        pictureTakenCallback = new PictureTakenCallback(
                ((AntaraApplication) context.getApplicationContext()).getPicturesQ(), context);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            Camera.Parameters parameters = camera.getParameters();
            List<Camera.Size> rawSupportedSizes = parameters.getSupportedPreviewSizes();
            for (Camera.Size size : rawSupportedSizes) {
                Log.d(LOG_TAG, "Supported sizes :" + size.width + ", " + size.height);
            }
            Camera.Size size = rawSupportedSizes.get(0);
            Log.d(LOG_TAG, "Selected sizes :" + size.width + ", " + size.height);
            parameters.setPreviewSize(size.width, size.height);
            camera.setParameters(parameters);

            parameters.getFocalLength();

            camera.setPreviewDisplay(holder);
            camera.startPreview();

            // startTimer();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(LOG_TAG, "Surface Changed");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(LOG_TAG, "Surface Destroyed");
        camera.stopPreview();
        // stopTimer();
    }

    public void takePicture() {
        camera.takePicture(null, null, pictureTakenCallback);
        camera.stopPreview();
        camera.startPreview();
    }

    private Timer timer = null;
    private TimerTask task = null;

    public void startTimer() {
        timer = new Timer();
        task = new TimerTask() {

            @Override
            public void run() {
                camera.takePicture(null, null, pictureTakenCallback);
            }
        };
        timer.schedule(task, 1000, 1000);
    }

    public void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}
