package renderEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import models.TexturedModel;
import shaders.StaticShader;
import shaders.TerrainShader;
import skybox.SkyboxRenderer;
import terrains.Terrain;

public class MasterRenderer {
	//field of view
	private static final float FOV = 70.0f;
	private static final float NEAR_PLANE = 0.1f;
		//how far can see
	private static final float FAR_PLANE = 1000.0f;
		
	private static final float[] FOG_COLOR = {0.43f, 0.43f, 0.43f, 1.0f};
	
	private static final float FOG_DENSITY = 0.005f;
	private static final float FOG_GRADIENT = 2.5f;
	private Matrix4f projectionMatrix;
	
	private StaticShader shader = new StaticShader();
	private EntityRenderer renderer;
	
	private TerrainRenderer terrainRenderer;
	private TerrainShader terrainShader = new TerrainShader();
	
	private Map<TexturedModel,List<Entity>> entities = new HashMap<TexturedModel,List<Entity>>();
	private List<Terrain> terrains = new ArrayList<Terrain>();
	
	private SkyboxRenderer skyboxRenderer;
	
	public MasterRenderer(Loader loader) {
		enableCulling();
		createProjectionMatrix();
		renderer = new EntityRenderer(shader,projectionMatrix);
		terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
		skyboxRenderer = new SkyboxRenderer(loader,projectionMatrix);
	}
	
	public static void enableCulling() {
		//Do not render faces at the back side of the model
				GL11.glEnable(GL11.GL_CULL_FACE);
				//Specify what faces do not render
				GL11.glCullFace(GL11.GL_BACK);
	}
	
	public static void disableCulling() {
		GL11.glDisable(GL11.GL_CULL_FACE);
		
	}
	public void render(List<Light> lights, Camera camera) {
		prepareToRender();
		shader.start();
		shader.loadFogVariables(FOG_DENSITY, FOG_GRADIENT);
		shader.loadSkyColor(FOG_COLOR[0], FOG_COLOR[1], FOG_COLOR[2]);
		shader.loadLights(lights);
		shader.loadViewMatrix(camera);
		renderer.render(entities);
		shader.stop();
		terrainShader.start();
		terrainShader.loadFogVariables(FOG_DENSITY, FOG_GRADIENT);
		terrainShader.loadSkyColor(FOG_COLOR[0], FOG_COLOR[1], FOG_COLOR[2]);
		terrainShader.loadLights(lights);
		terrainShader.loadViewMatrix(camera);
		terrainRenderer.render(terrains);
		terrainShader.stop();
		skyboxRenderer.render(camera, FOG_COLOR[0],FOG_COLOR[1],FOG_COLOR[2]);
		terrains.clear();
		entities.clear();
	}
	
	public void processTerrain(Terrain terrain) {
		terrains.add(terrain);
	}
	
	public void processEntity(Entity entity) {
		TexturedModel entityModel = entity.getModel();
		List<Entity> batch = entities.get(entityModel);
		if(batch!=null) {
			batch.add(entity);
		}else {
			List<Entity> newBatch = new ArrayList<Entity>();
			newBatch.add(entity);
			entities.put(entityModel, newBatch);
		}
	}
	
	public void prepareToRender() {
		//for correct 3D. It tests what triangles are in front of each other
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		//sky color
		GL11.glClearColor(FOG_COLOR[0],FOG_COLOR[1],FOG_COLOR[2],FOG_COLOR[3]);
		
	}
	
	private void createProjectionMatrix() {
		float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
		float yScale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))) * aspectRatio);
		float xScale = yScale / aspectRatio;
		float frustumLength = FAR_PLANE - NEAR_PLANE;
		
		projectionMatrix = new Matrix4f();
		projectionMatrix.m00 = xScale;
		projectionMatrix.m11 = yScale;
		projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE)/frustumLength);
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -((2* NEAR_PLANE * FAR_PLANE) / frustumLength);
		projectionMatrix.m33 = 0;
	}
	
	public void cleanUp() {
		shader.cleanUp();
		terrainShader.cleanUp();
	}
}
