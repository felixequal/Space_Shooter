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

public class GhostAvatar extends TriMesh
	{

	private Pyramid testPyramid;
	private Matrix3D positionMat;
	private Vector3D positionVec;
	private Vector3D rotationVec;
	private UUID ghostID;

	private TriMesh shipObj = new TriMesh();
	private OBJLoader loader = new OBJLoader();
	private Texture shipT;

	public GhostAvatar(UUID id, Vector3D initPosition)
		{

		ghostID = id;
		System.out.println("ghost avatar created: ID: " + ghostID);
		//positionVec = initPosition;
		moveAvatar(initPosition);
		shipObj = loader.loadModel("models/Spaceship.obj");
		shipObj.scale(0.1f, 0.1f, 0.1f);
		shipT = TextureManager.loadTexture2D("textures/playerUV.jpg");
		shipT.setApplyMode(ApplyMode.Replace);
		shipObj.setTexture(shipT);

		getAvatar();
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
		positionVec = newPos;
		Matrix3D mat = new Matrix3D();
		mat.translate(newPos.getX(), newPos.getY(), newPos.getZ());
		shipObj.setLocalTranslation(mat);
		}

	public void rotAvatar(Vector3D newRot)
		{
		System.out.println("in Ghost Avatar: Rotating avatar");
		System.out.println("rot vector: " + newRot.toString());
		Matrix3D mat = shipObj.getLocalRotation();
		newRot.mult(mat);
		mat.rotate(newRot.getX(), newRot.getY(), newRot.getZ());
		shipObj.setLocalRotation(mat);
		//shipObj.setLocalRotation(mat);
		}

	}
