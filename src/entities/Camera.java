package entities;



import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

public class Camera {
	
	private Vector3f position = new Vector3f(0,0,0);
	//rotation around axes
	private float pitch = 40;
	//how much left\right camera is aiming
	private float yaw = 0;
	//how much is tilted to one side
	private float roll;
	
	private float speed = 0.4f;
	
	private Player player;
	//user control
	private float distanceFromPlayer = 80;
	//left-right rotation
	private float angleAroundPlayer = 0;
	//+pitch
	private boolean downwardViewMode = false;
	
	public Camera(Player player) {
		this.player = player;
	}
	//public Camera() {
	//	this.position = new Vector3f(0,0,0);
	//}
	
	public Camera(Vector3f position) {
		this.position = position;
	}
	
	public void move() {
		calculateZoom();
		calculatePitch();
		calculateAngleAroundPlayer();
		float horizontalDistance = calculateHorizontalDistance();
		float verticalDistance = calculateVerticalDistance();
		calculateCameraPosition(horizontalDistance, verticalDistance);
		this.yaw = 180 - (player.getRotY() + angleAroundPlayer);
		if(Keyboard.isKeyDown(Keyboard.KEY_0)) {
			downwardViewMode = !downwardViewMode;
			return;
		}
		/*if(downwardViewMode) {
		this.pitch = 90;
		this.distanceFromPlayer = 100;
		}
		else if(!downwardViewMode){
			this.pitch = 20;
			this.distanceFromPlayer = 40;
		}*/
		
		
	}
	
	private void calculateZoom() {
		//how much the m wheel is turned. V - ^ value
		float zoomLevel = Mouse.getDWheel() * 0.1f;
		distanceFromPlayer -= zoomLevel;
		
	}
	
	private void calculatePitch() {
		//RMB
		if(Mouse.isButtonDown(1)) {
			float pitchChange = Mouse.getDY() * 0.1f;
			pitch -= pitchChange;
			if(pitch < 0) pitch = 0;
			else if(pitch > 90)pitch = 90;
		}
	}
	
	private void calculateAngleAroundPlayer() {
		if(Mouse.isButtonDown(0)) {
			float angleChange = Mouse.getDX() * 0.3f;
			angleAroundPlayer -= angleChange;
		}
	}
	
	private float calculateHorizontalDistance() {
		float hD =  (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
		if(hD < 0) hD = 0;
		return hD;
		
	}
	
	private float calculateVerticalDistance() {
		float vD = (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch)));
		if(vD < 0) vD = 0;
		return vD;
	}
	
	private void calculateCameraPosition(float horDistance, float vertDistance) {
		float theta = player.getRotY() + angleAroundPlayer;
		float offsetX = (float) (horDistance * Math.sin(Math.toRadians(theta)));
		float offsetZ = (float) (horDistance * Math.cos(Math.toRadians(theta)));
		this.position.x = player.getPosition().x - offsetX;
		this.position.z = player.getPosition().z - offsetZ;
		this.position.y = player.getPosition().y + vertDistance;
		//from above - player.pos, angle = -180* ?
	}
	

	public Vector3f getPosition() {
		return position;
	}

	public float getPitch() {
		return pitch;
	}

	public float getYaw() {
		return yaw;
	}

	public float getRoll() {
		return roll;
	}

}
