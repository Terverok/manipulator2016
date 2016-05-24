package source;

public class Controller {
	private Connection connection;
	
	private static final double przelozenieA = 11.2;
	private static final double przelozenieB = 5.0;
	private final int startSpeed = 45, startSpeedA, startSpeedB, startSpeedC;
	private Kinematics kinematics;
	
	
	public Controller() {
		
		startSpeedA = (int) Math.round(startSpeed * przelozenieA);
		startSpeedB = (int) Math.round(startSpeed * przelozenieB);
		startSpeedC = (int) Math.round(startSpeed);
		
		System.out.println(startSpeedA + " " + startSpeedB + " " + startSpeedC);
		connection = new PcConnection(startSpeedA, startSpeedB, startSpeedC);
		kinematics = new Kinematics(this);
	}
	
	public void adjustSpeedForDistance(double alpha, double beta, double delta) {
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
	
	static public int[] correctDegrees(double[] angles) {
		int[] correct = {
				(int) Math.round(angles[0] * przelozenieA),
				(int) Math.round(angles[1] * przelozenieB),
				(int) Math.round(angles[2])
		};
		return correct;
	}
	
	static public int[] correctDegrees(double alpha, double beta, double delta) {
		int[] correct = {
				(int) Math.round(alpha * przelozenieA),
				(int) Math.round(beta * przelozenieB),
				(int) Math.round(delta)
		};
		return correct;
	}
	
	static public double[] reverseCorrectDegrees(int[] angles) {
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
		
		int[] anglesToEngine = correctDegrees(alpha, beta, delta);
		connection.rotateMotorsTo(anglesToEngine[0], anglesToEngine[1], anglesToEngine[2]);
		
		while (connection.isMoving()) {
			continue; //poczekaj a� silniki si� zatrzymaj�
		}
		return connection.getMotorPositions();
	}
	
	public int[] rotateMotorsByDeg(double alpha, double beta, double delta) {
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
		double[] angles = reverseCorrectDegrees(motorPositions);
		return kinematics.calculateArmPosition(angles[0], angles[1], angles[2]);
	}
	
	public double[] moveArmTo(double x, double y, double z) {		
		for(int j = 0; j < 1; j++) {		
			double[] ang = kinematics.calculatechangeMotorPoisitons(x, y, z);
			//Computed angles with small error send to device
			rotateMotorsToDeg(ang[0], ang[1], ang[2]);
		}	
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
package source;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import lejos.nxt.Motor;

public class Controller {
	static int[] startingPosition;
	static float przelozenieA;
	static float przelozenieB;
	static int startSpeed, startSpeedA, startSpeedB, startSpeedC;
	
	static{
		startingPosition = getMotorPositions();
		//Sets starting speeds - added by Marek
		startSpeed = 200;
		startSpeedA = Math.round(startSpeed*3.5f);
		startSpeedB = Math.round(startSpeed*0.45f);
		startSpeedC = Math.round(startSpeed*0.1f);
		przelozenieA = 560/16.f;
		przelozenieB = 5.f;
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
	
	public static void setSpeed(int a, int b, int c){
		Motor.A.setSpeed(a);
		Motor.B.setSpeed(b);
		Motor.C.setSpeed(c);
	}
	
	public static void adjustSpeedForDistance(float alpha, float beta, float delta){
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
		if (delta < 9.0f/20.0f) {
			float adjust = (9.0f/20.0f) / delta;
			alpha *= adjust;
			beta *=adjust;
			delta = 9.0f/20.0f;
		}													
		setSpeed(Math.round(startSpeedA*alpha), Math.round(startSpeedB*beta), Math.round(startSpeedC*delta));
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
		setSpeed(startSpeedA, startSpeedB, startSpeedC);
		return rotateMotorsTo(startingPosition[0], startingPosition[1], startingPosition[2]);
	}
}
package source;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import lejos.nxt.Motor;

public class Controller {
	static int[] startingPosition;
	static float przelozenieA;
	static float przelozenieB;
	static int startSpeed, startSpeedA, startSpeedB, startSpeedC;
	
	static{
		startingPosition = getMotorPositions();
		//Sets starting speeds - added by Marek
		startSpeed = 200;
		startSpeedA = Math.round(startSpeed*3.5f);
		startSpeedB = Math.round(startSpeed*0.45f);
		startSpeedC = Math.round(startSpeed*0.1f);
		przelozenieA = 560/16.f;
		przelozenieB = 5.f;
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
	
	public static void setSpeed(int a, int b, int c){
		Motor.A.setSpeed(a);
		Motor.B.setSpeed(b);
		Motor.C.setSpeed(c);
	}
	
	public static void adjustSpeedForDistance(float alpha, float beta, float delta){
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
		if (delta < 9.0f/20.0f) {
			float adjust = (9.0f/20.0f) / delta;
			alpha *= adjust;
			beta *=adjust;
			delta = 9.0f/20.0f;
		}													
		setSpeed(Math.round(startSpeedA*alpha), Math.round(startSpeedB*beta), Math.round(startSpeedC*delta));
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
		setSpeed(startSpeedA, startSpeedB, startSpeedC);
		return rotateMotorsTo(startingPosition[0], startingPosition[1], startingPosition[2]);
	}
}
package source;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import lejos.nxt.Motor;

public class Controller {
	static int[] startingPosition;
	static float przelozenieA;
	static float przelozenieB;
	static int startSpeed, startSpeedA, startSpeedB, startSpeedC;
	
	static{
		startingPosition = getMotorPositions();
		//Sets starting speeds - added by Marek
		startSpeed = 200;
		startSpeedA = Math.round(startSpeed*3.5f);
		startSpeedB = Math.round(startSpeed*0.45f);
		startSpeedC = Math.round(startSpeed*0.1f);
		przelozenieA = 560/16.f;
		przelozenieB = 5.f;
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
	
	public static void setSpeed(int a, int b, int c){
		Motor.A.setSpeed(a);
		Motor.B.setSpeed(b);
		Motor.C.setSpeed(c);
	}
	
	public static void adjustSpeedForDistance(float alpha, float beta, float delta){
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
		if (delta < 9.0f/20.0f) {
			float adjust = (9.0f/20.0f) / delta;
			alpha *= adjust;
			beta *=adjust;
			delta = 9.0f/20.0f;
		}													
		setSpeed(Math.round(startSpeedA*alpha), Math.round(startSpeedB*beta), Math.round(startSpeedC*delta));
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
		setSpeed(startSpeedA, startSpeedB, startSpeedC);
		return rotateMotorsTo(startingPosition[0], startingPosition[1], startingPosition[2]);
	}
}
package source;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import lejos.nxt.Motor;

public class Controller {
	static int[] startingPosition;
	static float przelozenieA;
	static float przelozenieB;
	static int startSpeed, startSpeedA, startSpeedB, startSpeedC;
	
	static{
		startingPosition = getMotorPositions();
		//Sets starting speeds - added by Marek
		startSpeed = 200;
		startSpeedA = Math.round(startSpeed*3.5f);
		startSpeedB = Math.round(startSpeed*0.45f);
		startSpeedC = Math.round(startSpeed*0.1f);
		przelozenieA = 560/16.f;
		przelozenieB = 5.f;
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
	
	public static void setSpeed(int a, int b, int c){
		Motor.A.setSpeed(a);
		Motor.B.setSpeed(b);
		Motor.C.setSpeed(c);
	}
	
	public static void adjustSpeedForDistance(float alpha, float beta, float delta){
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
		if (delta < 9.0f/20.0f) {
			float adjust = (9.0f/20.0f) / delta;
			alpha *= adjust;
			beta *=adjust;
			delta = 9.0f/20.0f;
		}													
		setSpeed(Math.round(startSpeedA*alpha), Math.round(startSpeedB*beta), Math.round(startSpeedC*delta));
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
		setSpeed(startSpeedA, startSpeedB, startSpeedC);
		return rotateMotorsTo(startingPosition[0], startingPosition[1], startingPosition[2]);
	}
}
package source;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import lejos.nxt.Motor;

public class Controller {
	static int[] startingPosition;
	static float przelozenieA;
	static float przelozenieB;
	static int startSpeed, startSpeedA, startSpeedB, startSpeedC;
	
	static{
		startingPosition = getMotorPositions();
		//Sets starting speeds - added by Marek
		startSpeed = 200;
		startSpeedA = Math.round(startSpeed*3.5f);
		startSpeedB = Math.round(startSpeed*0.45f);
		startSpeedC = Math.round(startSpeed*0.1f);
		przelozenieA = 560/16.f;
		przelozenieB = 5.f;
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
	
	public static void setSpeed(int a, int b, int c){
		Motor.A.setSpeed(a);
		Motor.B.setSpeed(b);
		Motor.C.setSpeed(c);
	}
	
	public static void adjustSpeedForDistance(float alpha, float beta, float delta){
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
		if (delta < 9.0f/20.0f) {
			float adjust = (9.0f/20.0f) / delta;
			alpha *= adjust;
			beta *=adjust;
			delta = 9.0f/20.0f;
		}													
		setSpeed(Math.round(startSpeedA*alpha), Math.round(startSpeedB*beta), Math.round(startSpeedC*delta));
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
		setSpeed(startSpeedA, startSpeedB, startSpeedC);
		return rotateMotorsTo(startingPosition[0], startingPosition[1], startingPosition[2]);
	}
}
package source;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import lejos.nxt.Motor;

public class Controller {
	static int[] startingPosition;
	static float przelozenieA;
	static float przelozenieB;
	static int startSpeed, startSpeedA, startSpeedB, startSpeedC;
	
	static{
		startingPosition = getMotorPositions();
		//Sets starting speeds - added by Marek
		startSpeed = 200;
		startSpeedA = Math.round(startSpeed*3.5f);
		startSpeedB = Math.round(startSpeed*0.45f);
		startSpeedC = Math.round(startSpeed*0.1f);
		przelozenieA = 560/16.f;
		przelozenieB = 5.f;
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
	
	public static void setSpeed(int a, int b, int c){
		Motor.A.setSpeed(a);
		Motor.B.setSpeed(b);
		Motor.C.setSpeed(c);
	}
	
	public static void adjustSpeedForDistance(float alpha, float beta, float delta){
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
		if (delta < 9.0f/20.0f) {
			float adjust = (9.0f/20.0f) / delta;
			alpha *= adjust;
			beta *=adjust;
			delta = 9.0f/20.0f;
		}													
		setSpeed(Math.round(startSpeedA*alpha), Math.round(startSpeedB*beta), Math.round(startSpeedC*delta));
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
		setSpeed(startSpeedA, startSpeedB, startSpeedC);
		return rotateMotorsTo(startingPosition[0], startingPosition[1], startingPosition[2]);
	}
}
package source;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import lejos.nxt.Motor;

public class Controller {
	static int[] startingPosition;
	static float przelozenieA;
	static float przelozenieB;
	static int startSpeed, startSpeedA, startSpeedB, startSpeedC;
	
	static{
		startingPosition = getMotorPositions();
		//Sets starting speeds - added by Marek
		startSpeed = 200;
		startSpeedA = Math.round(startSpeed*3.5f);
		startSpeedB = Math.round(startSpeed*0.45f);
		startSpeedC = Math.round(startSpeed*0.1f);
		przelozenieA = 560/16.f;
		przelozenieB = 5.f;
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
	
	public static void setSpeed(int a, int b, int c){
		Motor.A.setSpeed(a);
		Motor.B.setSpeed(b);
		Motor.C.setSpeed(c);
	}
	
	public static void adjustSpeedForDistance(float alpha, float beta, float delta){
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
		if (delta < 9.0f/20.0f) {
			float adjust = (9.0f/20.0f) / delta;
			alpha *= adjust;
			beta *=adjust;
			delta = 9.0f/20.0f;
		}													
		setSpeed(Math.round(startSpeedA*alpha), Math.round(startSpeedB*beta), Math.round(startSpeedC*delta));
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
		setSpeed(startSpeedA, startSpeedB, startSpeedC);
		return rotateMotorsTo(startingPosition[0], startingPosition[1], startingPosition[2]);
	}
}
package source;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import lejos.nxt.Motor;

public class Controller {
	static int[] startingPosition;
	static float przelozenieA;
	static float przelozenieB;
	static int startSpeed, startSpeedA, startSpeedB, startSpeedC;
	
	static{
		startingPosition = getMotorPositions();
		//Sets starting speeds - added by Marek
		startSpeed = 200;
		startSpeedA = Math.round(startSpeed*3.5f);
		startSpeedB = Math.round(startSpeed*0.45f);
		startSpeedC = Math.round(startSpeed*0.1f);
		przelozenieA = 560/16.f;
		przelozenieB = 5.f;
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
	
	public static void setSpeed(int a, int b, int c){
		Motor.A.setSpeed(a);
		Motor.B.setSpeed(b);
		Motor.C.setSpeed(c);
	}
	
	public static void adjustSpeedForDistance(float alpha, float beta, float delta){
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
		if (delta < 9.0f/20.0f) {
			float adjust = (9.0f/20.0f) / delta;
			alpha *= adjust;
			beta *=adjust;
			delta = 9.0f/20.0f;
		}													
		setSpeed(Math.round(startSpeedA*alpha), Math.round(startSpeedB*beta), Math.round(startSpeedC*delta));
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
		setSpeed(startSpeedA, startSpeedB, startSpeedC);
		return rotateMotorsTo(startingPosition[0], startingPosition[1], startingPosition[2]);
	}
}
package source;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import lejos.nxt.Motor;

public class Controller {
	static int[] startingPosition;
	static float przelozenieA;
	static float przelozenieB;
	static int startSpeed, startSpeedA, startSpeedB, startSpeedC;
	
	static{
		startingPosition = getMotorPositions();
		//Sets starting speeds - added by Marek
		startSpeed = 200;
		startSpeedA = Math.round(startSpeed*3.5f);
		startSpeedB = Math.round(startSpeed*0.45f);
		startSpeedC = Math.round(startSpeed*0.1f);
		przelozenieA = 560/16.f;
		przelozenieB = 5.f;
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
	
	public static void setSpeed(int a, int b, int c){
		Motor.A.setSpeed(a);
		Motor.B.setSpeed(b);
		Motor.C.setSpeed(c);
	}
	
	public static void adjustSpeedForDistance(float alpha, float beta, float delta){
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
		if (delta < 9.0f/20.0f) {
			float adjust = (9.0f/20.0f) / delta;
			alpha *= adjust;
			beta *=adjust;
			delta = 9.0f/20.0f;
		}													
		setSpeed(Math.round(startSpeedA*alpha), Math.round(startSpeedB*beta), Math.round(startSpeedC*delta));
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
		setSpeed(startSpeedA, startSpeedB, startSpeedC);
		return rotateMotorsTo(startingPosition[0], startingPosition[1], startingPosition[2]);
	}
}
package source;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import lejos.nxt.Motor;

public class Controller {
	static int[] startingPosition;
	static float przelozenieA;
	static float przelozenieB;
	static int startSpeed, startSpeedA, startSpeedB, startSpeedC;
	
	static{
		startingPosition = getMotorPositions();
		//Sets starting speeds - added by Marek
		startSpeed = 200;
		startSpeedA = Math.round(startSpeed*3.5f);
		startSpeedB = Math.round(startSpeed*0.45f);
		startSpeedC = Math.round(startSpeed*0.1f);
		przelozenieA = 560/16.f;
		przelozenieB = 5.f;
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
	
	public static void setSpeed(int a, int b, int c){
		Motor.A.setSpeed(a);
		Motor.B.setSpeed(b);
		Motor.C.setSpeed(c);
	}
	
	public static void adjustSpeedForDistance(float alpha, float beta, float delta){
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
		if (delta < 9.0f/20.0f) {
			float adjust = (9.0f/20.0f) / delta;
			alpha *= adjust;
			beta *=adjust;
			delta = 9.0f/20.0f;
		}													
		setSpeed(Math.round(startSpeedA*alpha), Math.round(startSpeedB*beta), Math.round(startSpeedC*delta));
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
		setSpeed(startSpeedA, startSpeedB, startSpeedC);
		return rotateMotorsTo(startingPosition[0], startingPosition[1], startingPosition[2]);
	}
}
package source;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import lejos.nxt.Motor;

public class Controller {
	static int[] startingPosition;
	static float przelozenieA;
	static float przelozenieB;
	static int startSpeed, startSpeedA, startSpeedB, startSpeedC;
	
	static{
		startingPosition = getMotorPositions();
		//Sets starting speeds - added by Marek
		startSpeed = 200;
		startSpeedA = Math.round(startSpeed*3.5f);
		startSpeedB = Math.round(startSpeed*0.45f);
		startSpeedC = Math.round(startSpeed*0.1f);
		przelozenieA = 560/16.f;
		przelozenieB = 5.f;
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
	
	public static void setSpeed(int a, int b, int c){
		Motor.A.setSpeed(a);
		Motor.B.setSpeed(b);
		Motor.C.setSpeed(c);
	}
	
	public static void adjustSpeedForDistance(float alpha, float beta, float delta){
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
		if (delta < 9.0f/20.0f) {
			float adjust = (9.0f/20.0f) / delta;
			alpha *= adjust;
			beta *=adjust;
			delta = 9.0f/20.0f;
		}													
		setSpeed(Math.round(startSpeedA*alpha), Math.round(startSpeedB*beta), Math.round(startSpeedC*delta));
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
		setSpeed(startSpeedA, startSpeedB, startSpeedC);
		return rotateMotorsTo(startingPosition[0], startingPosition[1], startingPosition[2]);
	}
}
