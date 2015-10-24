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
	
    public static void normalizeVector(double v[])  
    {  
        double d = Math.sqrt(v[0]*v[0]+v[1]*v[1]+v[2]*v[2]);  
        if (d != 0.0) 
        {  
           v[0]/=d; 
           v[1]/=d;  
           v[2]/=d;  
        }  
    }
    
    public static void normalizeVector(float v[])  
    {  
        float d = (float) Math.sqrt(v[0]*v[0]+v[1]*v[1]+v[2]*v[2]);  
        if (d != 0.0) 
        {  
           v[0]/=d; 
           v[1]/=d;  
           v[2]/=d;  
        }  
    } 
    
    public static void normCrossProd(double v1[], double v2[], double out[])  
    {  
       out[0] = v1[1]*v2[2] - v1[2]*v2[1];  
       out[1] = v1[2]*v2[0] - v1[0]*v2[2];  
       out[2] = v1[0]*v2[1] - v1[1]*v2[0];  
       normalizeVector(out);  
    }
    
    public static double r(double t){
    	double x  = Math.cos(2 * Math.PI * t);
        return x;
    }
    
    public static double getY(double t){
    	
    	double y  = Math.sin(2 * Math.PI * t);
        return y;
    }
    
    public static double crossProduct (double p1[], double p2[]) {
		return Math.abs(p1[0]*p2[1] - p2[0]*p1[1]);
	}
}
