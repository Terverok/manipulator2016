import done.Letter;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by pawel on 18.04.2016.
 */
public class Draw2DObjects extends JFrame {
    private Shape shapes[];


    public Draw2DObjects() throws HeadlessException {
//        shapes = new Shape[5];
//        add("Center", new MyCanvas());

//        shapes[0] = new Line2D.Double(100.0, 200.0, 120.0, 120.0);
//        shapes[1] = new Line2D.Double(120.0, 120.0, 140.0, 200.0);
//        Point midRight = new Point(130, 160);
//        shapes[2] = new Line2D.Double(new Point(140, 200), midRight);
//        Point midLeft = new Point(110, 160);
//        shapes[3] = new Line2D.Double(midRight, midLeft);
//        GeneralPath path = new GeneralPath(new Line2D.Double(300.0, 100.0, 400.0, 150.0));
//        path.append(new Line2D.Double(25.0, 175.0, 300.0, 100.0), true);


        int x = 0;
        int y = 80;

        Point p1 = new Point(x, y);
        Point p2 = new Point(x + 20, y - 80);
        Point p3 = new Point(x + 40, y);
        Point p4 = new Point(x + 30, y - 40);
        Point p5 = new Point(x + 10, y - 40);

//        shapes[0] =  new Line2D.Double(p1, p2);
//        shapes[1] =  new Line2D.Double(p2, p3);
//        shapes[2] =  new Line2D.Double(p3, p4);
//        shapes[3] =  new Line2D.Double(p4, p5);


        List<Point> list = new LinkedList<>();
        list.add(p1);
        list.add(p2);
        list.add(p3);
        list.add(p4);
        list.add(p5);

        Letter letter = new Letter(list);

        shapes = letter.getWhole();



//        MyCanvas_1 myCanvas = new MyCanvas_1();
//        myCanvas.paint(this.getGraphics());
        add("Center", new MyCanvas_1());

        setSize(400, 400);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setVisible(true);
    }

    class MyCanvas_1 extends Canvas {

        public void paint(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics;
            System.out.println("asd");
            for (int i = 0; i < shapes.length; ++i) {
                System.out.println(i);
                if (shapes[i] != null)
                    g.draw(shapes[i]);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
