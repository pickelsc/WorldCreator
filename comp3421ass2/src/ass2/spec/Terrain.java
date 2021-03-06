package ass2.spec;

import java.awt.Dimension;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.util.gl2.GLUT;

/**
 * COMMENT: Comment HeightMap 
 *
 * @author malcolmr
 */
public class Terrain {

	private Dimension mySize;
	private double[][] myAltitude;
	private List<Tree> myTrees;
	private List<Road> myRoads;
	private float[] mySunlight;


	private double[] highest = new double[] {0,0,0};
	float terrainSpecular[] = {0.99f, 0.91f, 0.81f, 1.0f};

	// VBO things
	public int bufferIDs[];
	private double vertices[];
	private short indices[];
	private float colours[];
	private float texCoords[];
	private double normals[];

	private DoubleBuffer verticesBuffer;
	private ShortBuffer indicesBuffer;
	private FloatBuffer texBuffer;
	private FloatBuffer colourBuffer;
	private DoubleBuffer normalsBuffer;

	// lighting
	private float diffuse = 1.0f;
	private float specular = 1.0f;
	private float light0Amb[];
	private float light0Dif[];
	private float light0Spec[];
	private float light0Dir[];

	private float light1Amb[];
	private float light1Dif[];
	private float light1Spec[];
	private float light1Dir[];

	// textures using shaders
	private String terrainImage = "src/resources/spaceship_interior.jpg";
	private MyTexture terrainTexture; 
	public int texUnitLoc;
	public int shaderprogram;

	// shaders
	private static final String VERTEX_SHADER = "src/resources/PhongVertexTex.glsl";
	private static final String FRAGMENT_SHADER = "src/resources/PhongFragmentTex.glsl";

	/**
	 * Create a new terrain
	 *
	 * @param width The number of vertices in the x-direction
	 * @param depth The number of vertices in the z-direction
	 */
	public Terrain(int width, int depth) {
		mySize = new Dimension(width, depth);
		myAltitude = new double[width][depth];
		myTrees = new ArrayList<Tree>();
		myRoads = new ArrayList<Road>();
		mySunlight = new float[3];
	}

	public Terrain(Dimension size) {
		this(size.width, size.height);
	}

	public Dimension size() {
		return mySize;
	}

	public List<Tree> trees() {
		return myTrees;
	}

	public List<Road> roads() {
		return myRoads;
	}

	public float[] getSunlight() {
		return mySunlight;
	}

	public double[] getHighest() {
		return highest;
	}

	/**
	 * Generates the vertices that will form the mesh for the terrain
	 * Creates the mesh with a mid point between all the altitude points
	 * which has the altitude as the average of the four points
	 *     +-----+  
	 *	   |\   /|  
	 *	   |  +  |
	 *	   |/   \|
	 *	   +-----+
	 */
	public void makeVertices() {

		//					corner points				  + 	middle points
		vertices = new double[(mySize.height*mySize.width+(mySize.height-1)*(mySize.width-1))*3];

		// for each point in the altitudes
		for (int i=0; i<mySize.height; ++i) {
			for (int j=0; j<mySize.width; ++j) {
				vertices[3*(j+i*mySize.width)] = j;
				vertices[3*(j+i*mySize.width)+1] = myAltitude[j][i];
				vertices[3*(j+i*mySize.width)+2] = i;
				//				System.out.println("made vertices: "+(count++)+" "+(3*(j+i*mySize.width))+" ["+j+", "+myAltitude[j][i]+", "+i+"]");
				//				System.out.println(j+", "+myAltitude[j][i]+", "+i+",");
				if (myAltitude[j][i] > highest[1]) {
					//					System.out.println("New Highest: "+j+" "+myAltitude[j][i]+" "+i);
					highest[0] = j;
					highest[1] = myAltitude[j][i];
					highest[2] = i;
				}
			}
		}

		int offset = mySize.height*mySize.width*3;

		// for each mid grid point
		for (int i=0; i<mySize.height-1; ++i) {
			for (int j=0; j<mySize.width-1; ++j) {
				vertices[offset+3*(j+i*(mySize.width-1))] = j+0.5;
				vertices[offset+3*(j+i*(mySize.width-1))+1] = getAverageAlt(j,i);
				vertices[offset+3*(j+i*(mySize.width-1))+2] = i+0.5;
				//				System.out.println("made vertices: ["+(j+0.5)+", "+getAverageAlt(j,i)+", "+(i+0.5)+"]");
				//				System.out.println((j+0.5)+", "+getAverageAlt(j,i)+", "+(i+0.5)+",");
			}
		}


	}

