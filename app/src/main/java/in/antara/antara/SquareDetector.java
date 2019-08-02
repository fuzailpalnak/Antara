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
import java.util.Collections;
import java.util.List;

public class SquareDetector {

    private static final Scalar lOWERRANGE = new Scalar(60,60,60);
    private static final Scalar UPPERRANGE = new Scalar(120,255,255);

    private int largestContour = -1;

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

    private List<Point> getBiggestContour(List<MatOfPoint> contours){
        try{
            Double largestArea = -1.0;
            MatOfPoint2f largestSquareContour = new MatOfPoint2f();

            for (int i=0; i<contours.size();i++) {
                Double area = Imgproc.contourArea(contours.get(i));

                if (area > largestArea) {
                    largestArea = area;
                    contours.get(i).convertTo(largestSquareContour, CvType.CV_32FC2);
                    largestContour = i;
                }
            }

            Double epsilon;
            epsilon =  Imgproc.arcLength(largestSquareContour,true) * 0.05;

            MatOfPoint2f largestSquareContourApprox = new MatOfPoint2f();

            Imgproc.approxPolyDP(largestSquareContour,largestSquareContourApprox,epsilon,true);

            List<Point> largestSquareList = new ArrayList<>(largestSquareContourApprox.toList());

            largestSquareList =  getCoordinatesFromBbox(largestSquareList);

            return largestSquareList;
        }
        catch (Exception ex){
            return null;
        }
    }


    public List<Point> getBiggestSquare(Mat img){
        try{
            List<MatOfPoint> contours = new ArrayList<>();
            Mat hierarchy = new Mat();

            Imgproc.findContours(img,contours,hierarchy,Imgproc.RETR_TREE,Imgproc.CHAIN_APPROX_SIMPLE);

            //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            //FOR FIRST SQAURE LARGEST
            largestContour = -1;
            List<Point> largestSquareList = new ArrayList<>();
            largestSquareList.addAll(getBiggestContour(contours));
            contours.remove(largestContour);
            //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

            //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            //FOR FINDING 2 SQUARES

            //Remove largest contour

            if(contours.size() != 0){
                try{
                    largestContour = -1;
//                    largestSquareList.addAll(getBiggestContour(contours));

                    List<Point> secondLargestSquareList;
                    secondLargestSquareList = getBiggestContour(contours);
                    contours.remove(largestContour);
                    largestContour = -1;

                    //validation
                    Point topLeftBiggestContour,topLeftSecondBiggestContour;
                    topLeftBiggestContour = largestSquareList.get(0);
                    topLeftSecondBiggestContour = secondLargestSquareList.get(0);

                    DistanceUtility disObj = new DistanceUtility();
                    Double dist = disObj.calculateDistanceBetween2Points(topLeftBiggestContour.x,topLeftBiggestContour.y,topLeftSecondBiggestContour.x,topLeftSecondBiggestContour.y);

                    if(dist < 45){
                        List<Point> thirdLargestSquareList;
                        thirdLargestSquareList = getBiggestContour(contours);
                        contours.remove(largestContour);
                        largestContour = -1;
                        assert thirdLargestSquareList != null;
                        largestSquareList.addAll(thirdLargestSquareList);
                    }
                   else{
                        largestSquareList.addAll(secondLargestSquareList);
                    }


                }
                catch (Exception ex){
                    return largestSquareList;
                }

            }

            //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!


            Point leftTop1 = largestSquareList.get(0);
            Point leftTop2 = largestSquareList.get(4);


            if (leftTop1.x > leftTop2.x){

                List<Point> finalLargestSquareList;
                finalLargestSquareList = new ArrayList<>();

                Point tl = largestSquareList.get(4);
                Point bl = largestSquareList.get(5);
                Point br = largestSquareList.get(6);
                Point tr = largestSquareList.get(7);

                finalLargestSquareList.add(0,tl);
                finalLargestSquareList.add(1,bl);
                finalLargestSquareList.add(2,br);
                finalLargestSquareList.add(3,tr);
                finalLargestSquareList.add(largestSquareList.get(0));
                finalLargestSquareList.add(largestSquareList.get(1));
                finalLargestSquareList.add(largestSquareList.get(2));
                finalLargestSquareList.add(largestSquareList.get(3));

                return finalLargestSquareList;


            }


            return largestSquareList;



        }
        catch(Exception ex){
            ex.printStackTrace();
            return null;
        }
    }



    private List<Point> _getTopLeftAndTopRight(List<Point> allPoints) {
        try {

            List<Double> xCoordinates = new ArrayList<>();
            List<Double> yCoordinates = new ArrayList<>();

            for (Point pnt : allPoints) {
                xCoordinates.add(pnt.x);
                yCoordinates.add(pnt.y);
            }

            List<Double> yCoordinatesTemp = new ArrayList<>(yCoordinates);
            Collections.sort(yCoordinatesTemp);


            List<Integer> resultTemp = new ArrayList<>();

            for (int i = 0; i < yCoordinatesTemp.size(); i++) {
                for (int j = 0; j < yCoordinates.size(); j++) {
                    if (yCoordinatesTemp.get(i) == yCoordinates.get(j)) {
                        resultTemp.add(j);
                    }
                }

            }

            List<Double> xCoordinateSelectedPoint = new ArrayList<>();
            List<Double> yCoordinateSelectedPoint = new ArrayList<>();


            for (int i = 0; i < 2; i++) {
                int index = resultTemp.get(i);
                xCoordinateSelectedPoint.add(xCoordinates.get(index));
                yCoordinateSelectedPoint.add(yCoordinates.get(index));

            }

            List<Point> result = new ArrayList<>();

            if (xCoordinateSelectedPoint.get(0) < xCoordinateSelectedPoint.get(1)) {
                result.add(new Point(xCoordinateSelectedPoint.get(0), yCoordinateSelectedPoint.get(0)));
                result.add(new Point(xCoordinateSelectedPoint.get(1), yCoordinateSelectedPoint.get(1)));
            } else {
                result.add(new Point(xCoordinateSelectedPoint.get(1), yCoordinateSelectedPoint.get(1)));
                result.add(new Point(xCoordinateSelectedPoint.get(0), yCoordinateSelectedPoint.get(0)));
            }

            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private List<Point> _getBottomLeftAndBottomRight(List<Point> allPoints) {
        try {
            List<Point> result = new ArrayList<>();

            if (allPoints.get(0).x < allPoints.get(1).x) {
                result.add(allPoints.get(0));
                result.add(allPoints.get(1));
            } else {
                result.add(allPoints.get(1));
                result.add(allPoints.get(0));
            }
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private List<Point> getCoordinatesFromBbox(List<Point> bbox) {
        try {

            List<Point> finalBbox = new ArrayList<>();

            List<Point> topLeftTopRight = _getTopLeftAndTopRight(bbox);

            assert topLeftTopRight != null;
            bbox.remove(topLeftTopRight.get(0));
            bbox.remove(topLeftTopRight.get(1));

            List<Point> bottomLeftBottomRight = _getBottomLeftAndBottomRight(bbox);

            finalBbox.add(topLeftTopRight.get(0));

            assert bottomLeftBottomRight != null;
            finalBbox.add(bottomLeftBottomRight.get(0));
            finalBbox.add(bottomLeftBottomRight.get(1));
            finalBbox.add(topLeftTopRight.get(1));

            return finalBbox;

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }


}
