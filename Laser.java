package a3;

import java.awt.Color;

import sage.scene.shape.Rectangle;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;

public class Laser extends MoveableObject{
	private Rectangle laser;
	private float speed;
	private Point3D location;
	private SpaceShip ship;
	
	public Laser(SpaceShip ship, float speed){
		this.ship = ship;
		this.speed = speed;
		
		this.setLocation(ship.getLocation());
		laser.setColor(Color.RED);
		move();
	}
	
	public void move(){
		Vector3D viewDir = ship.getCamera().getViewDirection().normalize();
		Vector3D curLocVector = new Vector3D(this.getLocation());
		Vector3D newLocVector = new Vector3D();
		
		newLocVector = curLocVector.add(viewDir.mult(0.05 * speed));
		 
		double newX = newLocVector.getX();
		double newY = newLocVector.getY();
		double newZ = newLocVector.getZ();
		Point3D newLoc = new Point3D(newX,newY,newZ);
		this.setLocation(newLoc);
	}
	
	@Override
	public void setSpeed(float speed) {
		this.speed = speed;
	}

	@Override
	public float getSpeed() {
		return speed;
	}

	public void setLocation(Point3D location) {
		this.location = location;
	}

	public Point3D getLocation() {
		return location;
	}

}
