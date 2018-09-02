package skybox;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import models.RawModel;
import renderEngine.DisplayManager;
import renderEngine.Loader;

public class SkyboxRenderer {
	
	private static final float SIZE = 500f;
	
	private static final float[] VERTICES = {
			  -SIZE,  SIZE, -SIZE,
			    -SIZE, -SIZE, -SIZE,
			    SIZE, -SIZE, -SIZE,
			     SIZE, -SIZE, -SIZE,
			     SIZE,  SIZE, -SIZE,
			    -SIZE,  SIZE, -SIZE,

			    -SIZE, -SIZE,  SIZE,
			    -SIZE, -SIZE, -SIZE,
			    -SIZE,  SIZE, -SIZE,
			    -SIZE,  SIZE, -SIZE,
			    -SIZE,  SIZE,  SIZE,
			    -SIZE, -SIZE,  SIZE,

			     SIZE, -SIZE, -SIZE,
			     SIZE, -SIZE,  SIZE,
			     SIZE,  SIZE,  SIZE,
			     SIZE,  SIZE,  SIZE,
			     SIZE,  SIZE, -SIZE,
			     SIZE, -SIZE, -SIZE,

			    -SIZE, -SIZE,  SIZE,
			    -SIZE,  SIZE,  SIZE,
			     SIZE,  SIZE,  SIZE,
			     SIZE,  SIZE,  SIZE,
			     SIZE, -SIZE,  SIZE,
			    -SIZE, -SIZE,  SIZE,

			    -SIZE,  SIZE, -SIZE,
			     SIZE,  SIZE, -SIZE,
			     SIZE,  SIZE,  SIZE,
			     SIZE,  SIZE,  SIZE,
			    -SIZE,  SIZE,  SIZE,
			    -SIZE,  SIZE, -SIZE,

			    -SIZE, -SIZE, -SIZE,
			    -SIZE, -SIZE,  SIZE,
			     SIZE, -SIZE, -SIZE,
			     SIZE, -SIZE, -SIZE,
			    -SIZE, -SIZE,  SIZE,
			     SIZE, -SIZE,  SIZE	
	};
	
	private static String[] TEXTURE_FILES = {"right", "left", "top", "bottom", "back", "front"};
	private static String[] NIGHT_TEXTURE_FILES = {"nightRight", "nightLeft", "nightTop", "nightBottom", 
			"nightBack", "nightFront"};
	private static String[] ADD_TEXTURE_FILES = {"grass","grass","grass", "grass", "grass", "grass"};
	//cube model
	private RawModel cube;
	private int texture;
	private int nightTexture;
	private int morningTexture;
	private int eveningTexture;
	private SkyboxShader shader;
	
	private float time = 0;
	private float day = 0;
	private static int HOURS_IN_DAY = 24;
	
	public SkyboxRenderer(Loader loader, Matrix4f projectionMatrix) {
		cube = loader.loadToVAO(VERTICES,  3);
		texture = loader.loadCubeMap(TEXTURE_FILES);
		nightTexture = loader.loadCubeMap(NIGHT_TEXTURE_FILES);
		morningTexture = loader.loadCubeMap(TEXTURE_FILES);
		eveningTexture = loader.loadCubeMap(NIGHT_TEXTURE_FILES);
		shader = new SkyboxShader();
		shader.start();
		shader.connectTextureUnits();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
	}
	
	private void bindTextures() {
		time += DisplayManager.getFrameTimeSeconds();
		time %= HOURS_IN_DAY;
		day += DisplayManager.getFrameTimeSeconds() / HOURS_IN_DAY;
		System.out.println("time: " + time +" day: " + day);
		int texture1;
		int texture2;
		float blendFactor;		
		if(time >= 0 && time < 5){
			texture1 = nightTexture;
			texture2 = nightTexture;
			blendFactor = (time - 0)/(5 - 0);
		}else if(time >= 5 && time < 7){
			texture1 = nightTexture;
			texture2 = morningTexture;
			blendFactor = (time - 5)/(7 - 5);
		}else if(time >= 7 && time <= 8){
			texture1 = morningTexture;
			texture2 = texture;
			blendFactor = (time - 7)/(8 - 7);
		}else if(time >= 8 && time < 18){
			texture1 = texture;
			texture2 = eveningTexture;
			blendFactor = (time - 8)/(18 - 8);
		}else if(time >= 18 && time < 21){
			texture1 = eveningTexture;
			texture2 = eveningTexture;
			blendFactor = (time - 18)/(21 - 18);
		}else {
			texture1 = nightTexture;
			texture2 = nightTexture;
			blendFactor = (time - 21)/(24 - 21);
		}

		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texture1);
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texture2);
		shader.loadBlendFactor(blendFactor);
	}
	public void render(Camera camera, float fogR, float fogG, float fogB) {
		shader.start();
		shader.loadViewMatrix(camera);
		shader.loadFogColor(fogR, fogG, fogB);
		GL30.glBindVertexArray(cube.getVaoID());
		//positions
		GL20.glEnableVertexAttribArray(0);
		bindTextures();
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, cube.getVertexCount());
		//unbind
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		shader.stop();
	}
	
	
}
