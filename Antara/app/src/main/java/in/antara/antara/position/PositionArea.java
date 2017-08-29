package in.antara.antara.position;

import android.util.Log;

import in.antara.antara.objects.Circle;
import in.antara.antara.objects.Line;
import in.antara.antara.objects.Point;
import in.antara.antara.objects.SlantedText;
import in.antara.antara.objects.Square;

/**
 * Created by udar on 8/25/2017.
 */
public class PositionArea {
    private static final String LOG_TAG = PositionArea.class.getSimpleName();

    // in cm
    private static final int BOUNDING_DIST = 500;
    private static final int PILLAR_LEN = 100;
    private static final int DEFAULT_POSITION_DIST = 400;

    // in degrees
    private static final int PILLAR_TILT = 20;
    private static final int DEFAULT_POSITION_ANGLE = 240;

    // Static objects
    private Square pillar;
    private Line diagonalOne;
    private Line diagonalTwo;
    private Circle boundingArea;

    // Changing objects
    private Line distanceLine;
    private Circle position;
    private SlantedText distText;

    // Basic parameters
    private Point center;
    private int boundingAreaRadius;
    private float perPixelLen;


    public PositionArea(int widthPixels, int heightPixels) {
        int allotedWidth = widthPixels;
        int allotedHeight = heightPixels / 2;

        center = calcCenter(allotedWidth, allotedHeight);
        boundingAreaRadius = calcBoundingAreaRadius(allotedWidth, allotedHeight);
        perPixelLen = calcPerPixelLen(boundingAreaRadius);

        setPillar(center, perPixelLen);
        setDiagonals(center, boundingAreaRadius);
        setBoundingArea(center, boundingAreaRadius);

        // set changing objects
        setPosition(DEFAULT_POSITION_ANGLE, DEFAULT_POSITION_DIST);
    }

    public Square getPillar() {
        return pillar;
    }

    public Line getDiagonalOne() {
        return diagonalOne;
    }

    public Line getDiagonalTwo() {
        return diagonalTwo;
    }

    public Circle getBoundingArea() {
        return boundingArea;
    }

    public Line getDistanceLine() {
        return distanceLine;
    }

    public Circle getPosition() {
        return position;
    }

    public SlantedText getDistText() {
        return distText;
    }

    private Point calcCenter(int widthPixels, int heightPixels) {
        return new Point(widthPixels / 2, heightPixels / 2 - 25);
    }

    // Offset of 5 pixels
    private int calcBoundingAreaRadius(int widthPixels, int heightPixels) {
        boundingAreaRadius = (widthPixels / 2) - 5;
        if (heightPixels < widthPixels) {
            boundingAreaRadius = (heightPixels / 2) - 50;
        }
        Log.d(LOG_TAG, "Bounding circle radius: " + boundingAreaRadius);
        return boundingAreaRadius;
    }

    private float calcPerPixelLen(int boundingAreaRadius) {
        return BOUNDING_DIST / (float) boundingAreaRadius;
    }

    private void setPillar(Point center, float perPixelLen) {
        pillar = new Square(center, cmToPixel(PILLAR_LEN, perPixelLen), PILLAR_TILT);
    }

    private void setDiagonals(Point center, int boundingAreaRadius) {
        double diagAngle = diagonalAngle(pillar);
        int verticalOffset = (int) (Math.sin(diagAngle) * boundingAreaRadius);
        int horizontalOffset = (int) (Math.cos(diagAngle) * boundingAreaRadius);

        Point start = new Point(center.getX() + horizontalOffset, center.getY() - verticalOffset);
        Point end = new Point(center.getX() - horizontalOffset, center.getY() + verticalOffset);
        diagonalOne = new Line(start, end);

        start = new Point(center.getX() - horizontalOffset, center.getY() - verticalOffset);
        end = new Point(center.getX() + horizontalOffset, center.getY() + verticalOffset);
        diagonalTwo = new Line(start, end);
    }

    private void setBoundingArea(Point center, int boundingAreaRadius) {
        boundingArea = new Circle(center, boundingAreaRadius);
    }

    public void setPosition(int angle, int dist) {
        int distInPixel = cmToPixel(dist, perPixelLen);
        int positionX = (int) (Math.cos(Math.toRadians(angle)) * distInPixel);
        int positionY = (int) (Math.sin(Math.toRadians(angle)) * distInPixel);
        Point positionPt = new Point(center.getX() + positionX, center.getY() - positionY);

        distanceLine = new Line(center, positionPt);
        position = new Circle(positionPt, 10);

        String text = angle + " " + (char) 0x00B0 + ", " + dist + " cm";
        distText = new SlantedText(text, angle, distanceLine.center());

//        Log.d(LOG_TAG, "cm per pixel: " + perPixelLen);
//        Log.d(LOG_TAG, "distance in pixel: " + distInPixel);
//        Log.d(LOG_TAG, "center: " + center);
//        Log.d(LOG_TAG, "distance line: " + distanceLine);
//        Log.d(LOG_TAG, "position: " + position);
    }

    private int cmToPixel(int cm, float perPixelLen) {
        return (int) (cm / perPixelLen);
    }

    private double diagonalAngle(Square pillar) {
        return Math.atan(pillar.getVerticalLen() / pillar.getHorizontalLen());
    }
}
