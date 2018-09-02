package engineTester;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import models.RawModel;
import models.TexturedModel;
import objConverter.ModelData;
import objConverter.OBJFileLoader;
import renderEngine.*;
import shaders.StaticShader;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import guis.*;

public class mainGameLoop {
	
	public static void main(String[]args) {
		DisplayManager.createDisplay();
		Loader loader = new Loader();
		
		//model
		
		ModelData data = OBJFileLoader.loadOBJ("lowPolyTree");
		RawModel tree = loader.loadToVAO(data.getVertices(),data.getTextureCoords(),
				data.getNormals(), data.getIndices());
		//texture
		TexturedModel texturedModel = new TexturedModel(tree, new ModelTexture(loader.loadTexture("lowPolyTree")));
		//Shining
		//ModelTexture texture = texturedModel.getTexture();
		//texture.setShineDamper(10);
		//texture.setReflectivity(1);
		
		Random rand = new Random();
		ArrayList<Entity> entities = new ArrayList<Entity>();
		
		
		
		//terrain textures
		TerrainTexture bkTexture = new TerrainTexture(loader.loadTexture("grass"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("dirt"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("pinkFlowers"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));
		TerrainTexturePack texturePack = new TerrainTexturePack(bkTexture, rTexture,gTexture,bTexture);
		
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));
		TerrainTexture blendMap2 = new TerrainTexture(loader.loadTexture("blendMap2"));
		Terrain terrain = new Terrain(0,0, loader, texturePack, blendMap, "heightmap");
		Terrain terrain2 = new Terrain(0,-1, loader, texturePack, blendMap2, "heightmap2");
		
		//lights
		List<Light> lights = new ArrayList<Light>();
		//sun
		lights.add(new Light(new Vector3f(0,1000,-7000), new Vector3f(.4f,.4f,.4f)));
		//lights
		lights.add(new Light(new Vector3f(185,10,-293), new Vector3f(2,0,0), new Vector3f(1,.01f,.0002f)));
		lights.add(new Light(new Vector3f(370,17,-300), new Vector3f(0,2,0), new Vector3f(1,.01f,.0002f)));
		lights.add(new Light(new Vector3f(293,7,-305), new Vector3f(2,2,2), new Vector3f(1,.01f,.0002f)));
		TexturedModel lamp = new TexturedModel(OBJLoader.loadObjModel("lamp", loader),
				new ModelTexture(loader.loadTexture("lamp")));
		lamp.getTexture().setUseFakeLighting(true);
		entities.add(new Entity(lamp, new Vector3f(185,-40f,-293),0,0,0,1 ));
		entities.add(new Entity(lamp, new Vector3f(370,-40f,-300),0,0,0,1 ));
		entities.add(new Entity(lamp, new Vector3f(293,-40f,-305),0,0,0,1 ));
		//terrains
		ArrayList<Terrain> terrains = new ArrayList<Terrain>();
		terrains.add(terrain);
		terrains.add(new Terrain(-1,0, loader,texturePack, blendMap, "heightMap"));
		terrains.add(new Terrain(-1,-1, loader, texturePack, blendMap, "heightMap"));
		
		terrains.add(terrain2);
		//find max|min world coords
		float maxX = 0, minX = 0, maxZ = 0, minZ = 0;
		for(Terrain ter : terrains) {
			maxX = Math.max(ter.getX() + ter.getTileSize(), maxX);
			minX = Math.min(ter.getX(), minX);
			maxZ = Math.max(ter.getZ() + ter.getTileSize(), maxZ);
			minZ = Math.min(ter.getZ(), minZ);
		}
		System.out.println("X:" + minX + " " + maxX);
		System.out.println("Z:" + minZ + " " + maxZ
				);
		float x;
		float z;
		//lights
		//for(Terrain ter: terrains) {
		//for(int i =0; i< 10; i++) {
		//	x = rand.nextInt((int)(maxX + 1 - minX)) + minX;
		//	z = rand.nextInt((int)(maxZ + 1 - minZ)) + minZ;
		//	Light.createLamp(x, z, new Vector3f(2,0,0), bunny, ter, lights, entities);
		//}
		//}
		//Player
		//Player player = new Player(bunny,new Vector3f(terrain.getTileSize()/2,0,terrain.getTileSize()/2-10),
			//	0,0,0,.5f);
		TexturedModel bunny = new TexturedModel(OBJLoader.loadObjModel("bunny", loader),
				new ModelTexture(loader.loadTexture("gold")));
		Player player = new Player(bunny, new Vector3f(185,2,-293), 0,0,0,.5f);
		//under the player initialization
		Camera camera = new Camera(player);
		entities.add(player);
		//guis
		List<GuiTexture> guis = new ArrayList<GuiTexture>();
		GuiTexture gui = new GuiTexture(loader.loadTexture("health"),
				new Vector2f(-.75f,-.75f), new Vector2f(0.25f,0.25f));
		guis.add(gui);
		
		GuiRenderer guiRenderer = new GuiRenderer(loader);
		//Objects
		TexturedModel grass = new TexturedModel(OBJLoader.loadObjModel("grassModel", loader),
				new ModelTexture(loader.loadTexture("grassTexture")));
		grass.getTexture().setHasTransparency(true);
		grass.getTexture().setUseFakeLighting(true);
		
		TexturedModel flower = new TexturedModel(OBJLoader.loadObjModel("grassModel", loader),
				new ModelTexture(loader.loadTexture("flower")));
		flower.getTexture().setHasTransparency(true);
		flower.getTexture().setUseFakeLighting(true);
		
		ModelTexture fernTextureAtlas = new ModelTexture(loader.loadTexture("fern"));
		fernTextureAtlas.setNumberOfRows(2);
		TexturedModel fern = new TexturedModel(OBJLoader.loadObjModel("fern", loader),
				fernTextureAtlas);
		fern.getTexture().setHasTransparency(true);
		Terrain currentTerrain = terrains.get(0);
		for(Terrain ter: terrains) {
		for(int i=0; i< 1000; i++) {
			//tree
			x = rand.nextInt((int)(maxX + 1 - minX)) + minX;
			z = rand.nextInt((int)(maxZ + 1 - minZ)) + minZ;
			if(ter.isCollidedWithTerrain(x,z))
					entities.add(new Entity(texturedModel, 
							new Vector3f(x,
							ter.getHeightOfTerrain(x, z),
							z),
							0,0,0,rand.nextFloat() + .2f));
			//grass
			x = rand.nextInt((int)(maxX + 1 - minX)) + minX;
			z = rand.nextInt((int)(maxZ + 1 - minZ)) + minZ;
			if(ter.isCollidedWithTerrain(x,z))
			entities.add(new Entity(grass, 
					new Vector3f(x,
							ter.getHeightOfTerrain(x, z),
							z),
					0,0,0,1));
			//fern
			x = rand.nextInt((int)(maxX + 1 - minX)) + minX;
			z = rand.nextInt((int)(maxZ + 1 - minZ)) + minZ;
			if(ter.isCollidedWithTerrain(x,z))
			entities.add( new Entity(fern, rand.nextInt(4),
					new Vector3f(x,
							ter.getHeightOfTerrain(x, z),
							z),
					0,0,0,rand.nextFloat()));
			//flower
			x = rand.nextInt((int)(maxX + 1 - minX)) + minX;
			z = rand.nextInt((int)(maxZ + 1 - minZ)) + minZ;
			if(ter.isCollidedWithTerrain(x,z))
			entities.add( new Entity(flower, 
					new Vector3f(x,
							ter.getHeightOfTerrain(x, z),
							z),
					0,0,0,1));
					}
		}
		currentTerrain = terrains.get(0);
		MasterRenderer renderer = new MasterRenderer(loader);
		while(!Display.isCloseRequested()) {
			camera.move();
			//System.out.println(player.getPosition());
			for(Terrain ter: terrains) {
				renderer.processTerrain(ter);
			}
			for(Terrain ter: terrains) {
				if(ter.isCollidedWithTerrain(player.getPosition().x, player.getPosition().z))
				{
					currentTerrain = ter;
					break;
				}
			}
			player.move(currentTerrain);
			
			//for every entity
			
			for(Entity ent:entities) {
				renderer.processEntity(ent);
			}
			renderer.render(lights, camera);
			//gui
			guiRenderer.render(guis);
			//update window
			DisplayManager.updateDisplay();
		}
		//clean up the memory
		guiRenderer.cleanUp();
		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();
	}
	
	
}


//Vertex Shader - one per vertex. Finds vertex position on the screen. Outputs vertex values
//(Fragment shader input)
//Fragment shader - one time per pixel when the object comes on the screen. Output - RGB Color

//Color of pixel is linear interpolated between colors of vertices of triangle
//in which the pixel is located, depends of how close the vertex is to this pixel