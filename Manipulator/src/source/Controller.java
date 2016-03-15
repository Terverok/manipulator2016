package source;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import lejos.nxt.Motor;

public class Controller {
	static int[] startingPosition;
	
	public Controller() {
		// TODO Auto-generated constructor stub
		startingPosition = getMotorPositions();
		//Sets starting speeds - added by Marek
		int startSpeed = 200;
		Motor.A.setSpeed(Math.round(startSpeed*1.3f)); //for A to rotate faster
		Motor.B.setSpeed(startSpeed);
		Motor.C.setSpeed(startSpeed);
	}
	
	public boolean isMoving(){
		return Motor.A.isMoving() || Motor.B.isMoving() || Motor.C.isMoving();
	}
	
	public int[] CorrectDegrees(float[] angles) {//Added by Marek
		int[] correct = {
				Math.round(angles[0] * 560/12.f),
				Math.round(angles[1] * 5.f),
				Math.round(angles[2])
		};
		return correct;
	}
	
	public float[] ReverseCorrectDegrees(int[] angles) {//Added by Marek
		float[] rev = {
				angles[0] * 12/560.f,
				angles[1] * 1/5.f,
				angles[2]
		};
		return rev;
	}
	
	public int[] CorrectDegrees(float alpha, float beta, float delta) {//Added by Marek
		int[] correct = {
				Math.round(alpha * 560/12.f),
				Math.round(beta * 5.f),
				Math.round(delta)
		};
		return correct;
	}
	
	public int[] getMotorPositions(){
		int[] positions = {Motor.A.getTachoCount(),
				Motor.B.getTachoCount(),
				Motor.C.getTachoCount()};
		return positions;
	}
	
	public int[] rotateMotorsTo(int alfa, int beta, int delta){
		Motor.A.rotateTo(alfa, true);
		Motor.B.rotateTo(beta, true);
		Motor.C.rotateTo(delta, true);
		while (isMoving()) continue; //poczekaj a� silniki si� zatrzymaj�
		return getMotorPositions();
	}
	
	public int[] rotateMotorsToDeg(float alpha, float beta, float delta){ //Added by Marek, corrected for degrees
		int[] anglesToEngine = CorrectDegrees(alpha, beta, delta);
		Motor.A.rotateTo(anglesToEngine[0], true);
		Motor.B.rotateTo(anglesToEngine[1], true);
		Motor.C.rotateTo(anglesToEngine[2], true);
		while (isMoving()) continue; //poczekaj a� silniki si� zatrzymaj�
		return getMotorPositions();
	}
	
	public int[] rotateMotorsBy(int alfa, int beta, int delta){
		Motor.A.rotate(alfa, true);
		Motor.B.rotate(beta, true);
		Motor.C.rotate(delta, true);
		while (isMoving()) continue; //poczekaj a� silniki si� zatrzymaj�
		return getMotorPositions();
	}
	
	public int[] rotateMotorsByDeg(float alpha, float beta, float delta){ //Added by Marek, corrected for degrees
		int[] anglesToEngine = CorrectDegrees(alpha, beta, delta);
		Motor.A.rotate(anglesToEngine[0], true);
		Motor.B.rotate(anglesToEngine[1], true);
		Motor.C.rotate(anglesToEngine[2], true);
		while (isMoving()) continue; //poczekaj a� silniki si� zatrzymaj�
		return getMotorPositions();
	}
	
	public double[] getArmPosition(){
		int[] motorPositions = getMotorPositions();
		float[] angles = ReverseCorrectDegrees(motorPositions);
		return Kinematics.calculateArmPosition(angles[0], angles[1], angles[2]);
	}
	
	public double[] moveArmTo(float x, float y, float z){
		//compute inverse jacobian, so we could compute angle changes
		RealMatrix InvJac = Kinematics.calculateTransJacobian();
		double[] positionTo = {x, y, z, 1.0f};
		double[] positionNow = getArmPosition();
		float[] ang = ReverseCorrectDegrees(getMotorPositions());
		double[] dtheta = new double[3];
		
		for(int i = 0; i < 5; i++) {
			RealVector Now = MatrixUtils.createRealVector(positionNow);
			RealVector To = MatrixUtils.createRealVector(positionTo);
		
			To = To.subtract(Now);
		
			//small change of angles on manipulator
			dtheta = InvJac.operate(To).mapMultiply(0.01d).toArray();
			
			ang[0] += dtheta[0];
			ang[1] += dtheta[1];
			ang[2] += dtheta[2];
			
			System.out.println(positionNow[0] + " " + positionNow[1] + " " + positionNow[2]);
			System.out.println("Length: " + Math.sqrt(positionNow[0]*positionNow[0] + positionNow[1]*positionNow[1] + positionNow[2]*positionNow[2]));
			System.out.println("Theta: " + dtheta[0] + " " +dtheta[1] + " " + dtheta[2]);
			
			positionNow = Kinematics.calculateArmPosition(ang[0], ang[1], ang[2]);
		}
		//Computed angles with small error send to device
		rotateMotorsToDeg(ang[0], ang[1], ang[2]);
		
		return getArmPosition();
	}
	
	public float[] moveArmBy(float x, float y, float z){
		// TODO
		return null;
	}
	
	public int[] reset(){
		return rotateMotorsTo(startingPosition[0], startingPosition[1], startingPosition[2]);
	}
}
