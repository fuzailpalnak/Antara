package in.antara.antara;

import android.graphics.Bitmap;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.List;

public class AntaraCalculation {
    private DoubleAntara doubleAntara = new DoubleAntara();
    private SingleAntara singleAntara = new SingleAntara();


    public Mat detectAntaraObject(Bitmap bmp, SquareDetector detectorObj) {

        Mat matImg = new Mat();
        Utils.bitmapToMat(bmp, matImg);
        Mat hsvImg = detectorObj.convertRGBToHSV(matImg);
        Mat inRangeImg = detectorObj.inRangeThreshold(hsvImg);
        Utils.matToBitmap(inRangeImg, bmp);

        return inRangeImg;

    }

    public List<Point> detectExtremerPoints(Mat inRangeImg, SquareDetector detectorObj) {
        return detectorObj.getBiggestSquare(inRangeImg);
    }


    private void drawPoints(Mat matImg, Point point, Scalar scalar, int thickness) {
        Imgproc.circle(matImg, point, 10, scalar, thickness);
    }

    public double positionEstimator(List<Point> squareBbox, Bitmap bmp) {
        double distance = 0.0;
        if (squareBbox.size() == 4) {

            distance = singleAntara.singleAntaraObjectDistance(squareBbox, bmp);

        }

        if (squareBbox.size() == 8){

            //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

            //NOTE : 1 is left square,  2 is right square
            //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

            distance = doubleAntara.doubleAntaraObjectDistance(squareBbox, bmp);

        }


        return distance;
    }

    public double angleEstimator(List<Point> squareBbox, Bitmap bmp){
        double angle = 0.0;
        if (squareBbox.size() == 4) {

            angle = singleAntara.singleAntaraObjectAngle(squareBbox, bmp);

        }

        if (squareBbox.size() == 8){

            angle = doubleAntara.doubleAntaraObjectAngle(squareBbox, bmp);

        }
        return angle;
    }


}