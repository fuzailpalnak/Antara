package in.antara.antara;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.List;

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

       //Test Code for Distance Calculation
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.test);
        Mat testMat = new Mat();
        Utils.bitmapToMat(bm,testMat);

        SquareDetector detObj = new SquareDetector();
        AngleUtility obj = new AngleUtility();
        DistanceUtility distObj = new DistanceUtility();

        Mat hsvImg = detObj.convertRGBToHSV(testMat);
        Mat inrangeImg = detObj.inRangeThreshold(hsvImg);

        Bitmap inrangeImgBmp = Bitmap.createBitmap(testMat.cols(), testMat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(inrangeImg,inrangeImgBmp);

        Point topLeft,topRight,bottomLeft,bottomRight;

        List<Point> squareBbox = detObj.getBiggestSquare(inrangeImg);
// Get topleft, topright,etc from bbox
//        obj.getPointsFromBBox(squareBbox);


        topLeft = squareBbox.get(0);
        bottomLeft = squareBbox.get(1);
        bottomRight = squareBbox.get(2);
        topRight = squareBbox.get(3);

        Imgproc.circle(testMat, topLeft,10, new Scalar(255,0,0), -1);
        Utils.matToBitmap(testMat,inrangeImgBmp);
        Imgproc.circle(testMat, bottomLeft,10, new Scalar(0,255,0), -1);
        Utils.matToBitmap(testMat,inrangeImgBmp);
        Imgproc.circle(testMat, bottomRight,10, new Scalar(0,0,255), -1);
        Utils.matToBitmap(testMat,inrangeImgBmp);
        Imgproc.circle(testMat, topRight,10, new Scalar(100,100,100), -1);
        Utils.matToBitmap(testMat,inrangeImgBmp);
        //Utils.matToBitmap(testMat,inrangeImgBmp);


        // Distance Code Example
        Double focalLength = 3.57; //mm
        Double imageHeight = 4160.0; //Pixel
        Double detectedSquareHeight1 = 0.0; // Pixel
        Double detectedSquareHeight2 = 0.0; // Pixel
        Double distanceInMM = 0.0;

        detectedSquareHeight1 = distObj.calculateDistanceBetween2Points(topRight.x,topRight.y,bottomRight.x,bottomRight.y);
        detectedSquareHeight2 = distObj.calculateDistanceBetween2Points(topLeft.x,topLeft.y,bottomLeft.x,bottomLeft.y);

        if(detectedSquareHeight1>detectedSquareHeight2){
            distanceInMM = distObj.calculateDistanceInMM(focalLength,imageHeight,detectedSquareHeight1);
        }
        else{
            distanceInMM = distObj.calculateDistanceInMM(focalLength,imageHeight,detectedSquareHeight2);
        }

        Double distanceInInches = distObj.convertDistanceFromMMToInches(distanceInMM);
        Double distanceInCM = distObj.convertDistanceFromInchesToCM(distanceInInches);


        Toast.makeText(getApplicationContext(), String.valueOf(distanceInMM) + " mm",Toast.LENGTH_LONG);
        Toast.makeText(getApplicationContext(), String.valueOf(distanceInInches) + " inches",Toast.LENGTH_LONG);
        Toast.makeText(getApplicationContext(), String.valueOf(distanceInCM) + " cm",Toast.LENGTH_LONG);


        //Angle Code starts for 1 square
        float angle = obj.getAngleFor1Square(hsvImg,topLeft,bottomLeft,topRight,bottomRight);



    }
}
