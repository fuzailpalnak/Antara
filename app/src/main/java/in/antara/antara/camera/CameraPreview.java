package in.antara.antara.camera;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.List;



@SuppressLint("ViewConstructor")
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private static final String LOG_TAG = CameraPreview.class.getSimpleName();

    private Camera camera;


    public CameraPreview(Context context, Camera camera) {
        super(context);
        init(camera);
    }

    private void init(Camera camera) {
        this.camera = camera;
        getHolder().addCallback(this);
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

            for (Camera.Size size : parameters.getSupportedPictureSizes()) {

                if (size.width==2340 & size.height ==4160) {
                    parameters.setPreviewSize(size.width, size.height);
                    parameters.setPictureSize(size.width, size.height);
                    break;
                }
            }

            camera.setParameters(parameters);

            parameters.getFocalLength();

            camera.setPreviewDisplay(holder);
            camera.startPreview();


        }
        catch (IOException e) {
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
    }


}