package in.antara.antara.objects;

/**
 * Created by udar on 8/25/2017.
 */
public class Rectangle {

    private Point center;
    private int verticalLen;
    private int horizontalLen;
    private int tilt;


    public Rectangle(Point center, int verticalLen, int horizontalLen, int tilt) {
        this.center = center;
        this.verticalLen = verticalLen;
        this.horizontalLen = horizontalLen;
        this.tilt = tilt;
    }

    public Point getCenter() {
        return center;
    }

    public void setCenter(Point center) {
        this.center = center;
    }

    public int getVerticalLen() {
        return verticalLen;
    }

    public void setVerticalLen(int verticalLen) {
        this.verticalLen = verticalLen;
    }

    public int getHorizontalLen() {
        return horizontalLen;
    }

    public void setHorizontalLen(int horizontalLen) {
        this.horizontalLen = horizontalLen;
    }

    public int getTilt() {
        return tilt;
    }

    public void setTilt(int tilt) {
        this.tilt = tilt;
    }

    public int top() {
        return center.getY() - (verticalLen / 2);
    }

    public int bottom() {
        return center.getY() + (verticalLen / 2);
    }

    public int left() {
        return center.getX() - (horizontalLen / 2);
    }

    public int right() {
        return center.getX() + (horizontalLen / 2);
    }

    @Override
    public String toString() {
        return "left: " + left() + ", top: " + top()
                + ", right: " + right() + ", bottom: " + bottom();
    }
}
