package a3;

import graphicslib3D.Vector3D;
import sage.model.loader.OBJLoader;
import sage.scene.TriMesh;
import sage.texture.Texture;
import sage.texture.Texture.ApplyMode;
import sage.texture.TextureManager;

public class SpaceStation extends GameObject
{
	private TriMesh stationObj = new TriMesh();
	private OBJLoader loader = new OBJLoader();
	private Texture stationT;
	
	public SpaceStation()
	{
		stationObj = loader.loadModel("models/station.obj");
		stationT = TextureManager.loadTexture2D("textures/station_texture.jpg");
		stationT.setApplyMode(ApplyMode.Replace);
		stationObj.setTexture(stationT);
		stationObj.rotate(45, new Vector3D(1,0,0));
		stationObj.translate(20, 0, -100);

		loadObject();
	}
	
	public void rotateStation(){
		stationObj.rotate(.005f, new Vector3D(0,1,0));
	}
	
	public TriMesh loadObject(){
		return stationObj;
	}
}
