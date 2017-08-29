package in.antara.antara.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.util.Log;
import android.widget.Toast;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Point;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import in.antara.antara.AngleUtility;
import in.antara.antara.DistanceUtility;
import in.antara.antara.SquareDetector;

/**
 * Created by udar on 8/29/2017.
 */
public class PictureTakenCallback implements Camera.PictureCallback {
    private static final String LOG_TAG = PictureTakenCallback.class.getSimpleName();

    private LinkedBlockingQueue<Bitmap> picturesQ;
    private Context context;

    public PictureTakenCallback(LinkedBlockingQueue<Bitmap> pictures, Context context) {
        this.picturesQ = pictures;
        this.context = context;
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        try {
            Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
            // picturesQ.add(bmp);

            Mat matImg = new Mat();
            Utils.bitmapToMat(bmp, matImg);

            DistanceUtility utilityObj = new DistanceUtility();
            SquareDetector detectorObj = new SquareDetector();

            Mat hsvImg = detectorObj.convertRGBToHSV(matImg);
            Mat inrangeImg = detectorObj.inRangeThreshold(hsvImg);

            Point topLeft, topRight, bottomLeft, bottomRight;

            List<Point> squareBbox = detectorObj.getBiggestSquare(inrangeImg);

            topLeft = squareBbox.get(0);
            bottomLeft = squareBbox.get(1);
            bottomRight = squareBbox.get(2);
            topRight = squareBbox.get(3);

            Camera.Parameters parameters = camera.getParameters();
            parameters.getFocalLength();

            // Distance Code Example
            float focalLength = parameters.getFocalLength(); //mm
            int imageHeight = matImg.cols(); //Pixel
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

            Toast.makeText(context, "Distance : " + distanceInCM, Toast.LENGTH_LONG);
            Log.d(LOG_TAG, "Calculated distance : " + distanceInCM);

            AngleUtility angleObj = new AngleUtility();
            float angle = angleObj.getAngleFor1Square(matImg,topLeft,bottomLeft,topRight,bottomRight);
            Log.d(LOG_TAG, "Calculated Angle : " + angle);

        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        }
    }
}