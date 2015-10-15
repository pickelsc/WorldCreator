package ass2.spec;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;

import javax.media.opengl.DebugGL2;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLJPanel;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;

import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.gl2.GLUT;

/**
 * COMMENT: Comment Game
 *
 * @author malcolmr
 */
public class Game extends JFrame implements GLEventListener, KeyListener {

	private Terrain myTerrain;
	private Camera myCamera;
	
	private static final float GLOBAL_AMBIENCE = 0.2f; // Global ambient white light intensity.
	
	public Game(Terrain terrain) {
		super("Assignment 2");
		myTerrain = terrain;
	}
	
	/**
	 * Run the game.
	 *
	 */
	public void run() {
		// basic setup
		GLProfile glp = GLProfile.getDefault();
		GLCapabilities caps = new GLCapabilities(glp);
		
		GLJPanel panel = new GLJPanel();
		panel.addGLEventListener(this);
		panel.addKeyListener(this);
		panel.setFocusable(true);
		
		
		getContentPane().add(panel);
		setSize(800, 600);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		// Add an animator to call 'display' at 60fps
		FPSAnimator animator = new FPSAnimator(60);
		animator.add(panel);
		animator.start();


	}

	/**
	 * Load a level file and display it.
	 * 
	 * @param args
	 *            - The first argument is a level file in JSON format
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws FileNotFoundException {
		//Terrain terrain = LevelIO.load(new File(args[0]));
		Terrain terrain =  new Terrain(5, 5);
		Game game = new Game(terrain);
		game.run();
	}


	@Override
	public void display(GLAutoDrawable drawable) {

		GL2 gl = drawable.getGL().getGL2();

		gl.glClearColor(0.04f, 0.2f, 0.76f, 0.0f);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		
		myCamera.setView(gl);
		
//		gl.glMatrixMode(GL2.GL_PROJECTION);
//		gl.glLoadIdentity();
//		glu.gluLookAt(2.5, 3.0, 10.0, 2.5, 2.5, 2.5, 0.0, 1.0, 0.0);
		// apply necesary transformation
//		gl.glScaled(1.0/getScale(), 1.0/getScale(), 1);
//		gl.glRotated(-10, 1, 0, 0);
//		gl.glTranslated(-5, -5, -5);
		
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		
		// Global Ambience
		float globAmb[] = { GLOBAL_AMBIENCE, GLOBAL_AMBIENCE, GLOBAL_AMBIENCE, 1.0f };
        gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, globAmb,0); // Global ambient light.
		
        
		myTerrain.drawTerrain(gl);
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(GLAutoDrawable drawable) {

		//drawable.setGL(new DebugGL2(drawable.getGL().getGL2()));
		GL2 gl = drawable.getGL().getGL2();


		 // Enable depth testing.
		gl.glEnable(GL2.GL_DEPTH_TEST);
		
		// Enable Lighting
    	gl.glEnable(GL2.GL_LIGHTING);
    	gl.glEnable(GL2.GL_LIGHT0);
    	
    	gl.glEnable(GL2.GL_NORMALIZE);
    	
    	// Cull back faces.
    	gl.glEnable(GL2.GL_CULL_FACE);
    	gl.glCullFace(GL2.GL_BACK);
		
    	myCamera = new Camera();
    	
    	/*
    	// Anti Aliasing + Alpha Blending
    	gl.glEnable(GL2.GL_LINE_SMOOTH);
    	gl.glHint(GL2.GL_LINE_SMOOTH_HINT,GL2.GL_NICEST);
    	gl.glEnable(GL2.GL_BLEND);
    	gl.glBlendFunc(GL2.GL_SRC_ALPHA,GL2.GL_ONE_MINUS_SRC_ALPHA);
    	GLCapabilities capabilities = new GLCapabilities(null);
    	capabilities.setNumSamples(4);
    	capabilities.setSampleBuffers(true);
    	gl.glEnable(GL.GL_MULTISAMPLE);
    	*/
    	
    	/*
    	// Enable Textures
    	gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE); 
    	gl.glLightModeli(GL2.GL_LIGHT_MODEL_COLOR_CONTROL, GL2.GL_SEPARATE_SPECULAR_COLOR);
		*/
		
		// Create Terrain
		myTerrain.initTerrain(gl);
		
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
		GL2 gl = drawable.getGL().getGL2();
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();

		gl.glFrustum(-3.0, 3.0, -3.0, 3.0, 5.0, 100.0);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();

	}
	

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

    @Override
	public void keyPressed(KeyEvent e) {
    	switch (e.getKeyCode()) {
    	case KeyEvent.VK_W:
    		myCamera.WalkForward();
    		break;
    		
    	case KeyEvent.VK_S:
    		myCamera.WalkBackward();
    		break;
    		
    	case KeyEvent.VK_A:
    		myCamera.WalkLeft();
    		break;
    		
    	case KeyEvent.VK_D:
    		myCamera.WalkRight();
    		break;
    	
		case KeyEvent.VK_UP:
			myCamera.lookUp();
			break;
			 
		case KeyEvent.VK_DOWN:
			myCamera.lookDown();
			break;
		
		case KeyEvent.VK_LEFT:
			 myCamera.lookLeft();
			 break;
			 
		case KeyEvent.VK_RIGHT:
			 myCamera.lookRight();
			 break;
			
		case KeyEvent.VK_SPACE:
			break;
		
		default:
			break;
    	}
		
    }

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}
