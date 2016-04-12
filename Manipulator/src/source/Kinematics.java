package source;
import lejos.nxt.Motor;

import org.apache.commons.math3.analysis.function.Atan2;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
//Marek Tkaczyk
public class Kinematics {
	public static float TachometrPerRadian = (float) (Math.PI / 180.f);
	
	
	public static RealMatrix RotateX(float alpha) {
		double[][] RotX = {
				{1.d, 0.d, 0.d, 0.d}, 
				{0.d, Math.cos(alpha), -Math.sin(alpha), 0.d},
				{0.d, Math.sin(alpha), Math.cos(alpha), 0.d},
				{0.d, 0.d, 0.d, 1.d}
		};
		return MatrixUtils.createRealMatrix(RotX);
	}
	
	public static RealMatrix RotateY(float alpha) {
		double[][] RotY = {
				{Math.cos(alpha), 0.d, Math.sin(alpha), 0.d},
				{0.d, 1.d, 0.d, 0.d},
				{-Math.sin(alpha), 0.d, Math.cos(alpha), 0.d},
				{0.d, 0.d, 0.d, 1.d}
		};
		return MatrixUtils.createRealMatrix(RotY);
	}
	
	public static RealMatrix RotateZ(float alpha) {
		double[][] RotZ = {
				{Math.cos(alpha), -Math.sin(alpha), 0.d, 0.d},
				{Math.sin(alpha), Math.cos(alpha), 0.d, 0.d},
				{0.d, 0.d, 1.d, 0.d},
				{0.d, 0.d, 0.d, 1.d}
		};
		return MatrixUtils.createRealMatrix(RotZ);
	}
	
	public static RealMatrix Translate(float x, float y, float z) {
		double[][] Trans = {
				{1.d, 0.d, 0.d, x},
				{0.d, 1.d, 0.d, y},
				{0.d, 0.d, 1.d, z},
				{0.d, 0.d, 0.d, 1.d}
		};
		return MatrixUtils.createRealMatrix(Trans);
	}
	
	public static float ComputeThetaC(float alpha) {
		//For start it's hardcoded - needs refactoring
		double r1 = Math.sqrt(6.5d*6.5d + 27.5d*27.5d)/100.d;
		double r2 = Math.sqrt(8*8+1)/100.d;
		double r3 = 0.265d;
		double r4 = 0.135d;
		
		double theta1 = -Math.atan(6.5d/27.5d);
		double y3 = r1 * Math.sin(theta1) - r2 * Math.sin(alpha);
		double x3 = r1 * Math.cos(theta1) - r2 * Math.cos(alpha);
		
		return (float) (Math.atan2(y3, x3) + Math.acos((x3*x3 + y3*y3 +  r4*r4 - r3*r3) / (2 * -r4 * Math.sqrt(x3*x3 + y3*y3))));
	}

	public static double[] calculateArmPosition(float alfa, float beta, float delta) {
		float theta[] = new float[3];
		theta[0] = (float)Math.toRadians(alfa);
		theta[1] = (float)Math.toRadians(beta); 
		theta[2] = (float)Math.toRadians(delta - 90.f);
		
		//beta przeliczyć na kąt u góry(bezpośrednio do Motoru C)
		//theta[1] = ComputeThetaC(theta[1]);// + (float)Math.toRadians(-90.0f);
				
		RealMatrix t = RotateZ(theta[0]);
		t = t.multiply(Translate(0.f, 0.f, 36.0f));
		t = t.multiply(RotateY(theta[1]));
		t = t.multiply(Translate(24.5f, 0.f, 0.f));
		t = t.multiply(RotateY(theta[2]));
		t = t.multiply(Translate(19.5f, 0.f, 0.f));		
		
		double[] vec = {0.0f, 0.0f, 0.0f, 1.0f};
		return t.operate(MatrixUtils.createRealVector(vec)).toArray();
	}
	
	public static RealMatrix calculateTransJacobian() {
		double[] pos = calculateArmPosition(0.f, 0.f, 0.f);
		RealVector vec0 = MatrixUtils.createRealVector(pos); 
		
		pos = calculateArmPosition(0.5f, 0.f, 0.f);
		RealVector vec1 = MatrixUtils.createRealVector(pos);
		vec1 = vec1.subtract(vec0).mapMultiply(2.f);
		
		pos = calculateArmPosition(0.f, 0.5f, 0.f);
		RealVector vec2 = MatrixUtils.createRealVector(pos);
		vec2 = vec2.subtract(vec0).mapMultiply(2.f);
		
		pos = calculateArmPosition(0.f, 0.f, 0.5f);
		RealVector vec3 = MatrixUtils.createRealVector(pos);
		vec3 = vec3.subtract(vec0).mapMultiply(2.f);
		
		double[][] jacobian = {
				vec1.toArray(),
				vec2.toArray(),
				vec3.toArray()				
		};
		return MatrixUtils.createRealMatrix(jacobian);
	}

	public static float[] calculatechangeMotorPoisitons(float x, float y, float z) {
		//we need something better
		double[] positionNow = Controller.getArmPosition();
		double[] positionTo = {x, y, z, 1.0f};
		RealVector To = MatrixUtils.createRealVector(positionTo);
		
		//compute inverse jacobian, so we could compute angle changes
		final RealMatrix InvJac = calculateTransJacobian();
				
		double[] dtheta = new double[3];
		float[] ang = Controller.ReverseCorrectDegrees(Controller.getMotorPositions());
		
		for(int i = 0; i < 400; i++) {
			RealVector Now = MatrixUtils.createRealVector(positionNow);
					
			Now = To.subtract(Now);
		
			//small change of angles on manipulator
			dtheta = InvJac.operate(Now).mapMultiply(0.1d).toArray();
			
			ang[0] += dtheta[0];
			ang[1] += dtheta[1];
			ang[2] += dtheta[2];
			
			/*System.out.println(positionNow[0] + " " + positionNow[1] + " " + positionNow[2]);
			System.out.println("Length: " + Math.sqrt(positionNow[0]*positionNow[0] + positionNow[1]*positionNow[1] + positionNow[2]*positionNow[2]));
			System.out.println("Theta: " + dtheta[0] + " " +dtheta[1] + " " + dtheta[2]);*/
			
			positionNow = Kinematics.calculateArmPosition(ang[0], ang[1], ang[2]);
		}
			
		return ang;
	}

}
