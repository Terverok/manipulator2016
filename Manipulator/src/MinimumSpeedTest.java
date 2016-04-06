import java.util.Scanner;

import source.Controller;

public class MinimumSpeedTest {
	public static void main(String[] args) {
		try{
			Controller.reset();
			Scanner scanner = new Scanner(System.in);
			
			int speed;
			float distance = 180;
			do {
				speed = scanner.nextInt();
				Controller.setSpeed(0, 0, speed);
				Controller.rotateMotorsByDeg(0, 0, distance);
				distance = distance * (-1);
			} while (speed != 0);
			
		} catch (Exception e){
			e.printStackTrace();
		} finally {
			Controller.reset();
		}
	}
}
