package ass2.spec;

import javax.media.opengl.GL2;

import com.jogamp.opengl.util.gl2.GLUT;

public class GameObjectSphere extends GameObject {

	private double radius;
	
	public GameObjectSphere(GameObject parent) {
		super(parent);
	}
	
	public GameObjectSphere (GameObject parent, double radius) {
		super(parent);
		this.radius = radius;
	}

	@Override
	public void drawSelf (GL2 gl) {
		GLUT glut = new GLUT();
		glut.glutSolidSphere(radius, 10, 10);
	}
	
	@Override
	public void update(double dt) {
		System.out.println(dt);
//		this.translate(MathUtil.sinTable[dt%], dy);
	}
}
