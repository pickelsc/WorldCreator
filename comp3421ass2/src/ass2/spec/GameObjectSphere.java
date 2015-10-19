package ass2.spec;

import javax.media.opengl.GL2;

import com.jogamp.opengl.util.gl2.GLUT;

public class GameObjectSphere extends GameObject {

	private double radius;
	private boolean ground = true;
	
	public GameObjectSphere(GameObject parent) {
		super(parent);
		radius = 1;
	}
	
	public GameObjectSphere (GameObject parent, double radius) {
		super(parent);
		this.radius = radius;
	}

	@Override
	public void drawSelf (GL2 gl) {
		GLUT glut = new GLUT();
		if (ground) {
			gl.glTranslated(0, radius, 0);
		}
		glut.glutSolidSphere(radius, 10, 10);
//		glut.glutSolidTeapot(1.5);
	}
	
}
