package com.antara.palnak.antara_camera;



import android.content.Context;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;

/**
 * Created by fuzai_000 on 25-08-2017.
 */

public class Camera_Preview extends SurfaceView implements SurfaceHolder.Callback{;
    private android.hardware.Camera camera;
    private int cameraId ;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;

    private CompassActivity compassActivity;

    public Camera_Preview(Context context, Camera camera, int cameraId, SurfaceView sv,CompassActivity compassActivity ) {
        super(context);

        surfaceView=sv;

        surfaceHolder=surfaceView.getHolder();
        surfaceHolder.addCallback(this);

        surfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        this.camera=camera;
        this.cameraId=cameraId;

        this.compassActivity=compassActivity;
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        camera = Camera.open(cameraId);
        try{
            camera.setPreviewDisplay(surfaceHolder);
            camera.setDisplayOrientation(90);
        }catch (IOException e){
            e.printStackTrace();
        }


    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        if(camera != null) {
            Camera.Parameters parameters = camera.getParameters();
//            parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);

            camera.setParameters(parameters);
            camera.startPreview();
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

        camera.stopPreview();
        camera.release();
        camera = null;


    }



    public float getFocalLength(){
        return camera.getParameters().getFocalLength();
    }


    public void takePicture(){
        camera.takePicture(null, null,
                new Camera_Handler(getContext(),getFocalLength(),camera,compassActivity.getRotatedDegree()));
    }
}
