import java.util.Scanner;

import source.adapter.ControlAdapter2dTo3d;
import source.adapter.WhiteBoardAdapter;
import source.driver.Controller;

public class AdapterTest {
	public static void main(String[] args) {
		Controller.getInstance().reset();
		try{
			Scanner scanner = new Scanner(System.in);
			ControlAdapter2dTo3d adapter = new WhiteBoardAdapter(Controller.getInstance());
			adapter.moveTo(0, 0);
			adapter.penDown();
			adapter.penUp();
			adapter.penDown();
			double x, y;
			do{
				x = scanner.nextDouble();
				y = scanner.nextDouble();
				adapter.moveBy(x, y);
			} while (x !=0 || y!=0);
			adapter.penUp();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			Controller.getInstance().reset();
		}
	}
}
