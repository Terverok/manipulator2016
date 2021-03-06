import java.util.Scanner;

import source.driver.Controller;

public class MinimumSpeedTest {
	public static void main(String[] args) {
		Controller controller = Controller.getInstance();
		try (Scanner scanner = new Scanner(System.in)){
			controller.reset();			
			int speed;
			float distance = 180;
			do {
				speed = scanner.nextInt();
				controller.setSpeed(0, 0, speed);
				controller.rotateJointsByDeg(0, 0, distance);
				distance = distance * (-1);
			} while (speed != 0);
			
		} catch (Exception e){
			e.printStackTrace();
		} finally {
			controller.reset();
		}
	}
}
