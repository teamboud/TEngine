package renderEngine;
 
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.TexturedModel;
import normalMappingRenderer.NormalMappingRenderer;
import postProcessing.Fbo;
import postProcessing.PostProcessing;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;

import shaders.StaticShader;
import shaders.TerrainShader;
import shadows.ShadowMapMasterRenderer;
import skybox.SkyboxRenderer;
import terrains.Terrain;
import water.WaterFrameBuffers;
import water.WaterRenderer;
import water.WaterShader;
import water.WaterTile;
import entities.Camera;
import entities.Entity;
import entities.Light;
import maps.MapFile;
 
public class MasterRenderer {
 
    public static final float FOV = 70;
    public static final float NEAR_PLANE = 0.1f;
    public static final float FAR_PLANE = 1024;
 
    private static final float ORIGINALRED = 0.544f;
    private static final float ORIGINALGREEN = 0.61f;
    private static final float ORIGINALBLUE = 0.69f;
   //private static final float ORIGINALRED = 0.50f;
   //private static final float ORIGINALGREEN = 0.71f;
   //private static final float ORIGINALBLUE = 0.98f;
    public static float RED;
    public static float GREEN;
    public static float BLUE;
 
    private Matrix4f projectionMatrix;
 
    private StaticShader shader = new StaticShader();
    private EntityRenderer renderer;
 
    private TerrainRenderer terrainRenderer;
    private TerrainShader terrainShader = new TerrainShader();
 
    private NormalMappingRenderer normalMapRenderer;
    
    private SkyboxRenderer skyboxRenderer;
    
    private ShadowMapMasterRenderer shadowMapRenderer;
    
    private Map<TexturedModel, List<Entity>> entities = new HashMap<TexturedModel, List<Entity>>();
    private Map<TexturedModel, List<Entity>> normalMapEntities = new HashMap<TexturedModel, List<Entity>>();
    private List<Terrain> terrains = new ArrayList<Terrain>();
 
    public MasterRenderer(Loader loader,Camera camera,Light sun) {
        enableCulling();
        createProjectionMatrix();
        renderer = new EntityRenderer(shader, projectionMatrix);
        terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
        skyboxRenderer = new SkyboxRenderer(loader, projectionMatrix, sun);
        normalMapRenderer = new NormalMappingRenderer(projectionMatrix);
        shadowMapRenderer = new ShadowMapMasterRenderer(camera);
    }
 
    public Matrix4f getProjectionMatrix() {
        return this.projectionMatrix;
    }
 
    private void processAll(MapFile map){
        for (Terrain terrain : map.getTerrains()) {
            processTerrain(terrain);
        }
        for (Entity entity : map.getEntities()) {
            processEntity(entity);
        }
        for (Entity entity : map.getNormalMapEntities()) {
            processNormalMapEntity(entity);
        }
    }
    
