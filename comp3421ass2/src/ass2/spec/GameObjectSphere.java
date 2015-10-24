package ass2.spec;

import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLException;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import com.jogamp.common.nio.Buffers;

public class GameObjectSphere extends GameObject {

	private double radius;
	private boolean ground = true;
	
	private final int MAX_STACKS= 10;
    private final int MAX_SLICES = 20;
    
    private double[] vertices;
    private double[] texCoords;
    private double[] normals;
    private short[] indices;
    
    private DoubleBuffer verticesBuffer;
    private DoubleBuffer texBuffer;
    private DoubleBuffer normalsBuffer;
    private ShortBuffer indicesBuffer;
    
    private int[] bufferIDs;
    
    private MyTexture myTexture;
    private String myImage;
    private String ext;
    
    private float lightAmb[];
    private float lightDif[];
    private float lightSpec[];
    private float lightDir[];

	public GameObjectSphere(String texture, String ext) {
		super(GameObject.ROOT);
		radius = 1;
		this.myImage = texture;
		this.ext = ext;
	}

	public GameObjectSphere (double radius, String image, String ext) {
		super(GameObject.ROOT);
		this.radius = radius;
		this.myImage = "src/resources/"+image;
		this.ext = ext;
	}

	@Override
	public void init (GL2 gl) {

		makeSphere();
		makeIndices();
		
		initLights(gl);
		
		verticesBuffer = Buffers.newDirectDoubleBuffer(vertices);
		texBuffer = Buffers.newDirectDoubleBuffer(texCoords);
		normalsBuffer = Buffers.newDirectDoubleBuffer(normals);
		indicesBuffer = Buffers.newDirectShortBuffer(indices);
		
		try {
			myTexture = new MyTexture(gl,myImage,ext,false);
		} catch (GLException e) {
			e.printStackTrace();
		} 
		
		bufferIDs = new int[2];
		gl.glGenBuffers(2, bufferIDs, 0);
		
		gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL2.GL_NORMAL_ARRAY);
		gl.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
		
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, bufferIDs[0]);
		
		gl.glBufferData(GL2.GL_ARRAY_BUFFER, vertices.length*Double.BYTES + texCoords.length*Double.BYTES + normals.length*Double.BYTES, null, GL2.GL_STATIC_DRAW);
		
		// load vertices data
		gl.glBufferSubData(GL2.GL_ARRAY_BUFFER, 0, vertices.length*Double.BYTES, verticesBuffer);
		gl.glVertexPointer(3,GL2.GL_DOUBLE,0,0);
		
		// load vertex texture data
		gl.glBufferSubData(GL2.GL_ARRAY_BUFFER,vertices.length*Double.BYTES,texCoords.length*Double.BYTES,texBuffer);
		gl.glTexCoordPointer(2, GL2.GL_DOUBLE, 0, vertices.length*Double.BYTES);
		
		// load normals data
		gl.glBufferSubData(GL2.GL_ARRAY_BUFFER,vertices.length*Double.BYTES+texCoords.length*Double.BYTES,normals.length*Double.BYTES,normalsBuffer);
		gl.glNormalPointer(GL2.GL_DOUBLE, 0, vertices.length*Double.BYTES+texCoords.length*Double.BYTES);
		
		gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, bufferIDs[1]);
		gl.glBufferData(GL2.GL_ELEMENT_ARRAY_BUFFER, indices.length*Short.BYTES, indicesBuffer, GL2.GL_STATIC_DRAW);
	}

	private void makeIndices() {
		indices = new short[360*(180+1)*2*3];

		for (int i=0;i<179-1;++i) {
			for (int j=0;j<=360-1;++j) {
				indices[2*3*(j+i*360)] = (short)(3*(j+i*360));
				indices[2*3*(j+i*360)+1] = (short)(3*(j+1+i*360));
				indices[2*3*(j+i*360)+2] = (short)(3*(j+(i+1)*360));
				
				indices[2*3*(j+i*360)+3] = (short)(3*(j+1+i*360)+1) ;
				indices[2*3*(j+i*360)+4] = (short)(3*(j+1+(i+1)*360)+1);
				indices[2*3*(j+i*360)+5] = (short)(3*(j+(i+1)*360)+1);
			}
		}
	}
	
	private void makeSphere() {
		
		vertices = new double[(360*(180+1))*3];
		normals = new double[(360*(180+1))*3];
		texCoords = new double[(360*(180+1))*2];
		
		double x1, y1, z1;
		
		for (int i=0;i<180;++i) {
			for (int j=0;j<360;++j) {
				x1 = Math.cos(Math.toRadians(j))*Math.sin(Math.toRadians(i));
				y1 = Math.cos(Math.toRadians(i));
				z1 = Math.sin(Math.toRadians(j))*Math.sin(Math.toRadians(i));
				
				vertices[3*(j+i*360)] = x1;
				vertices[3*(j+i*360)+1] = y1;
				vertices[3*(j+i*360)+2] = z1;
				
				normals[3*(j+i*360)] = x1;
				normals[3*(j+i*360)+1] = y1;
				normals[3*(j+i*360)+2] = z1;
				
				// Textures
				texCoords[2*(j+i*360)] = (double)(i)/180d;
				texCoords[2*(j+i*360)+1] = (double)(j)/360d;
			}
		}
		
	}
	
	private void initLights (GL2 gl) {
		
        // Light 1 is the point light for the terrain
        lightAmb = new float[]{ 0.5f, 0.5f, 0.5f, 1f };
        lightDif = new float[]{ 0.8f, 0.8f, 0.8f, 1.0f };
        lightSpec = new float[]{ 1f, 1f, 1f, 1.0f};
        float[] mySunlight = Game.myTerrain.getSunlight();
        MathUtil.normalizeVector(mySunlight);
//        lightDir = new float[] {(float) (myTranslation[0] + radius*mySunlight[0]*1.1), (float) (myTranslation[1] + radius*mySunlight[1]*1.1), (float) (myTranslation[2] + radius*mySunlight[2]*1.1), 1};
        lightDir = new float[] {(float) myTranslation[0], (float) (myTranslation[1]+radius+2), (float) (myTranslation[2]-4)};
//        lightDir = new float[] {2.5f, 5f, 2.5f};
        System.out.println("lights: "+lightDir[0]+" "+lightDir[1]+" "+lightDir[2]);
	}

	@Override
	public void drawSelf (GL2 gl) {
		gl.glPushAttrib(GL2.GL_LIGHTING_BIT);//saves current lighting stuff
		if (ground) gl.glTranslated(0, radius, 0);
		
        // Set up directional lighting
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_AMBIENT, lightAmb, 0);
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_DIFFUSE, lightDif, 0);
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_SPECULAR, lightSpec, 0);
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, lightDir, 0);

        gl.glEnable(GL2.GL_LIGHT1);
		
		// set up texture things
        gl.glUseProgram(Game.myTerrain.shaderprogram);
    	gl.glUniform1i(Game.myTerrain.texUnitLoc , 0);
    	
    	// Set current texture
    	gl.glActiveTexture(GL2.GL_TEXTURE0);
    	gl.glBindTexture(GL2.GL_TEXTURE_2D, myTexture.getTextureId());
    	      
    	gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE);
    	//Set wrap mode for texture in S direction
    	gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT); 
    	//Set wrap mode for texture in T direction
    	gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);
		
    	// Set up VBOs for terrain
		gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
		gl.glEnableClientState(GL2.GL_NORMAL_ARRAY);
		
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, bufferIDs[0]);
		gl.glVertexPointer(3,GL2.GL_DOUBLE,0,0);
		gl.glTexCoordPointer(2, GL2.GL_DOUBLE, 0, vertices.length*Double.BYTES);
		gl.glNormalPointer(GL2.GL_DOUBLE, 0, vertices.length*Double.BYTES+texCoords.length*Double.BYTES);
		
		gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, bufferIDs[1]);
		
        // Draw Simple Shape using VBOS
		gl.glDrawElements(GL2.GL_TRIANGLES, indices.length, GL.GL_UNSIGNED_SHORT, 0);
		
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER,0);
		gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER,0);
    	gl.glBindTexture(GL2.GL_TEXTURE_2D,0);     
    	gl.glUseProgram(0);
        
    	gl.glDisableClientState(GL2.GL_NORMAL_ARRAY);
		gl.glDisableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
		gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
		
		gl.glDisable(GL2.GL_LIGHT1);
		gl.glPopAttrib();
	}

}
