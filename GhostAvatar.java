package space_shooter;

import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;

import java.util.UUID;

import sage.model.loader.OBJLoader;
import sage.scene.TriMesh;
import sage.scene.shape.Pyramid;
import sage.scene.shape.Sphere;
import sage.texture.Texture;
import sage.texture.TextureManager;
import sage.texture.Texture.ApplyMode;

public class GhostAvatar extends TriMesh {

	private Pyramid testPyramid;
	private Matrix3D positionMat;
	private Vector3D positionVec;
	private UUID ghostID;
	
	private TriMesh shipObj = new TriMesh();
	private OBJLoader loader = new OBJLoader();
	private Texture shipT;
	
	public GhostAvatar(UUID id, Vector3D initPosition)
	{
		ghostID = id;
		positionVec = initPosition;
		testPyramid = new Pyramid();
		
		shipObj = loader.loadModel("models/Spaceship.obj");
		shipT = TextureManager.loadTexture2D("textures/playerUV.jpg");
		shipT.setApplyMode(ApplyMode.Replace);
		shipObj.setTexture(shipT);
		shipObj.scale(.25f, .25f, .25f);
		//Sphere testSphere = new Sphere();
		//testSphere.
		//Matrix3D positionMat = testPyramid.getLocalTranslation();
		testPyramid.translate((float)positionVec.getX(), (float)positionVec.getY(), (float)positionVec.getZ());
		//testPyramid.setColor(Color.BLUE);
		//testPyramid.setLocalTranslation(positionMat);
		//testPyramid.translate(initPosition.getX(), initPosition.getY(), initPosition.getZ());
		
	}
	
	public Vector3D getPositionVec()
		{
		return positionVec;
		}

	public void setPositionVec(Vector3D positionVec)
		{
		this.positionVec = positionVec;
		moveAvatar(positionVec);
		}

	public UUID getGhostID()
		{
		return ghostID;
		}

	public void setGhostID(UUID ghostID)
		{
		this.ghostID = ghostID;
		}

	public TriMesh getAvatar()
	{
		return shipObj;
	}
	
	public void moveAvatar(Vector3D newPos)
	{
		
		//positionVec = newPos;
		//positionVec.mult(positionMat);
		//testPyramid.getWorldTranslation().setCol(3,  newPos);
		//testPyramid.setWorldTranslation(positionMat);
		testPyramid.translate((float)newPos.getX(), (float)newPos.getY(), (float)newPos.getY());
	}
	
}
