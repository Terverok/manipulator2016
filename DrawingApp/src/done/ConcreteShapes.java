package done;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Pawel on 2016-05-01.
 */
public class ConcreteShapes {
    private static int x = 10;
    private static int y = 110;

    public static Letter letterA() {
        List<Point> list = new LinkedList<>();
        list.add(new Point(x, y));
        list.add(new Point(x + 20, y - 80));
        list.add(new Point(x + 40, y));
        list.add(new Point(x + 30, y - 40));
        list.add(new Point(x + 10, y - 40));

        return new Letter(list);
    }

    public static Letter letterB() {
        List<Point> list = new LinkedList<>();
        list.add(new Point(x, y));
        list.add(new Point(x, y - 80));
        list.add(new Point(x + 40, y - 60));
        list.add(new Point(x, y - 40));
        list.add(new Point(x + 40, y - 20));
        list.add(new Point(x, y));

        return new Letter(list);
    }

    public static Letter letterC() {
        List<Point> list = new LinkedList<>();
        list.add(new Point(x + 40, y));
        list.add(new Point(x + 10, y));
        list.add(new Point(x, y - 40));
        list.add(new Point(x + 10, y - 80));
        list.add(new Point(x + 40, y - 80));

        return new Letter(list);
    }
}
