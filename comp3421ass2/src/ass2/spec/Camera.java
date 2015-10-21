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

	private SpaceShip model;
	
	private double[] myTranslation;
	private double[] myLookAt;
	private double[] myRotation;
	
	private boolean groundMode = false;
	private boolean followMode = false;
	
	private static final double LOOK_X_ANGLE = 2d;
	private static final double LOOK_Y_ANGLE = 2d;
	private static final double LOOK_Z_ANGLE = 2d;
	
	private static final double WALK_INTERVAL = 0.2d;
	
	private static final boolean DEBUG = true;
	
	private static final float GLOBAL_AMBIENCE = 0.2f; // Global ambient white light intensity.
	
	public Camera() {
		model = new SpaceShip(0.2, true);
		model.setRotationY(90);
		model.show(false);
		myTranslation = new double[] {-3d,1d,2.5d};
		myRotation = new double[] {90d,0d,0d};
		myLookAt = new double[] {-2d,1d,2.5d};
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
		if (DEBUG) System.out.print("Looking Up ");
		myRotation[2] += LOOK_Z_ANGLE;
		if (DEBUG) printStats();
	}
	
	public void lookDown () {
		if (DEBUG) System.out.print("Looking Down ");
		myRotation[2] -= LOOK_Z_ANGLE;
		if (DEBUG) printStats();
	}
	
	public void lookLeft () {
		if (DEBUG) System.out.print("Looking Left ");
		if (followMode == true) {
			myTranslation[0] = myLookAt[0]+2*MathUtil.sinTable[(int)MathUtil.normaliseAngle2(myRotation[0]+LOOK_X_ANGLE+180)];
			myTranslation[2] = myLookAt[2]+2*MathUtil.cosTable[(int)MathUtil.normaliseAngle2(myRotation[0]+LOOK_X_ANGLE+180)];
			model.rotateY(LOOK_X_ANGLE);
		} else {
			myLookAt[0] -= MathUtil.sinTable[(int)MathUtil.normaliseAngle2(myRotation[0])] - MathUtil.sinTable[(int)MathUtil.normaliseAngle2(myRotation[0]+LOOK_X_ANGLE)];
			myLookAt[2] -= MathUtil.cosTable[(int)MathUtil.normaliseAngle2(myRotation[0])] - MathUtil.cosTable[(int)MathUtil.normaliseAngle2(myRotation[0]+LOOK_X_ANGLE)];
		}
		myRotation[0] += LOOK_X_ANGLE;
		myRotation[0] = MathUtil.normaliseAngle2(myRotation[0]);
		if (DEBUG) printStats();
	}
	
	public void lookRight () {
		if (DEBUG) System.out.print("Looking Right ");
		if (followMode == true) {
			myTranslation[0] = myLookAt[0]-2*MathUtil.sinTable[(int)MathUtil.normaliseAngle2(myRotation[0]-LOOK_X_ANGLE)];
			myTranslation[2] = myLookAt[2]-2*MathUtil.cosTable[(int)MathUtil.normaliseAngle2(myRotation[0]-LOOK_X_ANGLE)];
			model.rotateY(-LOOK_X_ANGLE);
		} else {
			myLookAt[0] += MathUtil.sinTable[(int)MathUtil.normaliseAngle2(myRotation[0])] - MathUtil.sinTable[(int)MathUtil.normaliseAngle2(myRotation[0]+LOOK_X_ANGLE)];
			myLookAt[2] += MathUtil.cosTable[(int)MathUtil.normaliseAngle2(myRotation[0])] - MathUtil.cosTable[(int)MathUtil.normaliseAngle2(myRotation[0]+LOOK_X_ANGLE)];
		}
		myRotation[0] -= LOOK_X_ANGLE;
		myRotation[0] = MathUtil.normaliseAngle2(myRotation[0]);
		if (DEBUG) printStats();
	}

	// Walking Functions
	
	public void WalkForward() {
		if (DEBUG) System.out.print("Walking Forward ");
		double offset = myTranslation[1] - Game.myTerrain.altitude(myTranslation[0], myTranslation[2]);
		myTranslation[0] += WALK_INTERVAL*MathUtil.sinTable[(int)myRotation[0]];
		myTranslation[2] += WALK_INTERVAL*MathUtil.cosTable[(int)myRotation[0]];
		myLookAt[0] += WALK_INTERVAL*MathUtil.sinTable[(int)myRotation[0]];
		myLookAt[2] += WALK_INTERVAL*MathUtil.cosTable[(int)myRotation[0]];
		if (groundMode == true)	verticalAllign();
		if (followMode == true) updateModel(offset);
		if (DEBUG) printStats();
	}
	
	public void WalkBackward() {
		if (DEBUG) System.out.print("Walking Backward ");
		double offset = myTranslation[1] - Game.myTerrain.altitude(myTranslation[0], myTranslation[2]);
		myTranslation[0] -= WALK_INTERVAL*MathUtil.sinTable[(int)myRotation[0]];
		myTranslation[2] -= WALK_INTERVAL*MathUtil.cosTable[(int)myRotation[0]];
		myLookAt[0] -= WALK_INTERVAL*MathUtil.sinTable[(int)myRotation[0]];
		myLookAt[2] -= WALK_INTERVAL*MathUtil.cosTable[(int)myRotation[0]];
		if (groundMode == true)	verticalAllign();
		if (followMode == true) updateModel(offset);
		if (DEBUG) printStats();
	}
	
	public void WalkLeft() {
		if (DEBUG) System.out.print("Walking Left ");
		double offset = myTranslation[1] - Game.myTerrain.altitude(myTranslation[0], myTranslation[2]);
		myTranslation[0] += WALK_INTERVAL*MathUtil.sinTable[(int)MathUtil.normaliseAngle2(myRotation[0]+90)];
		myTranslation[2] += WALK_INTERVAL*MathUtil.cosTable[(int)MathUtil.normaliseAngle2(myRotation[0]+90)];
		myLookAt[0] += WALK_INTERVAL*MathUtil.sinTable[(int)MathUtil.normaliseAngle2(myRotation[0]+90)];
		myLookAt[2] += WALK_INTERVAL*MathUtil.cosTable[(int)MathUtil.normaliseAngle2(myRotation[0]+90)];
		if (groundMode == true)	verticalAllign();
		if (followMode == true) updateModel(offset);
		if (DEBUG) printStats();
	}
	
	public void WalkRight() {
		if (DEBUG) System.out.print("Walking Right ");
		double offset = myTranslation[1] - Game.myTerrain.altitude(myTranslation[0], myTranslation[2]);
		myTranslation[0] += WALK_INTERVAL*MathUtil.sinTable[(int)MathUtil.normaliseAngle2(myRotation[0]-90)];
		myTranslation[2] += WALK_INTERVAL*MathUtil.cosTable[(int)MathUtil.normaliseAngle2(myRotation[0]-90)];
		myLookAt[0] += WALK_INTERVAL*MathUtil.sinTable[(int)MathUtil.normaliseAngle2(myRotation[0]-90)];
		myLookAt[2] += WALK_INTERVAL*MathUtil.cosTable[(int)MathUtil.normaliseAngle2(myRotation[0]-90)];
		if (groundMode == true)	verticalAllign();
		if (followMode == true) updateModel(offset);
		if (DEBUG) printStats();
	}
	
	private void verticalAllign () {
		if (followMode == false) {
			myTranslation[1] = 1+Game.myTerrain.altitude(myTranslation[0], myTranslation[2]);
			myLookAt[1] = 1+Game.myTerrain.altitude(myTranslation[0], myTranslation[2]);
		} else {
			double diff = myLookAt[1] - (1+Game.myTerrain.altitude(myLookAt[0],myLookAt[2]));
			myTranslation[1] -= diff;
			myLookAt[1] -= diff;
		}
	}
	
	public void moveUp() {
		if (groundMode != true) {
			if (DEBUG) System.out.print("moving up ");
			myTranslation[1] += WALK_INTERVAL;
			myLookAt[1] += WALK_INTERVAL;
		}
		if (DEBUG) printStats();
	}
	
	public void moveDown () {
		if (groundMode != true) {
			if (DEBUG) System.out.print("moving down ");
			myTranslation[1] -= WALK_INTERVAL;
			myLookAt[1] -= WALK_INTERVAL;
		}
		if (DEBUG) printStats();
	}
	
	private void updateModel(double offset) {
		// hover effect
		model.setPosition(myLookAt[0], myLookAt[2], 0.7d);
	}
	
	public void toggleGroundMode() {
		if (groundMode == true) {
			if (DEBUG) System.out.print("fly mode ");
			myTranslation[1] += 1;
			myLookAt[1] += 1;
			groundMode = false;
		} else if (followMode == true) {
			if (DEBUG) System.out.print("ground mode w/follow ");
			myTranslation[1] = 3+Game.myTerrain.altitude(myTranslation[0],myTranslation[2]);
			myLookAt[1] = 1+Game.myTerrain.altitude(myTranslation[0],myTranslation[2]);
			groundMode = true;
		} else {
			if (DEBUG) System.out.print("ground mode w/o follow ");
			double diff = myTranslation[1] - (1+Game.myTerrain.altitude(myTranslation[0],myTranslation[2]));
			myTranslation[1] -= diff;
			myLookAt[1] -= diff;
			groundMode = true;
		}
		if (DEBUG) printStats();
	}
	
	public void toggleFollowMode() {
		if (followMode == true) {

			myTranslation[0] = myLookAt[0];
			myTranslation[1] = myLookAt[1];
			myTranslation[2] = myLookAt[2];
			
			myLookAt[0] += MathUtil.sinTable[(int) (myRotation[0])];
			myLookAt[2] += MathUtil.cosTable[(int) (myRotation[0])];
			
			model.show(false);
			followMode = false;
		} else {
			if (DEBUG) System.out.print("follow mode ");
			myLookAt[0] = myTranslation[0];
			myLookAt[1] = myTranslation[1];
			myLookAt[2] = myTranslation[2];
			myTranslation[0] += 2*MathUtil.sinTable[(int) (MathUtil.normaliseAngle2(180+myRotation[0]))];
			myTranslation[1] += 2;
			myTranslation[2] += 2*MathUtil.cosTable[(int) (MathUtil.normaliseAngle2(180+myRotation[0]))];
			
			if (DEBUG) System.out.println(MathUtil.sinTable[(int) (MathUtil.normaliseAngle2(180+myRotation[0]))]);
			
			model.setPosition(myLookAt[0], myLookAt[2]);
			model.show(true);
			followMode = true;
		}
		printStats();
	}
}
