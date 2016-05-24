package source;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
//Marek Tkaczyk
public class Kinematics {
	private static double TachometrPerRadian = Math.PI / 180.0;
	private Controller controller;
	
	public Kinematics(Controller controller){
		this.controller = controller;
	}
	
	
	public static RealMatrix RotateX(double alpha) {
		double[][] RotX = {
				{1.d, 0.d, 0.d, 0.d}, 
				{0.d, Math.cos(alpha), -Math.sin(alpha), 0.d},
				{0.d, Math.sin(alpha), Math.cos(alpha), 0.d},
				{0.d, 0.d, 0.d, 1.d}
		};
		return MatrixUtils.createRealMatrix(RotX);
	}
	
	public static RealMatrix RotateY(double alpha) {
		double[][] RotY = {
				{Math.cos(alpha), 0.d, Math.sin(alpha), 0.d},
				{0.d, 1.d, 0.d, 0.d},
				{-Math.sin(alpha), 0.d, Math.cos(alpha), 0.d},
				{0.d, 0.d, 0.d, 1.d}
		};
		return MatrixUtils.createRealMatrix(RotY);
	}
	
	public static RealMatrix RotateZ(double alpha) {
		double[][] RotZ = {
				{Math.cos(alpha), -Math.sin(alpha), 0.d, 0.d},
				{Math.sin(alpha), Math.cos(alpha), 0.d, 0.d},
				{0.d, 0.d, 1.d, 0.d},
				{0.d, 0.d, 0.d, 1.d}
		};
		return MatrixUtils.createRealMatrix(RotZ);
	}
	
	public static RealMatrix Translate(double x, double y, double z) {
		double[][] Trans = {
				{1.d, 0.d, 0.d, x},
				{0.d, 1.d, 0.d, y},
				{0.d, 0.d, 1.d, z},
				{0.d, 0.d, 0.d, 1.d}
		};
		return MatrixUtils.createRealMatrix(Trans);
	}
	
	public static double ComputeThetaC(double alpha) {
		//For start it's hardcoded - needs refactoring
		double r1 = Math.sqrt(6.5d*6.5d + 27.5d*27.5d)/100.d;
		double r2 = Math.sqrt(8*8+1)/100.d;
		double r3 = 0.265d;
		double r4 = 0.135d;
		
		double theta1 = -Math.atan(6.5d/27.5d);
		double y3 = r1 * Math.sin(theta1) - r2 * Math.sin(alpha);
		double x3 = r1 * Math.cos(theta1) - r2 * Math.cos(alpha);
		
		return  (Math.atan2(y3, x3) + Math.acos((x3*x3 + y3*y3 +  r4*r4 - r3*r3) / (2 * -r4 * Math.sqrt(x3*x3 + y3*y3))));
	}

	public static double[] calculateArmPosition(double alfa, double beta, double delta) {
		double theta[] = new double[3];
		theta[0] = Math.toRadians(alfa);
		theta[1] = Math.toRadians(beta + 55.0); 
		theta[2] = Math.toRadians(delta - 135.0);
		
		//beta przeliczyć na kąt u góry(bezpośrednio do Motoru C)
		//theta[1] = ComputeThetaC(theta[1]);// + Math.toRadians(-90.0f);
				
		RealMatrix t = RotateZ(theta[0]);
		t = t.multiply(Translate(0.0, 0.0, 36.00));
		t = t.multiply(RotateY(theta[1]));
		t = t.multiply(Translate(24.5, 0.0, 0.0));
		t = t.multiply(RotateY(theta[2]));
		t = t.multiply(Translate(19.5, 0.0, 0.0));		
		
		double[] vec = {0.0, 0.0, 0.0, 1.0};
		return t.operate(MatrixUtils.createRealVector(vec)).toArray();
	}
	
	public static RealMatrix calculateTransJacobian() {
		double[] pos = calculateArmPosition(0.0, 0.0, 0.0);
		RealVector vec0 = MatrixUtils.createRealVector(pos); 
		
		pos = calculateArmPosition(0.5, 0.0, 0.0);
		RealVector vec1 = MatrixUtils.createRealVector(pos);
		vec1 = vec1.subtract(vec0).mapMultiply(2.0);
		
		pos = calculateArmPosition(0.0, 0.5, 0.0);
		RealVector vec2 = MatrixUtils.createRealVector(pos);
		vec2 = vec2.subtract(vec0).mapMultiply(2.0);
		
		pos = calculateArmPosition(0.0, 0.0, 0.5);
		RealVector vec3 = MatrixUtils.createRealVector(pos);
		vec3 = vec3.subtract(vec0).mapMultiply(2.0);
		
		double[][] jacobian = {
				vec1.toArray(),
				vec2.toArray(),
				vec3.toArray()				
		};
		return MatrixUtils.createRealMatrix(jacobian);
	}

	public double[] calculateChangeMotorPoisitons(double x, double y, double z, double[] angles) {
		//we need something better
		double[] positionNow = calculateArmPosition(angles[0], angles[1], angles[2]);
		double[] positionTo = {x, y, z, 1.0};
		RealVector To = MatrixUtils.createRealVector(positionTo);
		
		//compute inverse jacobian, so we could compute angle changes
		final RealMatrix InvJac = calculateTransJacobian();
				
		double[] dtheta = new double[3];
		
		for(int i = 0; i < 400; i++) {
			RealVector Now = MatrixUtils.createRealVector(positionNow);
					
			Now = To.subtract(Now);
		
			//small change of angles on manipulator
			dtheta = InvJac.operate(Now).mapMultiply(0.1d).toArray();
			
			angles[0] += dtheta[0];
			angles[1] += dtheta[1];
			angles[2] += dtheta[2];
			
			/*System.out.println(positionNow[0] + " " + positionNow[1] + " " + positionNow[2]);
			System.out.println("Length: " + Math.sqrt(positionNow[0]*positionNow[0] + positionNow[1]*positionNow[1] + positionNow[2]*positionNow[2]));
			System.out.println("Theta: " + dtheta[0] + " " +dtheta[1] + " " + dtheta[2]);*/
			
			positionNow = Kinematics.calculateArmPosition(angles[0], angles[1], angles[2]);
		}
			
		return angles;
	}

}
