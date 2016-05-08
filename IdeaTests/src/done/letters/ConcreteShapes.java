package done.letters;

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

    public static Letter letterD() {
        List<Point> list = new LinkedList<>();
        list.add(new Point(x, y));
        list.add(new Point(x, y - 80));
        list.add(new Point(x + 15, y - 80));
        list.add(new Point(x + 40, y - 40));
        list.add(new Point(x + 15, y));
        list.add(new Point(x, y));

        return new Letter(list);
    }

    public static Letter letterE() {
        List<Point> list = new LinkedList<>();
        list.add(new Point(x, y));
        list.add(new Point(x, y - 80));
        list.add(new Point(x + 40, y - 80));
        list.add(new Point(x, y - 80));
        list.add(new Point(x, y - 40));
        list.add(new Point(x + 40, y - 40));
        list.add(new Point(x, y - 40));
        list.add(new Point(x, y));
        list.add(new Point(x + 40, y ));

        return new Letter(list);
    }

    public static Letter letterF() {
        List<Point> list = new LinkedList<>();
        list.add(new Point(x, y));
        list.add(new Point(x, y - 80));
        list.add(new Point(x + 40, y - 80));
        list.add(new Point(x, y - 80));
        list.add(new Point(x, y - 40));
        list.add(new Point(x + 40, y - 40));

        return new Letter(list);
    }

    public static Letter letterG() {
        List<Point> list = new LinkedList<>();
        list.add(new Point(x + 40, y - 80));
        list.add(new Point(x, y - 80));
        list.add(new Point(x, y));
        list.add(new Point(x + 40, y));
        list.add(new Point(x + 40, y - 40));
        list.add(new Point(x + 10, y - 40));

        return new Letter(list);
    }

    public static Letter letterH() {
        List<Point> list = new LinkedList<>();
        list.add(new Point(x, y - 80));
        list.add(new Point(x, y));
        list.add(new Point(x, y - 40));
        list.add(new Point(x + 40, y - 40));
        list.add(new Point(x + 40, y - 80));
        list.add(new Point(x + 40, y));

        return new Letter(list);
    }

    public static Letter letterI() {
        List<Point> list = new LinkedList<>();
        list.add(new Point(x + 20, y - 80));
        list.add(new Point(x + 20, y));

        return new Letter(list);
    }

    public static Letter letterJ() {
        List<Point> list = new LinkedList<>();
        list.add(new Point(x + 30, y - 80));
        list.add(new Point(x + 30, y));
        list.add(new Point(x + 10, y));
        list.add(new Point(x + 10, y - 20));

        return new Letter(list);
    }

    public static Letter letterK() {
        List<Point> list = new LinkedList<>();
        list.add(new Point(x, y - 80));
        list.add(new Point(x, y));
        list.add(new Point(x, y - 40));
        list.add(new Point(x + 40, y - 80));
        list.add(new Point(x, y - 40));
        list.add(new Point(x + 40, y));

        return new Letter(list);
    }

    public static Letter letterL() {
        List<Point> list = new LinkedList<>();
        list.add(new Point(x, y - 80));
        list.add(new Point(x, y));
        list.add(new Point(x + 40, y));

        return new Letter(list);
    }

    public static Letter letterM() {
        List<Point> list = new LinkedList<>();
        list.add(new Point(x, y));
        list.add(new Point(x, y - 80));
        list.add(new Point(x + 20, y - 40));
        list.add(new Point(x + 40, y - 80));
        list.add(new Point(x + 40, y));

        return new Letter(list);
    }

    public static Letter letterN() {
        List<Point> list = new LinkedList<>();
        list.add(new Point(x, y));
        list.add(new Point(x, y - 80));
        list.add(new Point(x + 40, y));
        list.add(new Point(x + 40, y - 80));

        return new Letter(list);
    }

    public static Letter letterO() {
        List<Point> list = new LinkedList<>();
        list.add(new Point(x, y - 60));
        list.add(new Point(x, y - 20));
        list.add(new Point(x + 10, y));
        list.add(new Point(x + 30, y));
        list.add(new Point(x + 40, y - 20));
        list.add(new Point(x + 40, y - 60));
        list.add(new Point(x + 30, y - 80));
        list.add(new Point(x + 10, y - 80));
        list.add(new Point(x, y - 60));

        return new Letter(list);
    }

    public static Letter letterP() {
        List<Point> list = new LinkedList<>();
        list.add(new Point(x, y));
        list.add(new Point(x, y - 80));
        list.add(new Point(x + 30, y - 80));
        list.add(new Point(x + 30, y - 50));
        list.add(new Point(x, y - 50));

        return new Letter(list);
    }

    public static Letter letterQ() {
        //TODO
        List<Point> list = new LinkedList<>();
        list.add(new Point(x, y - 60));
        list.add(new Point(x, y - 20));
        list.add(new Point(x + 10, y));
        list.add(new Point(x + 30, y));
        list.add(new Point(x + 40, y - 20));
        list.add(new Point(x + 40, y - 60));
        list.add(new Point(x + 30, y - 80));
        list.add(new Point(x + 10, y - 80));
        list.add(new Point(x, y - 60));

        return new Letter(list);
    }

    public static Letter letterR() {
        List<Point> list = new LinkedList<>();
        list.add(new Point(x, y));
        list.add(new Point(x, y - 80));
        list.add(new Point(x + 30, y - 80));
        list.add(new Point(x + 30, y - 50));
        list.add(new Point(x, y - 50));
        list.add(new Point(x + 40, y));

        return new Letter(list);
    }

    public static Letter letterS() {
        List<Point> list = new LinkedList<>();
        list.add(new Point(x + 40, y - 60));
        list.add(new Point(x + 30, y - 80));
        list.add(new Point(x + 10, y - 80));
        list.add(new Point(x, y - 60));
        list.add(new Point(x, y - 50));
        list.add(new Point(x + 40, y - 30));
        list.add(new Point(x + 40, y - 20));
        list.add(new Point(x + 30, y));
        list.add(new Point(x + 10, y));
        list.add(new Point(x, y - 20));

        return new Letter(list);
    }

    public static Letter letterT() {
        List<Point> list = new LinkedList<>();
        list.add(new Point(x + 20, y));
        list.add(new Point(x + 20, y - 80));
        list.add(new Point(x + 40, y - 80));
        list.add(new Point(x, y - 80));

        return new Letter(list);
    }

    public static Letter letterU() {
        List<Point> list = new LinkedList<>();
        list.add(new Point(x, y - 80));
        list.add(new Point(x, y));
        list.add(new Point(x + 40, y));
        list.add(new Point(x + 40, y - 80));

        return new Letter(list);
    }

    public static Letter letterW() {
        List<Point> list = new LinkedList<>();
        list.add(new Point(x, y - 80));
        list.add(new Point(x + 10, y));
        list.add(new Point(x + 20, y - 80));
        list.add(new Point(x + 30, y));
        list.add(new Point(x + 40, y - 80));

        return new Letter(list);
    }

    public static Letter letterX() {
        List<Point> list = new LinkedList<>();
        list.add(new Point(x, y - 80));
        list.add(new Point(x + 40, y));
        list.add(new Point(x + 20, y - 40));
        list.add(new Point(x + 40, y - 80));
        list.add(new Point(x, y));

        return new Letter(list);
    }

    public static Letter letterY() {
        List<Point> list = new LinkedList<>();
        list.add(new Point(x + 20, y));
        list.add(new Point(x + 20, y - 50));
        list.add(new Point(x + 40, y - 80));
        list.add(new Point(x + 20, y - 50));
        list.add(new Point(x, y - 80));

        return new Letter(list);
    }

    public static Letter letterZ() {
        List<Point> list = new LinkedList<>();
        list.add(new Point(x, y - 80));
        list.add(new Point(x + 40, y - 80));
        list.add(new Point(x, y));
        list.add(new Point(x + 40, y));

        return new Letter(list);
    }
    
    public static List<Letter> getAlphabet() {
        List<Letter> list = new LinkedList<>();

        list.add(letterA());
        list.add(letterB());
        list.add(letterC());
        list.add(letterD());
        list.add(letterE());
        list.add(letterF());
        list.add(letterG());
        list.add(letterH());
        list.add(letterI());
        list.add(letterJ());
        list.add(letterK());
        list.add(letterL());
        list.add(letterM());
        list.add(letterN());
        list.add(letterO());
        list.add(letterP());
        list.add(letterQ());
        list.add(letterR());
        list.add(letterS());
        list.add(letterT());
        list.add(letterU());
        list.add(letterW());
        list.add(letterX());
        list.add(letterY());
        list.add(letterZ());
        
        return list;
    }
}
