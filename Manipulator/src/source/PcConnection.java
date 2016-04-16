package source;

import lejos.nxt.Motor;

public class PcConnection implements Connection{
	private int[] startingPosition;
	
	public PcConnection(){
		startingPosition = getMotorPositions();
	}
	
	
	public PcConnection(int startSpeedA, int startSpeedB, int startSpeedC) {
		startingPosition = getMotorPositions();
		setSpeed(startSpeedA, startSpeedB, startSpeedC);
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
		return rotateMotorsTo(startingPosition[0], startingPosition[1], startingPosition[2]);
	} 
	
}
