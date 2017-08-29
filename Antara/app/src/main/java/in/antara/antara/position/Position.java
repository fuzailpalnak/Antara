package in.antara.antara.position;

/**
 * Created by udar on 8/29/2017.
 */
public class Position {
    private int angle;
    private int length;

    public Position(int angle, int length) {
        this.angle = angle;
        this.length = length;
    }

    public Position(float angle, float length) {
        this.angle = (int) angle;
        this.length = (int) length;
    }

    public int getAngle() {
        return angle;
    }

    public int getLength() {
        return length;
    }

    @Override
    public String toString() {
        return "angle: " + angle + ", distance: " + length;
    }
}
