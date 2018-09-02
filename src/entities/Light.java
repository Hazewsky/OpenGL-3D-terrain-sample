package entities;

import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import models.TexturedModel;
import terrains.Terrain;

public class Light {
	
	private Vector3f position;
	private Vector3f color;
	//ослабление
	private Vector3f attenuation = new Vector3f(1,0,0);
	
	public Light(Vector3f position, Vector3f color) {
		super();
		this.position = position;
		this.color = color;
	}

	public Light(Vector3f position, Vector3f color, Vector3f attenuation) {
		this.position = position;
		this.color = color;
		this.attenuation = attenuation;
	}
	
	
	public Vector3f getAttenuation() {
		return this.attenuation;
	}
	
	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public Vector3f getColor() {
		return color;
	}

	public void setColor(Vector3f color) {
		this.color = color;
	}
	
	public static void createLamp(float x, float z, Vector3f color, TexturedModel model,
			Terrain ter, List<Light> lights, List<Entity> entities) {
		if(z >= ter.getZ()
				&& z <= ter.getZ() + Terrain.getTileSize()
				&& x >= ter.getX() &&
				x <= ter.getX() + Terrain.getTileSize()) {
			lights.add(new Light(new Vector3f(x,ter.getHeightOfTerrain(x, z) + 10,z),
					color, new Vector3f(1,.01f,.0002f)));
			entities.add(new Entity(model, new Vector3f(x, ter.getHeightOfTerrain(x, z), z),0,0,0,1));
		}
		}

}
