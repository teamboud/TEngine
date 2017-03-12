package input;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

import engineTester.MainGameLoop;
import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import renderEngine.MasterRenderer;
import terrains.Terrain;
import toolbox.MousePicker;

public class MouseInput {

	private Camera camera;
    MousePicker picker;
    Light light;
    Inputs input;
	public MouseInput(Camera camera, MasterRenderer renderer, Terrain terrain,Light light){
		this.camera = camera;
		this.picker = new MousePicker(camera, renderer.getProjectionMatrix(), terrain);
		this.light = light;
		input = new Inputs(terrain,camera,camera.player,true);
		Mouse.setGrabbed(true);
	}
	private void setMouse(float times){
		//camera.move(times);	
		input.checkForInputs();
	}
	public void update(){
		if(Mouse.isGrabbed()){
				setMouse(1);
		}
				picker.update();
				Vector3f terrainPoint = picker.getCurrentTerrainPoint();
				if(Mouse.isButtonDown(1)){
					if(terrainPoint!=null){
						light.setPosition(new Vector3f(terrainPoint.x, terrainPoint.y + 5f, terrainPoint.z));
					}
				}
			
	}
}
