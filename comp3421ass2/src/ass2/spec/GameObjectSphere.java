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
//		glut.glutSolidTeapot(1.5);
	}
	
	@Override
	public void update(long dt) {
//		System.out.println(MathUtil.sinTable[(int) (dt%7200)/20]+", "+MathUtil.cosTable[(int) (dt%7200)/20]);
//		this.translate(MathUtil.sinTable[(int) (dt%7200)/120], MathUtil.cosTable[(int) (dt%7200)/120]);
		this.setPosition(2.5+MathUtil.sinTable[(int) (dt%7200)/20],2.5+MathUtil.cosTable[(int) ((dt)%7200)/20]);
//		System.out.println(myTranslation[0]+", "+myTranslation[2]);
	}
}
