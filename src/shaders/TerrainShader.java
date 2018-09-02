package shaders;

import java.util.List;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Light;
import toolBox.Maths;

public class TerrainShader extends ShaderProgram {	
	private static final int MAX_LIGHTS = 4;
	
	private static final String VERTEX_FILE = "src/shaders/terrainVertexShader.txt";
	private static final String FRAGMENT_FILE = "src/shaders/terrainFragmentShader.txt";
	
	private int locationTransformationMatrix;
	private int locationProjectionMatrix;
	private int locationViewMatrix;
	//light
	private int locationLightPosition[];
	private int locationLightColor[];
	private int locationAttenuation[];
	private int locationShineDamper;
	private int locationReflectivity;
	//fog
	private int locationSkyColor;
	private int locationFogDensity;
	private int locationFogGradient;
	//textures
	private int locationBackgroundTexture;
	private int locationRTexture;
	private int locationGTexture;
	private int locationBTexture;
	private int locationBlendMap;
	
	public TerrainShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");
		super.bindAttribute(2, "normal");
		
	}

	@Override
	protected void getAllUniformLocations() {
		locationTransformationMatrix = super.getUniformLocation("transformationMatrix");
		locationProjectionMatrix = super.getUniformLocation("projectionMatrix");
		locationViewMatrix = super.getUniformLocation("viewMatrix");
		//light
		locationShineDamper = super.getUniformLocation("shineDamper");
		locationReflectivity = super.getUniformLocation("reflectivity");
		locationLightPosition = new int[MAX_LIGHTS];
		locationLightColor = new int[MAX_LIGHTS];
		locationAttenuation = new int[MAX_LIGHTS];
		for(int i=0; i< MAX_LIGHTS; i++) {
			locationLightPosition[i] = super.getUniformLocation("lightPosition[" + i + "]");
			locationLightColor[i] = super.getUniformLocation("lightColor[" + i + "]");
			locationAttenuation[i] = super.getUniformLocation("attenuation[" + i + "]");
		}
		//fog
		locationSkyColor = super.getUniformLocation("skyColor");
		locationFogDensity = super.getUniformLocation("density");
		locationFogGradient = super.getUniformLocation("gradient");
		//textures
		locationBackgroundTexture = super.getUniformLocation("backgroundTexture");
		locationRTexture = super.getUniformLocation("rTexture");
		locationGTexture = super.getUniformLocation("gTexture");
		locationBTexture = super.getUniformLocation("bTexture");
		locationBlendMap = super.getUniformLocation("blendMap");
		
		
		 
	}
	
	public void connectTextureUnits() {
		super.loadInt(locationBackgroundTexture, 0);
		super.loadInt(locationRTexture, 1);
		super.loadInt(locationGTexture, 2);
		super.loadInt(locationBTexture, 3);
		super.loadInt(locationBlendMap, 4);
	}
	
	public void loadFogVariables(float density, float gradient) {
		super.loadFLoat(locationFogDensity, density);
		super.loadFLoat(locationFogGradient, gradient);
	}
	public void loadSkyColor(float r, float g, float b) {
		super.loadVector(locationSkyColor, new Vector3f(r,g,b));
	}
	public void loadShineVariables(float damper, float reflectivity) {
		super.loadFLoat(locationShineDamper, damper);
		super.loadFLoat(locationReflectivity, reflectivity);
	}
	
	public void loadTransformationMatrix(Matrix4f matrix) {
		super.loadMatrix(locationTransformationMatrix, matrix);
		
	}
	
	public void loadLights(List<Light> lights) {
		for(int i =0; i< MAX_LIGHTS; i++) {
			if(i < lights.size()) {
				super.loadVector(locationLightPosition[i], lights.get(i).getPosition());
				super.loadVector(locationLightColor[i], lights.get(i).getColor());
				super.loadVector(locationAttenuation[i], lights.get(i).getAttenuation());
			}else {
				super.loadVector(locationLightPosition[i], new Vector3f(0,0,0));
				super.loadVector(locationLightColor[i], new Vector3f(0,0,0));
				super.loadVector(locationAttenuation[i], new Vector3f(1,0,0));
			}
		}
	}
	
	public void loadProjectionMatrix(Matrix4f projection) {
		super.loadMatrix(locationProjectionMatrix, projection);
	}
	
	public void loadViewMatrix(Camera camera) { 
		Matrix4f viewMatrix = Maths.createViewMatrix(camera);
		super.loadMatrix(locationViewMatrix, viewMatrix);
	}

}
