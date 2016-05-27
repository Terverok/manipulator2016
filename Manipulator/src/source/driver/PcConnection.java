package source.driver;

import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.addon.AccelHTSensor;

public class PcConnection implements Connection{
	private int[] startingPosition;
	AccelHTSensor tilt;
	
	public PcConnection(){
		startingPosition = getMotorPositions();
		tilt = new AccelHTSensor(SensorPort.S1);
	}
	
	
	public PcConnection(int startSpeedA, int startSpeedB, int startSpeedC) {
		startingPosition = getMotorPositions();

		//System.out.println(startSpeedA + " " + startSpeedB + " " + startSpeedC);
		setSpeed(startSpeedA, startSpeedB, startSpeedC);
	}
	
	public void setSpeed(int a, int b, int c) {
		//System.out.println(a + " " + b + " " + c);
		Motor.A.setSpeed(a);
		Motor.B.setSpeed(b);
		Motor.C.setSpeed(c);
	}
	
	public boolean isMoving() {
		return Motor.A.isMoving() || Motor.B.isMoving() || Motor.C.isMoving();
	}		
	
	public int[] getMotorPositions() {
		int[] positions = {
				Motor.A.getTachoCount(),
				Motor.B.getTachoCount(),
				Motor.C.getTachoCount()
		};
		return positions;
	}
	
	public int[] rotateMotorsTo(int alfa, int beta, int delta) {
		System.out.println(Motor.A.getSpeed() + " " + Motor.B.getSpeed() + " " + Motor.C.getSpeed());
		Motor.A.rotateTo(alfa, true);
		Motor.B.rotateTo(beta, true);
		Motor.C.rotateTo(delta, true);
		while (isMoving()) {
			try {
				Thread.sleep(20);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		return getMotorPositions();
	}
	
	public int[] rotateMotorsBy(int alfa, int beta, int delta) {
		System.out.println(Motor.A.getSpeed() + " " + Motor.B.getSpeed() + " " + Motor.C.getSpeed());
		Motor.A.rotate(alfa, true);
		Motor.B.rotate(beta, true);
		Motor.C.rotate(delta, true);
		while (isMoving()) {
			try {
				Thread.sleep(20);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		return getMotorPositions();
	}
	
	public int[] getTilt() {
		int[] tiltData = {tilt.getXAccel(), tilt.getYAccel(), tilt.getZAccel()};
		return tiltData;
	}
	
	public int[] reset() {
		return rotateMotorsTo(startingPosition[0], startingPosition[1], startingPosition[2]);
	} 
	
}
