package com.antara.palnak.antara_camera;


import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;


public class MainActivity extends AppCompatActivity{
    private android.hardware.Camera camera=null;
    private int cameraId = 0;
    private SurfaceView mSurfaceView;
    private Button Click;
    private SensorManager mSensorManager;
    private  ImageView  Compass;
    TextView tvHeading;
    public CompassActivity  compassActivity;;
    private boolean cameraAccess;
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSurfaceView = (SurfaceView) findViewById(R.id.surfaceview);

        Click = (Button) findViewById(R.id.captureFront);;
        Compass = (ImageView) findViewById(R.id.imageViewCompass);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        tvHeading = (TextView) findViewById(R.id.tvHeading);


        if (!getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Toast.makeText(this, "No camera on this device", Toast.LENGTH_LONG)
                    .show();
        }
        else {
            cameraAccess=getCameraPermission() ;
            if (cameraAccess){
                cameraId=getCamereInfo();
                if (cameraId < 0) {
                    Toast.makeText(this, "No back facing camera found.",
                            Toast.LENGTH_LONG).show();
                } else {

                    final Camera_Preview cameraPreview=new Camera_Preview(getApplicationContext(),camera,cameraId, mSurfaceView,compassActivity);;
                    CompassActivity compassActivity=new CompassActivity(getApplicationContext(),mSensorManager,Compass);



                    Click.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            cameraPreview.takePicture();


                        }
                    });










                }
            }

        }


    }


    public  int getCamereInfo(){
        int cameraId = -1;
        int numberOfCameras = android.hardware.Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            android.hardware.Camera.CameraInfo  info=new android.hardware.Camera.CameraInfo();;
            android.hardware.Camera.getCameraInfo(i, info);
            if (info.facing == android.hardware.Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i;
                break;
            }
        }
        return cameraId;




    }

    @Override
    protected void onPause() {
        if (camera != null) {
            camera.release();
            camera = null;
        }


        super.onPause();

    }
    @Override
    protected void  onResume(){
        super.onResume();

    }
    public  boolean    getCameraPermission(){
        if (Build.VERSION.SDK_INT   >=  Build.VERSION_CODES.M) {
            if(ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED) {
                cameraAccess=true;

            }
            else {
                requestPermissions(new String[]{Manifest.permission.CAMERA},1);
                cameraAccess=true;


            }

    }
        return cameraAccess;

    }



}

