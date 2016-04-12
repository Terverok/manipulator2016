package source;

public class Controller_Ref {
	private Connection connection;
	
	private float przelozenieA;
	private float przelozenieB;
	private int startSpeed, startSpeedA, startSpeedB, startSpeedC;
	
	public Controller_Ref(int startSpeed) {		
		connection = new PcConnection(startSpeed);

		this.startSpeed = startSpeed;
		startSpeedA = Math.round(startSpeed * 3.5f);
		startSpeedB = Math.round(startSpeed * 0.45f);
		startSpeedC = Math.round(startSpeed * 0.1f);
		przelozenieA = 560 / 16.f;
		przelozenieB = 5.f;
		
		connection.setSpeed(startSpeedA, startSpeedB, startSpeedC);
	}
	
	public Controller_Ref() {		
		connection = new PcConnection(startSpeed);
		
		this.startSpeed = 200;
		startSpeedA = Math.round(startSpeed * 3.5f);
		startSpeedB = Math.round(startSpeed * 0.45f);
		startSpeedC = Math.round(startSpeed * 0.1f);
		przelozenieA = 560 / 16.f;
		przelozenieB = 5.f;
		
		connection.setSpeed(startSpeedA, startSpeedB, startSpeedC);
	}
	
	public void adjustSpeedForDistance(float alpha, float beta, float delta) {
		float maxValue = alpha;
		
		if (beta > maxValue) {
			maxValue = beta;
		}
		if (delta > maxValue) {
			maxValue = delta;
		}
		
		alpha /= maxValue;
		beta /= maxValue;
		delta /= maxValue;
		
		if (delta < 9.0f / 200.0f) {
			float adjust = (9.0f / 200.0f) / delta;
			alpha *= adjust;
			beta *= adjust;
			delta = 9.0f / 200.0f;
		}													
		connection.setSpeed(Math.round(startSpeedA * alpha), Math.round(startSpeedB * beta), Math.round(startSpeedC * delta));
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
	
	public double[] moveArmTo(float x, float y, float z) {
		return connection.moveArmTo(x, y, z);
	}
	
	public float[] moveArmBy(float x, float y, float z) {
		return connection.moveArmBy(x, y, z);
	}
}
