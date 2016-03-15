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
		super.paint(g);
		g.clearRect(0, 0, 800, 600);
		Source.draw(g, points);
	}
	
};

public class Source {	

	static int[] p = new int[2*4];
	
	
	public static void main(String[] args) throws InterruptedException {
		//Controller cont = new Controller();
		
		
		JFrame window = new MyWindow(p);
		
		window.setSize(800, 600);	
		window.setLocationRelativeTo(null);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		window.setVisible(true);
		
		double[] zeros = {0.d, 0.d, 0.d, 1.d};
		
		RealVector[] v = new RealVector[4];
		
		for(int i = 0; i < v.length; i++) v[i] = MatrixUtils.createRealVector(zeros);
				
		float alpha = 0.f*Kinematics.TachometrPerRadian;
		float beta = 0.f*Kinematics.TachometrPerRadian;
		float gamma = 0.f*Kinematics.TachometrPerRadian;
		
		float near = 0.1f;
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
		RealMatrix MatrixPers = MatrixUtils.createRealMatrix(PerspectiveMatdata);
		
		while(true) {
		alpha += 15.f*Kinematics.TachometrPerRadian;
		beta += 15f*Kinematics.TachometrPerRadian;
		//gamma += 5f*Kinematics.TachometrPerRadian;
		
		RealMatrix t = Kinematics.RotateX(0.35f).multiply(Kinematics.RotateY(0.24f));
		t = t.multiply(Kinematics.RotateZ(alpha));
		t = t.multiply(Kinematics.Translate(0.f, 0.f, 3*0.36f));
		
		v[1] = t.multiply(MatrixPers).operate(v[0]);
		
		t = t.multiply(Kinematics.RotateY(beta));
		t = t.multiply(Kinematics.Translate(3*0.245f, 0.f, 0.f));
		
		v[2] = t.multiply(MatrixPers).operate(v[0]);
		
		t = t.multiply(Kinematics.RotateY(gamma));
		t = t.multiply(Kinematics.Translate(3*0.195f, 0.f, 0.f));	
		
		v[3] = t.multiply(MatrixPers).operate(v[0]);
		
		for(int i = 0; i < v.length; i++) {
			if (v[i].getEntry(3) < near) v[i] = v[i].mapDivide(near +0.1f);
			else v[i] = v[i].mapDivide(v[i].getEntry(3));
			
			p[2*i] = (int)Math.round(v[i].getEntry(0)/v[i].getEntry(2) * window.getWidth()/2)+(window.getWidth()/2);
			p[2*i+1] = (int)Math.round(v[i].getEntry(1)/v[i].getEntry(2) * window.getHeight()/2)+(window.getHeight()/2);
			System.out.println(v[i].getEntry(0));
			System.out.println(v[i].getEntry(1));
			System.out.println(v[i].getEntry(2));
		}
		window.repaint();
		Thread.sleep(1500);
		}
		//double[] data = Kinematics.calculateArmPosition(5, 3, 8);
		//System.out.print(data[0] +" "+ data[1] + " "+ data[2]);
		//int[] pos = cont.getMotorPositions();
		//cont.rotateMotorsBy(2000, 0, -2);
		//cont.rotateMotorsBy(0, -10, 0);
		//System.out.println(pos[0] + " " + pos[1] + " " + pos[2]);
		//double[] vec = Kinematics.calculateArmPosition(pos[0], pos[1], pos[2]);
		//System.out.println(vec[0] + " " + vec[1] + " " + vec[2]);
		/*
		//pos = cont.rotateMotorsBy(1000, 500, 0);
		pos = cont.rotateMotorsByDeg(-90, 90, 0);
		
		System.out.println(pos[0] + " " + pos[1] + " " + pos[2]);
		vec = Kinematics.calculateArmPosition(pos[0], pos[1], pos[2]);
		System.out.println(vec[0] + " " + vec[1] + " " + vec[2]);
		
		//pos = cont.rotateMotorsBy(1000, 220, 0);
		pos = cont.rotateMotorsByDeg(0, 0, 100);
		
		System.out.println(pos[0] + " " + pos[1] + " " + pos[2]);
		vec = Kinematics.calculateArmPosition(pos[0], pos[1], pos[2]);
		System.out.println(vec[0] + " " + vec[1] + " " + vec[2]);*/
		
		/*while(!Button.ENTER.isDown()) {
			pos = cont.rotateMotorsByDeg(0, 0, 1);
			
			System.out.println(pos[0] + " " + pos[1] + " " + pos[2]);
			vec = cont.getArmPosition();
			System.out.println(vec[0] + " " + vec[1] + " " + vec[2]);
			System.out.println("Length: " + Math.sqrt(vec[0]*vec[0] + vec[1]*vec[1] + vec[2]*vec[2]));
		}*/
		
		//cont.moveArmTo(23.f, 5.f, 36.f);
		
		//cont.reset();
		
		
		
	}
	
	public static void draw(Graphics g, int[] points) {
		for(int i = 0; i < points.length-2; i+=2) {
			if (i % 3 == 0) g.setColor(Color.BLUE);
			else g.setColor(Color.RED);
			g.drawLine(points[i], points[i+1], points[i+2], points[i+3]);
		}
		
	}
	

}