    public void renderAll(WaterFrameBuffers fbos, Entity weapon, MapFile map, Camera weaponCamera, Camera camera, Vector4f clipPlane){
    	boolean isUnderW = isUnderWater(camera, map.getWaters());
      	processAll(map);
		GL11.glEnable(GL30.GL_CLIP_DISTANCE0);		
		fbos.bindReflectionFrameBuffer();
		float distance =  2 * (camera.getPosition().y - map.getWaters().get(0).getHeight());
		camera.getPosition().y -= distance;
		camera.invertPitch();
        render(map.getLights(), camera, new Vector4f(0, 1, 0, -map.getWaters().get(0).getHeight()+1), isUnderW);
		camera.getPosition().y += distance;
		camera.invertPitch();
		processAll(map);
		fbos.bindRefractionFrameBuffer();
		render(weapon,weaponCamera,map.getLights(), camera, new Vector4f(0, -1, 0, map.getWaters().get(0).getHeight()), isUnderW);
		processAll(map);
		fbos.unbindCurrentFrameBuffer();
		GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
        render(weapon,weaponCamera,map.getLights(), camera, clipPlane, isUnderW);
    }
    public void renderAll(WaterRenderer waterRenderer,Fbo outputFbo,Fbo fbo,WaterFrameBuffers fbos, MapFile map, Camera camera, Vector4f clipPlane){
    	boolean isUnderW = isUnderWater(camera, map.getWaters());
      	processAll(map);
		GL11.glEnable(GL30.GL_CLIP_DISTANCE0);		
		fbos.bindReflectionFrameBuffer();
		float distance =  2 * (camera.getPosition().y - map.getWaters().get(0).getHeight());
		camera.getPosition().y -= distance;
		camera.invertPitch();
        render(map.getLights(), camera, new Vector4f(0, 1, 0, -map.getWaters().get(0).getHeight()+1), isUnderW);
		camera.getPosition().y += distance;
		camera.invertPitch();
		processAll(map);
		fbos.bindRefractionFrameBuffer();
		render(map.getLights(), camera, new Vector4f(0, -1, 0, map.getWaters().get(0).getHeight()+1), isUnderW);
		processAll(map);
		fbos.unbindCurrentFrameBuffer();
		GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
		fbo.bindFrameBuffer();
        render(map.getLights(), camera, clipPlane, isUnderW);
        waterRenderer.render(map.getWaters(), camera, map.getLights().get(0),rotation);
		fbo.unbindFrameBuffer();
		fbo.resolveToFbo(outputFbo);
        PostProcessing.doPostProcessing(outputFbo.getColourTexture());
    }
    private boolean isUnderWater(Camera camera,List<WaterTile> waters){
		boolean bol = false;
		for(WaterTile water:waters){
		if(camera.getPosition().x <= water.getX() + water.TILE_SIZE && camera.getPosition().x >= water.getX() - water.TILE_SIZE && camera.getPosition().z <= water.getZ() + water.TILE_SIZE  && camera.getPosition().z >= water.getZ() - water.TILE_SIZE && water.getHeight() >= camera.getPosition().y){
			bol = true;
		}			
	}	
		return bol;
	}
 
    private final float tintRED = 0, tintGreen = 0.1f, tintBlue = 0.6f;
    public void render(List<Light> lights, Camera camera, Vector4f clipPlane, boolean underWater) {
        prepare();
        RED = skyboxRenderer.r;
        GREEN = skyboxRenderer.g;
        BLUE = skyboxRenderer.b;
        shader.start();
        shader.loadClipPlane(clipPlane);
        shader.loadSkyColor(RED, GREEN, BLUE);
        shader.loadLights(lights);
        if(underWater){
        	shader.loadTintColor(tintRED, tintGreen, tintBlue);
        	normalMapRenderer.loadTintColor(tintRED, tintGreen, tintBlue);
        	//skyboxRenderer.loadTintColor(tintRED, tintGreen, tintBlue);
        	rotation = 180;
        }else{
        	shader.loadTintColor(0, 0, 0);
        	normalMapRenderer.loadTintColor(0, 0, 0);
        	skyboxRenderer.loadTintColor(0, 0, 0);
        	rotation = 0;
        }
        shader.loadViewMatrix(camera);
        renderer.render(entities);
        shader.stop();
        normalMapRenderer.render(normalMapEntities, clipPlane, lights, camera);
        terrainShader.start();
        if(underWater){
        	terrainShader.loadTintColor(tintRED, tintGreen, tintBlue);
        }else{
        	terrainShader.loadTintColor(0, 0, 0);
        }
        terrainShader.loadShadowMapSize(shadowMapRenderer.SHADOW_MAP_SIZE);
        terrainShader.loadClipPlane(clipPlane);
        terrainShader.loadSkyColor(RED, GREEN, BLUE);
        terrainShader.loadLights(lights);
        terrainShader.loadViewMatrix(camera);
        terrainRenderer.render(terrains, shadowMapRenderer.getToShadowMapSpaceMatrix());
        terrainShader.stop();
        skyboxRenderer.startcounting = true;
        skyboxRenderer.render(camera, ORIGINALRED, ORIGINALGREEN, ORIGINALBLUE);
        terrains.clear();
        entities.clear();
        normalMapEntities.clear();
    }
    public float rotation;
    public void render(Entity weapon,Camera weaponCamera, List<Light> lights, Camera camera, Vector4f clipPlane, boolean underWater) {
        prepare();
        RED = skyboxRenderer.r;
        GREEN = skyboxRenderer.g;
        BLUE = skyboxRenderer.b;
        shader.start();
        shader.loadClipPlane(clipPlane);
        shader.loadSkyColor(RED, GREEN, BLUE);
        shader.loadLights(lights);
        if(underWater){
        	shader.loadTintColor(tintRED, tintGreen, tintBlue);
        	normalMapRenderer.loadTintColor(tintRED, tintGreen, tintBlue);
        	//skyboxRenderer.loadTintColor(tintRED, tintGreen, tintBlue);
        	rotation = 180;
        }else{
        	shader.loadTintColor(0, 0, 0);
        	normalMapRenderer.loadTintColor(0, 0, 0);
        	skyboxRenderer.loadTintColor(0, 0, 0);
        	rotation = 0;
        }
        shader.loadViewMatrix(weaponCamera);
        renderer.render(weapon);
        shader.loadViewMatrix(camera);
        renderer.render(entities);
        shader.stop();
        normalMapRenderer.render(normalMapEntities, clipPlane, lights, camera);
        terrainShader.start();
        if(underWater){
        	terrainShader.loadTintColor(tintRED, tintGreen, tintBlue);
        }else{
        	terrainShader.loadTintColor(0, 0, 0);
        }
        terrainShader.loadShadowMapSize(shadowMapRenderer.SHADOW_MAP_SIZE);
        terrainShader.loadClipPlane(clipPlane);
        terrainShader.loadSkyColor(RED, GREEN, BLUE);
        terrainShader.loadLights(lights);
        terrainShader.loadViewMatrix(camera);
        terrainRenderer.render(terrains, shadowMapRenderer.getToShadowMapSpaceMatrix());
        terrainShader.stop();
        skyboxRenderer.startcounting = true;
        skyboxRenderer.render(camera, ORIGINALRED, ORIGINALGREEN, ORIGINALBLUE);
        terrains.clear();
        entities.clear();
        normalMapEntities.clear();
    }
 
