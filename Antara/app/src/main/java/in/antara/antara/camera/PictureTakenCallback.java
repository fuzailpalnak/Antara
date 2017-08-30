package in.antara.antara.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.util.Log;
import android.widget.Toast;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Point;

import java.util.List;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

import in.antara.antara.AngleUtility;
import in.antara.antara.DistanceUtility;
import in.antara.antara.SquareDetector;
import in.antara.antara.compass.DirectionListener;
import in.antara.antara.position.Position;

/**
 * Created by udar on 8/29/2017.
 */
public class PictureTakenCallback implements Camera.PictureCallback {
    private static final String LOG_TAG = PictureTakenCallback.class.getSimpleName();

    private LinkedBlockingQueue<Bitmap> picturesQ;
    private LinkedBlockingQueue<Position> positionsQ;

    private Context context;
    private Camera camera;

    private DirectionListener directionListener;

    public PictureTakenCallback(LinkedBlockingQueue<Bitmap> picturesQ,
                                LinkedBlockingQueue<Position> positionsQ,
                                Context context,
                                Camera camera,
                                DirectionListener directionListener) {
        this.picturesQ = picturesQ;
        this.positionsQ = positionsQ;
        this.context = context;
        this.camera = camera;
        this.directionListener = directionListener;
    }

    public static Bitmap RotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        Log.d(LOG_TAG, "On Picture taken");
        // TODO: Remove it
        // positionsQ.add(new Position(randomDist(), randomDegrees()));
        // Log.d(LOG_TAG, "Available positions in Q " + positionsQ.size());
        try {
            Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
            // picturesQ.add(bmp);
            bmp = RotateBitmap(bmp,90.0f);

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
                camera.stopPreview();
                camera.startPreview();
                return;
            }

            Log.d(LOG_TAG, "Square box : " + squareBbox.size());
            topLeft = squareBbox.get(0);
            bottomLeft = squareBbox.get(1);
            bottomRight = squareBbox.get(2);
            topRight = squareBbox.get(3);

            Camera.Parameters parameters = camera.getParameters();
            parameters.getFocalLength();
            Log.d(LOG_TAG, "Focal length : " + parameters.getFocalLength());

            float focalLength = parameters.getFocalLength(); //mm
            int imageHeight = matImg.rows(); //Pixel
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
            float angle = angleObj.getAngleFor1Square(matImg, topLeft, bottomLeft, topRight, bottomRight);
            Log.d(LOG_TAG, "Calculated angle : " + angle);
            Log.d(LOG_TAG, "Calculated direction: " + directionListener.getDirectionDegree());

            positionsQ.add(new Position(angle, distanceInCM.floatValue()));
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        }
        camera.stopPreview();
        camera.startPreview();
    }


    private Random random = new Random();

    private int randomDist() {
        int rnd = random.nextInt();
        return Math.abs(rnd % 500);
    }

    private int randomDegrees() {
        int rnd = random.nextInt();
        return Math.abs(rnd % 360);
    }
}