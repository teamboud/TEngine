package skybox;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Light;
import models.RawModel;
import renderEngine.Loader;
import toolbox.FrameTime;

public class SkyboxRenderer {
	private static final float SIZE = 500f;

	private static final float[] VERTICES = {        
	    -SIZE,  SIZE, -SIZE,
	    -SIZE, -SIZE, -SIZE,
	    SIZE, -SIZE, -SIZE,
	     SIZE, -SIZE, -SIZE,
	     SIZE,  SIZE, -SIZE,
	    -SIZE,  SIZE, -SIZE,

	    -SIZE, -SIZE,  SIZE,
	    -SIZE, -SIZE, -SIZE,
	    -SIZE,  SIZE, -SIZE,
	    -SIZE,  SIZE, -SIZE,
	    -SIZE,  SIZE,  SIZE,
	    -SIZE, -SIZE,  SIZE,

	     SIZE, -SIZE, -SIZE,
	     SIZE, -SIZE,  SIZE,
	     SIZE,  SIZE,  SIZE,
	     SIZE,  SIZE,  SIZE,
	     SIZE,  SIZE, -SIZE,
	     SIZE, -SIZE, -SIZE,

	    -SIZE, -SIZE,  SIZE,
	    -SIZE,  SIZE,  SIZE,
	     SIZE,  SIZE,  SIZE,
	     SIZE,  SIZE,  SIZE,
	     SIZE, -SIZE,  SIZE,
	    -SIZE, -SIZE,  SIZE,

	    -SIZE,  SIZE, -SIZE,
	     SIZE,  SIZE, -SIZE,
	     SIZE,  SIZE,  SIZE,
	     SIZE,  SIZE,  SIZE,
	    -SIZE,  SIZE,  SIZE,
	    -SIZE,  SIZE, -SIZE,

	    -SIZE, -SIZE, -SIZE,
	    -SIZE, -SIZE,  SIZE,
	     SIZE, -SIZE, -SIZE,
	     SIZE, -SIZE, -SIZE,
	    -SIZE, -SIZE,  SIZE,
	     SIZE, -SIZE,  SIZE
	};
	
private static String[] TEXTURE_FILES = {"right","left","top","bottom","back","front"};
private static String[] NIGHT_TEXTURE_FILES = {"nightRight","nightLeft","nightTop","nightBottom","nightBack","nightFront"};
	private RawModel cube;
	private int texture;
	private int nightTexture;
	private SkyboxShader shader;
	public float time = 0;
	public float r,g,b;
	public float tintR, tintG, tintB;
	private float firstr,firstg,firstb;
	private boolean night = false;
	public boolean startcounting = true;
	private Light sun;
	public SkyboxRenderer(Loader loader, Matrix4f projectionMatrix,Light sun){
		this.sun = sun;
		cube = loader.loadToVAO(VERTICES, 3);
		texture = loader.loadCubeMap(TEXTURE_FILES);
		nightTexture = loader.loadCubeMap(NIGHT_TEXTURE_FILES);
		shader = new SkyboxShader();
		shader.start();
		shader.connectTextureUnits();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
	}
	public void loadTintColor(float r, float g, float b){
		tintR = r;
		tintG = g;
		tintB = b;
	}
	public void render(Camera camera, float r, float g, float b){
		shader.start();
		shader.loadViewMatrix(camera);
		firstr = r;
		firstg = g;
		firstb = b;
		if(!(night)){
		this.r = firstr;
		this.g = firstg;
		this.b = firstb;
		}
		shader.loadFogColor(this.r, this.g, this.b);
		shader.loadTintColor(tintR, tintG, tintB);
		GL30.glBindVertexArray(cube.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		bindTextures();
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, cube.getVertexCount());
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		shader.stop();
	}
	
	float blendFactor = 1;
	
	
	private final float startLight = 0.7f;
	private final float stopLight = 2.0f;
	private void bindTextures(){
		int texture1 = 0;
		int texture2 = 1;
		if(startcounting == true){
		time += FrameTime.getDelta() * 500;
		time %= 24000;
		//time = 5000; 
		if(time >= 0 && time < 5000){
			texture1 = nightTexture;
			texture2 = nightTexture;
			blendFactor = (time - 0)/(5000 - 0);
			night = true;
			r = 0;
			g = 0;
			b = 0;
			sun.setColor(new Vector3f(startLight,startLight,startLight));
		}else if(time >= 5000 && time < 8000){
			texture1 = nightTexture;
			texture2 = texture;
			blendFactor = (time - 5000)/(8000 - 5000);
			night = true;
			r = firstr * blendFactor;
			g = firstg * blendFactor;
			b = firstb * blendFactor;
			float blendFactor2 = blendFactor;
			if(blendFactor2 <= startLight / 2){
				blendFactor2 = startLight / 2;
			}
			sun.setColor(new Vector3f(2* blendFactor2,2* blendFactor2,2* blendFactor2));
		}else if(time >= 8000 && time < 21000){
			texture1 = texture;
			texture2 = texture;
			blendFactor = (time - 8000)/(21000 - 8000);
			night = false;
			r = firstr;
			g = firstg;
			b = firstb;
			sun.setColor(new Vector3f(2,2,2));
		}else{
			texture1 = texture;
			texture2 = nightTexture;
			blendFactor = (time - 21000)/(24000 - 21000);
			night = true;
			float num = 1 - blendFactor;
			r = firstr * num;
			g = firstg * num;
			b = firstb * num;
			float num2 = num;
			if(num2 <= startLight / 2){
				num2 = startLight / 2;
			}
			sun.setColor(new Vector3f(2* num2,2* num2,2* num2));
			}
		}
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texture1);
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texture2);
		shader.loadBlendFactor(blendFactor);
	}
}
