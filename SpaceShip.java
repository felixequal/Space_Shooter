package space_shooter;

import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;

import java.awt.Color;
import java.util.Random;
import java.util.Vector;

import sage.camera.ICamera;
import sage.display.IDisplaySystem;
import sage.renderer.IRenderer;
import sage.scene.HUDImage;
import sage.scene.HUDString;
import sage.scene.TriMesh;
import sage.scene.shape.Sphere;

public class SpaceShip extends MoveableObject{

   Random rand;
	private Point3D location;
	private ICamera camera;
	private IRenderer renderer;
	private IDisplaySystem display;
	private HUDImage cockpit, screen;
	private float speed;
	private Laser laser;
	private Sphere laserObj;
	private Vector<Laser> laserStorage = new Vector<>();
	private boolean ammoEmpty;
	private Vector3D locVec;
	private Vector3D viewDir;
	private float lastUpdatedTime;
	
	private HUDString scoreHUD, healthHUD, ammoHUD;
	private int score, health, ammo;
	
	//Build constructor
	public SpaceShip(IRenderer renderer, IDisplaySystem display){
		score = 0;
		health = 100;
		ammo = 20;
		this.renderer = renderer;
		this.display = display;
		rand = new Random(50);
		location = new Point3D(rand.nextDouble(),4,rand.nextDouble());
		camera = display.getRenderer().getCamera();
		camera.setPerspectiveFrustum(45,1,0.01,1000);
		speed = 0.0f;
		setLocation(location);
		lastUpdatedTime = System.nanoTime();
		initHUD();
	}
	
	public void initHUD(){
		scoreHUD = new HUDString("Score: " + score);
		scoreHUD.setName("score");
		scoreHUD.setColor(Color.green);
		scoreHUD.setLocation(0.5, 0.4);
		scoreHUD.setRenderMode(sage.scene.SceneNode.RENDER_MODE.ORTHO);
		scoreHUD.setCullMode(sage.scene.SceneNode.CULL_MODE.NEVER);
		camera.addToHUD(scoreHUD);
		
		cockpit = new HUDImage("Textures/cockpit.png");
		cockpit.rotateImage(180.0f);
		cockpit.scale(2.0f, 0.8f, 1.0f);
		cockpit.setLocation(0,-0.6);
		cockpit.setRenderMode(sage.scene.SceneNode.RENDER_MODE.ORTHO);
		cockpit.setCullMode(sage.scene.SceneNode.CULL_MODE.NEVER);
		camera.addToHUD(cockpit);
		
		screen = new HUDImage("Textures/screen.jpg");
		screen.setLocation(0, -0.7);
		screen.scale(0.45f, 0.9f, 0.7f);
		screen.setRenderMode(sage.scene.SceneNode.RENDER_MODE.ORTHO);
		screen.setCullMode(sage.scene.SceneNode.CULL_MODE.NEVER);
		camera.addToHUD(screen);
	}
	
	////////////////////////////////FIRE WEAPONS/////////////////////////////////////////////////////////////////////
	//Check to see if ship has any ammo left. If so, keep moving all lasers in Vector<> and check for collisions
	public void fireLaser()
		{
		//float currentTime = System.nanoTime();
		//float elapsedTime = ((currentTime-lastUpdatedTime)/10000000.0f);
		//if (elapsedTime > 50.0f)
			//{
			//System.out.println("Creating laser:");
			//System.out.println("CurrentTime: " + currentTime);
			//System.out.println("elapsedTime: " + elapsedTime);
			//System.out.println("LUTime: " + lastUpdatedTime);
			//lastUpdatedTime = currentTime;
			laser = new Laser(this, 6);	//Laser needs a ship and speed in order to fire
			laserObj = laser.getLaser();
			//laserObj.setWorldTranslation(this.getWorldTranslation());
			laserStorage.add(laser);	//Add laser to vector array
			if(laserStorage == null){
			ammoEmpty = true;
			}else{
			ammoEmpty = false;
			}
			//}

		}
	
	public Sphere getLaserOBj(){
		return laserObj;
	}
	public Vector<Laser> getLaserStorage(){
		return laserStorage;
	}

	
	public void setCamera(ICamera camera){this.camera = camera;}
	public ICamera getCamera(){return camera;}
	
	public void setLocation(Point3D location) {
		this.location = location;
		camera.setLocation(location);
	}

	public Point3D getLocation() {return location;}
	
	public Vector3D getRotationVec()
	{
	viewDir = camera.getViewDirection().normalize();
	return viewDir;
	}
	
	public Vector3D getLocationVec()
		{
		Vector3D newLoc = new Vector3D(location.getX(), location.getY(), location.getZ());
		return newLoc;
		}
	
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
		viewDir = camera.getViewDirection().normalize();
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
