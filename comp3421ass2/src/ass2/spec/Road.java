package ass2.spec;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;

/**
 * Class for Roads
 */
public class Road extends GameObject {

	private List<Double> myPoints;
	private double myWidth;

	private static final int NUM_PARTS = 20;
	private static final double FILL_LENGTH = 0.25d;
	private static final double ROAD_FLAIR = 0.05;
	
	// texture things
	private String roadFile = "src/resources/road.jpg";
	private MyTexture roadTex;

	/** 
	 * Create a new road starting at the specified point
	 */
	public Road(double width, double x0, double y0) {
		super(GameObject.ROOT);
		myWidth = width;
		myPoints = new ArrayList<Double>();
		myPoints.add(x0);
		myPoints.add(y0);
	}

	/**
	 * Create a new road with the specified spine 
	 *
	 * @param width
	 * @param spine
	 */
	public Road(double width, double[] spine) {
		super(GameObject.ROOT);
		myWidth = width;
		myPoints = new ArrayList<Double>();
		for (int i = 0; i < spine.length; i++) {
			myPoints.add(spine[i]);
		}
	}
	
	@Override
	public void init(GL2 gl) {
		roadTex = new MyTexture(gl,roadFile,"jpg",true);
	}

	@Override
	public void drawSelf(GL2 gl) {
		double increment = 1d/NUM_PARTS;
		
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glPushMatrix();
		gl.glPushAttrib(GL2.GL_LIGHTING_BIT);
		{
			double[] start = controlPoint(0);
			gl.glTranslated(start[0], Game.myTerrain.altitude(start[0],start[1])+0.11d, start[1]);
			
			/*
			// Road material vectors
			float matAmbAndDif[] = {0.1f, 0.1f, 0.1f, 1.0f};
			float matSpec[] = { 1.0f, 1.0f, 1.0f, 1.0f };
			float matShine[] = { 50.0f };
			// float emm[] = {0.0f, 0.0f, 0.0f, 1.0f};

			// Road material properties
			gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE, matAmbAndDif,0);
			gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, matSpec,0);
			gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SHININESS, matShine,0);
			// gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_EMISSION, emm,0);
			 */
			gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_REPLACE);
			gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP_TO_EDGE); 
			gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP_TO_EDGE);

			gl.glBindTexture(GL2.GL_TEXTURE_2D, roadTex.getTextureId());
			
			for (int i=0;i<this.size();++i) {
				double[] pc;
				double[] pn = controlPoint(i);
				double ac = 0;
				double an = 0;
				for (double j=increment;j<1d;j+=increment) {
					pc = pn;
					pn = point(i+j);

					double d = Math.sqrt( Math.pow((pn[0]-pc[0]),2) - Math.pow((pn[1]-pn[1]),2) );

					ac = an;
					an = getAngle(gl, pc, pn);
					gl.glRotated(an-ac, 0, 1, 0);
					drawArticulation(gl, pc, d);
					gl.glTranslated(d, 0, 0);
				}
			}
			gl.glBindTexture(GL2.GL_TEXTURE_2D, 0); 
		}
		gl.glPopAttrib();
		gl.glPopMatrix();
	}

	private double getAngle(GL2 gl, double[] pc, double[] pn) {
		double theta = -Math.atan((pn[1]-pc[1])/(pn[0]-pc[0]));
		return Math.toDegrees(theta);
	}

	private void drawArticulation(GL2 gl, double[] pc, double d) {


		// draw main articulation
		gl.glBegin(GL2.GL_POLYGON);
		{
			gl.glTexCoord2d(0, 0); gl.glVertex3d(0, 0, -myWidth/2d);
			gl.glTexCoord2d(1d, 0);gl.glVertex3d(0, 0, myWidth/2d);
			gl.glTexCoord2d(1d, d);gl.glVertex3d(d+FILL_LENGTH, 0, myWidth/2d);
			gl.glTexCoord2d(0, d);gl.glVertex3d(d+FILL_LENGTH, 0, -myWidth/2d);
			gl.glTexCoord2d(0, 0);gl.glVertex3d(0, 0, -myWidth/2d);
		}
		gl.glEnd();
		
		// draw road thickness right
		gl.glBegin(GL2.GL_POLYGON);
		{
			gl.glVertex3d(0, 0, -myWidth/2d);
			gl.glVertex3d(d+FILL_LENGTH, 0, -myWidth/2d);
			gl.glVertex3d(d+FILL_LENGTH, -ROAD_FLAIR, -myWidth/2d-ROAD_FLAIR);
			gl.glVertex3d(0, -ROAD_FLAIR, -myWidth/2d-ROAD_FLAIR);
			gl.glVertex3d(0, 0, -myWidth/2d);
		}
		gl.glEnd();
		
		// draw road thickness left
		gl.glBegin(GL2.GL_POLYGON);
		{
			gl.glVertex3d(0, 0, myWidth/2d);
			gl.glVertex3d(0, -ROAD_FLAIR, myWidth/2d+ROAD_FLAIR);
			gl.glVertex3d(d+FILL_LENGTH, -ROAD_FLAIR, myWidth/2d+ROAD_FLAIR);
			gl.glVertex3d(d+FILL_LENGTH, 0, myWidth/2d);
			gl.glVertex3d(0, 0, myWidth/2d);
		}
		gl.glEnd();

	}

	/**
	 * The width of the road.
	 * 
	 * @return
	 */
	public double width() {
		return myWidth;
	}

	/**
	 * Add a new segment of road, beginning at the last point added and ending at (x3, y3).
	 * (x1, y1) and (x2, y2) are interpolated as bezier control points.
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param x3
	 * @param y3
	 */
	public void addSegment(double x1, double y1, double x2, double y2, double x3, double y3) {
		myPoints.add(x1);
		myPoints.add(y1);
		myPoints.add(x2);
		myPoints.add(y2);
		myPoints.add(x3);
		myPoints.add(y3);        
	}

	/**
	 * Get the number of segments in the curve
	 * 
	 * @return
	 */
	public int size() {
		return myPoints.size() / 6;
	}

	/**
	 * Get the specified control point.
	 * 
	 * @param i
	 * @return
	 */
	public double[] controlPoint(int i) {
		double[] p = new double[2];
		p[0] = myPoints.get(i*2);
		p[1] = myPoints.get(i*2+1);
		return p;
	}

	/**
	 * Get a point on the spine. The parameter t may vary from 0 to size().
	 * Points on the kth segment take have parameters in the range (k, k+1).
	 * 
	 * @param t
	 * @return
	 */
	public double[] point(double t) {
		int i = (int)Math.floor(t);
		t = t - i;

		i *= 6;

		double x0 = myPoints.get(i++);
		double y0 = myPoints.get(i++);
		double x1 = myPoints.get(i++);
		double y1 = myPoints.get(i++);
		double x2 = myPoints.get(i++);
		double y2 = myPoints.get(i++);
		double x3 = myPoints.get(i++);
		double y3 = myPoints.get(i++);

		double[] p = new double[2];

		p[0] = b(0, t) * x0 + b(1, t) * x1 + b(2, t) * x2 + b(3, t) * x3;
		p[1] = b(0, t) * y0 + b(1, t) * y1 + b(2, t) * y2 + b(3, t) * y3;        

		return p;
	}

	/**
	 * Calculate the Bezier coefficients
	 * 
	 * @param i
	 * @param t
	 * @return
	 */
	private double b(int i, double t) {

		switch(i) {

		case 0:
			return (1-t) * (1-t) * (1-t);

		case 1:
			return 3 * (1-t) * (1-t) * t;

		case 2:
			return 3 * (1-t) * t * t;

		case 3:
			return t * t * t;
		}

		// this should never happen
		throw new IllegalArgumentException("" + i);
	}


}
