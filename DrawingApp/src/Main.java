import done.DrawLetters;
import done.Letter;

import java.awt.*;
import java.util.*;

/**
 * Created by pawel on 18.04.2016.
 */
public class Main {
    public static void main(String[] args) {
//        Draw2DObjects app = new Draw2DObjects();


        DrawLetters drawLetters = new DrawLetters();

        int x = 0;
        int y = 80;

        Point p1 = new Point(x, y);
        Point p2 = new Point(x + 20, y - 80);
        Point p3 = new Point(x + 40, y);
        Point p4 = new Point(x + 30, y - 40);
        Point p5 = new Point(x + 10, y - 40);

        java.util.List<Point> list = new LinkedList<>();
        list.add(p1);
        list.add(p2);
        list.add(p3);
        list.add(p4);
        list.add(p5);

        Letter letter = new Letter(list);

        drawLetters.addLetter(letter);
        drawLetters.draw();
    }
}
