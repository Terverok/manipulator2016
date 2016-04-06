import java.util.Scanner;

import source.Controller;

public class Main {
	public static void main(String[] args) {
		try{
			Scanner scanner = new Scanner(System.in);
			float[] pos = Controller.ReverseCorrectDegrees(Controller.getMotorPositions());
			System.out.println(pos[0] + " " + pos[1] + " " + pos[2]);
			Float alpha, beta, delta;
			do {
				alpha = scanner.nextFloat();
				beta = scanner.nextFloat();
				delta = scanner.nextFloat();
				Controller.rotateMotorsByDeg(alpha, beta, delta);
				pos = Controller.ReverseCorrectDegrees(Controller.getMotorPositions());
				System.out.println(pos[0] + " " + pos[1] + " " + pos[2]);
			} while (alpha != 0.0f || beta != 0.0f || delta != 0.0f);
			
		} catch (Exception e){
			e.printStackTrace();
		} finally {
			Controller.reset();
			float[] pos = Controller.ReverseCorrectDegrees(Controller.getMotorPositions());
			System.out.println(pos[0] + " " + pos[1] + " " + pos[2]);
		}
	}
}
