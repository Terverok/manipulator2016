package done;

import java.awt.*;

/**
 * Created by pawel on 18.04.2016.
 */
public interface ManipulatorShape {
    Shape[] getWhole();
    Shape getStep(int step);
    int getShapeCount();
    void setShift(int xShift, int yShift);
}
