package a3;

import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;

import java.util.Vector;

import sage.camera.ICamera;
import sage.display.IDisplaySystem;
import sage.renderer.IRenderer;
import sage.scene.HUDImage;

public class SpaceShip extends MoveableObject{
	private Point3D location = new Point3D(20,2,20);
	private ICamera camera;
	private IRenderer renderer;
	private IDisplaySystem display;
	private HUDImage cockpit;
	private float speed;
	private Laser laser;
	private Vector<Laser> laserStorage = new Vector<>();
	private boolean ammoEmpty;
	
	//Build constructor
	public SpaceShip(IRenderer renderer, IDisplaySystem display){
		this.renderer = renderer;
		this.display = display;
		
		camera = display.getRenderer().getCamera();
		camera.setPerspectiveFrustum(45,1,0.01,1000);
		speed = 0.0f;
		setLocation(location);
		initHUD();
	}
	
	public void initHUD(){
		cockpit = new HUDImage("Textures/cockpit.png");
		cockpit.rotateImage(180.0f);
		cockpit.scale(2.0f, 0.8f, 1.0f);
		cockpit.setLocation(0,-0.6);
		cockpit.setRenderMode(sage.scene.SceneNode.RENDER_MODE.ORTHO);
		cockpit.setCullMode(sage.scene.SceneNode.CULL_MODE.NEVER);
		this.addToHUD(cockpit);
	}
	
	////////////////////////////////FIRE WEAPONS/////////////////////////////////////////////////////////////////////
	//Check to see if ship has any ammo left. If so, keep moving all lasers in Vector<> and check for collisions
	public void fireLaser(){
		laser = new Laser(this, 6);	//Laser needs a ship and speed in order to fire
		laserStorage.add(laser);	//Add laser to vector array
		
		if(laserStorage == null){
			ammoEmpty = true;
		}else{
			ammoEmpty = false;
		}
	}
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public void setCamera(ICamera camera){this.camera = camera;}
	public ICamera getCamera(){return camera;}
	
	public void setLocation(Point3D location) {
		this.location = location;
		
		camera.setLocation(location);
	}

	public Point3D getLocation() {return location;}
	
	@Override
	public void setSpeed(float speed) {
		this.speed = speed;
	}

	@Override
	public float getSpeed() {
		return speed;
	}

	public void addToHUD(HUDImage cockPit){
		camera.addToHUD(cockPit);
	}
	
	public void move(){
		Vector3D viewDir = camera.getViewDirection().normalize();
		Vector3D curLocVector = new Vector3D(this.getLocation());
		Vector3D newLocVector = new Vector3D();
		
		newLocVector = curLocVector.add(viewDir.mult(0.005 * speed));
		 
		double newX = newLocVector.getX();
		double newY = newLocVector.getY();
		double newZ = newLocVector.getZ();
		Point3D newLoc = new Point3D(newX,newY,newZ);
		camera.setLocation(newLoc);	//Update camera coordinates
		this.setLocation(newLoc);	//Update ship coordinates
	}
	
	public void setAmmoBoolean(boolean ammoEmpty){
		this.ammoEmpty = ammoEmpty;
	}
	
	public boolean getAmmoBoolean(){return ammoEmpty;}
	
	public void setAmmoStorage(Vector<Laser> laserStorage){
		this.laserStorage = laserStorage;
	}
	
	public Vector<Laser> getAmmoStorage(){
		return laserStorage;
	}
}
