package ass2.spec;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;


/**
 * A GameObject is an object that can move around in the game world.
 * 
 * GameObjects form a scene tree. The root of the tree is the special ROOT object.
 * 
 * Each GameObject is offset from its parent by a rotation, a translation and a scale factor. 
 *
 * @author malcolmr
 */
public class GameObject {

	// the list of all GameObjects in the scene tree
	public final static List<GameObject> ALL_OBJECTS = new ArrayList<GameObject>();

	// the root of the scene tree
	public final static GameObject ROOT = new GameObject();

	// the links in the scene tree
	private GameObject myParent;
	private List<GameObject> myChildren;

	// the local transformation
	//myRotation should be normalised to the range (-180..180)
	protected double[] myRotation;
	protected double myScale;
	protected double[] myTranslation;

	// is this part of the tree showing?
	private boolean amShowing;

	/**
	 * Special private constructor for creating the root node. Do not use otherwise.
	 */
	private GameObject() {
		myParent = null;
		myChildren = new ArrayList<GameObject>();

		myTranslation = new double[] {0,0,0};
		myRotation = new double[] {0,0,0};
		myScale = 1;

		amShowing = true;

		ALL_OBJECTS.add(this);
	}

	/**
	 * Public constructor for creating GameObjects, connected to a parent (possibly the ROOT).
	 *  
	 * New objects are created at the same location, orientation and scale as the parent.
	 *
	 * @param parent
	 */
	public GameObject(GameObject parent) {
		myParent = parent;
		myChildren = new ArrayList<GameObject>();

		parent.myChildren.add(this);

		myTranslation = new double[] {0,0,0};
		myRotation = new double[] {0,0,0};
		myScale = 1;

		// initially showing
		amShowing = true;

		ALL_OBJECTS.add(this);
	}

	/**
	 * Remove an object and all its children from the scene tree.
	 */
	public void destroy() {
		for (GameObject child : myChildren) {
			child.destroy();
		}

		myParent.myChildren.remove(this);
		ALL_OBJECTS.remove(this);
	}

	/**
	 * Get the parent of this game object
	 * 
	 * @return
	 */
	public GameObject getParent() {
		return myParent;
	}

	/**
	 * Get the children of this object
	 * 
	 * @return
	 */
	public List<GameObject> getChildren() {
		return myChildren;
	}

	/**
	 * Get the local rotation (in degrees)
	 * 
	 * @return
	 */
	public double[] getRotation() {
		return myRotation;
	}

	/**
	 * Set the local rotation (in degrees)
	 * @return
	 */
	public void setRotationX(double rotation) {
		myRotation[0] = MathUtil.normaliseAngle(rotation);
	}
	
	/**
	 * Set the local rotation (in degrees)
	 * @return
	 */
	public void setRotationY(double rotation) {
		myRotation[1] = MathUtil.normaliseAngle(rotation);
	}
	
	/**
	 * Set the local rotation (in degrees)
	 * @return
	 */
	public void setRotationZ(double rotation) {
		myRotation[2] = MathUtil.normaliseAngle(rotation);
	}

	/**
	 * Rotate the object by the given angle (in degrees)
	 * @param angle
	 */
	public void rotateX(double angle) {
		myRotation[0] += angle;
		myRotation[0] = MathUtil.normaliseAngle(myRotation[0]);
	}
	
	/**
	 * Rotate the object by the given angle (in degrees)
	 * @param angle
	 */
	public void rotateY(double angle) {
		System.out.println("RotateY :"+angle);
		myRotation[1] += angle;
		myRotation[1] = MathUtil.normaliseAngle(myRotation[1]);
	}
	
	/**
	 * Rotate the object by the given angle (in degrees)
	 * @param angle
	 */
	public void rotateZ(double angle) {
		myRotation[2] += angle;
		myRotation[2] = MathUtil.normaliseAngle(myRotation[2]);
	}

	/**
	 * Get the local scale
	 * 
	 * @return
	 */
	public double getScale() {
		return myScale;
	}

	/**
	 * Set the local scale
	 * 
	 * @param scale
	 */
	public void setScale(double scale) {
		myScale = scale;
	}

	/**
	 * Multiply the scale of the object by the given factor
	 * 
	 * @param factor
	 */
	public void scale(double factor) {
		myScale *= factor;
	}

	/**
	 * Get the local position of the object 
	 * 
	 * @return
	 */
	public double[] getPosition() {
		double[] t = new double[3];
		t[0] = myTranslation[0];
		t[1] = myTranslation[1];
		t[2] = myTranslation[2];

		return t;
	}

