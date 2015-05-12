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
	
	public FireLaserAction(MyNetworkingClient mnc, SpaceShip spaceShip){
		this.mnc = mnc;
		this.spaceShip = spaceShip;
	}
	
	public void performAction(float time, Event e) {
		System.out.println("Firing Laser");
		
		spaceShip.fireLaser();
		laserObj = spaceShip.getLaserOBj();
		laserObj.setLocalTranslation(mnc.getCamLocation());

		mnc.addGameWorldObject(laserObj);
	}
}
