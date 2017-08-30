package in.antara.antara;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.w3c.dom.DOMConfiguration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by vermap on 8/28/2017.
 */
public class SquareDetector {

    public static final Scalar lOWERRANGE = new Scalar(60, 60, 60);
    public static final Scalar UPPERRANGE = new Scalar(120, 255, 255);


    public Mat convertRGBToHSV(Mat rgbImage) {
        try {

            Mat hsvImage = new Mat();
            Imgproc.cvtColor(rgbImage, hsvImage, Imgproc.COLOR_RGB2HSV);

            return hsvImage;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public Mat inRangeThreshold(Mat img) {
        try {
            Mat thresholdedImage = new Mat();
            Core.inRange(img, lOWERRANGE, UPPERRANGE, thresholdedImage);
            return thresholdedImage;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public List<Point> getBiggestSquare(Mat img) {
        try {
            List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
            Mat hierarchy = new Mat();

            Imgproc.findContours(img, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

            //Loop through all the contours and pick the biggest sqaure
            Double largestArea = -1.0;
            MatOfPoint2f largestSquareContour = new MatOfPoint2f();
            for (int i = 0; i < contours.size(); i++) {

                    Double area = Imgproc.contourArea(contours.get(i));
                    if (area > largestArea) {
                        largestArea = area;
                        contours.get(i).convertTo(largestSquareContour, CvType.CV_32FC2);

                    }

            }

            Double epsilon;
            epsilon = Imgproc.arcLength(largestSquareContour, true) * 0.05;

            MatOfPoint2f largestSquareContourApprox = new MatOfPoint2f();

            Imgproc.approxPolyDP(largestSquareContour, largestSquareContourApprox, epsilon, true);

            List<Point> largestSquareList = new ArrayList<Point>(largestSquareContourApprox.toList());

            largestSquareList = getCoordinatesFromBbox(largestSquareList);

            return largestSquareList;

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }


    public List<Point> _getTopLeftAndTopRight(List<Point> allPoints) {
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

    public List<Point> _getBottomLeftAndBottomRight(List<Point> allPoints) {
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

    public List<Point> getCoordinatesFromBbox(List<Point> bbox) {
        try {

            List<Point> finalBbox = new ArrayList<>();

            List<Point> topLeftTopRight = _getTopLeftAndTopRight(bbox);

            bbox.remove(topLeftTopRight.get(0));
            bbox.remove(topLeftTopRight.get(1));

            List<Point> bottomLeftBottomRight = _getBottomLeftAndBottomRight(bbox);

            finalBbox.add(topLeftTopRight.get(0));
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