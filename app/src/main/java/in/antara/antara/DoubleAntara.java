package in.antara.antara;

import android.graphics.Bitmap;

import org.opencv.core.Point;

import java.util.List;


public class  DoubleAntara {
    private DistanceUtility utilityObj = new DistanceUtility();
    private static final Double KNOWNDISTANCEBETWEENMIDPOINTS = 70.71;

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

    public double doubleAntaraObjectDistance(List<Point> squareBbox, Bitmap bmp) {
        double distance1 = this.getDistance(bmp, squareBbox.get(0), squareBbox.get(1), squareBbox.get(2),
                squareBbox.get(3));
        double distance2 = this.getDistance(bmp, squareBbox.get(4), squareBbox.get(5), squareBbox.get(6),
                squareBbox.get(7));

        return Math.max(distance1,distance2);

    }

    public double doubleAntaraObjectAngle(List<Point> squareBbox, Bitmap bmp) {

        double distance1 = this.getDistance(bmp, squareBbox.get(0), squareBbox.get(1), squareBbox.get(2),
                squareBbox.get(3));
        double distance2 = this.getDistance(bmp, squareBbox.get(4), squareBbox.get(5), squareBbox.get(6),
                squareBbox.get(7));
        double[] angle = this.getAngleFor2Squares(distance1,distance2);

        assert angle != null;
        return angle[0] + 180;

    }

    private double _getRightSideAngle(double leftSideDistanceSquare, double rightSideDistanceSquare,
                                      double knownDistanceBetweenMidpointsSquare, double leftSideDistance){
        try{

            double numerator = leftSideDistanceSquare + knownDistanceBetweenMidpointsSquare - rightSideDistanceSquare;
            double denominator = 2 * leftSideDistance * KNOWNDISTANCEBETWEENMIDPOINTS;

            double division = numerator / denominator;

            double angleRadians = Math.acos(division);
            double angleDegree = Math.toDegrees(angleRadians);

            return Math.abs(180 - angleDegree-45);

        }
        catch(Exception ex){
            ex.printStackTrace();
            return 0;
        }
    }

    private double _getLeftSideAngle(double leftSideDistanceSquare, double rightSideDistanceSquare,
                                     double knownDistanceBetweenMidpointsSquare, double rightSideDistance){
        try{
            double numerator = rightSideDistanceSquare + knownDistanceBetweenMidpointsSquare - leftSideDistanceSquare;
            double denominator = 2 * rightSideDistance * KNOWNDISTANCEBETWEENMIDPOINTS;
            double division = numerator / denominator;


            double angleRadians=Math.acos(division);
            double angleDegree=Math.toDegrees(angleRadians);

            //return angleDegree-45;
            return Math.abs(180 - angleDegree-45);
        }
        catch(Exception ex){
            ex.printStackTrace();
            return 0;
        }

    }

    private double[] getAngleFor2Squares(double leftSideDistance, double rightSideDistance){
        try{
            double right_side_distance_square = Math.pow(rightSideDistance,2);
            double left_side_distance_square = Math.pow(leftSideDistance,2);
            double known_distance_between_midpoints_square = Math.pow(KNOWNDISTANCEBETWEENMIDPOINTS,2);
            double right_side_angle = _getRightSideAngle(left_side_distance_square,right_side_distance_square,known_distance_between_midpoints_square,leftSideDistance);
            double left_side_angle = _getLeftSideAngle(left_side_distance_square,right_side_distance_square,known_distance_between_midpoints_square,rightSideDistance);
            double[] angle_list = new double[2];
            angle_list[0] = right_side_angle;
            angle_list[1] = left_side_angle;
            return angle_list;
        }
        catch(Exception ex){
            ex.printStackTrace();
            return null;
        }
    }
}
