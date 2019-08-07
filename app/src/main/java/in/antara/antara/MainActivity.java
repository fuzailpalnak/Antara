package in.antara.antara;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Point;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import in.antara.antara.camera.CameraPreview;
import in.antara.antara.position.Position;
import in.antara.antara.position.PositionView;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    static final int REQUEST_IMAGE_CAPTURE = 1994;
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    LinkedBlockingQueue<Position> positionsQ;
    private Camera camera;

    String mCurrentPhotoPath;

    private SquareDetector detectorObj = new SquareDetector();
    private AntaraCalculation antaraCalculation = new AntaraCalculation();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        positionsQ =  ((AntaraApplication) this.getApplicationContext()).getPositionsQ();

        Log.d(LOG_TAG, "Antara");

        if (!OpenCVLoader.initDebug()) {
            Log.d(LOG_TAG, "OpenCV not loaded");
        } else {
            Log.d(LOG_TAG, "OpenCV loaded");
        }

        ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);


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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_REQUEST_CODE) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                if (checkCameraHardware(getApplicationContext())) {

                    PositionView positionView = findViewById(R.id.position_view);
                    Button clickButton = findViewById(R.id.captureFront);
                    updateHeights(clickButton, positionView);

                } else {
                    Log.e(LOG_TAG, "Failed to access camera");
                    Toast.makeText(getApplicationContext(), "Failed to access camera",
                            Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();

            }

        }
    }
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Log.d(LOG_TAG, "Camera available on device");
            return true;
        } else {
            Log.d(LOG_TAG, "Camera not available on device");
            return false;
        }
    }


    public void takePicture(View view) {
        Log.d(LOG_TAG, "Take picture clicked");
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "in.antara.antara.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }




    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        Log.d(LOG_TAG, "Image path: "+ mCurrentPhotoPath);
        return image;
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap bmp = null;
            Mat matImg = new Mat();

            try{
                File f=new File(mCurrentPhotoPath);
                bmp = BitmapFactory.decodeStream(new FileInputStream(f));

            }
           catch (IOException  ex){
               Log.d(LOG_TAG, "IOException");

           }

            if (bmp != null) {
                Utils.bitmapToMat(bmp, matImg);

            } else {
                return;
            }


            Mat inRangeImg = antaraCalculation.detectAntaraObject(bmp, detectorObj);
            List<Point> squareBbox = antaraCalculation.detectExtremerPoints(inRangeImg, detectorObj);

            //Validation
            if(squareBbox == null){
                Log.d(LOG_TAG, "Square not detected");
                Toast.makeText(this, "Antara Object Not Found" , Toast.LENGTH_LONG).show();

                return;
            }

//            List<Point> squareBbox = detectorObj.getBiggestSquare(inRangeImg);
            double distance = antaraCalculation.positionEstimator(squareBbox, bmp);
            Log.d(LOG_TAG, "Calculated Distance : " + distance);
            Toast.makeText(this, "Distance : " + distance, Toast.LENGTH_LONG).show();

            double angle = antaraCalculation.angleEstimator(squareBbox, bmp);
            Log.d(LOG_TAG, "Calculated Angle: " + angle);
            Toast.makeText(this, "Angle : " + angle, Toast.LENGTH_LONG).show();

            positionsQ.add(new Position((float)angle, (float)distance));



        }
    }


    private void updateHeights(Button clickButton, PositionView positionView) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displayMetrics);
        int heightPixels = displayMetrics.heightPixels;
        int widthPixels = displayMetrics.widthPixels;

        clickButton.setHeight((int) (0.1 * heightPixels));
//        cameraPreview.getHolder().setFixedSize(widthPixels, (int) (0.45 * heightPixels));
        positionView.getHolder().setFixedSize(widthPixels, (int) (0.45 * heightPixels));
    }



}