	/**
	 * Given the top left point of a sqr, gets the average altitude of the
	 * points surrounding the mid
	 * @param a
	 * @param b
	 * @return
	 */
	private double getAverageAlt(int a, int b) {
		//		System.out.println(myAltitude[a][b]+" "+myAltitude[a+1][b]+" "+myAltitude[a][b+1]+" "+myAltitude[a+1][b+1]);
		MathUtil.clamp(a, 0, mySize.width);
		MathUtil.clamp(b, 0, mySize.height);
		return (myAltitude[a][b]+myAltitude[a+1][b]+myAltitude[a][b+1]+myAltitude[a+1][b+1])/4;
	}

	/**
	 * Generates the indices of the mesh for the terrain
	 * Four triangles are are generated for each square of altitudes
	 * Each is defined in counter clockwise 
	 * 		top
	 * 	   +-----+  
	 *	   |\   /|  
	 * left|  +  |right
	 *	   |/   \|
	 *	   +-----+
	 *		bottom
	 */
	public void makeIndices() {
		// the amount of sqrs * 4 triangles per square * 3 vertices per triangle
		indices = new short[(mySize.height-1)*(mySize.width-1)*4*3];

		// to find the correct middle points
		int offset = mySize.height*mySize.width;

		// for each sqr with top left corner (x=j,z=i)
		for (short i=0; i<mySize.height-1; ++i) {
			for (short j=0; j<mySize.width-1; ++j) {

				// for each square there is four triangles
				// top
				indices[3*4*(j+i*(mySize.width-1))] = (short) (j+i*mySize.width);
				indices[3*4*(j+i*(mySize.width-1))+1] = (short) (offset+(j+i*(mySize.width-1)));
				indices[3*4*(j+i*(mySize.width-1))+2] = (short) (j+1+i*mySize.width);
				// left
				indices[3*4*(j+i*(mySize.width-1))+3] = (short) (j+(i+1)*mySize.width);
				indices[3*4*(j+i*(mySize.width-1))+4] = (short) (offset+(j+i*(mySize.width-1)));
				indices[3*4*(j+i*(mySize.width-1))+5] = (short) (j+i*mySize.width);
				// bottom
				indices[3*4*(j+i*(mySize.width-1))+6] = (short) (j+1+(i+1)*mySize.width);
				indices[3*4*(j+i*(mySize.width-1))+7] = (short) (offset+(j+i*(mySize.width-1)));
				indices[3*4*(j+i*(mySize.width-1))+8] = (short) (j+(i+1)*mySize.width);
				// right
				indices[3*4*(j+i*(mySize.width-1))+9] = (short) (j+1+i*mySize.width);
				indices[3*4*(j+i*(mySize.width-1))+10] = (short) (offset+(j+i*(mySize.width-1)));
				indices[3*4*(j+i*(mySize.width-1))+11] = (short) (j+1+(i+1)*mySize.width);

				//				System.out.println(indices[3*4*(j+i*(mySize.width-1))]+" "+
				//						indices[3*4*(j+i*(mySize.width-1))+1]+" "+
				//						indices[3*4*(j+i*(mySize.width-1))+2]+" "+
				//						indices[3*4*(j+i*(mySize.width-1))+3]+" "+
				//						indices[3*4*(j+i*(mySize.width-1))+4]+" "+
				//						indices[3*4*(j+i*(mySize.width-1))+5]+" "+
				//						indices[3*4*(j+i*(mySize.width-1))+6]+" "+
				//						indices[3*4*(j+i*(mySize.width-1))+7]+" "+
				//						indices[3*4*(j+i*(mySize.width-1))+8]+" "+
				//						indices[3*4*(j+i*(mySize.width-1))+9]+" "+
				//						indices[3*4*(j+i*(mySize.width-1))+10]+" "+
				//						indices[3*4*(j+i*(mySize.width-1))+11]);
			}
		}
	}

