package space_shooter;

import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;
import sage.scene.state.RenderState;
import sage.scene.state.TextureState;
import sage.terrain.AbstractHeightMap;
import sage.terrain.ImageBasedHeightMap;
import sage.terrain.TerrainBlock;
import sage.texture.Texture;
import sage.texture.TextureManager;

public class Terrain {
	private MyNetworkingClient mnc;
	
	public Terrain(MyNetworkingClient mnc){
		this.mnc = mnc;
		
		initTerrain();
	}
	
	private void initTerrain(){
		ImageBasedHeightMap myHeightMap = new ImageBasedHeightMap("height.jpg");
		
		TerrainBlock imageTerrain = createTerBlock(myHeightMap);
		
		TextureState groundState;
		Texture groundTexture = TextureManager.loadTexture2D("null.jpg");
		groundTexture.setApplyMode(sage.texture.Texture.ApplyMode.Replace);
		groundState = (TextureState)mnc.getRenderer().createRenderState(RenderState.RenderStateType.Texture);
		groundState.setTexture(groundTexture,0);
		groundState.setEnabled(true);
		
		imageTerrain.setRenderState(groundState);
		//addGameWorldObject(imageTerrain);
	}
	
	private TerrainBlock createTerBlock(AbstractHeightMap heightMap)
	{
		float heightScale = .005f;
		Vector3D terrainScale = new Vector3D(0.2,heightScale,0.2);
		
		int terrainSize = heightMap.getSize();
		float cornerHeight = heightMap.getTrueHeightAtPoint(0, 0)*heightScale;
		Point3D terrainOrigin = new Point3D(0,-cornerHeight,0);	//Will need to pass in object x and z coordinates
		
		//create a terrain
		String name = "Terrain: " + heightMap.getClass().getSimpleName();
		TerrainBlock tb = new TerrainBlock(name, terrainSize, terrainScale,
								heightMap.getHeightData(), terrainOrigin);
		
		return tb;
	}
}
