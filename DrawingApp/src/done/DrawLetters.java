package done;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by pawel on 18.04.2016.
 */
public class DrawLetters extends JFrame {
    private List<ManipulatorShape> letterList;
    private int leftShift;
    private MyCanvas myCanvas;

    public DrawLetters() throws HeadlessException {
        letterList = new LinkedList<>();
        leftShift = 0;


        setSize(400, 400);
        setVisible(true);
        myCanvas = new MyCanvas(getGraphics());
        add(myCanvas);
        revalidate();
        repaint();
    }

    public void addLetter(ManipulatorShape shape) {
        letterList.add(shape);
    }

    public void draw() {
//        myCanvas.paint(getGraphics());
        myCanvas.paintManipulatorShape(letterList.get(0));

//        Graphics2D g = (Graphics2D)getGraphics();
//        g.draw(letterList.get(0).getWhole()[0]);
//        paintComponents(g);

//        invalidate();
        revalidate();
        repaint();
    }
}
