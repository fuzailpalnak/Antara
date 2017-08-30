package in.antara.antara;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import in.antara.antara.camera.CameraPreview;
import in.antara.antara.compass.DirectionListener;
import in.antara.antara.position.Position;
import in.antara.antara.position.PositionView;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    static final int REQUEST_IMAGE_CAPTURE = 1994;
    LinkedBlockingQueue<Position> positionsQ;
    private Camera camera;
    private CameraPreview cameraPreview;
    private PositionView positionView;
    private FrameLayout frameLayout;
    private Button clickButton;

    Uri mImageUri;

    String mCurrentPhotoPath;

    private DirectionListener directionListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        positionsQ =  ((AntaraApplication) this.getApplicationContext()).getPositionsQ();

        Log.d(LOG_TAG, "Antara");

        if (!OpenCVLoader.initDebug()) {
            Log.d(LOG_TAG, "OpenCV not loaded");
            // Toast.makeText(this, "OpenCV not loaded", Toast.LENGTH_SHORT).show();
        } else {
            Log.d(LOG_TAG, "OpenCV loaded");
            // Toast.makeText(this, "OpenCV loaded", Toast.LENGTH_SHORT).show();
        }

        if (checkCameraHardware(getApplicationContext())) {
            camera = getCameraInstance();
            if (camera == null) {
                Log.e(LOG_TAG, "Failed to create camera instance");
                Toast.makeText(getApplicationContext(), "Failed to create camera instance", Toast.LENGTH_SHORT);
                exit();
            }
            directionListener = new DirectionListener();
            cameraPreview = new CameraPreview(this, camera, directionListener);
//            FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
//            preview.addView(cameraPreview);


            positionView = (PositionView) findViewById(R.id.position_view);
            clickButton = (Button) findViewById(R.id.captureFront);

            updateHeights(clickButton, cameraPreview, positionView);


            registerListener();
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
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Log.d(LOG_TAG, "Camera available on device");
            return true;
        } else {
            Log.d(LOG_TAG, "Camera not available on device");
            return false;
        }
    }

    public Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
            c.setDisplayOrientation(90);
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        }
        return c; // returns null if camera is unavailable
    }

    public void takePicture(View view) {
        Log.d(LOG_TAG, "Take picture clicked");
//        cameraPreview.takePicture();

//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//        }

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
//            Bundle extras = data.getExtras();
//            Bitmap bmp = (Bitmap) extras.get("data");
//            mImageView.setImageBitmap(imageBitmap);

            //Save Image
//            Bitmap bmp = this.grabImage();

            Bitmap bmp = null;
            try{
                File f=new File(mCurrentPhotoPath);
                bmp = BitmapFactory.decodeStream(new FileInputStream(f));
            }
           catch (IOException  ex){

           }

            if(bmp == null){
                return;
            }

            Mat matImg = new Mat();
            Utils.bitmapToMat(bmp, matImg);

            DistanceUtility utilityObj = new DistanceUtility();
            SquareDetector detectorObj = new SquareDetector();

            Mat hsvImg = detectorObj.convertRGBToHSV(matImg);
            Mat inrangeImg = detectorObj.inRangeThreshold(hsvImg);


            Utils.matToBitmap(inrangeImg,bmp);


            Point topLeft, topRight, bottomLeft, bottomRight;

            List<Point> squareBbox = detectorObj.getBiggestSquare(inrangeImg);

            //Validation
            if(squareBbox == null){
                Log.d(LOG_TAG, "Square not detected");
                return;
            }

            Double distanceSqaure1 = 0.0;
            Double distanceSqaure2 = 0.0;
            double[] angle = new double[2];

            if(squareBbox.size()==4){
                topLeft = squareBbox.get(0);
                bottomLeft = squareBbox.get(1);
                bottomRight = squareBbox.get(2);
                topRight = squareBbox.get(3);

                Imgproc.circle(matImg, topLeft,10, new Scalar(255,0,0), -1);
                Utils.matToBitmap(matImg,bmp);
                Imgproc.circle(matImg, bottomLeft,10, new Scalar(0,255,0), -1);
                Utils.matToBitmap(matImg,bmp);
                Imgproc.circle(matImg, bottomRight,10, new Scalar(0,0,255), -1);
                Utils.matToBitmap(matImg,bmp);
                Imgproc.circle(matImg, topRight,10, new Scalar(100,100,100), -1);
                Utils.matToBitmap(matImg,bmp);


                distanceSqaure1 = getDistance(bmp,matImg,topLeft,bottomLeft,bottomRight,topRight);

                Log.d(LOG_TAG, "Calculated Square 1 Distance : " + distanceSqaure1);
            }


            //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

            //NOTE : 1 is left square,  2 is right square
            //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!



            if(squareBbox.size()==8){

                topLeft = squareBbox.get(0);
                bottomLeft = squareBbox.get(1);
                bottomRight = squareBbox.get(2);
                topRight = squareBbox.get(3);

//                Imgproc.circle(matImg, topLeft,10, new Scalar(255,0,0), -1);
//                Utils.matToBitmap(matImg,bmp);
//                Imgproc.circle(matImg, bottomLeft,10, new Scalar(0,255,0), -1);
//                Utils.matToBitmap(matImg,bmp);
//                Imgproc.circle(matImg, bottomRight,10, new Scalar(0,0,255), -1);
//                Utils.matToBitmap(matImg,bmp);
//                Imgproc.circle(matImg, topRight,10, new Scalar(100,100,100), -1);
//                Utils.matToBitmap(matImg,bmp);


                distanceSqaure1 = getDistance(bmp,matImg,topLeft,bottomLeft,bottomRight,topRight);

                Log.d(LOG_TAG, "Calculated Square 1 Distance : " + distanceSqaure1);



                //For 2 square
                topLeft = squareBbox.get(4);
                bottomLeft = squareBbox.get(5);
                bottomRight = squareBbox.get(6);
                topRight = squareBbox.get(7);
                distanceSqaure2 = getDistance(bmp,matImg,topLeft,bottomLeft,bottomRight,topRight);

//                Imgproc.circle(matImg, topLeft,10, new Scalar(255,0,0), -1);
//                Utils.matToBitmap(matImg,bmp);
//                Imgproc.circle(matImg, bottomLeft,10, new Scalar(0,255,0), -1);
//                Utils.matToBitmap(matImg,bmp);
//                Imgproc.circle(matImg, bottomRight,10, new Scalar(0,0,255), -1);
//                Utils.matToBitmap(matImg,bmp);
//                Imgproc.circle(matImg, topRight,10, new Scalar(100,100,100), -1);
//                Utils.matToBitmap(matImg,bmp);

                Log.d(LOG_TAG, "Calculated Square 2 Distance : " + distanceSqaure2);

                AngleUtility angleUtilityObj = new AngleUtility();
                //Get Angle
                angle = angleUtilityObj.getAngleFor2Squares(distanceSqaure1,distanceSqaure2);

                Log.d(LOG_TAG,"Calculated Angle 1 :" + angle[0]);
                Log.d(LOG_TAG,"Calculated Angle 2 :" + angle[1]);
            }


            double finalDistance = Math.max(distanceSqaure1,distanceSqaure2);
            double finalAngle = angle[0] + 180; //Math.max(angle[0],angle[1]) + 180.0;

            if(distanceSqaure2 == 0){
                finalAngle = 270;
            }




            positionsQ.add(new Position((float)finalAngle, (float)finalDistance));



//            Log.d(LOG_TAG, "Square box : " + squareBbox.size());



//            Imgproc.circle(matImg, topLeft,10, new Scalar(255,0,0), -1);
//            Utils.matToBitmap(matImg,bmp);
//            Imgproc.circle(matImg, bottomLeft,10, new Scalar(0,255,0), -1);
//            Utils.matToBitmap(matImg,bmp);
//            Imgproc.circle(matImg, bottomRight,10, new Scalar(0,0,255), -1);
//            Utils.matToBitmap(matImg,bmp);
//            Imgproc.circle(matImg, topRight,10, new Scalar(100,100,100), -1);
//            Utils.matToBitmap(matImg,bmp);



//            positionsQ.add(new Position(angle, distanceInCM.floatValue()));

        }
    }


    public Double getDistance(Bitmap bmp,Mat matImg,Point topLeft,Point bottomLeft,Point bottomRight,Point topRight){
        try{

            DistanceUtility utilityObj = new DistanceUtility();
            Double[] result = new Double[3];
            float focalLength = 3.57f; //mm
            int imageHeight = bmp.getWidth(); //matImg.rows(); //Pixel
            Double detectedSquareHeight1 = 0.0; // Pixel
            Double detectedSquareHeight2 = 0.0; // Pixel
            Double distanceInMM = 0.0;

            detectedSquareHeight1 = utilityObj.calculateDistanceBetween2Points(topRight.x, topRight.y, bottomRight.x, bottomRight.y);
            detectedSquareHeight2 = utilityObj.calculateDistanceBetween2Points(topLeft.x, topLeft.y, bottomLeft.x, bottomLeft.y);

            if (detectedSquareHeight1 > detectedSquareHeight2) {
                distanceInMM = utilityObj.calculateDistanceInMM(focalLength, imageHeight, detectedSquareHeight1);
            } else {
                distanceInMM = utilityObj.calculateDistanceInMM(focalLength, imageHeight, detectedSquareHeight2);
            }

            Double distanceInInches = utilityObj.convertDistanceFromMMToInches(distanceInMM);
            Double distanceInCM = utilityObj.convertDistanceFromInchesToCM(distanceInInches);

            Toast.makeText(this, "Distance : " + distanceInCM, Toast.LENGTH_LONG);
            Log.d(LOG_TAG, "Calculated distance : " + distanceInCM);

//            AngleUtility angleObj = new AngleUtility();
//            float angle = angleObj.getAngleFor1Square(matImg, topLeft, bottomLeft, topRight, bottomRight);
//            Log.d(LOG_TAG, "Calculated angle : " + angle);
//            Log.d(LOG_TAG, "Calculated direction: " + directionListener.getDirectionDegree());

//            result[0] = distanceInCM;
//            result[] = distanceInCM;

            return distanceInCM;
        }
        catch (Exception ex){
            ex.printStackTrace();
            return null;
        }
    }


    private void updateHeights(Button clickButton, CameraPreview cameraPreview,
                               PositionView positionView) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displayMetrics);
        int heightPixels = displayMetrics.heightPixels;
        int widthPixels = displayMetrics.widthPixels;

        clickButton.setHeight((int) (0.1 * heightPixels));
        cameraPreview.getHolder().setFixedSize(widthPixels, (int) (0.45 * heightPixels));
        positionView.getHolder().setFixedSize(widthPixels, (int) (0.45 * heightPixels));
    }

    private void registerListener() {
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        if (sensor != null) {
            sensorManager.registerListener(directionListener,
                    sensor,
                    SensorManager.SENSOR_DELAY_UI);
        } else {
            Log.e(LOG_TAG, "Orientation sensor not available");
            Toast.makeText(this, "Orientation sensor not available", Toast.LENGTH_LONG);
        }
    }

    private void unregisterListener() {
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.unregisterListener(directionListener);
    }
}
