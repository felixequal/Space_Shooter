package space_shooter;

import sage.input.action.*;
import net.java.games.input.Event;
import sage.camera.*;
import graphicslib3D.Vector3D;
import graphicslib3D.Matrix3D;

public class TiltLeftAction extends AbstractInputAction
{
	private ICamera camera;
	private SpaceShip ship;
	private MyClient client;
	
	public TiltLeftAction(MyClient thisClient, ICamera c, SpaceShip ship)
	{
		this.client = thisClient;
		this.camera = c;
		this.ship = ship;
	}

	@Override
	public void performAction(float time, Event e)
	{
		float rot = -0.15f;
		Matrix3D rotationAmt = new Matrix3D();
		Vector3D viewDir = camera.getViewDirection();
		Vector3D upDir = camera.getUpAxis();
		Vector3D rightDir = camera.getRightAxis();
		
		rotationAmt.rotate(rot, viewDir);
		
		upDir = upDir.mult(rotationAmt);
		rightDir = rightDir.mult(rotationAmt);
		
		camera.setRightAxis(rightDir.normalize());
		camera.setUpAxis(upDir.normalize());
		camera.setViewDirection(viewDir.normalize());
		
		ship.setCamera(camera);
		if (client != null)
			{
				client.processPackets();
				client.sendRotMessage(rot, viewDir);
			}
	}
}
