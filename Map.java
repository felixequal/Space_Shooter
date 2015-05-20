package space_shooter;

import graphicslib3D.Vector3D;
import sage.model.loader.OBJLoader;
import sage.scene.TriMesh;
import sage.texture.Texture;
import sage.texture.Texture.ApplyMode;
import sage.texture.TextureManager;

public class Map extends GameObject
{
	private TriMesh wall1Obj = new TriMesh();
	private TriMesh wall2Obj = new TriMesh();
	private TriMesh wall3Obj = new TriMesh();
	private TriMesh wall4Obj = new TriMesh();
	private TriMesh cargoShipObj = new TriMesh();
	private OBJLoader loader = new OBJLoader();
	
	public Map()
	{
		wall1Obj = loader.loadModel("models/wall1.obj");
		wall1Obj.translate(6, 6, -8);
		loadWall1();
		
		wall2Obj = loader.loadModel("models/wall2.obj");
		wall2Obj.translate(-1, 6, -8);
		loadWall2();
		
		wall3Obj = loader.loadModel("models/wall3.obj");
		wall3Obj.translate(-2, 4, -8);
		loadWall3();
		
		wall4Obj = loader.loadModel("models/wall4.obj");
		wall4Obj.translate(-2, 6, -8);
		loadWall4();
		
		cargoShipObj = loader.loadModel("models/cargoShip.obj");
		cargoShipObj.translate(-40, 4, -60);
		loadCargoShip();
	}
	
	public TriMesh loadWall1(){
		return wall1Obj;
	}
	
	public TriMesh loadWall2(){
		return wall2Obj;
	}
	
	public TriMesh loadWall3(){
		return wall3Obj;
	}
	
	public TriMesh loadWall4(){
		return wall4Obj;
	}
	
	public TriMesh loadCargoShip(){
		return cargoShipObj;
	}
}
