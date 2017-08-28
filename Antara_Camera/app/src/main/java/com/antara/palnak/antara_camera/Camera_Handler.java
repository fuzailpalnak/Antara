package com.antara.palnak.antara_camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;

import android.widget.Toast;



/**
 * Created by fuzai_000 on 25-08-2017.
 */

public class Camera_Handler implements Camera.PictureCallback {
    private float focalLength;
    private Camera camera;

    private float rotatedDegree;
    private final Context context;

    public Camera_Handler(Context context,float focalLength,Camera cam,float    rotatedDegree){
        this.context=context;
        this.focalLength=focalLength;
        this.camera=cam;
        this.rotatedDegree=rotatedDegree;
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {

        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);


        Toast.makeText(context, String.valueOf(focalLength),
                Toast.LENGTH_SHORT).show();
        Toast.makeText(context, "IN PICTURE TAKEN ",
                Toast.LENGTH_SHORT).show();
        Toast.makeText(context, String.valueOf(rotatedDegree),
                Toast.LENGTH_SHORT).show();


    }


}
