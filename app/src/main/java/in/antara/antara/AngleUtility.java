package in.antara.antara;

/**
 * Created by udar on 8/29/2017.
 */

public class AngleUtility {

    private static final Double KNOWNDISTANCEBETWEENMIDPOINTS = 70.71;

    private double _getRightSideAngle(double leftSideDistanceSquare, double rightSideDistanceSquare, double knownDistanceBetweenMidpointsSquare, double leftSideDistance){
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

    private double _getLeftSideAngle(double leftSideDistanceSquare, double rightSideDistanceSquare, double knownDistanceBetweenMidpointsSquare, double rightSideDistance){
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

    public double[] getAngleFor2Squares(double leftSideDistance, double rightSideDistance){
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
