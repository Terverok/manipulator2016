package done;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by pawel on 18.04.2016.
 */
public class MyCanvas extends Canvas {
    private java.util.List<ManipulatorShape> letterList;
    private Graphics2D graphics;

    public MyCanvas(List<ManipulatorShape> letterList) {
        super();
        this.letterList = letterList;
    }

    public MyCanvas(Graphics graphics) {
        super();
        this.graphics = (Graphics2D)graphics;
    }

    @Override
    public void paint(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics;
        System.out.println("asdasd");
        if (letterList != null) {
//            for(ManipulatorShape shape : letterList) {
//                System.out.println("sad");
//                g.draw(shape.getStep(0));
//            }
//            g.draw(letterList.get(0).getStep(0));
        }
    }

    public void paintManipulatorShape(ManipulatorShape shape) {
        if(graphics == null) {
            System.out.println("NULL");
        }
        for(Shape tmp : shape.getWhole()) {
            System.out.println("asd");
            graphics.draw(tmp);

//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        }
    }
}