	private void makeColours () {
		colours = new float[(mySize.height*mySize.width+(mySize.height-1)*(mySize.width-1))*3];

		// for each point in the altitudes
		for (int i=0; i<mySize.height; ++i) {
			for (int j=0; j<mySize.width; ++j) {
				colours[3*(j+i*mySize.width)] = 0.1f;
				colours[3*(j+i*mySize.width)+1] = 0.8f;
				colours[3*(j+i*mySize.width)+2] = 0.4f;
			}
		}

		int offset = mySize.height*mySize.width*3;
		// for each mid grid point
		for (int i=0; i<mySize.height-1; ++i) {
			for (int j=0; j<mySize.width-1; ++j) {
				colours[offset+3*(j+i*(mySize.width-1))] = 0.1f;
				colours[offset+3*(j+i*(mySize.width-1))+1] = 0.8f;
				colours[offset+3*(j+i*(mySize.width-1))+2] = 0.4f;
			}
		}

	}

	private void makeTextures(GL2 gl) {
		terrainTexture = new MyTexture(gl,terrainImage,"jpg",true);

		texCoords = new float[(mySize.height*mySize.width+(mySize.height-1)*(mySize.width-1))*2];
		float cx = 0f;
		float cy = 0f;

		for (int i=0; i<mySize.height; ++i) {
			for (int j=0; j<mySize.width; ++j) {
				texCoords[2*(j+i*mySize.width)] = cx%2f;
				texCoords[2*(j+i*mySize.width)+1] = cy%2f;
				//				System.out.println((cx%2f)+", "+(cy%2f));
				++cx;
			}
			++cy;
			cx = cx + mySize.width;
		}

		int offset = mySize.height*mySize.width*2;

		// for each mid grid point
		for (int i=0; i<mySize.height-1; ++i) {
			for (int j=0; j<mySize.width-1; ++j) {
				texCoords[offset+2*(j+i*(mySize.width-1))] = 0.5f;
				texCoords[offset+2*(j+i*(mySize.width-1))+1] = 0.5f;
			}
		}
	}

	private void makeNormals(GL2 gl) {
		normals = new double[(mySize.height*mySize.width+(mySize.height-1)*(mySize.width-1))*3];

		double[] n = new double[3];
		for (int i=0; i<mySize.height; ++i) {
			for (int j=0; j<mySize.width; ++j) {
				normalCorner(j,i,n);
				normals[3*(j+i*mySize.width)] = n[0];
				normals[3*(j+i*mySize.width)+1] = n[1];
				normals[3*(j+i*mySize.width)+2] = n[2];
			}
		}

		int offset = mySize.height*mySize.width*3;

		// for each mid grid point
		for (int i=0; i<mySize.height-1; ++i) {
			for (int j=0; j<mySize.width-1; ++j) {
				normalMid(i,j,n);
				normals[offset+3*(j+i*(mySize.width-1))] = n[0];
				normals[offset+3*(j+i*(mySize.width-1))+1] = n[1];
				normals[offset+3*(j+i*(mySize.width-1))+2] = n[2];
			}
		}
	}

	private void normalCorner (int x, int z, double[] normal) {

		MathUtil.clamp(x, 1, mySize.width-1);
		MathUtil.clamp(z, 1, mySize.height-1);

		System.out.println(myAltitude[x][z]);
		
		double[] vecs1 = new double[3];
		double[] vecs2 = new double[3];
		double[] vecs3 = new double[3];
		double[] vecs4 = new double[3];

		double[] vecN = new double[] {0, myAltitude[x][z>0?z-1:0],-1};
		double[] vecE = new double[] {1,myAltitude[x<mySize.width-2?x+1:mySize.width-1][z],0};
		double[] vecS = new double[] {0,myAltitude[x][z<mySize.height-2?z+1:mySize.height-1],1};
		double[] vecW = new double[] {-1,myAltitude[x>0?x-1:0][z],0};

		MathUtil.normCrossProd(vecN, vecW, vecs1);
		MathUtil.normCrossProd(vecW, vecS, vecs2);
		MathUtil.normCrossProd(vecS, vecE, vecs3);
		MathUtil.normCrossProd(vecE, vecN, vecs4);

		normal[0] = (vecs1[0]+vecs2[0]+vecs3[0]+vecs4[0])/4d;
		normal[1] = (vecs1[1]+vecs2[1]+vecs3[1]+vecs4[1])/4d;
		normal[2] = (vecs1[2]+vecs2[2]+vecs3[2]+vecs4[2])/4d;

		System.out.println("normal: "+normal[0]+" "+normal[1]+" "+normal[2]);
	}
	
