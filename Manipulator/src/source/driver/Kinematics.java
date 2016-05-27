package source.driver;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import source.Kinematics;
//Marek Tkaczyk
public class Kinematics {
	private static double TachometrPerRadian = Math.PI / 180.0;
	
	
	
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
	
	private static RealMatrix calculateTransJacobian() {
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

	public static double[] calculateChangeMotorPoisitons(double x, double y, double z, double[] angles) {
		//we need something better
		double[] positionNow = calculateArmPosition(angles[0], angles[1], angles[2]);
		double[] positionTo = {x, y, z, 1.0};
		RealVector To = MatrixUtils.createRealVector(positionTo);
		
		//compute inverse jacobian, so we could compute angle changes
		final RealMatrix InvJac = calculateTransJacobian();
				
		double[] startAngles = new double[3];
		
		for(int i = 0; i < 3; i++) startAngles[i] = angles[i];
			
		double[] dtheta = new double[3];
		
		RealVector Now = To;
		int iter = 0;
		boolean normal = true;
		float oldnorm = 0.0f;
		
		while(Now.getNorm() > 1.f && iter < 1000 && normal) {
			iter++;
			oldnorm = (float) Now.getNorm();
			
			Now = MatrixUtils.createRealVector(positionNow);
					
			Now = To.subtract(Now);
			if (Now.getNorm() > oldnorm) {
				System.out.println("[ERROR]Niestabilność norm, diffrence:" + (Now.getNorm() - oldnorm));
				normal = false;
			}
			//small change of angles on manipulator
			RealVector change = InvJac.operate(Now).mapMultiply(0.1);
//			System.out.println(""+change.getNorm());
//			if (change.getNorm() > 0.1f) System.out.println("przedział niestabilności!");
			//if (change.getNorm() > 0.3f) normal = false;
			dtheta = change.toArray();
	
			ang[0] += dtheta[0];
			ang[1] += dtheta[1];
			ang[2] += dtheta[2];
			
			/*System.out.println(positionNow[0] + " " + positionNow[1] + " " + positionNow[2]);
			System.out.println("Length: " + Math.sqrt(positionNow[0]*positionNow[0] + positionNow[1]*positionNow[1] + positionNow[2]*positionNow[2]));
			System.out.println("Theta: " + dtheta[0] + " " +dtheta[1] + " " + dtheta[2]);*/
			
			positionNow = Kinematics.calculateArmPosition(ang[0], ang[1], ang[2]);
		}
		if (!normal) {
			ang = startAngles;
			System.out.println("[ERROR]Obliczenie niestabilne numerycznie, Powrot do podstawowych danych ");
			System.out.println("[ERROR]Wykonanych iteracji"+iter);
		}
		else System.out.println("Znaleziono rozwiązanie w Iteracji:" + iter);
		
		return angles;
	}

}