	/**
	 * Set the local position of the object
	 * 
	 * @param x
	 * @param y
	 */
	public void setPosition(double x, double z) {
		myTranslation[0] = x;
		myTranslation[1] = Game.myTerrain.altitude(x,z);
		myTranslation[2] = z;
	}

	public void setPosition(double x, double z, double offset) {
		myTranslation[0] = x;
		myTranslation[1] = Game.myTerrain.altitude(x,z) + offset;
		myTranslation[2] = z;
	}
	
	/**
	 * Move the object by the specified offset in local coordinates
	 * 
	 * @param dx
	 * @param dz
	 */
	public void translate(double dx, double dz) {
		myTranslation[0] += dx;
		myTranslation[1] += dz;
	}

	/**
	 * Test if the object is visible
	 * 
	 * @return
	 */
	public boolean isShowing() {
		return amShowing;
	}

	/**
	 * Set the showing flag to make the object visible (true) or invisible (false).
	 * This flag should also apply to all descendents of this object.
	 * 
	 * @param showing
	 */
	public void show(boolean showing) {
		amShowing = showing;
	}

	
	public void init(GL2 gl) {
		// do nothing
	}
	
	/**
	 * Update the object. This method is called once per frame. 
	 * This does nothing in the base GameObject class. Override this in subclasses.
	 * @param dt The amount of time since the last update (in seconds)
	 */
	public void update(long dt) {
		// do nothing
	}

	
	/**
	 * Draw the object (but not any descendants)
	 * This does nothing in the base GameObject class. Override this in subclasses.
	 * @param gl
	 */
	public void drawSelf(GL2 gl) {
		// do nothing
	}

	/**
	 * Draw the object and all of its descendants recursively.
	 * @param gl
	 */
	public void draw(GL2 gl) {

		// don't draw if it is not showing
		if (!amShowing)	return;

		// save the coordinate frame so people above me are not affected
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glPushMatrix();
		{
			// make my transformations
			gl.glTranslated(this.myTranslation[0],this.myTranslation[1],this.myTranslation[2]);
			System.out.println(myRotation[1]);
			gl.glRotated(this.myRotation[0], 1, 0, 0);
			gl.glRotated(this.myRotation[1], 0, 1, 0);
			gl.glRotated(this.myRotation[2], 0, 0, 1);
			gl.glScaled(this.myScale,this.myScale,this.myScale);

			//draw me
			this.drawSelf(gl);

			//then draw all my children
			for (GameObject child :  myChildren) {
				child.draw(gl);
			}
		}
		gl.glPopMatrix();
	}
/*
	public double[] getGlobalPosition() {

		//get parents matrix
		double[] t = (this.myParent == null ? new double[] {0,0} : this.myParent.getGlobalPosition());
		double r = (this.myParent == null ? 0 : this.myParent.getGlobalRotation());
		double s = (this.myParent == null ? 1 : this.myParent.getGlobalScale());

		double[][] m = MathUtil.makeTransformationMatrix(t,r,s);

		double[] gp = new double[2];
		gp[0] = m[0][2] + this.myTranslation[0]*m[0][0] + this.myTranslation[1]*m[0][1];
		gp[1] = m[1][2] + this.myTranslation[0]*m[1][0] + this.myTranslation[1]*m[1][1];

		return gp;

	}

	public double getGlobalRotation() {

		double r = this.myRotation + (this.myParent == null ? 0 : this.myParent.getGlobalRotation());
		return MathUtil.normaliseAngle(r);
	}

	public double getGlobalScale() {

		double s = this.myScale * (this.myParent == null ? 1 : this.myParent.getGlobalScale());
		return s;
	}

	public void setParent(GameObject parent) {

		//get current global stuff
		//get new parent global stuff
		//apply the difference

		//get my matrix
		double[] p1 = this.getGlobalPosition();
		double r1 = this.getGlobalRotation();
		double s1 = this.getGlobalScale();

		//get parents matrix
		double[] p2 = (parent == null ? new double[] {0,0} : parent.getGlobalPosition());
		double r2 = (parent == null ? 0 : parent.getGlobalRotation());
		double s2 = (parent == null ? 1 : parent.getGlobalScale());

		double[][] m = MathUtil.makeTransformationMatrix(p2,r2,s2);

		// the need transformations must be the difference of the two and in reverse order
		this.myScale = s1/s2;
		this.myRotation = MathUtil.normaliseAngle(r1 - r2);
		this.myTranslation = MathUtil.SolveLinearEquations2(m[0][0], m[0][1], m[1][0], m[1][1], p1[0]-m[0][2], p1[1]-m[1][2]);;

		// actually reparent
		this.myParent.myChildren.remove(this);
		this.myParent = parent;
		this.myParent.myChildren.add(this);

	}
*/
}
