package in.antara.antara;

import android.graphics.Bitmap;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.List;

public class AntaraCalculation {
    private DistanceUtility utilityObj = new DistanceUtility();
    private AngleUtility angleUtilityObj = new AngleUtility();


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

    private double singleAntaraObjectDistance(Point topLeft, Point bottomLeft, Point bottomRight,
                                              Point topRight, Bitmap bmp) {
        double distance;
        distance = this.getDistance(bmp, topLeft, bottomLeft, bottomRight, topRight);
        return distance;

    }

    private double doubleAntaraObjectDistance(List<Point> squareBbox, Bitmap bmp) {
        double distance1 = this.singleAntaraObjectDistance(squareBbox.get(0), squareBbox.get(1), squareBbox.get(2),
                squareBbox.get(3), bmp);
        double distance2 = this.singleAntaraObjectDistance(squareBbox.get(4), squareBbox.get(5), squareBbox.get(6),
                squareBbox.get(7), bmp);

        return Math.max(distance1,distance2);

    }

    private double doubleAntaraObjectAngle(List<Point> squareBbox, Bitmap bmp) {

        double distance1 = this.singleAntaraObjectDistance(squareBbox.get(0), squareBbox.get(1), squareBbox.get(2),
                squareBbox.get(3), bmp);
        double distance2 = this.singleAntaraObjectDistance(squareBbox.get(4), squareBbox.get(5), squareBbox.get(6),
                squareBbox.get(7), bmp);
        double[] angle = angleUtilityObj.getAngleFor2Squares(distance1,distance2);

        return angle[0] + 180;

    }

    private void drawPoints(Mat matImg, Point point, Scalar scalar, int thickness) {
        Imgproc.circle(matImg, point, 10, scalar, thickness);
    }

    public double positionEstimator(List<Point> squareBbox, Bitmap bmp) {
        Point topLeft, topRight, bottomLeft, bottomRight;
        double distance = 0.0;
        if (squareBbox.size() == 4) {
            topLeft = squareBbox.get(0);
            bottomLeft = squareBbox.get(1);
            bottomRight = squareBbox.get(2);
            topRight = squareBbox.get(3);

            distance = this.singleAntaraObjectDistance(topLeft, bottomLeft, bottomRight, topRight, bmp);

        }

        if (squareBbox.size() == 8){

            //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

            //NOTE : 1 is left square,  2 is right square
            //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

            distance = this.doubleAntaraObjectDistance(squareBbox, bmp);

        }


        return distance;
    }

    public double angleEstimator(List<Point> squareBbox, Bitmap bmp){
        double angle = 0.0;
        if (squareBbox.size() == 8){

            angle = this.doubleAntaraObjectAngle(squareBbox, bmp);

        }
        return angle;
    }

    private double getDistance(Bitmap bmp, Point topLeft, Point bottomLeft, Point bottomRight, Point topRight){
        try{

            int imageHeight = bmp.getWidth(); //matImg.rows(); //Pixel
            double distanceInMM;

            double detectedSquareHeight1 = utilityObj.calculateDistanceBetween2Points(topRight.x,
                    topRight.y, bottomRight.x, bottomRight.y); // Pixel
            double detectedSquareHeight2 = utilityObj.calculateDistanceBetween2Points(topLeft.x,
                    topLeft.y, bottomLeft.x, bottomLeft.y); // Pixel

            if (detectedSquareHeight1 > detectedSquareHeight2) {
                distanceInMM = utilityObj.calculateDistanceInMM(imageHeight, detectedSquareHeight1);
            } else {
                distanceInMM = utilityObj.calculateDistanceInMM(imageHeight, detectedSquareHeight2);
            }

            double distanceInInches = utilityObj.convertDistanceFromMMToInches(distanceInMM);

            return utilityObj.convertDistanceFromInchesToCM(distanceInInches);
        }
        catch (Exception ex){
            ex.printStackTrace();
            return 0.0;
        }
    }

}