	private void normalMid (int x, int z, double[] normal) {

		// the coordinates of the top left point
		int sqrX = (int) x;
		int sqrZ = (int) z;

		double[] vecs1 = new double[3];
		double[] vecs2 = new double[3];
		double[] vecs3 = new double[3];
		double[] vecs4 = new double[3];

		double[] vecNW = new double[] {-0.5, myAltitude[sqrX][sqrZ]-altitude(sqrX+0.5,sqrZ+0.5),-0.5};
		double[] vecNE = new double[] {0.5,myAltitude[sqrX+1][sqrZ]-altitude(sqrX+0.5,sqrZ+0.5),-0.5};
		double[] vecSW = new double[] {-0.5,myAltitude[sqrX][sqrZ+1]-altitude(sqrX+0.5,sqrZ+0.5),0.5};
		double[] vecSE = new double[] {0.5,myAltitude[sqrX+1][sqrZ+1]-altitude(sqrX+0.5,sqrZ+0.5),0.5};

		MathUtil.normCrossProd(vecNW, vecSW, vecs1);
		MathUtil.normCrossProd(vecSW, vecSE, vecs2);
		MathUtil.normCrossProd(vecSE, vecNW, vecs3);
		MathUtil.normCrossProd(vecNE, vecNW, vecs4);

		normal[0] = (vecs1[0]+vecs2[0]+vecs3[0]+vecs4[0])/4d;
		normal[1] = (vecs1[1]+vecs2[1]+vecs3[1]+vecs4[1])/4d;
		normal[2] = (vecs1[2]+vecs2[2]+vecs3[2]+vecs4[2])/4d;
	}

	/**
	 * Set the sunlight direction. 
	 * 
	 * Note: the sun should be treated as a directional light, without a position
	 * 
	 * @param dx
	 * @param dy
	 * @param dz
	 */
	public void setSunlightDir(float dx, float dy, float dz) {
		mySunlight[0] = dx;
		mySunlight[1] = dy;
		mySunlight[2] = dz;
	}

	/**
	 * Resize the terrain, copying any old altitudes. 
	 * 
	 * @param width
	 * @param height
	 */
	public void setSize(int width, int height) {
		mySize = new Dimension(width, height);
		double[][] oldAlt = myAltitude;
		myAltitude = new double[width][height];

		for (int i = 0; i < width && i < oldAlt.length; i++) {
			for (int j = 0; j < height && j < oldAlt[i].length; j++) {
				myAltitude[i][j] = oldAlt[i][j];
			}
		}
	}

	/**
	 * Get the altitude at a grid point
	 * 
	 * @param x
	 * @param z
	 * @return
	 */
	public double getGridAltitude(int x, int z) {
		return myAltitude[x][z];
	}

	/**
	 * Set the altitude at a grid point
	 * 
	 * @param x
	 * @param z
	 * @return
	 */
	public void setGridAltitude(int x, int z, double h) {
		myAltitude[x][z] = h;
	}

	/**
	 * Get the altitude at an arbitrary point. 
	 * Non-integer points should be interpolated from neighbouring grid points
	 * 
	 * @param x
	 * @param z
	 * @return
	 */
	public double altitude(double x, double z) {


		if (x<0 || x > mySize.width-1 || z < 0 || z > mySize.height-1) {
			return 1;
		}
		x = MathUtil.clamp(x, 0, mySize.width-1);
		z = MathUtil.clamp(z, 0, mySize.height-1);

		// the coordinates of the top left point
		int sqrX = (int) x;
		int sqrZ = (int) z;

		// fraction progression
		double u = sqrX == 0 ? x : (x % sqrX);
		double v = sqrZ == 0 ? z : (z % sqrZ);

		// find the surrounding points
		double p1[] = {sqrX+(u+v>1?1d:0d),sqrZ+(u+v>1?1d:0d)};
		double p2[] = {sqrX+(u>v?1d:0d),sqrZ+(u>v?0d:1d)};
		double p3[] = {sqrX+0.5d,sqrZ+0.5d};

		// calculate the vectors
		double f1[] = {p1[0]-x,p1[1]-z};
		double f2[] = {p2[0]-x,p2[1]-z};
		double f3[] = {p3[0]-x,p3[1]-z};

		// calculate the areas and ratios
		double a = 0.5d;
		double a1 = MathUtil.crossProduct(f2,f3)/a;
		double a2 = MathUtil.crossProduct(f3,f1)/a;
		double a3 = MathUtil.crossProduct(f1,f2)/a;

		// get the altitudes at each corner of the triangle to interpolate
		double weightA = myAltitude[(int) p1[0]][(int) p1[1]];
		double weightB = myAltitude[(int) p2[0]][(int) p2[1]];
		double weightC = getAverageAlt(sqrX,sqrZ); 

		double val = weightA*a1 + weightB*a2 + weightC*a3;

		//		System.out.println(sqrX+", "+sqrZ+" "+val
		//				+" p1: ["+p1[0]+", "+p1[1]+"] : "+myAltitude[(int) p1[0]][(int) p1[1]]
		//				+" p2: ["+p2[0]+", "+p2[1]+"] : "+myAltitude[(int) p2[0]][(int) p2[1]]
		//				+" p3: ["+p3[0]+", "+p3[1]+"] : "+getAverageAlt(sqrX,sqrZ));

		return val;
	}


