package ass2.spec;

import java.util.ArrayList;
import java.util.Random;

import javax.media.opengl.GL2;

import com.jogamp.opengl.util.gl2.GLUT;

/**
 * COMMENT: Comment Tree 
 *
 * @author malcolmr
 */
public class Tree extends GameObject {

	private static final int DEPTH = 6;
	private float ANGLE = 20;
	private float num;
	private float lineWidth = 5f;
	private float length = 0.05f;
	private int delay;
	
	private Random r;
	
	private int currentTree;
	
	private ArrayList<String> trees;


	public Tree(double x, double y, double z) {
		super(GameObject.ROOT);
		myTranslation[0] = x;
		myTranslation[1] = y;
		myTranslation[2] = z;
		trees = new ArrayList<String>();
	}

	public void increaseTree () {
		if (currentTree < DEPTH) ++currentTree;
	}
	
	public void decreaseTree () {
		if (currentTree > 1) --currentTree;
	}
	
	@Override
	public void drawSelf (GL2 gl) {
		drawLTree(gl);
	}
	
	@Override
	public void update (long dt) {
		
		float incr = 0.1f*(float) MathUtil.sinTable[(int) ((dt+delay)%7200)/20];
		ANGLE += incr;
	}

	@Override
	public void init(GL2 gl) {
		r = new Random(System.currentTimeMillis());
		delay = r.nextInt();
		String str = "X";

		for(int i = 0; i <= DEPTH; i++){
			num = r.nextFloat();
			System.out.println(num+" "+str);
			str = expand(num, str);
			trees.add(str);
		}
		currentTree = DEPTH-1;
	}

	String expand(float num, String str){
		char ch;
		String tmp = "";

		for (int i = 0; i < str.length(); i++){
			ch = str.charAt(i);
			if (ch == 'D'){
				tmp += "DD";
			} else if (ch == 'X'){
				if (num < 0.4f){
					tmp += "D[LXV]D[RDXV]LX";
				} else {
					tmp += "D[RXV]D[LDXV]RX";
				}
			} else {
				tmp += ch;
			}
		}
		return tmp;
	}

	void drawLTree (GL2 gl){

		char ch;
		String str = trees.get(currentTree-1);
		for (int i = 0; i < str.length(); i++){
			ch = str.charAt(i);
			switch(ch) {
			case 'D':
			case 'X':
				drawLine(gl);
				break;
			case '[':
				push(gl);
				break;
			case ']':
				pop(gl);
				break;
			case 'V':
				leaf(gl);
				break;
			case 'R':
				rotR(gl);
				break;
			case 'L':
				rotL(gl);
				break;
			}
		}
	}

	void push(GL2 gl){
		gl.glPushMatrix();
		if (lineWidth > 0)
			lineWidth -= 1;
	}

	void pop(GL2 gl){
		gl.glPopMatrix();
		lineWidth += 1;
	}

	void rotL(GL2 gl){
		gl.glRotatef(ANGLE, 1, 0, 0);
		gl.glRotatef(ANGLE*4, 0, 1, 0);
		gl.glRotatef(ANGLE, 0, 0, 1);
	}
	void rotR(GL2 gl){
		gl.glRotatef(-ANGLE, 1, 0, 0);
		gl.glRotatef(ANGLE*4, 0, 1, 0);
		gl.glRotatef(-ANGLE, 0, 0, 1);
	}
	void leaf(GL2 gl){
		gl.glPushAttrib(GL2.GL_LIGHTING_BIT);//saves current lighting stuff
		//glColor3f(0.50, 1.0, 0.0);
		float ambient[] = new float[]{ 0.16f, 0.3f, 0.0f };    // ambient reflection
		float specular[] = new float[]{ 0.32f, 0.6f, 0.0f };   // specular reflection
		float diffuse[] = new float[]{ 0.16f, 0.3f, 0.0f };   // diffuse reflection

		// set the material properties for leaf
		gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, ambient,0);
		gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, diffuse,0);
		gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_SPECULAR, specular,0);
		gl.glMaterialf(GL2.GL_FRONT_AND_BACK, GL2.GL_SHININESS, 20.0f);

		
		//glutSolidCube(depth+1);
		gl.glBegin(GL2.GL_TRIANGLES);
		{
			gl.glVertex3f(0f, 0f, 0f);
			gl.glVertex3f(0.2f, 0f, 0.3f);
			gl.glVertex3f(0f, 1f, 0f);
			gl.glVertex3f(0f, 0f, 0f);
			gl.glVertex3f(-0.2f, 0f, -0.3f);
			gl.glVertex3f(0f, 1f, 0f);
		}
		gl.glEnd();
		gl.glPopAttrib();
	}

	void drawLine(GL2 gl){
		gl.glPushAttrib(GL2.GL_LIGHTING_BIT);

		//glColor3f(0.55, 0.27, 0.07);
		float ambient[] = new float[]{0.55f, 0.27f, 0.07f};    // ambient reflection
		float diffuse[] = new float[]{0.55f, 0.27f, 0.07f};   // diffuse reflection
		//		float specular[] = new float[]{0.55f, 0.27f, 0.07f};   // specular reflection

		// material properties for the line
		gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, ambient,0);
		gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, diffuse,0);
		//glMaterialfv(GL_FRONT_AND_BACK, GL_SPECULAR, specular);
		gl.glLineWidth(lineWidth);

		float[] mv = new float[16];
		
		gl.glBegin(GL2.GL_LINES);
		{
			gl.glPushMatrix();
			gl.glGetFloatv(GL2.GL_MODELVIEW_MATRIX, mv, 0);
			gl.glVertex3f(0, 0, 0);
			gl.glVertex3f(0, length, 0);
			gl.glPopMatrix();
			
		}
		gl.glEnd();
		

		gl.glTranslatef(0, length, 0);
		gl.glPopAttrib();
	}

}
