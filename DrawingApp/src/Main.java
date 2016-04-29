import done.DrawLetters;
import done.Letter;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by pawel on 18.04.2016.
 */
public class Main {
    public static void main(String[] args) {
//        Draw2DObjects app = new Draw2DObjects();


        DrawLetters drawLetters = new DrawLetters();

        int x = 10;
        int y = 110;

        Point p1 = new Point(x, y);
        Point p2 = new Point(x + 20, y - 80);
        Point p3 = new Point(x + 40, y);
        Point p4 = new Point(x + 30, y - 40);
        Point p5 = new Point(x + 10, y - 40);

        List<Point> list = new LinkedList<>();
        list.add(p1);
        list.add(p2);
        list.add(p3);
        list.add(p4);
        list.add(p5);

        Letter letter = new Letter(list);


//        Point p21 = new Point(x+40, y);
//        Point p22 = new Point(x+60, y+50);
//        List<Point> list1 = new LinkedList<>();
//        list1.add(p21);
//        list1.add(p22);
//        Letter letter1 = new Letter(list1);

        List<Point> list2 = new LinkedList<>(list);
        Letter letter2 = new Letter(list2);
        letter2.setShift(100, 0);

        List<Point> list3 = new LinkedList<>(list);
        Letter letter3 = new Letter(list3);
        letter3.setShift(-100, 100);

        List<Point> list4 = new LinkedList<>(list);
        Letter letter4 = new Letter(list4);
        letter4.setShift(100, 0);

        drawLetters.addLetter(letter);
        drawLetters.addLetter(letter2);
        drawLetters.addLetter(letter3);
        drawLetters.addLetter(letter4);
//        drawLetters.draw();
    }
}
