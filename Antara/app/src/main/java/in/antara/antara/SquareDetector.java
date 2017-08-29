package in.antara.antara;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vermap on 8/28/2017.
 */
public class SquareDetector {

    public static final Scalar lOWERRANGE = new Scalar(60,60,60);
    public static final Scalar UPPERRANGE = new Scalar(120,255,255);


    public Mat convertRGBToHSV(Mat rgbImage){
        try{

            Mat hsvImage = new Mat();
            Imgproc.cvtColor(rgbImage,hsvImage,Imgproc.COLOR_RGB2HSV);

            return hsvImage;
        }
        catch(Exception ex){
            ex.printStackTrace();
            return null;
        }
    }

    public Mat inRangeThreshold(Mat img){
        try{
            Mat thresholdedImage =  new Mat();
            Core.inRange(img,lOWERRANGE,UPPERRANGE,thresholdedImage);
            return thresholdedImage;
        }
        catch(Exception ex){
            ex.printStackTrace();
            return null;
        }
    }

    public List<Point> getBiggestSquare(Mat img){
        try{
            List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
            Mat hierarchy = new Mat();

            Imgproc.findContours(img,contours,hierarchy,Imgproc.RETR_TREE,Imgproc.CHAIN_APPROX_SIMPLE);

            //Loop through all the contours and pick the biggest sqaure
            Double largestArea = -1.0;
            MatOfPoint2f largestSquareContour = new MatOfPoint2f();
            for (int i=0; i<contours.size();i++) {

                    Double area = Imgproc.contourArea(contours.get(i));
                    if (area > largestArea){
                        largestArea = area;
                        contours.get(i).convertTo(largestSquareContour, CvType.CV_32FC2); ;
                    }
            }

            Double epsilon;
            epsilon =  Imgproc.arcLength(largestSquareContour,true) * 0.05;

            MatOfPoint2f largestSquareContourApprox = new MatOfPoint2f();

            Imgproc.approxPolyDP(largestSquareContour,largestSquareContourApprox,epsilon,true);

            //Convert back to MatOfPoint
//            MatOfPoint points = new MatOfPoint(largestSquareContourApprox.toArray() );
            // Get bounding rect of contour
//            Rect rect = Imgproc.boundingRect(points);

            List<Point> largestSquareList = largestSquareContourApprox.toList();
//            List<Point> largestSquareList = new ArrayList<>();

//            largestSquareList.add(new Point(rect.x,rect.y));
//            largestSquareList.add(new Point(rect.x,rect.y+rect.height));
//            largestSquareList.add(new Point(rect.x + rect.width,rect.y+rect.height));
//            largestSquareList.add(new Point(rect.x + rect.width,rect.y));

            return largestSquareList;

        }
        catch(Exception ex){
            ex.printStackTrace();
            return null;
        }
    }

}
