package ass2.spec;

import javax.media.opengl.GL2;

import com.jogamp.opengl.util.gl2.GLUT;

/**
 * COMMENT: Comment Tree 
 *
 * @author malcolmr
 */
public class Tree {

    private double[] myPos;
    
    public Tree(double x, double y, double z) {
        myPos = new double[3];
        myPos[0] = x;
        myPos[1] = y;
        myPos[2] = z;
    }
    
    public double[] getPosition() {
        return myPos;
    }
    
	public void drawTree (GL2 gl) {
		
		gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glPushMatrix();
        gl.glPushAttrib(GL2.GL_LIGHTING_BIT);
        {
	        gl.glTranslated(myPos[0], myPos[1], myPos[2]);
	        gl.glScaled(0.2, 0.2, 0.2);
	        
	        GLUT glut = new GLUT();
	        
	        // Teapot material vectors
	        float matAmbAndDif[] = {1.0f, 0.0f, 0.0f, 1.0f};
	        float matSpec[] = { 1.0f, 1.0f, 1.0f, 1.0f };
	        float matShine[] = { 50.0f };
	        float emm[] = {0.0f, 0.0f, 0.0f, 1.0f};
	        
	        // Teapot material properties
	        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE, matAmbAndDif,0);
	        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, matSpec,0);
	        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SHININESS, matShine,0);
	        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_EMISSION, emm,0);
	
	        // Draw Teapot
	        gl.glFrontFace(GL2.GL_CW);
	        glut.glutSolidTeapot(1.5);
	        gl.glFrontFace(GL2.GL_CCW);
        }
        gl.glPopAttrib();
        gl.glPopMatrix();
		
	}

}
