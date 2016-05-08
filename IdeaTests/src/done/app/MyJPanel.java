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
    private boolean showSteps;

    public MyJPanel(List<ManipulatorShape> letterList) {
        super();
        this.letterList = letterList;
        showSteps = false;
    }

    public MyJPanel(Graphics graphics) {
        super();
        this.graphics = (Graphics2D) graphics;
        showSteps = false;
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

            if(showSteps) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setShowSteps(boolean showSteps) {
        this.showSteps = showSteps;
    }
}
