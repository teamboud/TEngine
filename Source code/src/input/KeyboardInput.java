package input;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Controller;
import org.lwjgl.input.Controllers;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.input.ControllerAdapter;

import entities.Camera;
import entities.Player;
import renderEngine.DisplayManager;
import terrains.Terrain;
import toolbox.FrameTime;

public class KeyboardInput {

	private Camera camera;
	private Terrain terrain;
	private Player player;
	
	public KeyboardInput(Player player, Camera camera, Terrain terrain){
		this.player = player;
		this.camera = camera;
		this.terrain = terrain;
	}
	
	public static final float gravity  = 15f;
	static float movementSpeed = 15f;
	float velocityY = 0;
	float runningSpeed = 1;
	boolean inAir = false;
	boolean flying = false;
	
	boolean shiftPressed = false;
	
	private boolean checkKey(){
		boolean whatToReturn = false;
		if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)){
			shiftPressed = true;
		}else if(!(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))){
			if(shiftPressed == true){
			whatToReturn = true;
			shiftPressed = false;
			}
		}
		return whatToReturn;
	}
	
	boolean running = false;
	//TUT 35 9:19
	public void update(){
		float speed = (float) FrameTime.getDelta();
		
		if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)){
			System.exit(0);
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)){
			if(flying){
				player.goDown(speed * movementSpeed * 2);
			}
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)){
					runningSpeed = 1.5f;
			}
		else if(!(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL))){
				runningSpeed = 1;
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)){
			if(flying){
				player.goUp(speed * movementSpeed * 2);
			}else{
				if(!inAir){
					velocityY = gravity;
					inAir = true;
				}
			}
		
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_NUMPAD7)){
				flying = false;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_NUMPAD8)){
				velocityY = gravity;
				inAir = true;
				flying = true;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_NUMPAD4)){
			Mouse.setGrabbed(true);
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_NUMPAD5)){
			Mouse.setGrabbed(false);
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_LEFT)){
			DisplayManager.setFps(DisplayManager.getFps() - 1);
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_RIGHT)){
			DisplayManager.setFps(DisplayManager.getFps() + 1);
		}
		
	//	if(!(flying)){
	//		float terrainheight = terrain.getHeightOfTerrain(camera.getPosition().x, camera.getPosition().getZ());
	//				if (terrainheight == 0 || camera.getPosition().getY() <= terrainheight) {
	//						camera.goDown(gravity* 1.5f * speed);
	//				}else{
	//					terrainheight += 5.5f;
	//								if(inAir == true && camera.getPosition().y >= (terrainheight - 0.2f)){
	//									camera.goUp(velocityY * speed);      // Apply vertical velocity to X position
	//									velocityY += (-gravity * 1.5f) * speed;        // Apply gravity to vertical velocity
	//								}else{
	//									camera.getPosition().setY(terrainheight);
	//									inAir = false;
	//								}
	//				}
	//		}
		if(!(flying)){
			float terrainheight = terrain.getHeightOfTerrain(player.getPosition().x ,player.getPosition().z);
					if (terrainheight == 0 || player.getPosition().y <= terrainheight) {
							player.goDown(gravity* 1.5f * speed);
					}else{
						terrainheight += 0.5f;
									if(inAir == true && player.getPosition().y >= (terrainheight)){
										player.goUp(velocityY * speed);      // Apply vertical velocity to X position
										velocityY += (-gravity * 1.5f) * speed;        // Apply gravity to vertical velocity
									}else{
										player.getPosition().setY(terrainheight);
										inAir = false;
									}
					}
			}
	}
}
