package ass2.spec;

/** Functions for drawing a space ship
 *  For now do not worry about any lines that involve texturing - we will cover that soon.
 */ 

import javax.media.opengl.*;
import javax.media.opengl.glu.*;

public class SpaceShip extends GameObject {

	private final int SLICES = 32;
	
	private String metalFile = "src/resources/metal.jpg";
	private MyTexture metalTex;

	private float n[][] =
		{
				{-1.0f, 0.0f, 0.0f},
				{0.0f, 1.0f, 0.0f},
				{1.0f, 0.0f, 0.0f},
				{0.0f, -1.0f, 0.0f},
				{0.0f, 0.0f, 1.0f},
				{0.0f, 0.0f, -1.0f}
		};
	private int faces[][] =
		{
				{0, 1, 2, 3},
				{3, 2, 6, 7},
				{7, 6, 5, 4},
				{4, 5, 1, 0},
				{5, 6, 2, 1},
				{7, 4, 0, 3}
		};

	public SpaceShip(double scale) {
		super(GameObject.ROOT);
		this.setScale(scale);
	}
	
	@Override
	public void init (GL2 gl) {
		metalTex = new MyTexture(gl,metalFile,"jpg",true);
	}

	@Override
	public void drawSelf (GL2 gl){

		gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_REPLACE);
		gl.glBindTexture(GL2.GL_TEXTURE_2D, metalTex.getTextureId());


		GLU glu = new GLU();
		GLUquadric qobj = glu.gluNewQuadric();
		glu.gluQuadricDrawStyle(qobj, GLU.GLU_FILL);
		glu.gluQuadricTexture(qobj, true);
		gl.glTranslatef(0,0,-1.5f);
		gl.glPushMatrix();
		gl.glScaled(1, 1, 4);
		glu.gluCylinder(qobj, 0.7, 0.3, 1.0, SLICES, 5);
		gl.glPopMatrix();
		gl.glPushMatrix();
		gl.glRotatef(25, 0, 0, 1);
		wing(gl);
		gl.glRotatef(-50, 0, 0, 1);
		wing(gl);
		gl.glRotatef(-150, 0, 0, 1);
		wing(gl);
		gl.glRotatef(-30, 0, 0, 1);
		wing(gl);
		gl.glPopMatrix();
		gl.glPushMatrix();
		gl.glTranslatef(0, 0, 4);
		glu.gluCylinder(qobj, 0.3, 0, 0.4, SLICES, 5);
		gl.glPopMatrix();
		
		gl.glBindTexture(GL2.GL_TEXTURE_2D, 0); 
	} 

	private void cube(GL2 gl, double size){
		float v[][] = new float[8][3];
		int i;

		v[0][0] = v[1][0] = v[2][0] = v[3][0] = (float)-size / 2;
		v[4][0] = v[5][0] = v[6][0] = v[7][0] = (float)size / 2;
		v[0][1] = v[1][1] = v[4][1] = v[5][1] = (float)-size / 2;
		v[2][1] = v[3][1] = v[6][1] = v[7][1] = (float)size / 2;
		v[0][2] = v[3][2] = v[4][2] = v[7][2] = (float)-size / 2;
		v[1][2] = v[2][2] = v[5][2] = v[6][2] = (float)size / 2;

		for (i = 5; i >= 0; i--) {
			gl.glBegin(GL2.GL_POLYGON);
			gl.glNormal3fv(n[i],0);
			gl.glTexCoord2f(0,0);
			gl.glVertex3fv(v[faces[i][0]],0);
			gl.glTexCoord2f(1,0);
			gl.glVertex3fv(v[faces[i][1]],0);
			gl.glTexCoord2f(1,1);
			gl.glVertex3fv(v[faces[i][2]],0);
			gl.glTexCoord2f(0,1);
			gl.glVertex3fv(v[faces[i][3]],0);
			gl.glEnd();
		}
	}


	private void wing(GL2 gl){
		// Body of the wing. 
		gl.glPushMatrix();
		gl.glScaled(2.5, 0.1, 1);
		gl.glTranslated(0.5, 0, 0.5);
		cube(gl,1);
		gl.glPopMatrix();
		gl.glPushMatrix();
		gl.glTranslated(2.5, 0, 0);
		cannon(gl);
		gl.glPopMatrix();
	}

	private void cannon(GL2 gl){
		gl.glDisable(GL2.GL_CULL_FACE);
		GLU glu = new GLU();
		GLUquadric qobj = glu.gluNewQuadric();
		glu.gluQuadricTexture(qobj, true);
		gl.glPushMatrix();
		glu.gluQuadricDrawStyle(qobj, GLU.GLU_FILL);
		glu.gluCylinder(qobj, 0.1, 0.1, 1.2, 10, 5); 
		glu.gluCylinder(qobj, 0.05, 0.05, 2.4, 10, 5); 
		gl.glPopMatrix();
		gl.glEnable(GL2.GL_CULL_FACE);
	}
}
