package source;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import lejos.nxt.Motor;

public class Controller {
	static int[] startingPosition;
	float przelozenieA, przelozenieB, przelozenieC;
	
	public Controller() {
		// TODO Auto-generated constructor stub
		startingPosition = getMotorPositions();
		//Sets starting speeds - added by Marek
		int startSpeed = 200;
		przelozenieA = 560/16.f;
		przelozenieB = 5.f;
		przelozenieC = 1.f;
		Motor.A.setSpeed(Math.round(startSpeed*2.2f)); //for A to rotate faster
		Motor.B.setSpeed(Math.round(startSpeed*0.5f));//0.04
		Motor.C.setSpeed(Math.round(startSpeed*0.25f));//0.05
	}
	
	public boolean isMoving(){
		return Motor.A.isMoving() || Motor.B.isMoving() || Motor.C.isMoving();
	}
	
	public int[] CorrectDegrees(float[] angles) {//Added by Marek
		int[] correct = {
				Math.round(angles[0] * 560/16.f),
				Math.round(angles[1] * 5.f),
				Math.round(angles[2])
		};
		return correct;
	}
	
	public int[] CorrectDegrees(float alpha, float beta, float delta) {//Added by Marek
		int[] correct = {
				Math.round(alpha * 560/16.f),
				Math.round(beta * 5.f),
				Math.round(delta)
		};
		return correct;
	}
	
	public static float[] ReverseCorrectDegrees(int[] angles) {//Added by Marek
		float[] rev = {
				angles[0] * 16/560.f,
				angles[1] * 1/5.f,
				angles[2]
		};
		return rev;
	}
	
	public static int[] getMotorPositions(){
		int[] positions = {Motor.A.getTachoCount(),
				Motor.B.getTachoCount(),
				Motor.C.getTachoCount()};
		return positions;
	}
	
	public int[] rotateMotorsTo(int alfa, int beta, int delta){
		Motor.A.rotateTo(alfa, true);
		Motor.B.rotateTo(beta, true);
		Motor.C.rotateTo(delta, true);
		while (isMoving()) {
			try{
				Thread.sleep(20);
				}
			catch(Exception e){
				e.printStackTrace();
				}
		} //poczekaj a� silniki si� zatrzymaj�
		return getMotorPositions();
	}
	
	public int[] rotateMotorsToDeg(float alpha, float beta, float delta){ //Added by Marek, corrected for degrees
		//zabezpieczenia prze groźnymi kątami
		float alphaMin = -175.f;
		if (alpha < alphaMin) alpha = -175.f;
		else if (alpha > 80.f) alpha = 80.f;
		
		if (beta < -58.f) beta = -58.f;
		else if (beta > 60.f) beta = 60.f;
		
		if (delta < -50.f) delta = -50.f;
		else if (delta > 245.f) delta = 245.f;
		
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
		while (isMoving()) {} //poczekaj a� silniki si� zatrzymaj�
		return getMotorPositions();
	}
	
	public int[] rotateMotorsByDeg(float alpha, float beta, float delta){ //Added by Marek, corrected for degrees
		//need check for dangerous angles!!!!
		//put here!
		int[] anglesToEngine = CorrectDegrees(alpha, beta, delta);
		Motor.A.rotate(anglesToEngine[0], true);
		Motor.B.rotate(anglesToEngine[1], true);
		Motor.C.rotate(anglesToEngine[2], true);
		while (isMoving()) continue; //poczekaj a� silniki si� zatrzymaj�
		return getMotorPositions();
	}
	
	public static double[] getArmPosition(){
		int[] motorPositions = getMotorPositions();
		float[] angles = ReverseCorrectDegrees(motorPositions);
		return Kinematics.calculateArmPosition(angles[0], angles[1], angles[2]);
	}
	
	public double[] moveArmTo(float x, float y, float z){
				
		for(int j = 0; j < 1; j++) {
		
			float[] ang = Kinematics.calculatechangeMotorPoisitons(x, y, z);
			//Computed angles with small error send to device
			rotateMotorsToDeg(ang[0], ang[1], ang[2]);
		}
		
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
