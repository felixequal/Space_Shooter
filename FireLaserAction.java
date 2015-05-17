package space_shooter;

import java.util.Vector;

import net.java.games.input.Event;
import sage.input.action.AbstractInputAction;
import sage.scene.shape.Sphere;

public class FireLaserAction extends AbstractInputAction 
{
	private MyNetworkingClient mnc;
	private SpaceShip spaceShip;
	private Sphere laserObj;
	private Vector<Laser> laserStorage;
	private float lastUpdatedLasTime;
	
	public FireLaserAction(MyNetworkingClient mnc, SpaceShip spaceShip){
		this.mnc = mnc;
		this.spaceShip = spaceShip;
		lastUpdatedLasTime = System.nanoTime();
	}
	
	public void performAction(float time, Event e) {
		System.out.println("Firing Laser");
		float currentLasTime = System.nanoTime();
		float elapsedLasTime = ((currentLasTime-lastUpdatedLasTime)/10000000.0f);
		if (elapsedLasTime > 20.0f)
			{
			lastUpdatedLasTime = currentLasTime;
			
		spaceShip.fireLaser();
		laserObj = spaceShip.getLaserOBj();
		laserObj.translate((float)spaceShip.getLocation().getX(), (float)spaceShip.getLocation().getY(), (float)spaceShip.getLocation().getZ());

		mnc.addGameWorldObject(laserObj);
		mnc.addPhysicsObject(laserObj);
			}
	}
}
