package skybox;



import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import renderEngine.DisplayManager;
import shaders.ShaderProgram;
import toolBox.Maths;

public class SkyboxShader extends ShaderProgram{

	private static final String VERTEX_FILE = "src/skybox/skyboxVertexShader.txt";
	private static final String FRAGMENT_FILE = "src/skybox/skyboxFragmentShader.txt";
	
	private static final float ROTATE_SPEED = .2f;
	
	private int locationProjectionMatrix;
	private int locationViewMatrix;
	private int locationFogColor;
	private int locationCubeMap;
	private int locationCubeMap2;
	private int locationBlendFactor;
	
	private float currentRotation = 0;
	
	public SkyboxShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}
	
	public void loadProjectionMatrix(Matrix4f matrix){
		super.loadMatrix(locationProjectionMatrix, matrix);
	}

	public void loadViewMatrix(Camera camera){
		Matrix4f matrix = Maths.createViewMatrix(camera);
		//translation
		matrix.m30 = 0;
		matrix.m31 = 0;
		matrix.m32 = 0;
		currentRotation += ROTATE_SPEED * DisplayManager.getFrameTimeSeconds();
		Matrix4f.rotate((float)Math.toRadians(currentRotation), new Vector3f(0,1,0), matrix, matrix);
		super.loadMatrix(locationViewMatrix, matrix);
	}
	public void loadFogColor(float r,float g, float b) {
		super.loadVector(locationFogColor, new Vector3f(r,g,b));
	}
	
	public void connectTextureUnits() {
		//day
		super.loadInt(locationCubeMap, 0);
		//evening
		super.loadInt(locationCubeMap2, 1);
		//implement later
		/*
		//day
		super.loadInt(locationDayCubeMap, 0);
		//evening
		super.loadInt(locationNightCubeMap, 1);
		//night
		super.loadInt(locationNightCubeMap, 2);
		//morning
		super.loadInt(locationNightCubeMap, 3);
		*/
	}
	public void loadBlendFactor(float blendFactor) {
		super.loadFLoat(locationBlendFactor, blendFactor);
	}
	
	@Override
	protected void getAllUniformLocations() {
		locationProjectionMatrix = super.getUniformLocation("projectionMatrix");
		locationViewMatrix = super.getUniformLocation("viewMatrix");
		locationFogColor = super.getUniformLocation("fogColor");
		locationCubeMap = super.getUniformLocation("cubeMap");
		locationCubeMap2 = super.getUniformLocation("cubeMap2");
		locationBlendFactor = super.getUniformLocation("blendFactor");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}

}
