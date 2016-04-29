package source;

public class Controller {
	private Connection connection;
	
	private static final float przelozenieA = 11.2f;
	private static final float przelozenieB = 5.f;
	private final int startSpeed = 20, startSpeedA, startSpeedB, startSpeedC;
	private Kinematics kinematics;
	
	
	public Controller() {
		
		startSpeedA = Math.round(startSpeed * przelozenieA);
		startSpeedB = Math.round(startSpeed * przelozenieB);
		startSpeedC = Math.round(startSpeed);
		
		System.out.println(startSpeedA + " " + startSpeedB + " " + startSpeedC);
		connection = new PcConnection(startSpeedA, startSpeedB, startSpeedC);
		kinematics = new Kinematics(this);
	}
	
	public void adjustSpeedForDistance(float alpha, float beta, float delta) {
		alpha = Math.abs(alpha);
		beta = Math.abs(beta);
		delta = Math.abs(delta);
		float maxValue = alpha;
		if (beta > maxValue) maxValue = beta;
		if (delta > maxValue) maxValue = delta;
		alpha /= maxValue;
		beta /= maxValue;
		if (delta > 0.0f) delta /= maxValue;
		else delta = 1.0f;
		System.out.println(alpha + " " + beta + " " + delta);
		if (delta < 9.0f/startSpeedC) {
			float adjust = (9.0f/startSpeedC) / delta;
			alpha *= adjust;
			beta *=adjust;
			delta = 9.0f/20.0f;
		}													
		setSpeed(startSpeedA*alpha,
				startSpeedB*beta,
				startSpeedC*delta);
	}
	
	public void setSpeed(int a, int b, int c) {
		int[] round = roundTo9(a, b, c);
		connection.setSpeed(round[0], round[1], round[2]);
	}
	
	public void setSpeed(float a, float b, float c){
		int[] round = roundTo9(a, b, c);
		connection.setSpeed(round[0], round[1], round[2]);
	}
	
	private int[] roundTo9(float a, float b, float c){
		a /= 9.f;
		b /= 9.f;
		c /= 9.f;
		int A = Math.round(a) * 9;
		int B = Math.round(b) * 9;
		int C = Math.round(c) * 9;
		return new int[]{A, B, C};
	}
	
	private int[] roundTo9(int a, int b, int c){
		Float A = ((float) a)/9.f;
		Float B = ((float) b)/9.f;
		Float C = ((float) c)/9.f;
		a = Math.round(A) * 9;
		b = Math.round(B) * 9;
		c = Math.round(C) * 9;
		return new int[]{a, b, c};
	}
	
	static public int[] correctDegrees(float[] angles) {
		int[] correct = {
				Math.round(angles[0] * przelozenieA),
				Math.round(angles[1] * przelozenieB),
				Math.round(angles[2])
		};
		return correct;
	}
	
	static public int[] correctDegrees(float alpha, float beta, float delta) {
		int[] correct = {
				Math.round(alpha * przelozenieA),
				Math.round(beta * przelozenieB),
				Math.round(delta)
		};
		return correct;
	}
	
	static public float[] reverseCorrectDegrees(int[] angles) {
		float[] rev = {
				angles[0] / przelozenieA,
				angles[1] / przelozenieB,
				angles[2]
		};
		return rev;
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
		
		int[] anglesToEngine = correctDegrees(alpha, beta, delta);
		connection.rotateMotorsTo(anglesToEngine[0], anglesToEngine[1], anglesToEngine[2]);
		
		while (connection.isMoving()) {
			continue; //poczekaj a� silniki si� zatrzymaj�
		}
		return connection.getMotorPositions();
	}
	
	public int[] rotateMotorsByDeg(float alpha, float beta, float delta) {
		//need check for dangerous angles!!!!
		//put here!
		int[] anglesToEngine = correctDegrees(alpha, beta, delta);
		connection.rotateMotorsBy(anglesToEngine[0], anglesToEngine[1], anglesToEngine[2]);
		while (connection.isMoving()) {
			continue; //poczekaj a� silniki si� zatrzymaj�
		}
		return connection.getMotorPositions();
	}
	
	public double[] getArmPosition() {
		int[] motorPositions = connection.getMotorPositions();
		float[] angles = reverseCorrectDegrees(motorPositions);
		return kinematics.calculateArmPosition(angles[0], angles[1], angles[2]);
	}
	
	public double[] moveArmTo(float x, float y, float z) {		
		for(int j = 0; j < 1; j++) {		
			float[] ang = kinematics.calculatechangeMotorPoisitons(x, y, z);
			//Computed angles with small error send to device
			rotateMotorsToDeg(ang[0], ang[1], ang[2]);
		}	
		return getArmPosition();
	}
	
	public float[] moveArmBy(float x, float y, float z) {
		// TODO
		return null;
	}

	public void reset() {
		setSpeed(startSpeedA, startSpeedB, startSpeedC);
		connection.reset();		
	}

	public int[] getMotorPositions() {
		return connection.getMotorPositions();
	}
}
