package space_shooter;

import java.awt.Color;

import sage.scene.shape.Sphere;
import graphicslib3D.Matrix3D;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;

public class Laser extends MoveableObject
	{
	private Sphere laser;
	private float speed;
	private Point3D location;
	private SpaceShip ship;

	public Laser(SpaceShip ship, float speed)
		{
		laser = new Sphere();
		laser.setRenderMode(RENDER_MODE.OPAQUE);
		this.ship = ship;
		this.speed = speed;
		laser.scale(0.1f, 0.1f, 0.1f);
		laser.setColor(Color.RED);
		laser.translate((float) ship.getCamera().getLocation().getX(), (float) ship.getCamera().getLocation().getY(), (float) ship.getCamera().getLocation().getZ());
		}


	public Sphere getLaser()
		{
		return laser;
		}

	@Override
	public void setSpeed(float speed)
		{
		this.speed = speed;
		}

	@Override
	public float getSpeed()
		{
		return speed;
		}

	public void setLocation(Point3D location)
		{
		this.location = location;
		}

	public Point3D getLocation()
		{
		return location;
		}

	}
