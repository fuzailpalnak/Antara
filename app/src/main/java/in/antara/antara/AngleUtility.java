package in.antara.antara;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by udar on 8/29/2017.
 */

public class AngleUtility {

    public static final Double KNOWNDISTANCEBETWEENMIDPOINTS = 70.71;


    // Code for Perspective Angle Module Begins
    public Point[] _correction(Point topLeft, Point bottomLeft, Point topRight, Point bottomRight){
        try{
            Point topLeftNew,bottomRightNew;

            topLeftNew = new Point(bottomLeft.x,topLeft.y);
            bottomRightNew = new Point(topRight.x,bottomRight.y) ;

            Point[] newBBox = new Point[4];
            newBBox[0] = topRight;
            newBBox[1] = topLeftNew;
            newBBox[3] = bottomRightNew;
            newBBox[2] = bottomLeft;

            return newBBox;


        }
        catch(Exception ex){
            ex.printStackTrace();
            return null;
        }
    }

    public Double _calculateWarpPerspective(Point[] bbox){
        try{

            Point topLeft, bottomLeft, topRight, bottomRight;

            topLeft = bbox[1];
            topRight = bbox[0];
            bottomRight = bbox[3];
            bottomLeft = bbox[2];


            // height (in pixel) of largest vertical line
            Double largestVerticleLine = 0.0;
            largestVerticleLine = Math.abs(bottomRight.y - topRight.y);

            List<Point> src_pnt = new ArrayList<Point>();
            src_pnt.add(topRight);
            src_pnt.add(topLeft);
            src_pnt.add(bottomLeft);
            src_pnt.add(bottomRight);

            Mat startM = Converters.vector_Point2f_to_Mat(src_pnt);

            Point newTopLeft = new Point(topRight.x - largestVerticleLine, topRight.y);
            Point newBottomLeft = new Point(bottomRight.x - largestVerticleLine, bottomRight.y);

            src_pnt = new ArrayList<Point>();
            src_pnt.add(topRight);
            src_pnt.add(newTopLeft);
            src_pnt.add(newBottomLeft);
            src_pnt.add(bottomRight);

            Mat endM = Converters.vector_Point2f_to_Mat(src_pnt);

            //Mat m = new Mat(3, 3, CvType.CV_32F);
            //Core.perspectiveTransform(pts1,pts2,m);

            Mat perspectiveTransform = Imgproc.getPerspectiveTransform(startM, endM);


            Double deg1 = Math.toDegrees(Math.atan(perspectiveTransform.get(0,0)[0]));
            Double deg2 = Math.toDegrees(Math.atan(perspectiveTransform.get(1,0)[0]));

            Double val = Math.abs(deg1) + Math.abs(deg2);

            return val;
        }
        catch (Exception ex){
            ex.printStackTrace();
            return 0.0;
        }
    }

    public float _getAngle(Double val){
        try{
            if( 141 <= val && val <= 160)
                return 10;

            if( 171 <= val && val <= 180)
                return 20;

            if( 161 <= val && val <= 170)
                return 30;

            if( 131 <= val && val <= 140)
                return 40;

            if( 111 <= val && val <= 130)
                return 45;

            if( 101 <= val && val <= 110)
                return 50;

            if( 75 <= val && val <= 90)
                return 60;

            if( 61 <= val && val <= 75)
                return 70;

            if( 51 <= val && val <= 60)
                return 80;

            if( 41 <= val && val <= 50)
                return 90;


            return -1;

        }

        catch (Exception ex){
            ex.printStackTrace();
            return -1;
        }
    }
    public float getAngleFor1Square(Mat img, Point topLeft, Point bottomLeft, Point topRight, Point bottomRight){
        try{
            Boolean fromLeft = true;

            //left and right vertical line len
            Double lvLineLen = Math.abs(topLeft.y - bottomLeft.y);
            Double rvLineLen = Math.abs(topRight.y - bottomRight.y);

            if(lvLineLen == rvLineLen){
                return 90.0f;
            }

            if (rvLineLen > lvLineLen){
                fromLeft = false;
            }

            //TODO
            if(fromLeft){
                Core.flip(img,img,1);

                SquareDetector decObject = new SquareDetector();

                Mat hsvImg = decObject.convertRGBToHSV(img);
                Mat inrangeImg = decObject.inRangeThreshold(hsvImg);

                List<Point> squareBbox = decObject.getBiggestSquare(inrangeImg);
                //TODO find topLeft,bottomLeft,etc

            }

            Point[] correctedBbox = _correction(topLeft,bottomLeft,topRight,bottomRight);

            Double val = _calculateWarpPerspective(correctedBbox);

            float angle = _getAngle(val);

            return angle;
        }

        catch(Exception ex){
            ex.printStackTrace();
            return -1;
        }
    }



    public double _getRightSideAngle(double leftSideDistanceSquare,double rightSideDistanceSquare,double knownDistanceBetweenMidpointsSquare,double leftSideDistance){
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

    public double _getLeftSideAngle(double leftSideDistanceSquare,double rightSideDistanceSquare,double knownDistanceBetweenMidpointsSquare,double rightSideDistance){
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
