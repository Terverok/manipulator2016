//Marek Tkaczyk
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import lejos.nxt.Button;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.addon.AccelHTSensor;
import source.*;

class MyWindow extends JFrame implements KeyListener{
	private static final long serialVersionUID = 1L;
	long lasttime;
	float time;
	float dt;
	int[] points;
	Controller con;
	boolean end;
	float[] angles;
	double[] pos;
	AccelHTSensor tilt;
	
	public MyWindow(int[] p) {
		super("Manipulator");
		addKeyListener(this);
		angles = new float [3];
		for (int i = 0; i < 3; i++) angles[i] = 0.0f;
		tilt = new AccelHTSensor(SensorPort.S1);
		
		time = 0.0f;
		dt = 0.1f;
		lasttime = System.nanoTime();
		
		con = new Controller();
		points = p;
		end = false;
		pos = con.reverseCorrectDegrees(con.getMotorPositions());
	}
	
	void AutoCalibration() throws InterruptedException{
		float xoff = 10;//-113;
		float yoff = -200;//-154;
		
		float error = Math.abs(tilt.getYAccel() + yoff + tilt.getXAccel() + xoff);
		System.out.println("Calibration process! input error:"+error);
		while(error > 2 && !end) {
			
			if (tilt.getXAccel() > -xoff) con.rotateMotorsByDeg(0, -1.f-0.2*error, 0);
			else con.rotateMotorsByDeg(0, 1.f+0.2*error, 0);
			
			error = Math.abs(tilt.getYAccel() + yoff + tilt.getXAccel() + xoff);
			
			System.out.println(""+error);
			Thread.sleep(100);
			
		}
		
		con.rotateMotorsByDeg(0.f, 55.f, 0.f);
	}
	
	public void mainloop() {
				
	
		try{
		
		AutoCalibration();
			
		pos = con.reverseCorrectDegrees(con.getMotorPositions());
		double[] start = con.getArmPosition();
		double[] mov = {0, 0, 0};
		double[] vec = {0 , 0, 0} ;
		
		Thread.sleep(100);
		
		con.rotateMotorsByDeg(0.f, 0.f, 0.f);		
		
		Thread.sleep(100);
		
//		while(!end) {
//			con.rotateMotorsToDeg(angles[0], angles[1], angles[2]);
//		}
		System.out.println("exit");
		while(!end) {
			con.moveArmTo(start[0]+mov[0], (float)start[1]+mov[1], start[2]+mov[2]);
			mov[0] = angles[0];
			mov[1] = angles[1];
			mov[2] = angles[2];
			pos = con.reverseCorrectDegrees(con.getMotorPositions());
			
//			System.out.println("\n" + pos[0] + " " + pos[1] + " " + pos[2]);
			
//			vec = con.getArmPosition();
//			System.out.println("3D pos:" + vec[0] + " " + vec[1] + " " + vec[2]);
			
//			System.out.println("tilt x:" + tilt.getXAccel() + " y:" + tilt.getYAccel() + " z:" + tilt.getZAccel());
//			System.out.println("Length: " + Math.sqrt(vec[0]*vec[0] + vec[1]*vec[1] + vec[2]*vec[2]));
			repaint();
			Thread.sleep(100);
		}
		
		}
		catch (Exception e)
		{
			con.reset();
		}
		
		
		con.reset();
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		g.clearRect(0, 0, 800, 600);
		
		double[] zeros = {0.d, 0.d, 0.d, 1.d};
				
		RealVector[] v = new RealVector[5];
		
		for(int i = 0; i < v.length; i++) v[i] = MatrixUtils.createRealVector(zeros);
		
		RealMatrix t = Kinematics.RotateZ((float) Math.toRadians(pos[0]));
		t = t.multiply(Kinematics.Translate(0.f, 0.f, 0.36f));
		
		v[1] = t.operate(v[0]);
		
		t = t.multiply(Kinematics.RotateY((float) Math.toRadians(pos[1]+55.f)));
		t = t.multiply(Kinematics.Translate(0.245f, 0.f, 0.f));
		
		v[2] = t.operate(v[0]);
		
		t = t.multiply(Kinematics.RotateY((float) Math.toRadians(pos[2]-150.f)));
		t = t.multiply(Kinematics.Translate(0.195f, 0.f, 0.f));	
		
		v[3] = t.operate(v[0]);
		
		v[4] = MatrixUtils.createRealVector(Kinematics.calculateArmPosition(pos[0], pos[1], pos[2]));
		v[4] = v[4].mapMultiply(0.01f);
		
		for(int i = 0; i < v.length; i++) {
//			if (v[i].getEntry(3) < near) v[i] = v[i].mapDivide(near +0.1f);
//			else v[i] = v[i].mapDivide(v[i].getEntry(3));/=*/
			
			points[2*i] = (int)Math.round(-v[i].getEntry(0) * this.getWidth()/2)+(this.getWidth()/2);
			points[2*i+1] = (int)Math.round(-v[i].getEntry(2) * this.getHeight()/2)+(this.getHeight()/2);
			//System.out.println("x:" + v[i].getEntry(0) + " y:" + v[i].getEntry(1) + " z:" + v[i].getEntry(2));
		}
		
		for(int i = 0; i < points.length-2; i+=2) {
			if (i % 3 == 0) g.setColor(Color.BLUE);
			else g.setColor(Color.RED);
			g.drawLine(points[i], points[i+1], points[i+2], points[i+3]);
		}

	}


	@Override
	public void keyPressed(KeyEvent e) {
		dt = (System.nanoTime() - this.lasttime )/1000000000.0f;
		float speed = 4.0f;
		if (dt > .5f) dt = 1 / speed;
	
		if (e.getKeyChar() == 'p') {
			end = true;
		}
		else if (e.getKeyChar() == 'q') {
			angles[0] += speed * dt;
		}
		else if (e.getKeyChar() == 'e') {
			angles[0] -= speed * dt;
		}
		else if (e.getKeyChar() == 'w') {
			angles[1] += speed * dt;
		}
		else if (e.getKeyChar() == 's') {
			angles[1] -= speed * dt;
		}
		else if (e.getKeyChar() == 'a') {
			angles[2] += speed * dt;
		}
		else if (e.getKeyChar() == 'd') {
			angles[2] -= speed * dt;
		}
		
		
		
		System.out.println("angles:" + angles[0] + " " + angles[1] + " " + angles[2]);
		
		lasttime = System.nanoTime();
			
	}


	@Override
	public void keyReleased(KeyEvent e) {
		
		
	}


	@Override
	public void keyTyped(KeyEvent e) {
		
		
	}
	
};

public class ReverseKinematicsTest {	

	static int[] p = new int[2*5];
	
	
	public static void main(String[] args) throws InterruptedException {
			
		
		MyWindow window = new MyWindow(p);
		
		window.setSize(800, 600);	
		window.setLocationRelativeTo(null);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		window.setVisible(true);
		
		window.repaint();
		
		window.mainloop();
	
		System.exit(0);
		
	}
	

}
