import java.util.Scanner;

import source.Controller;

public class MinimumSpeedTest {
	public static void main(String[] args) {
		Controller controller = new Controller();
		try (Scanner scanner = new Scanner(System.in)){
			controller.reset();			
			int speed;
			float distance = 180;
			do {
				speed = scanner.nextInt();
				controller.setSpeed(0, 0, speed);
				controller.rotateMotorsByDeg(0, 0, distance);
				distance = distance * (-1);
			} while (speed != 0);
			
		} catch (Exception e){
			e.printStackTrace();
		} finally {
			controller.reset();
		}
	}
}
