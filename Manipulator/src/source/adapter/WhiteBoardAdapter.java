package source.adapter;

import source.driver.Controller;

public class WhiteBoardAdapter implements ControlAdapter2dTo3d {
	private Controller controller;
	private double currentAltitude;
	private final double X0=12, Y0=30, X_MAX=0, Y_MAX=0;
	private double currentX, currentY;
	private final double altOn = 20.0, altOff = 0.0;
	
	public WhiteBoardAdapter(Controller controller) {
		this.controller = controller;
		currentX = X0;
		currentY = Y0;
		currentAltitude = altOn;
	}
	
	private double[] execute(){
		return controller.moveArmTo(currentX, currentAltitude, currentY);
	}
	
	@Override
	public double[] moveTo(double x, double y) {
		// TODO Auto-generated method stub
		currentX = x;
		currentY = y;
		return controller.moveArmTo(x, y, currentAltitude);
	}
	
	@Override
	public double[] moveBy(double x, double y) {
		// TODO Auto-generated method stub
		return controller.moveArmBy(x, y, 0);
	}

	@Override
	public double[] penUp() {
		// TODO Auto-generated method stub
		currentAltitude = altOff;
		return controller.getArmPosition();
	}

	@Override
	public double[] penDown() {
		// TODO Auto-generated method stub
		currentAltitude = altOn;
		return controller.getArmPosition();
	}
}
