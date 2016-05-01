package done.letters;


import java.awt.*;
import java.awt.geom.Line2D;
import java.util.List;
import java.util.ListIterator;


/**
 * Created by pawel on 18.04.2016.
 */
public class Letter implements ManipulatorShape {
    private int xShift, yShift;
    private List<Point> pointList;
    private Shape[] shapes;


    public Letter(List<Point> pointList) {
        this.pointList = pointList;
        xShift = 0;
        yShift = 0;

        init();
    }

    public Letter(List<Point> pointList, int xShift, int yShift) {
        this.pointList = pointList;
        this.xShift = xShift;
        this.yShift = yShift;

        init();
    }

    private void init() {
        shapes = new Shape[pointList.size() - 1];

        int index = 0;
        ListIterator<Point> it = pointList.listIterator();

        for (int i = 0; i < pointList.size() - 1; i++) {
            shapes[index++] = new Line2D.Double(it.next(), it.next());
            it.previous();
        }
    }

    public void setShift(int xShift, int yShift) {
        this.xShift = xShift;
        this.yShift = yShift;

        for (Point point : pointList) {
            point.setLocation(point.getX() + xShift, point.getY() + yShift);
        }
        init();
    }

    public Shape[] getWhole() {
        return shapes;
    }

    public Shape getStep(int step) {
        return null;
    }

    public int getShapeCount() {
        return pointList.size();
    }

    public double getWidth() {
        double max = 0;

        for (Point point : pointList) {
            if(point.getX() > max) {
                max = point.getX();
            }
        }

        return max;
    }

    public double getHeight() {
        double max = 0;

        for (Point point : pointList) {
            if(point.getY() > max) {
                max = point.getY();
            }
        }

        return max;
    }
}
