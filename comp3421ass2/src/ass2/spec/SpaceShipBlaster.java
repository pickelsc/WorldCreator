package ass2.spec;

import static javax.media.opengl.GL.GL_TRIANGLE_STRIP;

import java.util.Random;

import javax.media.opengl.GL2;
import javax.media.opengl.GLException;



public class SpaceShipBlaster extends SpaceShip {

	boolean blastOff;
	long start;
	Random r;


	// Particle things
	private static final int MAX_PARTICLES = 1000; // max number of particles
	private Particle[] particles = new Particle[MAX_PARTICLES];

	private static boolean enabledBurst = false;

	// Pull forces in each direction
	private static float gravityY = -0.0008f; // gravity

	// Global speed for all the particles
	private static float speedYGlobal = 0.1f;
	private static float z = -40.0f; //zOffset
	private static float y = 5.0f;   //yOffset

	// Texture applied over the shape
	private MyTexture starTexture;
	private String starImage = "src/resources/star.bmp";

	public SpaceShipBlaster(double scale) {
		super(scale, false);
		blastOff = false;
	}

	@Override
	public void drawSelf (GL2 gl) {
		super.drawSelf(gl);


		if (blastOff) {
			if (!enabledBurst) enabledBurst = true;
			// Render the particles
			for (int i = 0; i < MAX_PARTICLES; i++) {
				if (particles[i].active) {
					// Draw the particle using our RGB values, fade the particle based on it's life
					gl.glColor4f(particles[i].r, particles[i].g, particles[i].b, particles[i].life);

					gl.glBindTexture(GL2.GL_TEXTURE_2D, starTexture.getTextureId()); 

					gl.glBegin(GL_TRIANGLE_STRIP); // build quad from a triangle strip

					float px = particles[i].x;
					float py = particles[i].y;
					float pz = particles[i].z;

					gl.glTexCoord2d(1, 1);
					gl.glVertex3f(px + 0.5f, py + 0.5f, pz); // Top Right
					gl.glTexCoord2d(0, 1);
					gl.glVertex3f(px - 0.5f, py + 0.5f, pz); // Top Left
					gl.glTexCoord2d(1, 0);
					gl.glVertex3f(px + 0.5f, py - 0.5f, pz); // Bottom Right
					gl.glTexCoord2d(0, 0);
					gl.glVertex3f(px - 0.5f, py - 0.5f, pz); // Bottom Left
					gl.glEnd();

					// Move the particle
					particles[i].x += particles[i].speedX;
					particles[i].y += particles[i].speedY;
					particles[i].z += particles[i].speedZ;

					//particles[i].life -= 0.01;

					// Apply the gravity force on y-axis
					particles[i].speedY += gravityY;
					
					gl.glBindTexture(GL2.GL_TEXTURE_2D, 0); 

					if (enabledBurst) {
						particles[i].burst();
					}
				}
			}
			if (enabledBurst) enabledBurst = false;
		}
	}

	@Override
	public void init (GL2 gl) {
		super.init(gl);
		r = new Random(System.currentTimeMillis());

		//Load the texture image
		try {
			starTexture = new MyTexture(gl,starImage,"bmp",false);
			//gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_REPLACE);
		} catch (GLException e) {
			e.printStackTrace();
		} 

		// Initialize the particles
		for (int i = 0; i < MAX_PARTICLES; i++) {
			particles[i] = new Particle();
		}
	}

	@Override
	public void update (long dt) {
		//		super.update(dt);
		if (blastOff == false) {
			double[] p = Game.myCamera.getPosition();
			if (MathUtil.distanceBetweenPoints(myTranslation, p) < 2) {
				System.out.println("INITIATE ROCKET ALPHA!");
				blastOff = true;
				start = dt;
			}
		} else {
			double xr = r.nextDouble();
			double x = xr*0.03f*MathUtil.sinTable[(int) ((dt%540)/1.5)];
			myTranslation[0] -= x;

			double zr = r.nextDouble();
			double z = zr*0.04f*MathUtil.sinTable[(int) (dt%360)];
			myTranslation[2] -= z;

			if ((dt - start) < 10000) {
				double yr = r.nextDouble();
				double y = yr*0.01f*MathUtil.cosTable[(int) (dt%360)];
				myTranslation[1] -= y;
			} else if (myTranslation[1] < 20) {
				System.out.println("BLASTOFF!!!!");
				myTranslation[1] += 0.1d*Math.pow((dt-start)/10000,3);


			} else {
				show(false);
			}
		}
	}

	// Particle (inner class)
	class Particle {
		boolean active; // always active in this program
		float life;     // life time
		float fade;     // fading speed, which reduces the life time
		float r, g, b;  // color
		float x, y, z;  // position
		float speedX, speedY, speedZ; // speed in the direction

		private final float[][] colors = {    // rainbow of 12 colors
				{ 1.0f, 0.5f, 0.5f }, { 1.0f, 0.75f, 0.5f }, { 1.0f, 1.0f, 0.5f },
				{ 0.75f, 1.0f, 0.5f }, { 0.5f, 1.0f, 0.5f }, { 0.5f, 1.0f, 0.75f },
				{ 0.5f, 1.0f, 1.0f }, { 0.5f, 0.75f, 1.0f }, { 0.5f, 0.5f, 1.0f },
				{ 0.75f, 0.5f, 1.0f }, { 1.0f, 0.5f, 1.0f }, { 1.0f, 0.5f, 0.75f } };

		private Random rand = new Random();

		// Constructor
		public Particle() {
			active = true;
			//burst();
		}

		public void burst() {
			life = 1.0f;

			// Set a random fade speed value between 0.003 and 0.103
			fade = rand.nextInt(100) / 1000.0f + 0.003f;

			// Set the initial position
			x = y = z = 0.0f;

			// Generate a random speed and direction in polar coordinate, then resolve
			// them into x and y.
			float maxSpeed = 0.1f;
			float speed = 0.02f + (rand.nextFloat() - 0.5f) * maxSpeed; 
			float angle = (float)Math.toRadians(rand.nextInt(360));

			speedX = speed * (float)Math.cos(angle);
			speedY = speed * (float)Math.sin(angle) + speedYGlobal;
			speedZ = (rand.nextFloat() - 0.5f) * maxSpeed;

			int colorIndex = (int)(((speed - 0.02f) + maxSpeed) / (maxSpeed * 2) * colors.length) % colors.length;
			// Pick a random color
			r = colors[colorIndex][0];
			g = colors[colorIndex][1];
			b = colors[colorIndex][2];
		}
	}
}
