package ass2.spec;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import com.jogamp.opengl.util.gl2.GLUT;

public class GameObjectSphere extends GameObject {

	private double radius;
	private boolean ground = true;
	
	public GameObjectSphere() {
		super(GameObject.ROOT);
		radius = 1;
	}
	
	public GameObjectSphere (double radius) {
		super(GameObject.ROOT);
		this.radius = radius;
	}

	@Override
	public void drawSelf (GL2 gl) {
		GLUT glut = new GLUT();
		if (ground) {
			gl.glTranslated(0, radius, 0);
		}
		gl.glPushAttrib(GL2.GL_LIGHTING_BIT);
		{
	        // Teapot material vectors
	        float matAmbAndDif[] = {0.5f, 0.5f, 0.5f, 0.5f};
	        float matSpec[] = { 1.0f, 1.0f, 1.0f, 0.5f };
	        float matShine[] = { 50.0f };
	        float emm[] = {0.1f, 0.8f, 0.6f, 1.0f};
	        
	        // Teapot material properties
	        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE, matAmbAndDif,0);
	        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, matSpec,0);
	        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SHININESS, matShine,0);
	        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_EMISSION, emm,0);
	        
	        GLU glu = new GLU();
            // the built in glut sphere does not have texture coordinates set
            //glut.glutSolidSphere(1, 20, 20);
            GLUquadric quadric = glu.gluNewQuadric();
            glu.gluQuadricTexture(quadric, true);
            glu.gluQuadricNormals(quadric, GLU.GLU_SMOOTH);
            glu.gluSphere(quadric, 0.5d, 64, 64);
		}
		gl.glPopAttrib();
	}
	
}
