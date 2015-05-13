package space_shooter;

import sage.model.loader.OBJLoader;
import sage.scene.TriMesh;

public class CargoShip extends GameObject{
	private TriMesh cargoShipOBJ = new TriMesh();
	private OBJLoader loader = new OBJLoader();
	
	public CargoShip()
	{
		cargoShipOBJ = loader.loadModel("models/cargoShip");
		loadShip();
	}
	
	public TriMesh loadShip()
	{
		return cargoShipOBJ;
	}
}
