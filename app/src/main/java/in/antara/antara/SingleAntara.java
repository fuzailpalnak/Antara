package in.antara.antara;

import android.graphics.Bitmap;

import org.opencv.core.Point;

import java.util.List;

public class SingleAntara {
    private DistanceUtility utilityObj = new DistanceUtility();
    private static final Double KNOWNDISTANCEBETWEENPOINTS = 250.0; //MM

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

    public double singleAntaraObjectDistance(List<Point> squareBbox, Bitmap bmp) {

        return this.getDistance(bmp, squareBbox.get(0), squareBbox.get(1), squareBbox.get(2),
                squareBbox.get(3));

    }

    private Point getMidPoint(Point start, Point end){
        double newX = (start.x+end.x) / 2;
        double newY = (start.y+end.y) / 2;
        return new Point(newX, newY);
    }

    public double singleAntaraObjectAngle(List<Point> squareBbox, Bitmap bmp) {
        Point topLeft, topRight, bottomLeft, bottomRight;
        topLeft = squareBbox.get(0);
        bottomLeft = squareBbox.get(1);
        bottomRight = squareBbox.get(2);
        topRight = squareBbox.get(3);

        Point topCenterPoint = getMidPoint(topLeft, topRight);
        Point bottomCenterPoint = getMidPoint(bottomLeft, bottomRight);

        double detectedSquareHeight1 = utilityObj.calculateDistanceBetween2Points(topCenterPoint.x,
                topCenterPoint.y, bottomCenterPoint.x, bottomCenterPoint.y);

        double detectedSquareHeight2 = utilityObj.calculateDistanceBetween2Points(topRight.x,
                topRight.y, bottomRight.x, bottomRight.y);

        int imageHeight = bmp.getWidth();

        double midpointDistanceInMM = utilityObj.calculateDistanceInMM(imageHeight, detectedSquareHeight1);
        double rightDistanceInMM = utilityObj.calculateDistanceInMM(imageHeight, detectedSquareHeight2);

        double mid_sq = KNOWNDISTANCEBETWEENPOINTS * KNOWNDISTANCEBETWEENPOINTS;
        double mid_point_dist_sq = midpointDistanceInMM * midpointDistanceInMM;
        double right_dist_sq = rightDistanceInMM * rightDistanceInMM;

        double numerator = (mid_sq + mid_point_dist_sq - right_dist_sq);
        double denominator = (2 * KNOWNDISTANCEBETWEENPOINTS * midpointDistanceInMM);

        double division = numerator / denominator;

        double angleRadians=Math.acos(division);
        return Math.toDegrees(angleRadians);


    }






    }
