package toolbox;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.opengl.ImageIOImageData;

import de.matthiasmann.twl.utils.PNGDecoder;
import entities.Camera;
import entities.Entity;
import renderEngine.Loader;
import textures.ModelTexture;

public class Maths {
	
	public static float barryCentric(Vector3f p1, Vector3f p2, Vector3f p3, Vector2f pos) {
		float det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z);
		float l1 = ((p2.z - p3.z) * (pos.x - p3.x) + (p3.x - p2.x) * (pos.y - p3.z)) / det;
		float l2 = ((p3.z - p1.z) * (pos.x - p3.x) + (p1.x - p3.x) * (pos.y - p3.z)) / det;
		float l3 = 1.0f - l1 - l2;
		return l1 * p1.y + l2 * p2.y + l3 * p3.y;
	}
	public static Matrix4f createTransformationMatrix(Vector2f translation, Vector2f scale) {
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		Matrix4f.translate(translation, matrix, matrix);
		Matrix4f.scale(new Vector3f(scale.x, scale.y, 1f), matrix, matrix);
		return matrix;
	}
	public static Matrix4f createTransformationMatrix(Vector3f translation, float rx, float ry,
			float rz, float scale){
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		Matrix4f.translate(translation, matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(rx), new Vector3f(1,0,0), matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(ry), new Vector3f(0,1,0), matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(rz), new Vector3f(0,0,1), matrix, matrix);
		Matrix4f.scale(new Vector3f(scale,scale,scale), matrix, matrix);
	return matrix;
	}
	
	public static boolean detectCollision(Entity entity, Camera camera){
		return false;	
	}
	
	public static int getDisplayMode(DisplayMode[] modes){
		 int resnum = 0;
			for (int i=0;i<modes.length;i++) {
					DisplayMode current = modes[i];
						String res;
							res = current.getWidth() + "x" + current.getHeight() + "x" +
									current.getBitsPerPixel() + " " + current.getFrequency() + "Hz";
							System.out.println(res);
							if(res.contains("1360x768")){
											resnum = i;
										}
			}
		return resnum;	
	}
	
	public static ByteBuffer getIcon(String str) throws IOException{
		InputStream in = new FileInputStream("res/icon/"+str+".png");
		try {
		   PNGDecoder decoder = new PNGDecoder(in);
		   ByteBuffer buf = ByteBuffer.allocateDirect(4*decoder.getWidth()*decoder.getHeight());
		   decoder.decode(buf, decoder.getWidth()*4, PNGDecoder.Format.RGBA);
		   buf.flip();
		   return buf;
		} finally {	
		   in.close();
		}
	}
	
	public static Matrix4f createViewMatrix(Camera camera){
		  Matrix4f viewMatrix = new Matrix4f();
		  viewMatrix.setIdentity();
		  Matrix4f.rotate((float) Math.toRadians(camera.getPitch()), new Vector3f(1,0,0), viewMatrix,
		    viewMatrix);
		  Matrix4f.rotate((float) Math.toRadians(camera.getYaw()), new Vector3f(0,1,0), viewMatrix,
		    viewMatrix);
		  Vector3f cameraPos = camera.getPosition();
		  Vector3f negativeCameraPos = new Vector3f(-cameraPos.x,-cameraPos.y,-cameraPos.z);
		  Matrix4f.translate(negativeCameraPos, viewMatrix, viewMatrix);
		  return viewMatrix;
	}
}