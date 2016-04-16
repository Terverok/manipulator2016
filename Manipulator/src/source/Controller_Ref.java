package source;

public class Controller_Ref {
	private Connection connection;
	
	private final float przelozenieA = 11.2f;
	private final float przelozenieB = 5.f;
	private final int startSpeed = 20, startSpeedA, startSpeedB, startSpeedC;
	
	
	public Controller_Ref() {
		
		startSpeedA = Math.round(startSpeed * przelozenieA);
		startSpeedB = Math.round(startSpeed * przelozenieB);
		startSpeedC = Math.round(startSpeed);
		
		System.out.println(startSpeedA + " " + startSpeedB + " " + startSpeedC);
		connection = new PcConnection(startSpeedA, startSpeedB, startSpeedC);
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
		connection.setSpeed(Math.round(startSpeedA*alpha),
							Math.round(startSpeedB*beta),
							Math.round(startSpeedC*delta));
	}
	
	public int[] CorrectDegrees(float[] angles) {
		int[] correct = {
				Math.round(angles[0] * przelozenieA),
				Math.round(angles[1] * przelozenieB),
				Math.round(angles[2])
		};
		return correct;
	}
	
	public int[] CorrectDegrees(float alpha, float beta, float delta) {
		int[] correct = {
				Math.round(alpha * przelozenieA),
				Math.round(beta * przelozenieB),
				Math.round(delta)
		};
		return correct;
	}
	
	public float[] ReverseCorrectDegrees(int[] angles) {
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
		
		int[] anglesToEngine = CorrectDegrees(alpha, beta, delta);
		connection.rotateMotorsTo(anglesToEngine[0], anglesToEngine[1], anglesToEngine[2]);
		
		while (connection.isMoving()) {
			continue; //poczekaj a� silniki si� zatrzymaj�
		}
		return connection.getMotorPositions();
	}
	
	public int[] rotateMotorsByDeg(float alpha, float beta, float delta) {
		//need check for dangerous angles!!!!
		//put here!
		int[] anglesToEngine = CorrectDegrees(alpha, beta, delta);
		connection.rotateMotorsBy(anglesToEngine[0], anglesToEngine[1], anglesToEngine[2]);
		while (connection.isMoving()) {
			continue; //poczekaj a� silniki si� zatrzymaj�
		}
		return connection.getMotorPositions();
	}
	
	public double[] getArmPosition() {
		int[] motorPositions = connection.getMotorPositions();
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

	public void reset() {
		connection.setSpeed(startSpeedA, startSpeedB, startSpeedC);
		connection.reset();		
	}

	public int[] getMotorPositions() {
		return connection.getMotorPositions();
	}
}
