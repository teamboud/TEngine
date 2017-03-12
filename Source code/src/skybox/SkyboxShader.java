package skybox;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import shaders.ShaderProgram;
import toolbox.FrameTime;
import toolbox.Maths;

public class SkyboxShader extends ShaderProgram{

	private static final String VERTEX_FILE = "src/skybox/skyboxVertexShader.txt";
	private static final String FRAGMENT_FILE = "src/skybox/skyboxFragmentShader.txt";
	
	private static final float ROTATE_SPEED = 0.5f;
	
	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int location_fogColor;
	private int location_cubeMap1;
	private int location_cubeMap2;
	private int location_blendFactor;
	private int location_tintColor;
	
	private float rotation = 0;
	
	public SkyboxShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}
	
	public void loadProjectionMatrix(Matrix4f matrix){
		super.loadMatrix(location_projectionMatrix, matrix);
	}
	float speed;
	public void loadViewMatrix(Camera camera){
		Matrix4f matrix = Maths.createViewMatrix(camera);
		matrix.m30 = 0;
		matrix.m31 = 0;
		matrix.m32 = 0;
        rotation += ROTATE_SPEED * FrameTime.getDelta();
        //System.out.println(rotation);
        Matrix4f.rotate((float) Math.toRadians(rotation), new Vector3f(0,1,0), matrix, matrix);
        rotation%=360;
		super.loadMatrix(location_viewMatrix, matrix);
	}
	public void loadTintColor(float r, float g, float b){
		super.loadVector(location_tintColor, new Vector3f(r,g,b));
	}
	public void connectTextureUnits(){
		super.loadInt(location_cubeMap1, 0);
		super.loadInt(location_cubeMap2, 1);
	}
	
	public void loadBlendFactor(float blend){
		super.loadFloat(location_blendFactor, blend);
	}
	
	public void loadFogColor(float r, float g, float b){
		super.loadVector(location_fogColor, new Vector3f(r,g,b));
	}
	
	@Override
	protected void getAllUniformLocations() {
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
		location_fogColor = super.getUniformLocation("fogColor");
		location_cubeMap1 = super.getUniformLocation("cubeMap");
		location_cubeMap2 = super.getUniformLocation("cubeMap2");
		location_blendFactor = super.getUniformLocation("blendFactor");
		location_tintColor = super.getUniformLocation("tintColor");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}

}
