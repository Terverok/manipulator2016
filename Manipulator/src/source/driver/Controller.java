package source.driver;

public class Controller {
	
	private static Controller instance;
	private Connection connection;
	
	private final double przelozenieA = 11.2;
	private final double przelozenieB = 5.0;
	private final int startSpeed = 45, startSpeedA, startSpeedB, startSpeedC;
	
	double alphaMin = -175.0,
			alphaMax = 80.0,
			betaMin = -58.0,
			betaMax = 60.0,
			deltaMin = -50.0,
			deltaMax = 245.0; //safe angles
	
	static {
		instance = new Controller();
	}
	
	private Controller() {
		
		startSpeedA = (int) Math.round(startSpeed * przelozenieA);
		startSpeedB = (int) Math.round(startSpeed * przelozenieB);
		startSpeedC = (int) Math.round(startSpeed);
		
		System.out.println(startSpeedA + " " + startSpeedB + " " + startSpeedC);
		connection = new PcConnection(startSpeedA, startSpeedB, startSpeedC);
	}
	
	public static Controller getInstance(){
		return instance;
	}
	
	private void adjustSpeedForDistance(double alpha, double beta, double delta) {
		alpha = Math.abs(alpha);
		beta = Math.abs(beta);
		delta = Math.abs(delta);
		double maxValue = alpha;
		if (beta > maxValue) maxValue = beta;
		if (delta > maxValue) maxValue = delta;
		alpha /= maxValue;
		beta /= maxValue;
		if (delta > 0.0) delta /= maxValue;
		else delta = 1.0;
		System.out.println(alpha + " " + beta + " " + delta);
		if (delta < 9.0/startSpeedC) {
			double adjust = (9.0/startSpeedC) / delta;
			alpha *= adjust;
			beta *=adjust;
			delta = 9.0/20.0;
		}													
		setSpeed(startSpeedA*alpha,
				startSpeedB*beta,
				startSpeedC*delta);
	}
	
	public void setSpeed(int a, int b, int c) {
		int[] round = roundTo9(a, b, c);
		connection.setSpeed(round[0], round[1], round[2]);
	}
	
	public void setSpeed(double a, double b, double c){
		int[] round = roundTo9(a, b, c);
		connection.setSpeed(round[0], round[1], round[2]);
	}
	
	private int[] roundTo9(double a, double b, double c){
		a /= 9.0;
		b /= 9.0;
		c /= 9.0;
		int A = (int) Math.round(a) * 9;
		int B = (int) Math.round(b) * 9;
		int C = (int) Math.round(c) * 9;
		return new int[]{A, B, C};
	}
	
	private int[] roundTo9(int a, int b, int c){
		double A = ((double) a)/9.0;
		double B = ((double) b)/9.0;
		double C = ((double) c)/9.0;
		a = (int) Math.round(A) * 9;
		b = (int) Math.round(B) * 9;
		c = (int) Math.round(C) * 9;
		return new int[]{a, b, c};
	}
	
	public double[] getJointAngles(){
		return motorDegreesToJointDegrees(getMotorPositions());
	}
	
	private int[] jointDegreesToMotorDegrees(double[] angles) {
		int[] correct = {
				(int) Math.round(angles[0] * przelozenieA),
				(int) Math.round(angles[1] * przelozenieB),
				(int) Math.round(angles[2])
		};
		return correct;
	}
	
	private int[] jointDegreesToMotorDegrees(double alpha, double beta, double delta) {
		int[] correct = {
				(int) Math.round(alpha * przelozenieA),
				(int) Math.round(beta * przelozenieB),
				(int) Math.round(delta)
		};
		return correct;
	}
	
	private double[] motorDegreesToJointDegrees(int[] angles) {
		double[] rev = {
				angles[0] / przelozenieA,
				angles[1] / przelozenieB,
				angles[2]
		};
		return rev;
	}
	
	public int[] rotateJointsToDeg(double alpha, double beta, double delta) {		
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
		
		double[] currentAngles = motorDegreesToJointDegrees(getMotorPositions());
		alpha -= currentAngles[0];
		beta -= currentAngles[1];
		delta -= currentAngles[2];
		
		return executeMovement(alpha, beta, delta);
	}
	
	public int[] rotateJointsByDeg(double alpha, double beta, double delta) {
		double[] currentAngles = motorDegreesToJointDegrees(getMotorPositions());
		double a = currentAngles[0] + alpha;
		double b = currentAngles[1] + beta;
		double c = currentAngles[2] + delta;
		
		if (a < alphaMin) {
			alpha = currentAngles[0] - alphaMin;
		}
		else if (a > alphaMax) {
			alpha = currentAngles[0] - alphaMax;
		}
		
		if (b < betaMin) {
			beta = currentAngles[1] - betaMin;
		}
		else if (b > betaMax) {
			beta = currentAngles[1] - betaMax;
		}
		
		if (c < deltaMin) {
			delta = currentAngles[2] - deltaMin;
		}
		else if (c > deltaMax) {
			delta = currentAngles[2] - deltaMax;
		}
		
		return executeMovement(alpha, beta, delta);
	}
	
	private int[] executeMovement(double alpha, double beta, double delta){
		int[] anglesToEngine = jointDegreesToMotorDegrees(alpha, beta, delta);
		adjustSpeedForDistance(alpha, beta, delta);
		return connection.rotateMotorsBy(anglesToEngine[0], anglesToEngine[1], anglesToEngine[2]);
	}
	
	public double[] getArmPosition() {
		int[] motorPositions = connection.getMotorPositions();
		double[] angles = motorDegreesToJointDegrees(motorPositions);
		return Kinematics.calculateArmPosition(angles[0], angles[1], angles[2]);
	}
	
	public double[] moveArmTo(double x, double y, double z) {
		double[] ang = motorDegreesToJointDegrees(getMotorPositions());
		ang = Kinematics.calculateChangeMotorPoisitons(x, y, z, ang);
		rotateJointsToDeg(ang[0], ang[1], ang[2]);
		return getArmPosition();
	}
	
	public double[] moveArmBy(double x, double y, double z) {
		double[] pos = getArmPosition();
		return moveArmTo(pos[0] + x, pos[1] + y, pos[2] + z);
	}

	public void reset() {
		setSpeed(startSpeedA, startSpeedB, startSpeedC);
		connection.reset();		
	}

	public int[] getMotorPositions() {
		return connection.getMotorPositions();
	}
}
