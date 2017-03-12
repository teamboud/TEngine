package maps;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.lwjgl.util.vector.Vector3f;

import entities.Entity;
import entities.Light;
import models.TexturedModel;
import models.RawModel;
import normalMappingObjConverter.NormalMappedObjLoader;
import objConverter.ModelData;
import objConverter.OBJFileLoader;
import renderEngine.Loader;
import renderEngine.OBJLoader;
import textures.ModelTexture;
import water.WaterTile;

public class MapLoader {
	private static final float bias = -0.4f;
	public MapFile loadMap(String mapName) {
		BufferedReader reader = null;
		Loader loader = new Loader();
		MapFile map = null;
		try {
			reader = new BufferedReader(new FileReader(mapName));
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}
		
		try {
			String line = null;
			while((line = reader.readLine()) != null){
				if(line == "END"){
					break;
				}else{
					String[] lineCon = line.split(" ");
					if(line.startsWith("Terrain: ")){
						map = new MapFile(Integer.parseInt(lineCon[1]), lineCon[2], lineCon[3], lineCon[4], lineCon[5], lineCon[6], lineCon[7]);
					}
					
					else if(line.startsWith("Light: ")){
						String[] positionValues = lineCon[1].split(",");
						Vector3f position = new Vector3f(Float.parseFloat(positionValues[0]),Float.parseFloat(positionValues[1]),Float.parseFloat(positionValues[2]));
						String[] colorValues = lineCon[2].split(",");
						Vector3f color = new Vector3f(Float.parseFloat(colorValues[0]),Float.parseFloat(colorValues[1]),Float.parseFloat(colorValues[2]));
						Light light = new Light(position, color);
						map.processLight(light);
					}
					
					else if(line.startsWith("AttLight: ")){
						String[] positionValues = lineCon[1].split(",");
						Vector3f position = new Vector3f(Float.parseFloat(positionValues[0]),Float.parseFloat(positionValues[1]),Float.parseFloat(positionValues[2]));
						String[] colorValues = lineCon[2].split(",");
						Vector3f color = new Vector3f(Float.parseFloat(colorValues[0]),Float.parseFloat(colorValues[1]),Float.parseFloat(colorValues[2]));
						String[] attValues = lineCon[3].split(",");
						Vector3f attenuation = new Vector3f(Float.parseFloat(attValues[0]),Float.parseFloat(attValues[1]),Float.parseFloat(attValues[2]));
						Light light = new Light(position, color, attenuation);
						map.processLight(light);
					}	
					else if(line.startsWith("normalEntity: ")){
						RawModel rawmodel = NormalMappedObjLoader.loadOBJ(lineCon[1], loader);
						ModelTexture texture = new ModelTexture(loader.loadTexture(lineCon[2],bias));
						TexturedModel model = new TexturedModel(rawmodel, texture);
						model.getTexture().setNormalMap(loader.loadTexture(lineCon[3],bias));
						String[] positionValues = lineCon[4].split(",");
						Vector3f position = new Vector3f(Float.parseFloat(positionValues[0]),Float.parseFloat(positionValues[1]),Float.parseFloat(positionValues[2]));
						float scale = 1;
						if(lineCon.length == 6){
							scale = Float.parseFloat(lineCon[5]);
						}
						Entity entity = new Entity(model, position, 0, 0, 0, scale);
						map.processNormalEntity(entity);
					}
					else if(line.startsWith("Entity: ")){	
						RawModel rawmodel = OBJFileLoader.loadOBJ(lineCon[1],loader);
						
						//RawModel rawmodel = NormalMappedObjLoader.loadOBJ(lineCon[1], loader);
						ModelTexture texture = new ModelTexture(loader.loadTexture(lineCon[2],bias));
						TexturedModel model = new TexturedModel(rawmodel, texture);
						String[] positionValues = lineCon[3].split(",");
						Vector3f position = new Vector3f(Float.parseFloat(positionValues[0]),Float.parseFloat(positionValues[1]),Float.parseFloat(positionValues[2]));
						float scale = 1;
						if(lineCon.length == 5){
							scale = Float.parseFloat(lineCon[4]);
						}
						Entity entity = new Entity(model, position, 0, 0, 0, scale);
						map.processEntity(entity);
					}
					else if(line.startsWith("AEntity: ")){	
						RawModel rawmodel = OBJLoader.loadObjModel(lineCon[1],loader);
						
						//RawModel rawmodel = NormalMappedObjLoader.loadOBJ(lineCon[1], loader);
						ModelTexture texture = new ModelTexture(loader.loadTexture(lineCon[2],bias));
						TexturedModel model = new TexturedModel(rawmodel, texture);
						String[] positionValues = lineCon[3].split(",");
						Vector3f position = new Vector3f(Float.parseFloat(positionValues[0]),Float.parseFloat(positionValues[1]),Float.parseFloat(positionValues[2]));
						float scale = 1;
						if(lineCon.length == 5){
							scale = Float.parseFloat(lineCon[4]);
						}
						Entity entity = new Entity(model, position, 0, 0, 0, scale);
						map.processEntity(entity);
					}
					else if(line.startsWith("MTLEntity: ")){	
						RawModel rawmodel = OBJFileLoader.loadMTLOBJ(lineCon[1],loader);
						ModelTexture texture = new ModelTexture(loader.loadTexture(lineCon[2],bias));
						TexturedModel model = new TexturedModel(rawmodel, texture);
						String[] positionValues = lineCon[lineCon.length-1].split(",");
						Vector3f position = new Vector3f(Float.parseFloat(positionValues[0]),Float.parseFloat(positionValues[1]),Float.parseFloat(positionValues[2]));
						float scale = 1;
						Entity entity = new Entity(model, position, 0, 0, 0, scale);
						map.processEntity(entity);
					}
					else if(line.startsWith("Water: ")){
						String[] positionValues = lineCon[1].split(",");
						WaterTile water = new WaterTile(Float.parseFloat(positionValues[0]),Float.parseFloat(positionValues[1]),Float.parseFloat(positionValues[2]));
						System.out.println(positionValues[0] + " " + positionValues[1] + " " + positionValues[2]);
						map.processWater(water);
					}
				}
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}
	
}
