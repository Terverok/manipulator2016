package source;

public class PcConnection implements Connection{
	private int[] startingPosition;
	private int startSpeed, startSpeedA, startSpeedB, startSpeedC;
	
	public PcConnection(int startSpeed) {
		startingPosition = getMotorPositions();
		this.startSpeed = startSpeed;
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
	
	public int[] rotateMotorsToDeg(float alpha, float beta, float delta) {		
		float alphaMin = -175.f,
		alphaMax = 80.f,
		betaMin = -58.f,
		betaMax = 60.f,
		deltaMin = -50.f,
		deltaMax = 245.f;
		
		if (alpha < alphaMin) {
			alpha = alphaMin;
		}
		else if (alpha > alphaMax) {
			alpha = alphaMax;
		}
		
		if (beta < betaMin) {
			beta = betaMin;
		}
		else if (beta > betaMax) {
			beta = betaMax;
		}
		
		if (delta < deltaMin) {
			delta = deltaMin;
		}
		else if (delta > deltaMax) {
			delta = deltaMax;
		}
		
		int[] anglesToEngine = CorrectDegrees(alpha, beta, delta);
		Motor.A.rotateTo(anglesToEngine[0], true);
		Motor.B.rotateTo(anglesToEngine[1], true);
		Motor.C.rotateTo(anglesToEngine[2], true);
		
		while (isMoving()) {
			continue; //poczekaj a� silniki si� zatrzymaj�
		}
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
	
	public int[] rotateMotorsByDeg(float alpha, float beta, float delta) {
		//need check for dangerous angles!!!!
		//put here!
		int[] anglesToEngine = CorrectDegrees(alpha, beta, delta);
		Motor.A.rotate(anglesToEngine[0], true);
		Motor.B.rotate(anglesToEngine[1], true);
		Motor.C.rotate(anglesToEngine[2], true);
		while (isMoving()) {
			continue; //poczekaj a� silniki si� zatrzymaj�
		}
		return getMotorPositions();
	}
	
	public double[] getArmPosition() {
		int[] motorPositions = getMotorPositions();
		float[] angles = ReverseCorrectDegrees(motorPositions);
		return Kinematics.calculateArmPosition(angles[0], angles[1], angles[2]);
	}
	
	public double[] moveArmTo(float x, float y, float z) {		
		for(int j = 0; j < 1; j++) {		
			float[] ang = Kinematics.calculatechangeMotorPoisitons(x, y, z);
			//Computed angles with small error send to device
			rotateMotorsToDeg(ang[0], ang[1], ang[2]);
		}	
		return getArmPosition();
	}
	
	public float[] moveArmBy(float x, float y, float z) {
		// TODO
		return null;
	}
	
	public int[] reset() {
		setSpeed(startSpeedA, startSpeedB, startSpeedC);
		return rotateMotorsTo(startingPosition[0], startingPosition[1], startingPosition[2]);
	}
	
}
