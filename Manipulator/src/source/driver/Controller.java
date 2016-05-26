package source.driver;

public class Controller {
	
	private static Controller instance;
	private Connection connection;
	
	private final double przelozenieA = 11.2;
	private final double przelozenieB = 5.0;
	private final int startSpeed = 45, startSpeedA, startSpeedB, startSpeedC;
	
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
	
	public int[] rotateMotorsToDeg(double alpha, double beta, double delta) {		
		double alphaMin = -175.0,
		alphaMax = 80.0,
		betaMin = -58.0,
		betaMax = 60.0,
		deltaMin = -50.0,
		deltaMax = 245.0;
		
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
		
		return rotateMotorsByDeg(alpha, beta, delta);
	}
	
	public int[] rotateMotorsByDeg(double alpha, double beta, double delta) {
		//need check for dangerous angles!!!!
		//put here!
		int[] anglesToEngine = jointDegreesToMotorDegrees(alpha, beta, delta);
		adjustSpeedForDistance(alpha, beta, delta);
		connection.rotateMotorsBy(anglesToEngine[0], anglesToEngine[1], anglesToEngine[2]);
		while (connection.isMoving()) {
			continue; //poczekaj a� silniki si� zatrzymaj�
		}
		return connection.getMotorPositions();
	}
	
	public double[] getArmPosition() {
		int[] motorPositions = connection.getMotorPositions();
		double[] angles = motorDegreesToJointDegrees(motorPositions);
		return Kinematics.calculateArmPosition(angles[0], angles[1], angles[2]);
	}
	
	public double[] moveArmTo(double x, double y, double z) {
		double[] ang = getArmPosition();
		for(int j = 0; j < 1; j++) {		
			ang = Kinematics.calculateChangeMotorPoisitons(x, y, z, ang);
			//Computed angles with small error send to device
		}
		rotateMotorsToDeg(ang[0], ang[1], ang[2]);
		return getArmPosition();
	}
	
	public double[] moveArmBy(double x, double y, double z) {
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
