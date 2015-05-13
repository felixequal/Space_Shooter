package space_shooter;

import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;

public class GhostNPC extends GameObject{
	CargoShip body;
	
	public GhostNPC(Vector3D position)
	{
		this.body = new CargoShip();
		setPosition(position);
	}
	
	public void setPosition(Vector3D position)
	{
		Matrix3D trans = new Matrix3D();
		trans.translate(position.getX(), position.getY(), position.getZ());
		body.setLocalTranslation(trans);
	}
}
