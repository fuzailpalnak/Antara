package in.antara.antara.objects;

/**
 * Created by udar on 8/25/2017.
 */
public class Line {

    private Point start;
    private Point end;

    public Line(Point start, Point end) {
        this.start = start;
        this.end = end;
    }

    public Point getStart() {
        return start;
    }

    public void setStart(Point start) {
        this.start = start;
    }

    public Point getEnd() {
        return end;
    }

    public void setEnd(Point end) {
        this.end = end;
    }

    public Point center() {
        int x = (start.getX() + end.getX()) / 2;
        int y = (start.getY() + end.getY()) / 2;
        return new Point(x, y);
    }

    @Override
    public String toString() {
        return "start: " + start + ", end: " + end;
    }
}
