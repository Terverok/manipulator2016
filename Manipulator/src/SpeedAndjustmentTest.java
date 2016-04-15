import java.util.Scanner;

import source.Controller;
import source.Controller_Ref;

public class SpeedAndjustmentTest {
	public static void main(String[] args) {
		Controller_Ref controller = new Controller_Ref();
		try{
			Scanner scanner = new Scanner(System.in);
			
			
			float[] pos = controller.ReverseCorrectDegrees(Controller.getMotorPositions());
			System.out.println(pos[0] + " " + pos[1] + " " + pos[2]);
			float alpha, beta, delta;
			do {
				alpha = scanner.nextFloat();
				beta = scanner.nextFloat();
				delta = scanner.nextFloat();
				controller.adjustSpeedForDistance(alpha, beta, delta);
				controller.getConnection().rotateMotorsBy((int)alpha, (int)beta, (int)delta);
				pos = controller.ReverseCorrectDegrees(controller.getConnection().getMotorPositions());
				System.out.println(pos[0] + " " + pos[1] + " " + pos[2]);
			} while (alpha != 0.0f || beta != 0.0f || delta != 0.0f);
			
		} catch (Exception e){
			e.printStackTrace();
		} finally {
			controller.getConnection().reset();
			float[] pos = controller.ReverseCorrectDegrees(controller.getConnection().getMotorPositions());
			System.out.println(pos[0] + " " + pos[1] + " " + pos[2]);
		}
	}
}
