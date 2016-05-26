import java.util.Scanner;

import source.driver.Controller;

public class SpeedAndjustmentTest {
	public static void main(String[] args) {
		Controller controller  = Controller.getInstance();
		try (Scanner scanner = new Scanner(System.in);){
//			double[] pos = controller.reverseCorrectDegrees(controller.getMotorPositions());
//			System.out.println(pos[0] + " " + pos[1] + " " + pos[2]);
			Float alpha, beta, delta;
			do {
				alpha = scanner.nextFloat();
				beta = scanner.nextFloat();
				delta = scanner.nextFloat();
				controller.rotateJointsByDeg(alpha, beta, delta);
//				System.out.println(pos[0] + " " + pos[1] + " " + pos[2]);
			} while (alpha != 0.0f || beta != 0.0f || delta != 0.0f);
			
		} catch (Exception e){
			e.printStackTrace();
		} finally {
			controller.reset();
//			System.out.println(pos[0] + " " + pos[1] + " " + pos[2]);
		}
	}
}
