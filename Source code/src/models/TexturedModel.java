package models;

import textures.ModelTexture;

public class TexturedModel {

	private RawModel rawmodel;
	private ModelTexture texture;
	
	public TexturedModel(RawModel model, ModelTexture texture){
		this.rawmodel = model;
		this.texture = texture;
	}

	public RawModel getRawModel() {
		return rawmodel;
	}

	public ModelTexture getTexture() {
		return texture;
	}
	
}
