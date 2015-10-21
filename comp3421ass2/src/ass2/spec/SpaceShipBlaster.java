package ass2.spec;

import java.util.Random;

import javax.media.opengl.GL2;

public class SpaceShipBlaster extends SpaceShip {

	boolean blastOff;
	long start;
	Random r;
	
	public SpaceShipBlaster(double scale) {
		super(scale, false);
		blastOff = false;
	}

	@Override
	public void init (GL2 gl) {
		super.init(gl);
		r = new Random(System.currentTimeMillis());
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
			} else if (myTranslation[2] < 20) {
				System.out.println("BLASTOFF!!!!");
				myTranslation[1] += 0.08d*Math.pow((dt-start)/10000,3);
			} else {
				show(false);
			}
		}
	}
}
