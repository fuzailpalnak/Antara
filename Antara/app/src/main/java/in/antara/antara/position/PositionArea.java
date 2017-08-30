package in.antara.antara.position;

import android.util.Log;

import in.antara.antara.objects.Circle;
import in.antara.antara.objects.Line;
import in.antara.antara.objects.Point;
import in.antara.antara.objects.Rectangle;
import in.antara.antara.objects.SlantedText;

/**
 * Created by udar on 8/25/2017.
 */
public class PositionArea {
    private static final String LOG_TAG = PositionArea.class.getSimpleName();

    // in cm
    private static final int ROOM_WIDTH = 500;
    private static final int ROOM_LEN = 600;
    private static final int DEFAULT_POSITION_DIST = 400;

    // in degrees
    private static final int TILT = 0;
    private static final int DEFAULT_POSITION_ANGLE = 240;

    // Static objects
    private Rectangle room;

    // Changing objects
    private Line distanceLine;
    private Circle position;
    private SlantedText distText;

    // Basic parameters
    private Point center;
    private float perPixelLen;


    public PositionArea(int widthPixels, int heightPixels) {
        int allotedWidth = widthPixels;
        int allotedHeight = heightPixels / 2;

        center = calcCenter(allotedWidth, allotedHeight);
        Log.d(LOG_TAG, "Center : " + center);
        perPixelLen = calcPerPixelLen(allotedHeight);

        setRoom(center, perPixelLen);

        // set changing objects
        setPosition(DEFAULT_POSITION_ANGLE, DEFAULT_POSITION_DIST);
    }

    public Rectangle getRoom() {
        return room;
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

    private void setRoom(Point center, float perPixelLen) {
        int verticalLen = cmToPixel(ROOM_LEN, perPixelLen);
        int horizontalLen = cmToPixel(ROOM_WIDTH, perPixelLen);
        Log.d(LOG_TAG, "Horizontal len: " + horizontalLen + ", Vertical len: " + verticalLen);
        room = new Rectangle(center, horizontalLen, verticalLen, TILT);
    }

    public void setPosition(int angle, int dist) {
        Point tr = new Point(room.right(), room.top());

        int distInPixel = cmToPixel(dist, perPixelLen);
        int positionX = (int) (Math.cos(Math.toRadians(angle)) * distInPixel);
        int positionY = (int) (Math.sin(Math.toRadians(angle)) * distInPixel);
        Point positionPt = new Point(tr.getX() + positionX, tr.getY() - positionY);

        distanceLine = new Line(tr, positionPt);
        position = new Circle(positionPt, 10);

        String text = angle + " " + (char) 0x00B0 + ", " + dist + " cm";
        distText = new SlantedText(text, angle, distanceLine.center());
    }

    private Point calcCenter(int widthPixels, int heightPixels) {
        return new Point(widthPixels / 2, heightPixels / 2 - 50);
    }

    private float calcPerPixelLen(int height) {
        return ROOM_LEN / (float) (height - 50);
    }

    private int cmToPixel(int cm, float perPixelLen) {
        return (int) (cm / perPixelLen);
    }
}
