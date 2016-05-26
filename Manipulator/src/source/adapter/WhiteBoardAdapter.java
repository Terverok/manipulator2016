package source.adapter;

import source.driver.Controller;

public class WhiteBoardAdapter implements ControlAdapter2dTo3d {
	private Controller controller;
	private double currentAltitude;
	private final double altOn = 20.0, altOff = 15.0;
	
	public WhiteBoardAdapter(Controller controller) {
		this.controller = controller;
		currentAltitude = altOn;
	}
	
	@Override
	public double[] moveTo(double x, double y) {
		// TODO Auto-generated method stub
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
