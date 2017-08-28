package com.antara.palnak.antara_camera;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Created by fuzai_000 on 26-08-2017.
 */

public class CompassActivity implements SensorEventListener {
    private SensorManager mSensorManager;
    private  ImageView  Compass;
    private float currentDegree = 0f;
    private float rotatedDegree;
    private final Context context;


    public CompassActivity(Context context, SensorManager mSensorManager, ImageView  Compass) {
        this.mSensorManager=mSensorManager;
        this.Compass=Compass;
        this.context=context;
        this.rotatedDegree=0f;


        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),SensorManager.SENSOR_DELAY_GAME);;



    }

    @Override
    public void onSensorChanged(SensorEvent event) {


        // get the angle around the z-axis rotated
        float degree = Math.round(event.values[0]);
        setRotatedDegree(degree);

//        tvHeading.setText("Heading: " + Float.toString(degree) + " degrees");

        RotateAnimation ra = new RotateAnimation(
                currentDegree,
                -degree,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);

        ra.setDuration(210);

        ra.setFillAfter(true);

        Compass.startAnimation(ra);
        currentDegree = -degree;

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }




    public float getRotatedDegree() {
        Log.d("get",String.valueOf(this.rotatedDegree));

        return this.rotatedDegree;
    }

    public void setRotatedDegree(float rotatedDegree) {;
        Log.d("set",String.valueOf(this.rotatedDegree));

        this.rotatedDegree = rotatedDegree;
    }
//    public void onResume(){
//        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),SensorManager.SENSOR_DELAY_GAME);
//
//    }


}
