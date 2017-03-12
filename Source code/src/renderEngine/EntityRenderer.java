package renderEngine;

import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import entities.Entity;
import models.TexturedModel;
import models.RawModel;
import shaders.StaticShader;
import textures.ModelTexture;
import toolbox.Maths;

public class EntityRenderer {
	
	private StaticShader shader;
	
	public EntityRenderer(StaticShader shader,Matrix4f projectionMatrix){
		this.shader = shader;
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
	}
	
	

		public void render(Map<TexturedModel,List<Entity>> entities){
			for(TexturedModel model:entities.keySet()){
				prepareTexturedModel(model);
				List<Entity> batch = entities.get(model);
				for(Entity Entity:batch){
					prepareInstance(Entity);
					GL11.glDrawElements(GL11.GL_TRIANGLES,
							model.getRawModel().getVertexCount(),GL11.GL_UNSIGNED_INT,0);

				}
				unbindTexturedModel();
			}
		}
		
		public void render(Entity entity){
				prepareTexturedModel(entity.getModel());
				prepareInstance(entity);
					GL11.glDrawElements(GL11.GL_TRIANGLES,
							entity.getModel().getRawModel().getVertexCount(),GL11.GL_UNSIGNED_INT,0);
				unbindTexturedModel();
			}
		
		public void prepareTexturedModel(TexturedModel model){
			RawModel rawmodel = model.getRawModel();
			GL30.glBindVertexArray(rawmodel.getVaoID());
			GL20.glEnableVertexAttribArray(0);
			GL20.glEnableVertexAttribArray(1);
			GL20.glEnableVertexAttribArray(2);
			ModelTexture texture = model.getTexture();
			shader.loadNumberOfRows(texture.getNumberOfRows());
			if(texture.isHasTransaparency()){
				MasterRenderer.disableCulling();
			}
			shader.loadFakeLightningVariable(texture.isUseFakeLightning());
			shader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getID());
		}
		
		public void unbindTexturedModel(){
			MasterRenderer.enableCulling();
			GL20.glDisableVertexAttribArray(0);
			GL20.glDisableVertexAttribArray(1);
			GL20.glDisableVertexAttribArray(2);
			GL30.glBindVertexArray(0);
		}
		
		private void prepareInstance(Entity Entity){
			Matrix4f transformationMatrix = Maths.createTransformationMatrix(Entity.getPosition(), Entity.getRotX()
					, Entity.getRotY(), Entity.getRotZ(), Entity.getScale());
			shader.loadTransformationMatrix(transformationMatrix);
			shader.loadOffset(Entity.getTextureXOffset(),Entity.getTextureYOffset());
		}
		
		
}
