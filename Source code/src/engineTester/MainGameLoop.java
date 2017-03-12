package engineTester;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import entities.Camera;
import entities.Light;
import entities.Player;
import fontMeshCreator.FontType;
import fontMeshCreator.GUIText;
import fontRendering.TextMaster;
import guis.GuiRenderer;
import guis.GuiTexture;
import input.KeyboardInput;
import input.MouseInput;
import maps.MapFile;
import maps.MapLoader;
import entities.Entity;
import models.TexturedModel;
import models.RawModel;
import normalMappingObjConverter.NormalMappedObjLoader;
import particles.Particle;
import particles.ParticleMaster;
import particles.ParticleSystem;
import particles.ParticleTexture;
import postProcessing.Fbo;
import postProcessing.PostProcessing;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJLoader;
import renderEngine.EntityRenderer;
import shaders.StaticShader;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import toolbox.FrameTime;
import toolbox.Maths;
import toolbox.MousePicker;
import water.WaterFrameBuffers;
import water.WaterRenderer;
import water.WaterShader;
import water.WaterTile;

public class MainGameLoop {
	
/**
 * @param args
 */
public static void main(String[] args) {

	/*
	 * 		TUT 18: 8:48
	 * 
	 * 
	 */
		
	DisplayManager.createDisplay();
	
	Loader loader = new Loader();
	//Camera camera = new Camera(286,500,376);
	MapLoader maploader = new MapLoader();
	MapFile map = maploader.loadMap("res/maps/examplemap.tmap");

	Player player = new Player(map.entities.get(1).getModel(), new Vector3f(100,53,100),0,0,0,1);
	Camera camera = new Camera(player,917,50,775,true);
	//Camera weaponCamera = new Camera(player,-0.5f,2,2);
	Light sun = map.getLights().get(0);
	
	MasterRenderer renderer = new MasterRenderer(loader,camera, sun);
	GuiRenderer guiRenderer = new GuiRenderer(loader);
	
	TextMaster.init(loader);
	ParticleMaster.init(loader, renderer.getProjectionMatrix());
	
	
	FontType font = new FontType("bold", loader);
	GUIText text = new GUIText("TEngine - Testing Build 12", 1,font, new Vector2f(0.8f,0), 1f, false);

	text.setColour(1, 1, 1);

	
	//Guis
	List<GuiTexture> guis = new ArrayList<GuiTexture>();
	GuiTexture gui1 = new GuiTexture(loader.loadTextureW("icon/icon32", 0),new Vector2f(0.97647f,-0.958335f), new Vector2f(0.0235f,0.045f));
	GuiTexture crosshair = new GuiTexture(loader.loadTexture("crosshair", 0),new Vector2f(0,0), new Vector2f(0.018f,0.032f));
	guis.add(gui1);
	guis.add(crosshair);

	//water
	WaterFrameBuffers fbos = new WaterFrameBuffers();
	WaterShader waterShader = new WaterShader();
	WaterRenderer waterRenderer = new WaterRenderer(loader,waterShader, renderer.getProjectionMatrix(), fbos);

	MouseInput mouse = new MouseInput(camera,renderer,map.getTerrains().get(0),map.getLights().get(1));
	KeyboardInput keyboard = new KeyboardInput(player, camera, map.getTerrains().get(0));

	boolean underWater = false;

	ParticleTexture particleTexture = new ParticleTexture(loader.loadTexture("fire", 0), 8);
	
	ParticleSystem system = new ParticleSystem(particleTexture,50,8,10,0.3f,1);
	Vector4f clipPlane = new Vector4f(0, -1, 0, 10000);
	map.processEntity(player);
	
	Fbo msfbo = new Fbo(Display.getWidth(),Display.getHeight());
	Fbo outputFbo = new Fbo(Display.getWidth(),Display.getHeight(), Fbo.DEPTH_TEXTURE);
	PostProcessing.init(loader);
	while(!Display.isCloseRequested()){
		FrameTime.updateTime();
		//player.move();
		mouse.update();
		
    				ParticleMaster.update(camera);	
    				GUIText textposition = new GUIText("Position:  " + (int)camera.getPosition().x + " " + (int)camera.getPosition().y + " " + (int)camera.getPosition().z,
    						1,font, new Vector2f(0.15f,0.95f), 1f, false);
    				textposition.setColour(1, 1, 1);
		renderer.renderShadowMap(map.getEntities(), map.getNormalMapEntities(), sun);	
    	renderer.renderAll(waterRenderer,outputFbo,msfbo,fbos, map, camera, clipPlane);
		ParticleMaster.renderParticles(camera);	
						
						
						guiRenderer.render(guis);
						
					TextMaster.render();
					TextMaster.removeText(textposition);
            	
				
	DisplayManager.updateDisplay();
    			
			}
	PostProcessing.cleanUp();
		msfbo.cleanUp();
		outputFbo.cleanUp();
		fbos.cleanUp();
		ParticleMaster.cleanUp();
		waterShader.CleanUp();
		//guiRenderer.cleanUp();
		renderer.cleanUp();
		loader.CleanUp();
		TextMaster.cleanUp();
		DisplayManager.closeDisplay(); //Closes the display
	}

}