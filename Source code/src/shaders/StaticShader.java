package shaders;

import java.util.List;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import entities.Camera;
import entities.Light;
import toolbox.Maths;

public class StaticShader extends ShaderProgram{

	private static final int MAX_LIGHTS = 25;
	private static final int MAX_SAMPLERS = 16;
	
	private static final String VERTEX_FILE = "src/shaders/vertexShader.txt";
	private static final String FRAGMENT_FILE = "src/shaders/fragmentShader.txt";
			
	private int location_transformationMatrix;
	private int location_projectionMatrix;
	private int location_viewMatrix;
	
	private int location_lightPosition[];
	private int location_lightColor[];
	private int location_attenuation[];
	private int location_shineDamper;
	private int location_reflectivity;
	private int location_useFakeLightning;
	private int location_skyColor;
	private int location_numberOfRows;
	private int location_offset;
	private int location_plane;
	private int location_tintColor;
	
	private int location_textureSampler[];
	public StaticShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");
		super.bindAttribute(2, "normal");
	}

	@Override
	protected void getAllUniformLocations() {
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
		location_shineDamper = super.getUniformLocation("shineDamper");
		location_reflectivity = super.getUniformLocation("reflectivity");
		location_useFakeLightning = super.getUniformLocation("useFakeLightning");
		location_skyColor = super.getUniformLocation("skyColor");
		location_numberOfRows = super.getUniformLocation("numberOfRows");
		location_offset = super.getUniformLocation("offset");
		location_plane = super.getUniformLocation("plane");
		location_tintColor = super.getUniformLocation("tintColor");
		
		location_lightPosition = new int[MAX_LIGHTS];
		location_lightColor = new int[MAX_LIGHTS];
		location_attenuation = new int[MAX_LIGHTS];
		location_textureSampler = new int[MAX_SAMPLERS];
		for(int i=0; i<MAX_LIGHTS;i++){
			location_lightPosition[i] = super.getUniformLocation("lightPosition[" + i + "]");
			location_lightColor[i] = super.getUniformLocation("lightColor[" + i + "]");
			location_attenuation[i] = super.getUniformLocation("attenuation[" + i +"]");
		}
		for(int g=0; g<MAX_SAMPLERS;g++){
			location_textureSampler[g] = super.getUniformLocation("textureSampler[" + g + "]");
		}
	}
	
	public void loadTextures(){
		super.loadInt(location_textureSampler[0], 0);
		super.loadInt(location_textureSampler[1], 1);
		super.loadInt(location_textureSampler[2], 2);
		super.loadInt(location_textureSampler[3], 3);
		super.loadInt(location_textureSampler[4], 4);
		super.loadInt(location_textureSampler[5], 5);
		super.loadInt(location_textureSampler[6], 6);
		super.loadInt(location_textureSampler[7], 7);
		super.loadInt(location_textureSampler[8], 8);
		super.loadInt(location_textureSampler[9], 9);
		super.loadInt(location_textureSampler[10], 10);
		super.loadInt(location_textureSampler[11], 11);
		super.loadInt(location_textureSampler[12], 12);
		super.loadInt(location_textureSampler[13], 13);
		super.loadInt(location_textureSampler[14], 14);
		super.loadInt(location_textureSampler[15], 15);
		super.loadInt(location_textureSampler[16], 16);
	}
	
	public void loadTintColor(float r, float g, float b){
		super.loadVector(location_tintColor, new Vector3f(r,g,b));
	}
	
	public void loadClipPlane(Vector4f plane){
		super.load4DVector(location_plane, plane);
	}
	
	public void loadNumberOfRows(int numberOfRows){
		super.loadFloat(location_numberOfRows, numberOfRows);
	}
	public void loadOffset(float x, float y){
		super.load2DVector(location_offset, new Vector2f(x,y));
	}
	public void loadSkyColor(float r, float g, float b){
		super.loadVector(location_skyColor, new Vector3f(r,g,b));
	}
	
	public void loadFakeLightningVariable(boolean useFake){
		super.loadBoolean(location_useFakeLightning, useFake);
	}
	
	public void loadShineVariables(float damper, float reflectivity){
		super.loadFloat(location_shineDamper, damper);
		super.loadFloat(location_reflectivity, reflectivity);
	}
	public void loadTransformationMatrix(Matrix4f matrix){
	super.loadMatrix(location_transformationMatrix, matrix);
	}
	public void loadLights(List<Light> lights){
			for(int i=0; i<MAX_LIGHTS; i++){
					if(i<lights.size()){
						super.loadVector(location_lightPosition[i], lights.get(i).getPosition());
						super.loadVector(location_lightColor[i], lights.get(i).getColor());
						super.loadVector(location_attenuation[i], lights.get(i).getAttenuation());
					}else{
						super.loadVector(location_lightPosition[i], new Vector3f(0,0,0));
						super.loadVector(location_lightColor[i], new Vector3f(0,0,0));	
						super.loadVector(location_attenuation[i], new Vector3f(1,0,0));
					}
				}
	}
	public void loadProjectionMatrix(Matrix4f matrix){
		super.loadMatrix(location_projectionMatrix, matrix);
	}
	public void loadViewMatrix(Camera camera){
		Matrix4f matrix = Maths.createViewMatrix(camera);
		super.loadMatrix(location_viewMatrix, matrix);
	}
	public void loadViewMatrix(Matrix4f matrix){
		super.loadMatrix(location_viewMatrix, matrix);
	}
}
