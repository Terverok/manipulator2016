package source.driver;

public interface Connection {
	void setSpeed(int a, int b, int c);
	boolean isMoving();
	int[] getMotorPositions();
	int[] rotateMotorsTo(int alfa, int beta, int delta);
	int[] rotateMotorsBy(int alfa, int beta, int delta);
	int[] getTilt();
	int[] reset();
}