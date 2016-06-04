import java.util.Scanner;

import source.adapter.Adapter2dTo3d;
import source.adapter.WhiteBoardAdapter;
import source.driver.Controller;

public class AdapterTest {
	public static void main(String[] args) {
		Controller.getInstance().reset();
		try{
			Scanner scanner = new Scanner(System.in);
			Adapter2dTo3d adapter = new WhiteBoardAdapter(Controller.getInstance());
			adapter.moveTo(0, 0);
			adapter.penDown();
			adapter.penUp();
			adapter.penDown();
			adapter.moveTo(0, 0);
			double x, y;
			x = 0;
			y = 0;
			//draws triangle
			for(int i = 0; i < 3; i++) {
				adapter.moveTo(0, 10);
				adapter.moveTo(10, 10);
				adapter.moveTo(10, 20);
				adapter.moveTo(10, 30);
				adapter.moveTo(0, 10);
			}
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
