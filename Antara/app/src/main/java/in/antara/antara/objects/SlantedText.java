package in.antara.antara.objects;

/**
 * Created by udar on 8/26/2017.
 */
public class SlantedText {

    private String text;
    private int angle;
    private Point startPt;

    public SlantedText(String text, int angle, Point startPt) {
        this.text = text;
        this.angle = angle;
        this.startPt = startPt;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getAngle() {
        return angle;
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }

    public Point getStartPt() {
        return startPt;
    }

    public void setStartPt(Point startPt) {
        this.startPt = startPt;
    }

    @Override
    public String toString() {
        return "text: " + text + ", angle: " + angle + ", start point: " + startPt;
    }
}
