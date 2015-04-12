package a3;

import graphicslib3D.Point3D;
import sage.renderer.IRenderer;
import sage.scene.SkyBox;
import sage.scene.state.RenderState.RenderStateType;
import sage.scene.state.TextureState;
import sage.scene.state.ZBufferState;
import sage.texture.Texture;
import sage.texture.TextureManager;

public class Space extends SkyBox{
	private Texture nT,sT,wT,eT,uT,dT;
	private IRenderer renderer;
	private Point3D location;
	private ZBufferState zbuff;
	
	public Space(IRenderer renderer){
		this.renderer = renderer;
		
		setupFaces();
		setZBuffer();
	}
	
	public void setupFaces(){
		nT = TextureManager.loadTexture2D("Textures/space.jpg");
		this.setTexture(Face.North, nT);
		TextureState nTState = (TextureState)renderer.createRenderState(RenderStateType.Texture);
		nTState.setEnabled(true);
		this.setRenderState(nTState);
		
		sT = TextureManager.loadTexture2D("Textures/space.jpg");
		this.setTexture(Face.South, sT);
		TextureState sTState = (TextureState)renderer.createRenderState(RenderStateType.Texture);
		sTState.setEnabled(true);
		this.setRenderState(sTState);
		
		wT = TextureManager.loadTexture2D("Textures/space.jpg");
		this.setTexture(Face.West, wT);
		TextureState wTState = (TextureState)renderer.createRenderState(RenderStateType.Texture);
		wTState.setEnabled(true);
		this.setRenderState(wTState);
		
		eT = TextureManager.loadTexture2D("Textures/space.jpg");
		this.setTexture(Face.East, eT);
		TextureState eTState = (TextureState)renderer.createRenderState(RenderStateType.Texture);
		eTState.setEnabled(true);
		this.setRenderState(eTState);
		
		uT = TextureManager.loadTexture2D("Textures/space.jpg");
		this.setTexture(Face.Up, uT);
		TextureState uTState = (TextureState)renderer.createRenderState(RenderStateType.Texture);
		uTState.setEnabled(true);
		this.setRenderState(uTState);
		
		dT = TextureManager.loadTexture2D("Textures/space.jpg");
		this.setTexture(Face.Down, dT);
		TextureState dTState = (TextureState)renderer.createRenderState(RenderStateType.Texture);
		dTState.setEnabled(true);
		this.setRenderState(dTState);
	}
	
	public void setZBuffer(){
		zbuff = (ZBufferState)renderer.createRenderState(RenderStateType.ZBuffer);
		zbuff.setWritable(false);
		zbuff.setDepthTestingEnabled(false);
		zbuff.setFunction(ZBufferState.TestFunction.LessThanOrEqualTo);
		zbuff.setEnabled(true);
	}
	
	public void setBuf(ZBufferState zbuff){
		this.zbuff = zbuff;
	}
	
	public ZBufferState getBuf(){
		return zbuff;
	}
	
	public void setLocation(Point3D location){
		this.location = location;
	}
	
	public Point3D getLocation(){
		return location;
	}
}
