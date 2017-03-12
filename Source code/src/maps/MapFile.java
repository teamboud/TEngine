package maps;

import java.util.ArrayList;
import java.util.List;
import entities.Entity;
import entities.Light;
import renderEngine.Loader;
import terrains.Terrain;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import water.WaterTile;

public class MapFile {
	public float terrainSize;
	
	public List<Entity> entities = new ArrayList<Entity>();
	public List<Entity> normalMapEntities = new ArrayList<Entity>();
	public List<Terrain> terrains = new ArrayList<Terrain>();
	public List<WaterTile> waters = new ArrayList<WaterTile>();
	public List<Light> lights = new ArrayList<Light>();
	
	public List<Entity> getEntities() {
		return entities;
	}
	public List<Entity> getNormalMapEntities() {
		return normalMapEntities;
	}
	public List<Terrain> getTerrains() {
		return terrains;
	}
	public List<WaterTile> getWaters() {
		return waters;
	}
	public List<Light> getLights() {
		return lights;
	}
	public MapFile(int terrainSize, String blackTexture, String redTexture, String greenTexture, String blueTexture,String heightMap, String blendMapStr){
		Loader loader = new Loader();
	    TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture(blackTexture, 0.4f));
	    TerrainTexture rTexture = new TerrainTexture(loader.loadTexture(redTexture,0));
	    TerrainTexture gTexture = new TerrainTexture(loader.loadTexture(greenTexture,0));
	    TerrainTexture bTexture = new TerrainTexture(loader.loadTexture(blueTexture,0));  
	    TerrainTexture blendMap = new TerrainTexture(loader.loadTexture(blendMapStr,0));	    
	    TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture,bTexture); 
		Terrain terrain = new Terrain(terrainSize, 0,0,loader,texturePack, blendMap, heightMap);
		terrains.add(terrain);
	}
	public void processLight(Light light){
		lights.add(light);
	}
	public void processWater(WaterTile water){
		waters.add(water);
	}
	public void processEntity(Entity entity){
		entities.add(entity);
	}
	public void processNormalEntity(Entity entity){
		normalMapEntities.add(entity);
	}
}
