package postProcessing;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

public class ContrastChanger {
		
	private ImageRenderer renderer;
	private ContrastShader shader;
	public ContrastChanger(boolean toFbo){
		shader = new ContrastShader();
		if(toFbo){
		renderer = new ImageRenderer(Display.getWidth(), Display.getHeight());
		}else{
			renderer = new ImageRenderer();
		}
	}
	
	public void render(int texture){
		shader.start();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
		renderer.renderQuad();
		shader.stop();
	}
	
	public int getOutputTexture(){
		return renderer.getOutputTexture();
	}
	
	public void cleanUp(){
		renderer.cleanUp();
		shader.CleanUp();
	}
}
