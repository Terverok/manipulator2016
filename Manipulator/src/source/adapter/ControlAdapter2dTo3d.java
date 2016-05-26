package source.adapter;


public interface ControlAdapter2dTo3d {
	double[] moveTo(double x, double y);
	double[] moveBy (double x, double y);
	double[] penUp();
	double[] penDown();
}
