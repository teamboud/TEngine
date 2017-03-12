package entities;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.vector.Vector3f;

import engineTester.MainGameLoop;
import toolbox.FrameTime;
import toolbox.Maths;

public class Camera {

	public Vector3f position = new Vector3f(0,0,0);
	public float pitch;
	public float yaw;
	public float roll;
	
    public Player player;
	private float distanceFromPlayer = 5;
    private float angleAroundPlayer = 0;
    private boolean thirdPersonViewEnabled = true;
    private float mouseSensitivity = 0.05f;
    private float dx        = 0.0f;
    private float dy        = 0.0f;
	private final float RUN_SPEED = 20;
	private final float TURN_SPEED = 160;
	float movementSpeed = 15f;
	float velocityY = 0;
	float runningSpeed = 1;
	
	public Camera(Player player, float x, float y, float z,boolean isFPS){
		this.position = new Vector3f(x,y,z);
		this.player = player;
		thirdPersonViewEnabled = !isFPS;
	}
	
	public float getMouseSensitivity() {
		return mouseSensitivity;
	}

	public void setMouseSensitivity(float mouseSensitivity) {
		this.mouseSensitivity = mouseSensitivity;
	}

	private void calculateZoom(){
		float zoomLevel = Mouse.getDWheel() * 0.05f;
		distanceFromPlayer -= zoomLevel;
	}
	
	private void calculatePitch(){
		float pitchChange = Mouse.getDY() * 0.1f;
		pitch -= pitchChange;
	}
	
	private void calculateAngleAroundPlayer(){
		float angleChange = Mouse.getDX() * 0.3f;
		angleAroundPlayer -= angleChange;
	}
	
	private float calculatedHorizontalDistance(){
		return (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
	}
	
	private float calculatedVerticalDistance(){
		return (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch)));
	}
	
	private void calculateCameraPosition(float horizontalDistance, float verticalDistance){
		float theta = player.getRotY() + angleAroundPlayer;
		float offsetX = (float) (horizontalDistance * Math.sin(Math.toRadians(theta)));
		float offsetZ = (float) (horizontalDistance * Math.cos(Math.toRadians(theta)));
		position.x = player.getPosition().x - offsetX;
		position.z = player.getPosition().z - offsetZ;
		position.y = player.getPosition().y + verticalDistance + 3f;
				
	}
	private void checkInputs(){
		float speed = (float) FrameTime.getDelta();
		if(thirdPersonViewEnabled == true){
				if(Keyboard.isKeyDown(Keyboard.KEY_W)){
						player.currentSpeed = RUN_SPEED * speed;
				}else if(Keyboard.isKeyDown(Keyboard.KEY_S)){
						player.currentSpeed = -RUN_SPEED* speed;
				}else{
						player.currentSpeed = 0;
				}if(Keyboard.isKeyDown(Keyboard.KEY_D)){
						player.currentTurnSpeed = -TURN_SPEED* speed;
				}else if(Keyboard.isKeyDown(Keyboard.KEY_A)){
						player.currentTurnSpeed = TURN_SPEED* speed;
				}else{
						player.currentTurnSpeed = 0;
				}
		}else{
			if(Keyboard.isKeyDown(Keyboard.KEY_W)){
				walkForward(speed * movementSpeed  * runningSpeed);
			}
			if(Keyboard.isKeyDown(Keyboard.KEY_S)){
				walkBackwards(speed * movementSpeed  * runningSpeed);
			}
			if(Keyboard.isKeyDown(Keyboard.KEY_A)){
				strafeLeft(speed * movementSpeed  * runningSpeed);
			}
			if(Keyboard.isKeyDown(Keyboard.KEY_D)){
				strafeRight(speed * movementSpeed  * runningSpeed);
			}
		}
	}
	public void move(float times){
		
	}
	public void amove(float times){
		checkInputs();
		if(thirdPersonViewEnabled == true){
				calculateZoom();
				calculatePitch();
				calculateAngleAroundPlayer();
				float horizontalDistance = calculatedHorizontalDistance();
				float verticalDistance = calculatedVerticalDistance();
				calculateCameraPosition(horizontalDistance,verticalDistance);
				this.yaw = 180 - (player.getRotY() + angleAroundPlayer);
				yaw %= 360;
				System.out.println(distanceFromPlayer);
							if(Keyboard.isKeyDown(Keyboard.KEY_W)){
															if(!(yaw == 180)){
																angleAroundPlayer = 0;
															}
													}
		}else{
			dx = Mouse.getDX();
			dy = Mouse.getDY();
			yaw(dx * mouseSensitivity * times);
			pitch(dy * mouseSensitivity * times);
		}
	}
	
	public void invertPitch(){
		this.pitch = -pitch;
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
	public void walkBackwards(float distance)
	{
	    position.x -= distance * (float)Math.sin(Math.toRadians(yaw));
	    position.z += distance * (float)Math.cos(Math.toRadians(yaw));
	}
	 
	//moves the camera backward relative to its current rotation (yaw)
	public void walkForward(float distance)
	{
	    position.x += distance * (float)Math.sin(Math.toRadians(yaw));
	    position.z -= distance * (float)Math.cos(Math.toRadians(yaw));
	}
	 
	//strafes the camera left relitive to its current rotation (yaw)
	public void strafeRight(float distance)
	{
	    position.x -= distance * (float)Math.sin(Math.toRadians(yaw-90));
	    position.z += distance * (float)Math.cos(Math.toRadians(yaw-90));
	}
	 
	//strafes the camera right relitive to its current rotation (yaw)
	public void strafeLeft(float distance)
	{
	    position.x -= distance * (float)Math.sin(Math.toRadians(yaw+90));
	    position.z += distance * (float)Math.cos(Math.toRadians(yaw+90));
	}
	public void yaw(float amount)
	{
	    //increment the yaw by the amount param
	    yaw += amount;
	}
	public void roll(float amount)
	{
	    //increment the yaw by the amount param
	    roll += amount;
	}
	//increment the camera's current yaw rotation
	public void pitch(float amount)
	{
	    //increment the pitch by the amount param
	    pitch -= amount;
	    if(pitch <= -90){
	    	pitch = -90;
	    }else if (pitch >= 90){
	    	pitch = 90;
	    }
	}
	public void goUp(float amount){
		position.y += amount;
	}
	public void goDown(float amount){
		position.y -= amount;
	}

    public boolean isThirdPersonViewEnabled() {
		return thirdPersonViewEnabled;
	}

	public void setThirdPersonViewEnabled(boolean thirdPersonViewEnabled) {
		this.thirdPersonViewEnabled = thirdPersonViewEnabled;
	}

	public Vector3f getPosition() {
		return position;
	}
	
}
