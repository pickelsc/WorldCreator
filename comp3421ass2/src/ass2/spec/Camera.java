package ass2.spec;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

/**
 * The camera is a GameObject that can be moved, rotated and scaled like any other.
 * 
 * TODO: You need to implment the setView() method.
 *       The methods you need to complete are at the bottom of the class
 *
 * @author malcolmr
 */
public class Camera {

	private double[] myTranslation;
	private double myRotationY;
	private double myRotationZ;
//	private double myScale;
	
	private static final double LOOK_Z_ANGLE = 2d;
	private static final double LOOK_Y_ANGLE = 2d;
	
	private static final double WALK_INTERVAL = 0.2d;
	
	public Camera() {
		myTranslation = new double[] {0d,1d,0d};
		myRotationY = -20;
		myRotationZ = 0;
//		myScale = 1;
	}

	public void setView(GL2 gl) {

		// init the camera
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		
		// apply necesary transformation
//		gl.glScaled(1.0/myScale, 1.0/myScale, 1);
		gl.glRotated(-myRotationY, 0, 1, 0);
//		gl.glRotated(-myRotationZ, 0, 0, 1);
		gl.glTranslated(-myTranslation[0], -myTranslation[1], -myTranslation[2]);

	}
	
	private void printStats () {
		System.out.println("["+myTranslation[0]+", "+myTranslation[1]+", "+myTranslation[2]+"] Y: "+myRotationY);
	}
	
	// Looking Functions
	
	public void lookUp () {
		System.out.print("Looking Up ");
		myRotationZ += LOOK_Z_ANGLE;
		printStats();
	}
	
	public void lookDown () {
		System.out.print("Looking Down ");
		myRotationZ -= LOOK_Z_ANGLE;
		printStats();
	}
	
	public void lookLeft () {
		System.out.print("Looking Left ");
		myRotationY += LOOK_Y_ANGLE;
		printStats();
	}
	
	public void lookRight () {
		System.out.print("Looking Right ");
		myRotationY -= LOOK_Y_ANGLE;
		printStats();
	}

	// Walking Functions
	
	public void WalkForward() {
		System.out.print("Walking Forward ");
		myTranslation[0] += Math.sin(Math.toRadians(myRotationY)) * WALK_INTERVAL;
		myTranslation[2] -= Math.cos(Math.toRadians(myRotationY)) * WALK_INTERVAL;
		printStats();
	}
	
	public void WalkBackward() {
		System.out.print("Walking Backward ");
		myTranslation[0] -= Math.sin(Math.toRadians(myRotationY)) * WALK_INTERVAL;
		myTranslation[2] += Math.cos(Math.toRadians(myRotationY)) * WALK_INTERVAL;
		printStats();
	}
	
	public void WalkLeft() {
		System.out.print("Walking Left ");
		myTranslation[0] += Math.cos(Math.toRadians(myRotationY)) * WALK_INTERVAL;
		myTranslation[2] -= Math.sin(Math.toRadians(myRotationY)) * WALK_INTERVAL;
		printStats();
	}
	
	public void WalkRight() {
		System.out.print("Walking Right ");
		myTranslation[0] -= Math.cos(Math.toRadians(myRotationY)) * WALK_INTERVAL;
		myTranslation[2] += Math.sin(Math.toRadians(myRotationZ)) * WALK_INTERVAL;
		printStats();
	}
}