    public static void enableCulling() {
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
    }
 
    public static void disableCulling() {
        GL11.glDisable(GL11.GL_CULL_FACE);
    }
 
    public void processTerrain(Terrain terrain) {
        terrains.add(terrain);
    }
 
    public void processEntity(Entity entity) {
        TexturedModel entityModel = entity.getModel();
        List<Entity> batch = entities.get(entityModel);
        if (batch != null) {
            batch.add(entity);
        } else {
            List<Entity> newBatch = new ArrayList<Entity>();
            newBatch.add(entity);
            entities.put(entityModel, newBatch);
        }
    }
    public void processNormalMapEntity(Entity entity) {
        TexturedModel entityModel = entity.getModel();
        List<Entity> batch = normalMapEntities.get(entityModel);
        if (batch != null) {
            batch.add(entity);
        } else {
            List<Entity> newBatch = new ArrayList<Entity>();
            newBatch.add(entity);
            normalMapEntities.put(entityModel, newBatch);
        }
    }
 
    public void renderShadowMap(List<Entity> entityList,List<Entity> normalMapEntityList, Light sun){
    	for(Entity entity:normalMapEntityList){
    		processNormalMapEntity(entity);
    	}
    	for(Entity entity:entityList){
    		processEntity(entity);
    	}
    	shadowMapRenderer.render(entities,normalMapEntities, sun);
    	entities.clear();
    }
    
    public int getShadowMapTexture(){
    	return shadowMapRenderer.getShadowMap();
    }
    
    public void cleanUp() {
        shader.CleanUp();
        terrainShader.CleanUp();
        normalMapRenderer.cleanUp();
        shadowMapRenderer.cleanUp();
    }
 
    public void prepare() {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glClearColor(RED, GREEN, BLUE, 1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL13.glActiveTexture(GL13.GL_TEXTURE5);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, getShadowMapTexture());
    }
 
    private void createProjectionMatrix(){
    	projectionMatrix = new Matrix4f();
		float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
		float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))));
		float x_scale = y_scale / aspectRatio;
		float frustum_length = FAR_PLANE - NEAR_PLANE;

		projectionMatrix.m00 = x_scale;
		projectionMatrix.m11 = y_scale;
		projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
		projectionMatrix.m33 = 0;
    }
 
}