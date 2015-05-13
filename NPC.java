package space_shooter;

import graphicslib3D.Point3D;

public class NPC {
	Point3D location;
	public double getX(){return location.getX();}
	public double getY(){return location.getY();}
	public double getZ(){return location.getZ();}
	
	public void updateLocation(){
		//Move the GhostNPC here
	}
}
