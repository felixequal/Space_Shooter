package a3;

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
	
	public MoveBackwardAction(ICamera c, SpaceShip ship)
	{
		this.camera = c;
		this.ship = ship;
	}

	@Override
	public void performAction(float speed, Event e)
	{
		if(ship.getSpeed() > -5){
			ship.setSpeed(ship.getSpeed() - 0.25f);
		}
	}
}
