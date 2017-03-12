package models;


public class RawModel {
	private int vaoID;
	private int vertexCount;
	
	public RawModel(int vaoID, int vertexCount){
		this.vaoID = vaoID;
		this.vertexCount = vertexCount;
	}
	
	public int getVaoID() { //Getter for vaoid
		return vaoID;
	}
	public int getVertexCount() { //Getter for vertexcount
		return vertexCount;
	}
	
}
