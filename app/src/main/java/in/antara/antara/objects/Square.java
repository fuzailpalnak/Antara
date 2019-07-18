package in.antara.antara.objects;

/**
 * Created by udar on 8/25/2017.
 */

public class Square extends Rectangle {

    public Square(Point center, int length, int tilt) {
        super(center, length, length, tilt);
    }

    public void setLength(int length) {
        super.setHorizontalLen(length);
        super.setVerticalLen(length);
    }

    public void setVerticalLen(int length) {
        super.setHorizontalLen(length);
        super.setVerticalLen(length);
    }

    public void setHorizontalLen(int width) {
        super.setHorizontalLen(width);
        super.setVerticalLen(width);
    }
}
