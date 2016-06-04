package source.adapter;

import source.driver.Controller;

public class WhiteBoardAdapter implements Adapter2dTo3d {
	private Controller controller;
	private double currentAltitude;
	private final double X0=34, Y0=62, X_MAX=12, Y_MAX=30;
	private double currentX, currentY;
	private final double altOn = -6.0, altOff = 0.0;
	
	public WhiteBoardAdapter(Controller controller) {
		this.controller = controller;
		currentX = X0;
		currentY = Y0;
		currentAltitude = altOn;
	}
	
	private double[] execute(){
		if (currentAltitude != altOff) antiGravity();
		return controller.moveArmTo(currentX, currentAltitude, currentY);
	}
	
	private void check(){
		if (currentX > X0) currentX = X0;
		else if (currentX < X_MAX) currentX = X_MAX;
		
		if (currentY > Y0) currentY = Y0;
		else if (currentY < Y_MAX) currentY = Y_MAX; 
	}
	
	private void antiGravity(){  //countermeasure against gravitation
		currentAltitude = altOn - (currentY-Y_MAX)*0.035 * 2;
		System.out.println("Altitude:" + currentAltitude);
	}
	
	@Override
	public double[] moveTo(double x, double y) {
		currentX = X0 - x;
		currentY = Y0 - y;
		check();
		return execute();
	}
	
	@Override
	public double[] moveBy(double x, double y) {
		currentX -= x;
		currentY -= y;
		check();
		return execute();
	}

	@Override
	public double[] penUp() {
		currentAltitude = altOff;
		return execute();
	}

	@Override
	public double[] penDown() {
		currentAltitude = altOn;
		return execute();
	}
}
