package space_shooter;

import sage.input.action.*;
import net.java.games.input.Event;
import sage.camera.*;
import graphicslib3D.Vector3D;
import graphicslib3D.Point3D;

public class StopMovingAction extends AbstractInputAction
{
	private ICamera camera;
	private SpaceShip ship;
	private float speed;
	private MyClient game;
	private MyNetworkingClient mnc;
	
	public StopMovingAction(MyNetworkingClient mnc, MyClient game, ICamera c, SpaceShip ship)
	{
		this.mnc = mnc;
		this.game = game;
		this.camera = c;
		this.ship = ship;
	}

	@Override
	public void performAction(float speed, Event e)
	{
		if(ship.getSpeed() < 5){		//Set a speed limit for aircraft
			ship.setSpeed(ship.getSpeed()*0);
			mnc.stopThrustersSound();
		}
		Point3D loc = ship.getLocation();
		Vector3D newLoc = new Vector3D(loc.getX(), loc.getY(), loc.getZ());
		//game.sendMoveMessage(newLoc);
	}

}
