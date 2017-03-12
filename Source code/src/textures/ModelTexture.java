package textures;

public class ModelTexture {

	private int textureID;
	
	public int getNormalMap() {
		return normalMap;
	}
	public void setNormalMap(int normalMap) {
		this.normalMap = normalMap;
	}
	private int normalMap;
	
	private float shineDamper = 1;
	private float reflectivity = 0;
	
	private boolean hasTransaparency = false;
	private boolean useFakeLightning = false;
	
	private int numberOfRows = 1;
	
	
	public int getNumberOfRows() {
		return numberOfRows;
	}
	public void setNumberOfRows(int numberOfRows) {
		this.numberOfRows = numberOfRows;
	}
	public boolean isUseFakeLightning() {
		return useFakeLightning;
	}
	public void setUseFakeLightning(boolean useFakeLightning) {
		this.useFakeLightning = useFakeLightning;
	}
	public float getShineDamper() {
		return shineDamper;
	}
	public void setShineDamper(float shineDamper) {
		this.shineDamper = shineDamper;
	}
	public boolean isHasTransaparency() {
		return hasTransaparency;
	}
	public void setHasTransaparency(boolean hasTransaparency) {
		this.hasTransaparency = hasTransaparency;
	}
	public float getReflectivity() {
		return reflectivity;
	}
	public void setReflectivity(float reflectivity) {
		this.reflectivity = reflectivity;
	}
	public ModelTexture(int id){
		this.textureID = id;
	}
	public int getID(){
		return this.textureID;
	}
}
