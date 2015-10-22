package ass2.spec;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLException;

public class GameObjectDeathStar extends GameObjectSphere {

	private MyTexture deathstarTexture;
	private String deathstarImage = "src/resources/deathstar.jpg";
	
	@Override
	public void init (GL2 gl) {
		super.init(gl);
		//Load the texture image
		try {
			deathstarTexture = new MyTexture(gl,deathstarImage,"jpg",false);
		} catch (GLException e) {
			e.printStackTrace();
		} 
	}
	
	@Override
	public void drawSelf(GL2 gl) {
		gl.glBindTexture(GL.GL_TEXTURE_2D, deathstarTexture.getTextureId());
		super.drawSelf(gl);
		gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
	}
}
