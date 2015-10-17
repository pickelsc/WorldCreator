	package ass2.spec;

import java.lang.Math.*;

/**
 * A collection of useful math methods 
 *
 * TODO: The methods you need to complete are at the bottom of the class
 *
 * @author malcolmr
 */
public class MathUtil {
	
	public static double sinTable[];
	public static double cosTable[];

	/**
	 * Normalise an angle to the range (-180, 180]
	 * 
	 * @param angle 
	 * @return
	 */
	static public double normaliseAngle(double angle) {
		//System.out.println("Normalising "+angle);
		return ((angle + 180.0) % 360.0 + 360.0) % 360.0 - 180.0;
	}
	
	static public double normaliseAngle2(double angle) {
		//System.out.println("Normalising "+angle);
		return (angle % 360.0 + 360.0) % 360.0;
	}

	/**
	 * Clamp a value to the given range
	 * 
	 * @param value
	 * @param min
	 * @param max
	 * @return
	 */

	public static double clamp(double value, double min, double max) {
		return Math.max(min, Math.min(max, value));
	}

	/**
	 * Multiply two matrices
	 * 
	 * @param p A 3x3 matrix
	 * @param q A 3x3 matrix
	 * @return
	 */
	public static double[][] multiply(double[][] p, double[][] q) {

		double[][] m = new double[3][3];

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				m[i][j] = 0;
				for (int k = 0; k < 3; k++) {
					m[i][j] += p[i][k] * q[k][j]; 
				}
			}
		}

		return m;
	}

	public static double[][] multiply (double[][] a, double[][] b, double[][] c) {

		return multiply(multiply(a,b),c);
	}

	/**
	 * Multiply a vector by a matrix
	 * 
	 * @param m A 3x3 matrix
	 * @param v A 3x1 vector
	 * @return
	 */
	public static double[] multiply(double[][] m, double[] v) {

		double[] u = new double[3];

		for (int i = 0; i < 3; i++) {
			u[i] = 0;
			for (int j = 0; j < 3; j++) {
				u[i] += m[i][j] * v[j];
			}
		}

		return u;
	}


	public static double[][] subtract(double[][] m, double[][] n) {

		double[][] o = new double[3][3];

		for (int i=0; i<3; ++i) {
			for (int j=0; j<3; ++j) {
				o[i][j] = m[i][j] - n[i][j];
			}
		}

		return o;
	}


	public static void printMatrix(double[][] p) {

		System.out.println("========");
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				System.out.print(p[i][j] + " ");
			}
			System.out.println();
			if (i!=2)  System.out.println();
		}
		System.out.println("========");
	}

	/**
	 * @param pos
	 * @return
	 */
	public static double[][] translationMatrix(double[] v) {

		double[][] d = new double[][] {
				{1,0,v[0]}
				,{0,1,v[1]}
				,{0,0,1}
		};

		return d;
	}

	/**
	 * @param angle in degrees
	 * @return
	 */
	public static double[][] rotationMatrix(double angle) {

		double rads = Math.toRadians(angle);

		//System.out.println("angle: "+angle+" rads: "+rads+" cos: "+Math.cos(rads));

		double[][] d = new double[][] {
				{Math.cos(rads),-(Math.sin(rads)),0}
				,{Math.sin(rads),Math.cos(rads),0}
				,{0,0,1}
		};
		//MathUtil.printMatrix(d);
		return d;
	}

	/**
	 * @param scale
	 * @return
	 */
	public static double[][] scaleMatrix(double scale) {

		double[][] d = new double[][] {
				{scale,0,0}
				,{0,scale,0}
				,{0,0,1}
		};

		return d;
	}

	/**
	 * Solves simultaneous equations
	 */
	public static double[] SolveLinearEquations (double a,double b,double c,double d,double u,double v) {
		System.out.println("SolveLinearEquations received "+" "+a+" "+b+" "+c+" "+d+" "+u+" "+v);

		double f,g;
		double x,y;
		if (Math.abs(a) > Math.abs(c)) {
			f = u * c / a;
			g = b * c / a;
			y = (v - f) / (d - g);
			if(c != 0) x = (f - g * y) / c;
			else x = (u - b * y)/a;
		} else {
			f = v * a / c;
			g = d * a / c;
			x = (u - f) / (b - g);
			if (a != 0) y = (f - g * x) / a ;
			else y = (v - d * x)/c;
		}
		return new double[] {x,y};
	}

	public static double[] SolveLinearEquations2 (double a,double b,double c,double d,double u,double v) {
		double determinant = a*d - b*c;
		double x = 0,y = 0;
		if(determinant != 0) {
			x = (u*d - b*v)/determinant;
			y = (a*v - u*c)/determinant;
		} else {
			System.err.println("Determinant DNE"); 
		}
		return new double[] {x,y};
	}
	
	public static double distanceBetweenPoints(double x1, double y1, double x2, double y2) {
		
		return Math.sqrt(Math.pow(Math.abs(x2-x1),2.0) + Math.pow(Math.abs(y2-y1),2.0));
	}
	
	public static double[][] makeTransformationMatrix (double[] t, double r, double s) {
		
		double[][] tm = MathUtil.translationMatrix(t);
		double[][] rm = MathUtil.rotationMatrix(r);
		double[][] sm = MathUtil.scaleMatrix(s);

		return MathUtil.multiply(tm,rm,sm);
	}
	
	public static void genTrigTables () {
		sinTable = new double[360];
		cosTable = new double[360];
		for (int i=0;i<360;++i) {
			sinTable[i] = Math.sin(Math.toRadians(i));
			cosTable[i] = Math.cos(Math.toRadians(i));
		}
	}
}
