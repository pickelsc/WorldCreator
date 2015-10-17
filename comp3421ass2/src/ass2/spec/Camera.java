package ass2.spec;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

/**
 * The camera maintains the position an the calls glyLookAt();
 * 
 * The Z axis is the '0 degrees'
 */
public class Camera {

	private double[] myTranslation;
	private double[] myLookAt;
	private double[] myRotation;
	
	private static final double LOOK_X_ANGLE = 2d;
	private static final double LOOK_Y_ANGLE = 2d;
	private static final double LOOK_Z_ANGLE = 2d;
	
	private static final double WALK_INTERVAL = 0.2d;
	
	private static final float GLOBAL_AMBIENCE = 0.2f; // Global ambient white light intensity.
	
	public Camera() {
		myTranslation = new double[] {-3d,0d,2.5d};
		myRotation = new double[] {90d,0d,0d};
		myLookAt = new double[] {-2d,0d,2.5d};
		
		
	}

	public void setView(GL2 gl) {
		gl.glClearColor(0.04f, 0.2f, 0.76f, 0.0f);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		
		// Global Ambience
		float globAmb[] = { GLOBAL_AMBIENCE, GLOBAL_AMBIENCE, GLOBAL_AMBIENCE, 1.0f };
        gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, globAmb,0); // Global ambient light.

		gl.glMatrixMode(GL2.GL_MODELVIEW);
		GLU glu = new GLU();
		gl.glLoadIdentity();
		glu.gluLookAt(myTranslation[0], myTranslation[1], myTranslation[2], // pos
						myLookAt[0], myLookAt[1], myLookAt[2], 				// lookat
							0.0, 1.0, 0.0);									// up
		
	}
	
	private void printStats () {
		System.out.println("pos: ["+myTranslation[0]+", "+myTranslation[1]+", "+myTranslation[2]+"]"
								+ " lookAt: ["+myLookAt[0]+", "+myLookAt[1]+", "+myLookAt[2]+"]"
								+ " rotation: ["+myRotation[0]+", "+myRotation[1]+", "+myRotation[2]+"]");
	}
	
	// Looking Functions
	
	public void lookUp () {
		System.out.print("Looking Up ");
		myRotation[2] += LOOK_Z_ANGLE;
		printStats();
	}
	
	public void lookDown () {
		System.out.print("Looking Down ");
		myRotation[2] -= LOOK_Z_ANGLE;
		printStats();
	}
	
	public void lookLeft () {
		System.out.print("Looking Left ");
		myLookAt[0] -= MathUtil.sinTable[(int)MathUtil.normaliseAngle2(myRotation[0])] - MathUtil.sinTable[(int)MathUtil.normaliseAngle2(myRotation[0]+LOOK_X_ANGLE)];
		myLookAt[2] -= MathUtil.cosTable[(int)MathUtil.normaliseAngle2(myRotation[0])] - MathUtil.cosTable[(int)MathUtil.normaliseAngle2(myRotation[0]+LOOK_X_ANGLE)];
		myRotation[0] += LOOK_X_ANGLE;
		myRotation[0] = MathUtil.normaliseAngle2(myRotation[0]);
		printStats();
	}
	
	public void lookRight () {
		System.out.print("Looking Right ");
		myLookAt[0] += MathUtil.sinTable[(int)MathUtil.normaliseAngle2(myRotation[0])] - MathUtil.sinTable[(int)MathUtil.normaliseAngle2(myRotation[0]+LOOK_X_ANGLE)];
		myLookAt[2] += MathUtil.cosTable[(int)MathUtil.normaliseAngle2(myRotation[0])] - MathUtil.cosTable[(int)MathUtil.normaliseAngle2(myRotation[0]+LOOK_X_ANGLE)];
		myRotation[0] -= LOOK_X_ANGLE;
		myRotation[0] = MathUtil.normaliseAngle2(myRotation[0]);
		printStats();
	}

	// Walking Functions
	
	public void WalkForward() {
		System.out.print("Walking Forward ");
		myTranslation[0] += WALK_INTERVAL*MathUtil.sinTable[(int)myRotation[0]];
		myTranslation[2] += WALK_INTERVAL*MathUtil.cosTable[(int)myRotation[0]];
		myLookAt[0] += WALK_INTERVAL*MathUtil.sinTable[(int)myRotation[0]];
		myLookAt[2] += WALK_INTERVAL*MathUtil.cosTable[(int)myRotation[0]];
		printStats();
	}
	
	public void WalkBackward() {
		System.out.print("Walking Backward ");
		myTranslation[0] -= WALK_INTERVAL*MathUtil.sinTable[(int)myRotation[0]];
		myTranslation[2] -= WALK_INTERVAL*MathUtil.cosTable[(int)myRotation[0]];
		myLookAt[0] -= WALK_INTERVAL*MathUtil.sinTable[(int)myRotation[0]];
		myLookAt[2] -= WALK_INTERVAL*MathUtil.cosTable[(int)myRotation[0]];
		printStats();
	}
	
	public void WalkLeft() {
		System.out.print("Walking Left ");
		myTranslation[0] += WALK_INTERVAL*MathUtil.sinTable[(int)MathUtil.normaliseAngle2(myRotation[0]+90)];
		myTranslation[2] += WALK_INTERVAL*MathUtil.cosTable[(int)MathUtil.normaliseAngle2(myRotation[0]+90)];
		myLookAt[0] += WALK_INTERVAL*MathUtil.sinTable[(int)MathUtil.normaliseAngle2(myRotation[0]+90)];
		myLookAt[2] += WALK_INTERVAL*MathUtil.cosTable[(int)MathUtil.normaliseAngle2(myRotation[0]+90)];
		printStats();
	}
	
	public void WalkRight() {
		System.out.print("Walking Right ");
		myTranslation[0] += WALK_INTERVAL*MathUtil.sinTable[(int)MathUtil.normaliseAngle2(myRotation[0]-90)];
		myTranslation[2] += WALK_INTERVAL*MathUtil.cosTable[(int)MathUtil.normaliseAngle2(myRotation[0]-90)];
		myLookAt[0] += WALK_INTERVAL*MathUtil.sinTable[(int)MathUtil.normaliseAngle2(myRotation[0]-90)];
		myLookAt[2] += WALK_INTERVAL*MathUtil.cosTable[(int)MathUtil.normaliseAngle2(myRotation[0]-90)];
		printStats();
	}
	
	public void moveUp() {
		System.out.println("moving up");
		myTranslation[1] += WALK_INTERVAL;
		myLookAt[1] += WALK_INTERVAL;
	}
	
	public void moveDown () {
		System.out.println("moving down");
		myTranslation[1] -= WALK_INTERVAL;
		myLookAt[1] -= WALK_INTERVAL;
	}
}
