package renderEngine;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.PixelFormat;

import de.matthiasmann.twl.utils.PNGDecoder;
import toolbox.Maths;

public class DisplayManager {

	private static final int WIDTH = 1280; //Width
	private static final int HEIGHT = 720; //Height
	public static int getWidth() {
		return WIDTH;
	}
	public static int getHeight() {
		return HEIGHT;
	}
	private static int fps = 60; //FPS Cap
	public static int getFps() {
		return fps;
	}
	public static void setFps(int fps) {
		DisplayManager.fps = fps;
	}
	private static final String title = "TEngine - Testing Build 12"; //Title
	public static void createDisplay(){
		ContextAttribs attribs = new ContextAttribs(3,2)
				.withForwardCompatible(true)
				.withProfileCore(true); //All the attributes
		try {
			//Display settings
// Antialliasing 
			Display.setDisplayMode(new DisplayMode(WIDTH,HEIGHT)); // for custom resolution --NOT FULLSCREEN--
			//Display.setFullscreen(true);
			Display.create(new PixelFormat().withDepthBits(24), attribs); //Actually Creates display
			GL11.glEnable(GL13.GL_MULTISAMPLE);
			//Display.setVSyncEnabled(true);
			Display.setTitle(title);
try { //Sets the Icon
	Display.setIcon(new ByteBuffer[]{
	Maths.getIcon("icon16"),
	Maths.getIcon("icon32")});
} catch (IOException e) {
	e.printStackTrace();
}
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		GL11.glViewport(0, 0, WIDTH, HEIGHT); //Area to render, Here all of the window
	}
	public static void updateDisplay(){
		Display.sync(fps); //Limits the fps --FPS CAP--
		Display.update(); //Update the display
	}
	public static void closeDisplay(){
		Display.destroy(); //Close the display
	}
}
