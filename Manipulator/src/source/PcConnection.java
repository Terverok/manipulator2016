package source;

import lejos.nxt.Motor;

public class PcConnection implements Connection{
	private int[] startingPosition;
	private int startSpeedA, startSpeedB, startSpeedC;
	
	public PcConnection(int startSpeed) {
		startingPosition = getMotorPositions();		
		startSpeedA = Math.round(startSpeed * 3.5f);
		startSpeedB = Math.round(startSpeed * 0.45f);
		startSpeedC = Math.round(startSpeed * 0.1f);
		
		Motor.A.setSpeed(startSpeedA); //for A to rotate faster
		Motor.B.setSpeed(startSpeedB);//0.04
		Motor.C.setSpeed(startSpeedC);//0.05
		Motor.A.flt(false);
		Motor.B.flt(false);
		Motor.C.flt(false);
		Motor.A.smoothAcceleration(false);
		Motor.B.smoothAcceleration(false);
		Motor.C.smoothAcceleration(false);	
	}
	
	public void setSpeed(int a, int b, int c) {
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
		} //poczekaj a� silniki si� zatrzymaj�
		return getMotorPositions();
	}
	
	public int[] rotateMotorsBy(int alfa, int beta, int delta) {
		Motor.A.rotate(alfa, true);
		Motor.B.rotate(beta, true);
		Motor.C.rotate(delta, true);
		while (isMoving()) {
			continue; //poczekaj a� silniki si� zatrzymaj�
		}
		return getMotorPositions();
	}
	
	public int[] reset() {
		setSpeed(startSpeedA, startSpeedB, startSpeedC);
		return rotateMotorsTo(startingPosition[0], startingPosition[1], startingPosition[2]);
	}
	
}
