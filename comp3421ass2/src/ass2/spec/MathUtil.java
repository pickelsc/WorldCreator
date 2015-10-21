package ass2.spec;

import java.lang.Math.*;

/**
 * A collection of useful math methods 
 */
public class MathUtil {
	
	public static double sinTable[];
	public static double cosTable[];

	static public double normaliseAngle(double angle) {
		//System.out.println("Normalising "+angle);
		return ((angle + 180.0) % 360.0 + 360.0) % 360.0 - 180.0;
	}
	
	static public double normaliseAngle2(double angle) {
		//System.out.println("Normalising "+angle);
		return (angle % 360.0 + 360.0) % 360.0;
	}

	public static double clamp(double value, double min, double max) {
		return Math.max(min, Math.min(max, value));
	}
	
	public static double distanceBetweenPoints(double[] a, double[] b) {
		
		return Math.sqrt(Math.pow(a[0]-b[0],2.0) + Math.pow(a[1]-b[1],2.0) + Math.pow(a[2]-b[2],2.0));
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
