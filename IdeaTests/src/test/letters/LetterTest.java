package test.letters;

import main.done.letters.Letter;
import org.junit.Test;

import java.awt.*;
import java.util.*;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by pawel on 02.06.2016.
 */
public class LetterTest {
    private static int x = 10;
    private static int y = 110;

    @Test
    public void getWidthTest() {
        //Litera A
        java.util.List<Point> list = new LinkedList<>();
        list.add(new Point(x, y));
        list.add(new Point(x + 20, y - 80));
        list.add(new Point(x + 40, y));
        list.add(new Point(x + 30, y - 40));
        list.add(new Point(x + 10, y - 40));

        Letter letterA = new Letter(list);

        assertEquals((double)(x + 40), letterA.getWidth());

        //Litera B
        List<Point> list1 = new LinkedList<>();
        list1.add(new Point(x + 40, y));
        list1.add(new Point(x + 10, y));
        list1.add(new Point(x, y - 40));
        list1.add(new Point(x + 10, y - 80));
        list1.add(new Point(x + 40, y - 80));

        Letter letterB = new Letter(list1);

        assertEquals((double)(x + 40), letterB.getWidth());
    }

    @Test
    public void getHeightTest() {
        //Litera A
        java.util.List<Point> list = new LinkedList<>();
        list.add(new Point(x, y));
        list.add(new Point(x + 20, y - 80));
        list.add(new Point(x + 40, y));
        list.add(new Point(x + 30, y - 40));
        list.add(new Point(x + 10, y - 40));

        Letter letterA = new Letter(list);

        assertEquals((double)(y), letterA.getHeight());

        //Litera B
        List<Point> list1 = new LinkedList<>();
        list1.add(new Point(x + 40, y));
        list1.add(new Point(x + 10, y));
        list1.add(new Point(x, y - 40));
        list1.add(new Point(x + 10, y - 80));
        list1.add(new Point(x + 40, y - 80));

        Letter letterB = new Letter(list1);

        assertEquals((double)(y), letterB.getHeight());
    }

    @Test
    public void getShapeCountTest() {
        //Litera A
        java.util.List<Point> list = new LinkedList<>();
        list.add(new Point(x, y));
        list.add(new Point(x + 20, y - 80));
        list.add(new Point(x + 40, y));
        list.add(new Point(x + 30, y - 40));
        list.add(new Point(x + 10, y - 40));

        Letter letterA = new Letter(list);

        assertEquals(list.size(), letterA.getShapeCount());

        //Litera B
        List<Point> list1 = new LinkedList<>();
        list1.add(new Point(x + 40, y));
        list1.add(new Point(x + 10, y));
        list1.add(new Point(x, y - 40));
        list1.add(new Point(x + 10, y - 80));
        list1.add(new Point(x + 40, y - 80));

        Letter letterB = new Letter(list1);

        assertEquals(list1.size(), letterB.getShapeCount());
    }

    @Test
    public void setShiftTest() {
        //Litera A
        java.util.List<Point> list = new LinkedList<>();
        list.add(new Point(x, y));
        list.add(new Point(x + 20, y - 80));
        list.add(new Point(x + 40, y));
        list.add(new Point(x + 30, y - 40));
        list.add(new Point(x + 10, y - 40));

        Letter letterA = new Letter(list);

        int xShift = 20, yShift = 30;
        letterA.setShift(xShift, yShift);

        assertEquals((double)(x + xShift), letterA.getPoint(0).getX());
        assertEquals((double)(y + yShift), letterA.getPoint(0).getY());

        assertEquals((double)(x + 20 + xShift), letterA.getPoint(1).getX());
        assertEquals((double)(y - 80 + yShift), letterA.getPoint(1).getY());

        assertEquals((double)(x + 40 + xShift), letterA.getPoint(2).getX());
        assertEquals((double)(y + yShift), letterA.getPoint(2).getY());

        assertEquals((double)(x + 30 + xShift), letterA.getPoint(3).getX());
        assertEquals((double)(y - 40 + yShift), letterA.getPoint(3).getY());

        assertEquals((double)(x + 10 + xShift), letterA.getPoint(4).getX());
        assertEquals((double)(y - 40 + yShift), letterA.getPoint(4).getY());
    }

    @Test
    public void getPointTest() {
        //Litera A
        Point p0 = new Point(x, y);
        Point p1 = new Point(x + 20, y - 80);
        Point p2 = new Point(x + 40, y);
        Point p3 = new Point(x + 30, y - 40);
        Point p4 = new Point(x + 10, y - 40);

        java.util.List<Point> list = new LinkedList<>();
        list.add(p0);
        list.add(p1);
        list.add(p2);
        list.add(p3);
        list.add(p4);

        Letter letterA = new Letter(list);

        assertEquals(p0, letterA.getPoint(0));
        assertEquals(p1, letterA.getPoint(1));
        assertEquals(p2, letterA.getPoint(2));
        assertEquals(p3, letterA.getPoint(3));
        assertEquals(p4, letterA.getPoint(4));
    }
}