import java.util.Scanner;

import source.driver.Controller;

public class MinimumDistanceTest {
	public static void main(String[] args) {
		Controller controller = Controller.getInstance();
		float distance;
		float direction = 1;
		int[] motorPositions;
		try (Scanner scanner = new Scanner(System.in)){
			controller.reset();
			do {
				distance = scanner.nextInt();
				controller.setSpeed(0, 0, 45);
				controller.rotateJointsByDeg(0, 0, direction * distance);
				direction *= (-1);
				motorPositions = controller.getMotorPositions();
				System.out.println(motorPositions[0] + " " + motorPositions[1] + " " + motorPositions[2]);
			} while (distance != 0);
			
		} catch (Exception e){
			e.printStackTrace();
		} finally {
			controller.reset();
		}
	}
}
