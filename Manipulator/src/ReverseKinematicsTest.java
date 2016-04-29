//Marek Tkaczyk
import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JFrame;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import lejos.nxt.Button;
import lejos.nxt.Motor;
import source.*;

class MyWindow extends JFrame {
	private static final long serialVersionUID = 1L;
	int[] points;
	
	public MyWindow(int[] p) {
		super("Manipulator");
		points = p;
	}
	
	@Override
	public void paint(Graphics g) {
//		super.paint(g);
//		g.clearRect(0, 0, 800, 600);
//		
//		double[] zeros = {0.d, 0.d, 0.d, 1.d};
//		float[] pos = Controller.ReverseCorrectDegrees(Controller.getMotorPositions());
//		
//		RealVector[] v = new RealVector[5];
//		
//		for(int i = 0; i < v.length; i++) v[i] = MatrixUtils.createRealVector(zeros);
//		
//		RealMatrix t = Kinematics.RotateZ((float) Math.toRadians(pos[0]));
//		t = t.multiply(Kinematics.Translate(0.f, 0.f, 0.36f));
//		
//		v[1] = t.operate(v[0]);
//		
//		t = t.multiply(Kinematics.RotateY((float) Math.toRadians(pos[1])));
//		t = t.multiply(Kinematics.Translate(0.245f, 0.f, 0.f));
//		
//		v[2] = t.operate(v[0]);
//		
//		t = t.multiply(Kinematics.RotateY((float) Math.toRadians(pos[2]-90.f)));
//		t = t.multiply(Kinematics.Translate(0.195f, 0.f, 0.f));	
//		
//		v[3] = t.operate(v[0]);
//		
//		v[4] = MatrixUtils.createRealVector(Kinematics.calculateArmPosition(pos[0], pos[1], pos[2]));
//		v[4] = v[4].mapMultiply(0.01f);
//		
////		for(int i = 0; i < v.length; i++) {
////			if (v[i].getEntry(3) < near) v[i] = v[i].mapDivide(near +0.1f);
////			else v[i] = v[i].mapDivide(v[i].getEntry(3));/=*/
////			
////			points[2*i] = (int)Math.round(-v[i].getEntry(0) * this.getWidth()/2)+(this.getWidth()/2);
////			points[2*i+1] = (int)Math.round(-v[i].getEntry(2) * this.getHeight()/2)+(this.getHeight()/2);
////			//System.out.println("x:" + v[i].getEntry(0) + " y:" + v[i].getEntry(1) + " z:" + v[i].getEntry(2));
////		}
////		
////		Source.draw(g, points);
////		try {
////			Thread.sleep(100);
////		} catch (InterruptedException e) {
////			// TODO Auto-generated catch block
////			e.printStackTrace();
////		}
	}
	
};

public class ReverseKinematicsTest {	

	static int[] p = new int[2*5];
	
	
	public static void main(String[] args) throws InterruptedException {
			
		
		JFrame window = new MyWindow(p);
		
		window.setSize(800, 600);	
		window.setLocationRelativeTo(null);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		window.setVisible(true);
		
		window.repaint();
		/*float near = 0.1f;
		float right = window.getWidth()/2;
		float left = -right;
		float top = window.getHeight()/2;
		float botton = -top;
		float far = 100.f;
		double[][] PerspectiveMatdata = {
				{2 * near / (right - left), 0.d, (right + left) / (right - left), 0.d},
				{0.d, 2 * near / (top - botton), (top + botton) / (top - botton), 0.d},
				{0.d, 0.d, (-far - near) / (far - near), -2 * far * near / (far - near)},
				{0.d, 0.d, -1.d, 0.d}
		};
		RealMatrix MatrixPers = MatrixUtils.createRealMatrix(PerspectiveMatdata);*/
		Controller controller = new Controller();
		try{
		
		float[] pos = Controller.reverseCorrectDegrees(controller.getMotorPositions());
		double[] vec = controller.getArmPosition();
		
		Thread.sleep(100);
		
		controller.rotateMotorsByDeg(0.f, 0.f, 0.f);		
		
		Thread.sleep(100);
		vec[0] = 33.f;
		vec[1] = -20.f;
		vec[2] = 36.f;
		
		while(!Button.ENTER.isDown()) {
			//pos = cont.ReverseCorrectDegrees(cont.rotateMotorsByDeg(0.f, 1, 0.f));
			
			//System.out.println("\n" + pos[0] + " " + pos[1] + " " + pos[2]);
			controller.moveArmTo(33.f, (float)vec[1]+3.5f, 36.f);
			
			vec = controller.getArmPosition();
			System.out.println("3D pos:" + vec[0] + " " + vec[1] + " " + vec[2]);
			System.out.println("Length: " + Math.sqrt(vec[0]*vec[0] + vec[1]*vec[1] + vec[2]*vec[2]));
			
			window.repaint();
			Thread.sleep(100);
		}
		
		}
		catch (Exception e)
		{
			controller.reset();
		}
		/*vec = cont.getArmPosition();
		System.out.println("Pozycja startowa:");
		System.out.println(vec[0] + " " + vec[1] + " " + vec[2]);
		
		cont.rotateMotorsByDeg(-30.0f, 20.f, 40.f);
		
		System.out.println("Pozycja przesunieta:");
		vec = cont.getArmPosition();
		System.out.println(vec[0] + " " + vec[1] + " " + vec[2]);
		
		cont.reset();
		
		System.out.println("Szybki test kinematyki odwrotnej:");
		cont.moveArmTo((float)vec[0], (float)vec[1], (float)vec[2]);
		
		System.out.println("Pozycja przesunieta:");
		vec = cont.getArmPosition();
		System.out.println(vec[0] + " " + vec[1] + " " + vec[2]);*/
		
		controller.reset();
		System.exit(0);
		
	}
	
	public static void draw(Graphics g, int[] points) {
		for(int i = 0; i < points.length-2; i+=2) {
			if (i % 3 == 0) g.setColor(Color.BLUE);
			else g.setColor(Color.RED);
			g.drawLine(points[i], points[i+1], points[i+2], points[i+3]);
		}
		
	}
	

}
