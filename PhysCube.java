package space_shooter;

import sage.scene.shape.Cube;

public class PhysCube extends Cube
	{
private float sizeX;
private float sizeY;
private float sizeZ;
private float[] size;
private Cube cube;

	public PhysCube(float initX, float initY, float initZ, Cube c)
		{
		cube = c;
		sizeX = initX;
		sizeY = initY;
		sizeZ = initZ;
		}

	public float[] getSize()
	{
		size = new float[3];
		size[0] = sizeX;
		size[1] =  sizeY;
		size[2] = sizeZ;
		return size;
	}
	public Cube getCube()
		{
		return cube;
		}

	public void setCube(Cube cube)
		{
		this.cube = cube;
		}

	public float getX()
		{
		return sizeX;
		}

	public void setX(float x)
		{
		this.sizeX = x;
		}

	public float getY()
		{
		return sizeY;
		}

	public void setY(float y)
		{
		this.sizeY = y;
		}

	public float getZ()
		{
		return sizeZ;
		}

	public void setZ(float z)
		{
		this.sizeZ = z;
		}

	}
