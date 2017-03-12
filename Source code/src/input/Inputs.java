package input;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import entities.Camera;
import entities.Player;
import terrains.Terrain;
import toolbox.FrameTime;

public class Inputs {
	private Camera camera; //FIELDS FOR CONSTRUCTOR
	private Player player;
	public boolean isFPS = false;
	private Terrain terrain;
    public Inputs(Terrain terrain,Camera camera, Player player, boolean isFPS) {
		super();
		this.camera = camera;
		this.player = player;
		this.isFPS = isFPS;
		this.terrain = terrain;
	}
    public void checkForInputs(){
    	dt = (float) (FrameTime.getDelta() * 2f);
    	checkMouse();
    	checkKeyboard();
    	checkGravity();
    }
	private float dx        = 0.0f; //FPS MOUSE
    private float dy        = 0.0f;
    public static float mouseSensitivity = 1.5f;
    private float dt;
    
    private float angleAroundPlayer = 0; // 3rdPView MOUSE
    private float distanceFromPlayer = 5;
    
	float movementSpeed = 15f; //FPS KEYBOARD
	float runningSpeed = 1;
	
	
	boolean inAir = false; //Gravity and jumping
	boolean flying = false;
	float velocityY = 0;
	public static final float gravity  = 20f;
	
	private void checkMouse(){
		if(isFPS){
			dx = Mouse.getDX();
			dy = Mouse.getDY();
			camera.yaw(dx * mouseSensitivity * dt);
			camera.pitch(dy * mouseSensitivity * dt);
		}else{
			calculateZoom();
			calculatePitch();
			calculateAngleAroundPlayer();
			float horizontalDistance = calculatedHorizontalDistance();
			float verticalDistance = calculatedVerticalDistance();
			calculateCameraPosition(horizontalDistance,verticalDistance);
			camera.yaw = 180 - (player.getRotY() + angleAroundPlayer);
			camera.yaw %= 360;
			if(Keyboard.isKeyDown(Keyboard.KEY_W)){
				if(!(camera.yaw == 180)){
					angleAroundPlayer = 0;
				}
			}
		}
	    if(camera.pitch <= -90){
	    	camera.pitch = -90;
	    }else if (camera.pitch >= 90){
	    	camera.pitch = 90;
	    }
	}
	private void checkKeyboard(){
		if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)){
			System.exit(0);
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_NUMPAD4)){
			Mouse.setGrabbed(true);
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_NUMPAD5)){
			Mouse.setGrabbed(false);
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_NUMPAD7)){
			flying = false;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_NUMPAD8)){
			velocityY = gravity;
			inAir = true;
			flying = true;
		}
		if(isFPS){
			if(Keyboard.isKeyDown(Keyboard.KEY_W)){
				camera.walkForward(dt * movementSpeed  * runningSpeed);
			}
			if(Keyboard.isKeyDown(Keyboard.KEY_S)){
				camera.walkBackwards(dt * movementSpeed  * runningSpeed);
			}
			if(Keyboard.isKeyDown(Keyboard.KEY_A)){
				camera.strafeLeft(dt * movementSpeed  * runningSpeed);
			}
			if(Keyboard.isKeyDown(Keyboard.KEY_D)){
				camera.strafeRight(dt * movementSpeed  * runningSpeed);
			}
			if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)){
				if(flying){
					camera.goUp(dt * movementSpeed * 2);
				}else{
					if(!inAir){
						velocityY = gravity;
						inAir = true;
					}
				}
			
			}
		}else{
			if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)){
				if(flying){
					player.goUp(dt * movementSpeed * 2);
				}else{
					if(!inAir){
						velocityY = gravity;
						inAir = true;
					}
				}
			
			}
		}
	}
	private void checkGravity(){
		if(isFPS){
			if(!(flying)){
				float terrainheight = terrain.getHeightOfTerrain(camera.getPosition().x, camera.getPosition().getZ());
					if (terrainheight == 0 || camera.getPosition().getY() <= terrainheight) {
								camera.goDown(gravity * dt);
					}else{
							terrainheight += 5.5f;
										if(inAir == true && camera.getPosition().y >= (terrainheight - 0.2f)){
											camera.goUp(velocityY * dt);      // Apply vertical velocity to X position
											velocityY += (-gravity * 1.5f) * dt;        // Apply gravity to vertical velocity
										}else{
											camera.getPosition().setY(terrainheight);
											inAir = false;
										}
						}
				}
		}else{
		if(!(flying)){
				float terrainheight = terrain.getHeightOfTerrain(player.getPosition().x ,player.getPosition().z);
						if (terrainheight == 0 || player.getPosition().y <= terrainheight) {
								player.goDown(gravity* dt);
						}else{
							terrainheight += 0.5f;
										if(inAir == true && player.getPosition().y >= (terrainheight)){
											player.goUp(velocityY * dt);      // Apply vertical velocity to X position
											velocityY += (-gravity * 1.5f) * dt;        // Apply gravity to vertical velocity
										}else{
											player.getPosition().setY(terrainheight);
											inAir = false;
										}
						}
				}
		}
	}
	private void calculateZoom(){
		float zoomLevel = Mouse.getDWheel() * mouseSensitivity *0.05f;
		distanceFromPlayer -= zoomLevel;
	}
	
	private void calculatePitch(){
		float pitchChange = Mouse.getDY() * mouseSensitivity * 0.1f;
		camera.pitch -= pitchChange;
	}
	
	private void calculateAngleAroundPlayer(){
		float angleChange = Mouse.getDX() * mouseSensitivity * 0.3f;
		angleAroundPlayer -= angleChange;
	}
	
	private float calculatedHorizontalDistance(){
		return (float) (distanceFromPlayer * Math.cos(Math.toRadians(camera.pitch)));
	}
	
	private float calculatedVerticalDistance(){
		return (float) (distanceFromPlayer * Math.sin(Math.toRadians(camera.pitch)));
	}
	
	private void calculateCameraPosition(float horizontalDistance, float verticalDistance){
		float theta = player.getRotY() + angleAroundPlayer;
		float offsetX = (float) (horizontalDistance * Math.sin(Math.toRadians(theta)));
		float offsetZ = (float) (horizontalDistance * Math.cos(Math.toRadians(theta)));
		camera.position.x = player.getPosition().x - offsetX;
		camera.position.z = player.getPosition().z - offsetZ;
		camera.position.y = player.getPosition().y + verticalDistance + 3f;
				
	}
	
}