	/**
	 * Add a tree at the specified (x,z) point. 
	 * The tree's y coordinate is calculated from the altitude of the terrain at that point.
	 * 
	 * @param x
	 * @param z
	 */
	public void addTree(double x, double z) {
		double y = altitude(x, z);
		Tree tree = new Tree(x, y, z);
		myTrees.add(tree);
	}


	/**
	 * Add a road. 
	 * 
	 * @param x
	 * @param z
	 */
	public void addRoad(double width, double[] spine) {
		Road road = new Road(width, spine);
		myRoads.add(road);		
	}

	/**
	 * 
	 */
	public void drawTerrain (GL2 gl) {

		// Set up directional lighting
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, light0Amb, 0);
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, light0Dif, 0);
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, light0Spec, 0);
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, light0Dir, 0);

		// Set up directional lighting
		gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_AMBIENT, light1Amb, 0);
		gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_DIFFUSE, light1Dif, 0);
		gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_SPECULAR, light1Spec, 0);
		gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, light1Dir, 0);

		gl.glEnable(GL2.GL_LIGHT1);

		// set up texture things
		gl.glUseProgram(shaderprogram);
		gl.glUniform1i(texUnitLoc , 0);

		// Set current texture
		gl.glActiveTexture(GL2.GL_TEXTURE0);
		gl.glBindTexture(GL2.GL_TEXTURE_2D, terrainTexture.getTextureId());        

		gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE);
		//Set wrap mode for texture in S direction
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT); 
		//Set wrap mode for texture in T direction
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);

		// Set up VBOs for terrain
		gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
		//		gl.glEnableClientState(GL2.GL_COLOR_ARRAY);
		gl.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
		gl.glEnableClientState(GL2.GL_NORMAL_ARRAY);

		//		System.out.println("In draw "+bufferIDs[0]);

		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, bufferIDs[0]);
		gl.glVertexPointer(3,GL2.GL_DOUBLE,0,0);
		//		gl.glColorPointer(3,GL.GL_FLOAT,0,vertices.length*Double.BYTES );
		gl.glTexCoordPointer(2, GL.GL_FLOAT, 0, vertices.length*Double.BYTES);
		gl.glNormalPointer(GL2.GL_DOUBLE, 0, vertices.length*Double.BYTES+texCoords.length*Float.BYTES);

		gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, bufferIDs[1]);

		// Material Properties
		gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_SPECULAR, terrainSpecular,0);
		gl.glMaterialf(GL2.GL_FRONT_AND_BACK, GL2.GL_SHININESS, 70.0f);

		// Draw Simple Shape using VBOS
		gl.glDrawElements(GL2.GL_TRIANGLES, indices.length, GL.GL_UNSIGNED_SHORT, 0);

		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER,0);
		gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER,0);
		gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);     
		gl.glUseProgram(0);


		gl.glDisableClientState(GL2.GL_NORMAL_ARRAY);
		gl.glDisableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
		//		gl.glDisableClientState(GL2.GL_COLOR_ARRAY);
		gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);

		gl.glDisable(GL2.GL_LIGHT1);
	}

	private void initLights (GL2 gl) {

		// Light 0 is sunlight property vectors.
		light0Amb = new float[]{ 1f, 1f, 1f, 1f };
		light0Dif = new float[]{ diffuse, diffuse, diffuse, 1.0f };
		light0Spec = new float[]{ specular, specular, specular, 1.0f};
		light0Dir = new float[] {mySunlight[0], mySunlight[1], mySunlight[2], 0};

		// Light 1 is the point light for the terrain
		light1Amb = new float[]{ 0.5f, 0.5f, 0.5f, 1f };
		light1Dif = new float[]{ 0.8f, 0.8f, 0.8f, 1.0f };
		light1Spec = new float[]{ 1f, 1f, 1f, 1.0f};
		light1Dir = new float[] {2*mySunlight[0], 2+2*mySunlight[1], 2*mySunlight[2], 1};
	}

	/**
	 * Initialiases the terrain
	 */
	public void initTerrain (GL2 gl) {

		initLights(gl);

		makeVertices();
		makeIndices();
		makeColours();
		makeTextures(gl);
		makeNormals(gl);

		try {
			shaderprogram = Shader.initShaders(gl,VERTEX_SHADER,FRAGMENT_SHADER);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		texUnitLoc = gl.glGetUniformLocation(shaderprogram,"texUnit1");


		verticesBuffer = Buffers.newDirectDoubleBuffer(vertices);
		indicesBuffer = Buffers.newDirectShortBuffer(indices);
		texBuffer = Buffers.newDirectFloatBuffer(texCoords);
		colourBuffer = Buffers.newDirectFloatBuffer(colours);
		normalsBuffer = Buffers.newDirectDoubleBuffer(normals);

		bufferIDs = new int[2];
		gl.glGenBuffers(2, bufferIDs, 0);

		gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
		//		gl.glEnableClientState(GL2.GL_COLOR_ARRAY);
		gl.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
		gl.glEnableClientState(GL2.GL_NORMAL_ARRAY);

		// make vertices buffer
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, bufferIDs[0]);
		//		gl.glBufferData(GL2.GL_ARRAY_BUFFER, vertices.length*Double.BYTES, verticesBuffer, GL2.GL_STATIC_DRAW);
		//		gl.glBufferData(GL2.GL_ARRAY_BUFFER, vertices.length*Double.BYTES + texCoords.length*Float.BYTES, null, GL2.GL_STATIC_DRAW);
		//		gl.glBufferData(GL2.GL_ARRAY_BUFFER, vertices.length*Double.BYTES + colours.length*Float.BYTES, null, GL2.GL_STATIC_DRAW);
		gl.glBufferData(GL2.GL_ARRAY_BUFFER, vertices.length*Double.BYTES + texCoords.length*Float.BYTES + normals.length*Double.BYTES, null, GL2.GL_STATIC_DRAW);

		// load vertices data
		gl.glBufferSubData(GL2.GL_ARRAY_BUFFER, 0, vertices.length*Double.BYTES, verticesBuffer);
		gl.glVertexPointer(3,GL2.GL_DOUBLE,0,0);

		// load vertex texture data
		gl.glBufferSubData(GL2.GL_ARRAY_BUFFER,vertices.length*Double.BYTES,texCoords.length*Float.BYTES,texBuffer);
		gl.glTexCoordPointer(2, GL2.GL_FLOAT, 0, vertices.length*Double.BYTES);

		// load normals data
		gl.glBufferSubData(GL2.GL_ARRAY_BUFFER,vertices.length*Double.BYTES+texCoords.length*Float.BYTES,normals.length*Double.BYTES,normalsBuffer);
		gl.glNormalPointer(GL2.GL_DOUBLE, 0, vertices.length*Double.BYTES+texCoords.length*Float.BYTES);


		//		gl.glVertexAttribPointer(vPos,3,GL.GL_FLOAT, false,0, 0); 
		/*
		// load vertex colour data
		gl.glBufferSubData(GL2.GL_ARRAY_BUFFER, vertices.length*Double.BYTES, colours.length*Float.BYTES, colourBuffer);
		gl.glColorPointer(3,GL.GL_FLOAT,0,vertices.length*Double.BYTES );
		 */
		// make indices buffer
		gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, bufferIDs[1]);
		gl.glBufferData(GL2.GL_ELEMENT_ARRAY_BUFFER, indices.length*Short.BYTES, indicesBuffer, GL2.GL_STATIC_DRAW);

	}

	public void increaseTrees() {
		for (Tree t : myTrees) t.increaseTree();
	}

	public void decreaseTrees() {
		for (Tree t : myTrees) t.decreaseTree();
	}
}
