package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import models.TexturedModel;
import toolbox.FrameTime;

public class Player extends Entity{
	
	public float currentSpeed = 0;
	public float currentTurnSpeed = 0;
	
	public Player(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		super(model, position, rotX, rotY, rotZ, scale);
		// TODO Auto-generated constructor stub
	}
			
	public void move(){
		super.increaseRotation(0, (float) (currentTurnSpeed * FrameTime.getDelta()), 0);
		float distance = (float) (currentSpeed * FrameTime.getDelta());
		float dx = (float) (distance * Math.sin(Math.toRadians(super.getRotY())));
		float dz = (float) (distance * Math.cos(Math.toRadians(super.getRotY())));
		super.increasePosition(dx, 0, dz);
		}
	public void goUp(float amount){
		super.getPosition().y += amount;
	}
	public void goDown(float amount){
		super.getPosition().y -= amount;
	}

}
