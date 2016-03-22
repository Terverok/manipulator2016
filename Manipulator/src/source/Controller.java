package source;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import lejos.nxt.Motor;

public class Controller {
	static int[] startingPosition;
	static float przelozenieA;
	static float przelozenieB;
	
	static{
		// TODO Auto-generated constructor stub
		startingPosition = getMotorPositions();
		//Sets starting speeds - added by Marek
		int startSpeed = 200;
		przelozenieA = 560/16.f;
		przelozenieB = 5.f;
		Motor.A.setSpeed(Math.round(startSpeed*2.2f)); //for A to rotate faster
		Motor.B.setSpeed(Math.round(startSpeed*0.5f));//0.04
		Motor.C.setSpeed(Math.round(startSpeed*0.25f));//0.05
		Motor.A.flt(true);
		Motor.B.flt(true);
		Motor.C.flt(true);
	}	
	
	public static boolean isMoving(){
		return Motor.A.isMoving() || Motor.B.isMoving() || Motor.C.isMoving();
	}
	
	public static int[] CorrectDegrees(float[] angles) {//Added by Marek
		int[] correct = {
				Math.round(angles[0] * przelozenieA),
				Math.round(angles[1] * przelozenieB),
				Math.round(angles[2])
		};
		return correct;
	}
	
	public static int[] CorrectDegrees(float alpha, float beta, float delta) {//Added by Marek
		int[] correct = {
				Math.round(alpha * przelozenieA),
				Math.round(beta * przelozenieB),
				Math.round(delta)
		};
		return correct;
	}
	
	public static float[] ReverseCorrectDegrees(int[] angles) {//Added by Marek
		float[] rev = {
				angles[0] / przelozenieA,
				angles[1] / przelozenieB,
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
	
	public static int[] rotateMotorsTo(int alfa, int beta, int delta){
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
	
	public static int[] rotateMotorsToDeg(float alpha, float beta, float delta){ //Added by Marek, corrected for degrees
		//zabezpieczenia prze groźnymi kątami
		float alphaMin = -175.f,
		alphaMax = 80.f,
		betaMin = -58.f,
		betaMax = 60.f,
		deltaMin = -50.f,
		deltaMax = 245.f;
		if (alpha < alphaMin) alpha = alphaMin;
		else if (alpha > alphaMax) alpha = alphaMax;
		
		if (beta < betaMin) beta = betaMin;
		else if (beta > betaMax) beta = betaMax;
		
		if (delta < deltaMin) delta = deltaMin;
		else if (delta > deltaMax) delta = deltaMax;
		
		int[] anglesToEngine = CorrectDegrees(alpha, beta, delta);
		Motor.A.rotateTo(anglesToEngine[0], true);
		Motor.B.rotateTo(anglesToEngine[1], true);
		Motor.C.rotateTo(anglesToEngine[2], true);
		while (isMoving()) continue; //poczekaj a� silniki si� zatrzymaj�
		return getMotorPositions();
	}
	
	public static int[] rotateMotorsBy(int alfa, int beta, int delta){
		Motor.A.rotate(alfa, true);
		Motor.B.rotate(beta, true);
		Motor.C.rotate(delta, true);
		while (isMoving()) {} //poczekaj a� silniki si� zatrzymaj�
		return getMotorPositions();
	}
	
	public static int[] rotateMotorsByDeg(float alpha, float beta, float delta){ //Added by Marek, corrected for degrees
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
	
	public static double[] moveArmTo(float x, float y, float z){
				
		for(int j = 0; j < 1; j++) {
		
			float[] ang = Kinematics.calculatechangeMotorPoisitons(x, y, z);
			//Computed angles with small error send to device
			rotateMotorsToDeg(ang[0], ang[1], ang[2]);
		}	
		return getArmPosition();
	}
	
	public static float[] moveArmBy(float x, float y, float z){
		// TODO
		return null;
	}
	
	public static int[] reset(){
		return rotateMotorsTo(startingPosition[0], startingPosition[1], startingPosition[2]);
	}
}
