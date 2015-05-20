package space_shooter;

import sage.input.action.*;
import net.java.games.input.Event;
import sage.camera.*;
import graphicslib3D.Vector3D;
import graphicslib3D.Point3D;

public class MoveBackwardAction extends AbstractInputAction
{
	private ICamera camera;
	private SpaceShip ship;
	private float speed;
	private MyNetworkingClient mnc;
	
	public MoveBackwardAction(MyNetworkingClient mnc, ICamera c, SpaceShip ship)
	{
		this.mnc = mnc;
		this.camera = c;
		this.ship = ship;
	}

	@Override
	public void performAction(float speed, Event e)
	{
		if(ship.getSpeed() > -12){
			ship.setSpeed(ship.getSpeed() - 0.25f);
			mnc.playThrustersSound();
		}
	}
}
