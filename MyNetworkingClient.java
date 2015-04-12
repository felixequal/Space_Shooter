package a3;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import graphicslib3D.Matrix3D;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;
import sage.app.BaseGame;
import sage.display.IDisplaySystem;
import sage.input.IInputManager;
import sage.networking.IGameConnection.ProtocolType;
import sage.renderer.IRenderer;
import sage.scene.SceneNode;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import net.java.games.input.Component.Identifier.Key;

import java.io.*;
import java.util.*;

public class MyNetworkingClient extends BaseGame{
	private IDisplaySystem display;
	private IRenderer renderer;
	private IInputManager im;
	private SpaceShip ship;
	private Space skyBox;
	private String gpName, kbName;
	private FindComponents findControls;
	private SpaceStation station;
	MyClient thisClient;
	InetAddress remAddr;

	private ScriptEngineManager factory;
	private String worldScript = "scripts/SetupWorld.js";
	private String signatureScript = "scripts/SignatureScript.js";
	private ScriptEngine jsEngine;
	private SceneNode rootNode;
	private String serverAddress;
	private int serverPort;
	
	public MyNetworkingClient(String serverAddr, int serverPrt)
	{
		super();
		this.serverAddress = serverAddr;
		this.serverPort = serverPrt;
	}
	
	@Override
	public void initGame(){
		 try
			{
				remAddr = InetAddress.getByName(serverAddress);
			} catch (UnknownHostException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		try
			{ thisClient = new MyClient(InetAddress.getByName(serverAddress), serverPort, ProtocolType.TCP, this);
			}
			catch (UnknownHostException e) { e.printStackTrace();
			}
			catch (IOException e) { e.printStackTrace(); }
			if(thisClient != null) { thisClient.sendJoinMessage(); }
		
		
		im = getInputManager();
		
		display = getDisplaySystem();
		display.setTitle("Space Shooter");
		
		renderer = getDisplaySystem().getRenderer();
		factory = new ScriptEngineManager();
		
		//Get a list of the script engines
		List<ScriptEngineFactory> list = factory.getEngineFactories();
		jsEngine = factory.getEngineByName("js");
				
		System.out.println("Script Engine Factories Found: ");
		for(ScriptEngineFactory f : list)
			{
				System.out.println("Name =" + f.getEngineName() 
									+ " language =" + f.getLanguageName() + " extensions =" + f.getExtensions());
			}
		
		//Add all game objects including skybox
		
		initGameObjects();
//Run script
		setupWorld();		
		//Setup rest of the game objects and inpu
		initInput();
	//Run Signature Script
		signature();
	}
	
	public void signature()
	{
		this.runScript(jsEngine, signatureScript);
	}
	
	public void setupWorld()
	{	
		//Setup the axis with a javaScript
		this.runScript(jsEngine, worldScript);	//run the javaScript engine
						
		//Get scenegraph created by the script and add to the game
		rootNode = (SceneNode)jsEngine.get("rootNode");
		addGameWorldObject(rootNode);
	}
	
	
	public void initGameObjects(){
	//Add SkyBox w/ ZBuffer disabled
		skyBox = new Space(renderer);
		skyBox.scale(500.0f,500.0f,500.0f);
		
		addGameWorldObject(skyBox);
		
		//Now enabled the ZBuffer
		skyBox.getBuf().setDepthTestingEnabled(true);
		
		//Add other objects
		ship = new SpaceShip(renderer,display);

		//Add Space Station
		station = new SpaceStation();
		addGameWorldObject(station.loadObject());
	}

	@Override
	public void addGameWorldObject(SceneNode obj)
	{
		super.addGameWorldObject(obj);
	}
	private void runScript(ScriptEngine engine, String scriptFileName)
	{
		try
		{
			FileReader fileReader = new FileReader(scriptFileName);
			engine.eval(fileReader);
			fileReader.close();
		}
		catch(FileNotFoundException e1)
		{System.out.println(scriptFileName + "not found" + e1);}
		catch(IOException e2)
		{System.out.println("IO Problem With " + scriptFileName + e2);}
		catch(ScriptException e3)
		{System.out.println("ScriptException in " + scriptFileName + e3);}
		catch(NullPointerException e4)
		{System.out.println("Null ptr exception in " + scriptFileName + e4);}
	}

	
	public void initInput(){
		
		findControls = new FindComponents();	//Look for all controls connected to computer that can be used for game
		//findControls.listControllers();			//List out available controllers
		
		//Add Action Classes
		MoveForwardAction forward = new MoveForwardAction(thisClient, ship.getCamera(), ship);
		MoveBackwardAction backward = new MoveBackwardAction(ship.getCamera(), ship);
		PitchAction pitch = new PitchAction(ship.getCamera(), ship);
		PitchUpAction pitchUp = new PitchUpAction(ship.getCamera(), ship);
		PitchDownAction pitchDown = new PitchDownAction(ship.getCamera(), ship);
		YawRightAction yawRight = new YawRightAction(ship.getCamera(), ship);
		YawLeftAction yawLeft = new YawLeftAction(ship.getCamera(), ship);
		TiltRightAction tiltRight = new TiltRightAction(ship.getCamera(), ship);
		TiltLeftAction tiltLeft = new TiltLeftAction(ship.getCamera(), ship);
///////////////////////////////////////////////////////////////////////////////////////////////////////		
		kbName = im.getKeyboardName();
		im.associateAction(kbName, Key.W, forward,IInputManager.INPUT_ACTION_TYPE.ON_PRESS_AND_RELEASE);
		im.associateAction(kbName, Key.S, backward,IInputManager.INPUT_ACTION_TYPE.ON_PRESS_AND_RELEASE);
		im.associateAction(kbName, Key.DOWN, pitchUp,IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(kbName, Key.UP, pitchDown,IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(kbName, Key.RIGHT, yawRight,IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(kbName, Key.LEFT, yawLeft,IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(kbName, Key.D, tiltRight,IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(kbName, Key.A, tiltLeft,IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		
		//Check to see if gamepad is connected
		if(!(im.getFirstGamepadName() == null)){
			gpName = im.getFirstGamepadName();
			
			//Assign controls
			im.associateAction(gpName, net.java.games.input.Component.Identifier.Button._2, forward,
					IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
			im.associateAction(gpName, net.java.games.input.Component.Identifier.Button._3, backward,
					IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
			im.associateAction(gpName, net.java.games.input.Component.Identifier.Axis.Y, pitch,
					IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		}
	}
	
	@Override
	public void update(float elapsedTimeMS){
		//Update ship's movement according to speed
		ship.move();
		thisClient.processPackets();
		station.rotateStation();
		//Update SkyBox according to ship's position
		Point3D camLoc = ship.getCamera().getLocation();
		Matrix3D camTranslation = new Matrix3D();
		camTranslation.translate(camLoc.getX(),  camLoc.getY(), camLoc.getZ());
		skyBox.setLocalTranslation(camTranslation);
		//thisClient.sendByeMessage();
		//If Laser class ammoEmpty boolean is not true, then move any missile within the Laser's missile vector
		
		//Update everything in the world
		super.update(elapsedTimeMS);
	}

	public void setIsConnected(boolean b) {
		// TODO Auto-generated method stub
		
	}

	public Vector3D getPlayerPosition() {
		Vector3D playerPositionVector = new Vector3D(ship.getLocation());
		// TODO Auto-generated method stub
		return playerPositionVector;
	}
	
	@Override
	public void shutdown()
	{	
		super.shutdown();
		 thisClient.sendByeMessage();
		 try
			{
				thisClient.shutdown();
			} catch (IOException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		 try
			{
				Thread.sleep(4000);
			} catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 
	}
}
