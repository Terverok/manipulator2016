package done.app;

import done.letters.ManipulatorShape;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Created by Pawel on 2016-04-28.
 */
public class MyJPanel extends JPanel {
    private List<ManipulatorShape> letterList;
    private Graphics2D graphics;

    public MyJPanel(List<ManipulatorShape> letterList) {
        super();
        this.letterList = letterList;
    }

    public MyJPanel(Graphics graphics) {
        super();
        this.graphics = (Graphics2D) graphics;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.blue);
    }

    public void paintManipulatorShape(ManipulatorShape shape) {
        super.paintComponents(graphics);
        graphics.setColor(Color.blue);
        System.out.println("IN");
        if(graphics == null) {
            System.out.println("NULL");
        }
        for(Shape tmp : shape.getWhole()) {
            System.out.println("draw: " + tmp.getBounds().toString());
            graphics.draw(tmp);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
