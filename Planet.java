package space_shooter;

import graphicslib3D.Vector3D;
import sage.model.loader.OBJLoader;
import sage.scene.Group;
import sage.scene.TriMesh;
import sage.texture.Texture;
import sage.texture.Texture.ApplyMode;
import sage.texture.TextureManager;

public class Planet extends GameObject
{
	private TriMesh oceanObj = new TriMesh();
	private TriMesh landObj = new TriMesh();
	private TriMesh grassObj = new TriMesh();
	private OBJLoader loader = new OBJLoader();
	private Group rootNode = new Group();
	
	public Planet()
	{
		oceanObj = loader.loadModel("models/ocean.obj");
		landObj = loader.loadModel("models/landscape.obj");
		grassObj = loader.loadModel("models/grass.obj");
		
		oceanObj.scale(.1f, .1f, .1f);
		landObj.scale(.1f, .1f, .1f);
		grassObj.scale(.1f, .1f, .1f);
		
		rootNode.addChild(oceanObj);
		rootNode.addChild(landObj);
		rootNode.addChild(grassObj);
		
		rootNode.translate(0,0,-500);
		rootNode.setCullMode(cullMode);

		loadObject();
	}
	
	public void rotatePlanet(){
		rootNode.rotate(.5f, new Vector3D(0,1,0));
	}
	
	public Group loadObject(){
		return rootNode;
	}
}
