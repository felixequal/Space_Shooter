package space_shooter;
/* This is the main game client. It inits a physics handler and a network hander (CLASS:MyClient) which is attached and runs the network part.
 * If there is no server, it ignores network functionality and just runs standalone.
 * 
 */
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import graphicslib3D.Matrix3D;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;
import sage.app.BaseGame;
import sage.audio.AudioManagerFactory;
import sage.audio.AudioResource;
import sage.audio.AudioResourceType;
import sage.audio.IAudioManager;
import sage.audio.Sound;
import sage.audio.SoundType;
import sage.display.IDisplaySystem;
import sage.input.IInputManager;
import sage.networking.IGameConnection.ProtocolType;
import sage.physics.IPhysicsEngine;
import sage.physics.IPhysicsObject;
import sage.physics.PhysicsEngineFactory;
import sage.renderer.IRenderer;
import sage.scene.Group;
import sage.scene.SceneNode;
import sage.scene.TriMesh;
import sage.scene.shape.Cube;
import sage.scene.shape.Sphere;
import sage.terrain.TerrainBlock;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import net.java.games.input.Component.Identifier.Key;

import java.io.*;
import java.util.*;

public class MyNetworkingClient extends BaseGame
	{
	private IDisplaySystem display;
	private IRenderer renderer;
	private IInputManager im;
	private SpaceShip ship;
	private Space skyBox;
	private String gpName, kbName;
	private FindComponents findControls;
	private SpaceStation station;
	private Map map;
	private Planet planet;
	private Cube cube;
	private Group planetGrp;
	private Terrain terrain;
	private TerrainBlock tBlock;
	private double rangeMin = -50;
	private double rangeMax = 50;
	private ArrayList<PhysCube> physCubeList;
	PhysCube physCube;
	MyClient thisClient;
	InetAddress remAddr;
	private float currentTime;
	private float lastUpdatedTime;

	private ScriptEngineManager factory;
	private String worldScript = "scripts/SetupWorld.js";
	private String signatureScript = "scripts/SignatureScript.js";
	private ScriptEngine jsEngine;
	private SceneNode rootNode;
	private String serverAddress;
	private int serverPort;
	private IPhysicsEngine physicsEngine;

	private IPhysicsObject shipBall, cubeP, laserP;
	private Matrix3D camTranslation;
	
	IAudioManager audioMgr;
	Sound thrusterSound;
	AudioResource resource1;


	public MyNetworkingClient(String serverAddr, int serverPrt)
		{
		super();
		this.serverAddress = serverAddr;
		this.serverPort = serverPrt;
		System.out.println("Server Addr: " + serverAddress);		}

	@Override
	public void initGame()
		{
		try
			{
				remAddr = InetAddress.getByName(serverAddress);
			} catch (UnknownHostException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		try
			{
				thisClient = new MyClient(InetAddress.getByName(serverAddress),
						serverPort, ProtocolType.TCP, this);
			} catch (UnknownHostException e)
			{
				e.printStackTrace();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		if (thisClient != null)
			{
				thisClient.sendJoinMessage();
			}

		im = getInputManager();

		display = getDisplaySystem();
		display.setTitle("Space Shooter");

		renderer = getDisplaySystem().getRenderer();
		factory = new ScriptEngineManager();

		// Get a list of the script engines
		List<ScriptEngineFactory> list = factory.getEngineFactories();
		jsEngine = factory.getEngineByName("js");

		System.out.println("Script Engine Factories Found: ");
		for (ScriptEngineFactory f : list)
			{
				System.out.println("Name =" + f.getEngineName() + " language ="
						+ f.getLanguageName() + " extensions ="
						+ f.getExtensions());
			}

		//Add all game objects including skybox

		initGameObjects();
		// Run script
		setupWorld();
		// Setup rest of the game objects and inpu
		initInput();
		
		initAudio();
		
		initPhysicsSystem();
		createSagePhysicsWorld();
		// Run Signature Script
		signature();
		currentTime = System.nanoTime();
		//elapsedTime = System.currentTimeMillis();
		}

	public void signature()
		{
		this.runScript(jsEngine, signatureScript);
		}

	public void setupWorld()
		{
		// Setup the axis with a javaScript
		this.runScript(jsEngine, worldScript); // run the javaScript engine

		// Get scenegraph created by the script and add to the game
		rootNode = (SceneNode) jsEngine.get("rootNode");
		addGameWorldObject(rootNode);
		}

	protected void initPhysicsSystem()
	{
		String engine = "sage.physics.ODE4J.ODE4JPhysicsEngine";
		physicsEngine = PhysicsEngineFactory.createPhysicsEngine(engine);
		physicsEngine.initSystem();
		float[] gravity = {0,0,0};
		physicsEngine.setGravity(gravity);
	}
	
	private void createSagePhysicsWorld()
	{/*
		float massCube =  1000.0f;
		for (PhysCube pcube: physCubeList){
		cubeP = physicsEngine.addBoxObject(physicsEngine.nextUID(),massCube,pcube.getCube().getWorldTransform().getValues(), pcube.getSize());
		cubeP.setBounciness(1.0f);
		pcube.getCube().setPhysicsObject(cubeP);
		}
		float massShip = 5.0f;
		shipBall =  physicsEngine.addSphereObject(physicsEngine.nextUID(),massShip,ship.getWorldTransform().getValues(), 1.0f);
		shipBall.setBounciness(1.0f);
		ship.setPhysicsObject(shipBall); 
		*/
	}
	public void initGameObjects()
		{
		physCubeList = new ArrayList<PhysCube>();
		// Add SkyBox w/ ZBuffer disabled
		skyBox = new Space(renderer);
		skyBox.scale(500.0f, 500.0f, 500.0f);
		addGameWorldObject(skyBox);
		// Now enabled the ZBuffer
		skyBox.getBuf().setDepthTestingEnabled(true);
		/*for (int p = 0; p < 15; p++)
			{
				Random r = new Random();
				double a = rangeMin + (rangeMax - rangeMin) * r.nextDouble();
				double b = rangeMin + (rangeMax - rangeMin) * r.nextDouble();
				double c = rangeMin + (rangeMax - rangeMin) * r.nextDouble();
				float x = (float) (a / 3) * 1.5f;
				float y = (float) (b / 4) * 1.5f;
				float z = (float) (c / 3) * 1.5f;
				Matrix3D localTranslation = new Matrix3D();
				localTranslation.translate(a, b, c);
				cube = new Cube();
				cube.scale(x ,y ,z);
				cube.setLocalTranslation(localTranslation);
				cube.updateWorldBound();
				physCube = new PhysCube(x,y,z,cube);
				physCubeList.add(physCube);
				addGameWorldObject(cube);
			}
			*/
		// planet = new Planet();
		// planetGrp = planet.loadObject();
		// planetGrp.translate(0, 0, 0);
		// addGameWorldObject(planetGrp);
=======
			}*/
		 planet = new Planet();
		 planetGrp = planet.loadObject();
		 planetGrp.translate(0, 0, 0);
		 addGameWorldObject(planetGrp);
>>>>>>> Map
		// Add other objects
		ship = new SpaceShip(renderer, display);

		// Add Space Station
		//station = new SpaceStation();
		//addGameWorldObject(station.loadObject());

		map = new Map();
		addGameWorldObject(map.loadWall1());
		addGameWorldObject(map.loadWall2());
		addGameWorldObject(map.loadWall3());
		addGameWorldObject(map.loadWall4());
		addGameWorldObject(map.loadCargoShip());
		
		// Load terrain
		// terrain = new Terrain(this);
		//tBlock = terrain.getTerrain();
		// addGameWorldObject(tBlock);
		}

	public void addGameWorldObject(SceneNode obj)
		{
		super.addGameWorldObject(obj);
		}

	public boolean removeGameWorldObject(SceneNode obj)
		{
		boolean b = super.removeGameWorldObject(obj);
		return b;
		}

	private void runScript(ScriptEngine engine, String scriptFileName)
		{
		try
			{
				FileReader fileReader = new FileReader(scriptFileName);
				engine.eval(fileReader);
				fileReader.close();
			} catch (FileNotFoundException e1)
			{
				System.out.println(scriptFileName + "not found" + e1);
			} catch (IOException e2)
			{
				System.out.println("IO Problem With " + scriptFileName + e2);
			} catch (ScriptException e3)
			{
				System.out.println("ScriptException in " + scriptFileName + e3);
			} catch (NullPointerException e4)
			{
				System.out.println("Null ptr exception in " + scriptFileName
						+ e4);
			}
		}

	public void initInput()
	{
		findControls = new FindComponents(); // Look for all controls connected											// to computer that can be used
												// for game
		// findControls.listControllers(); //List out available controllers

		// Add Action Classes
		MoveForwardAction forward = new MoveForwardAction(this, thisClient,
				ship.getCamera(), ship);
		MoveBackwardAction backward = new MoveBackwardAction(this, ship.getCamera(),
				ship);
		StopMovingAction stop = new StopMovingAction(this, thisClient,
				ship.getCamera(), ship);
		PitchAction pitch = new PitchAction(ship.getCamera(), ship);
		PitchUpAction pitchUp = new PitchUpAction(thisClient, ship.getCamera(), ship);
		PitchDownAction pitchDown = new PitchDownAction(ship.getCamera(), ship);
		YawRightAction yawRight = new YawRightAction(ship.getCamera(), ship);
		YawLeftAction yawLeft = new YawLeftAction(ship.getCamera(), ship);
		TiltRightAction tiltRight = new TiltRightAction(ship.getCamera(), ship);
		TiltLeftAction tiltLeft = new TiltLeftAction(ship.getCamera(), ship);
		
		FireLaserAction fireLaser = new FireLaserAction(this, ship);
		///////////////////////////////////////////////////////////////////////////////////////////////////////
		kbName = im.getKeyboardName();
		im.associateAction(kbName, Key.W, forward,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(kbName, Key.X, stop,
				IInputManager.INPUT_ACTION_TYPE.ON_PRESS_AND_RELEASE);
		im.associateAction(kbName, Key.S, backward,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(kbName, Key.DOWN, pitchUp,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(kbName, Key.UP, pitchDown,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(kbName, Key.RIGHT, yawRight,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(kbName, Key.LEFT, yawLeft,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(kbName, Key.D, tiltRight,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(kbName, Key.A, tiltLeft,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		
		im.associateAction(kbName, Key.SPACE, fireLaser,
				IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
			
		// Check to see if gamepad is connected
			if (!(im.getFirstGamepadName() == null))
			{
				gpName = im.getFirstGamepadName();

				// Assign controls
				im.associateAction(gpName,
						net.java.games.input.Component.Identifier.Button._2,
						forward,
						IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
				im.associateAction(gpName,
						net.java.games.input.Component.Identifier.Button._3,
						backward,
						IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
				im.associateAction(gpName,
						net.java.games.input.Component.Identifier.Axis.Y,
						pitch,
						IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
			}
		}
	
	public void initAudio(){
		audioMgr = AudioManagerFactory.createAudioManager("sage.audio.joal.JOALAudioManager");
		
		if(!audioMgr.initialize())
		{
			System.out.println("Audio Manager failed to initialize");
			return;
		}
		resource1 = audioMgr.createAudioResource("sounds/thrusters.wav", AudioResourceType.AUDIO_SAMPLE);
		
		thrusterSound = new Sound(resource1, SoundType.SOUND_EFFECT, 100, true);
		thrusterSound.initialize(audioMgr);
		thrusterSound.setMaxDistance(50.0f);
		thrusterSound.setMinDistance(3.0f);
		thrusterSound.setRollOff(5.0f);
		thrusterSound.setLocation(new Point3D(ship.getWorldTranslation().getCol(3)));
	}
	
	public void playThrustersSound(){
		thrusterSound.play();
	}
	
	public void stopThrustersSound(){
		thrusterSound.stop();
	}

	public Matrix3D getCamLocation(){
		return camTranslation;
	}
	
	@Override
	public void update(float elapsedTimeMS)
		{
		currentTime = System.nanoTime();
		float elapsedTime = ((currentTime-lastUpdatedTime)/1000000.000f);
		//System.out.println("elapsedTime: " + elapsedTime);
		if (elapsedTime >= 10.0f)
			{
			//System.out.println("TICK");
				lastUpdatedTime = currentTime;
				// Update ship's movement according to speed
				ship.move();
				if (thisClient != null)
					{
						thisClient.processPackets();
						thisClient.sendMoveMessage(ship.getLocationVec());
					}
				//station.rotateStation();
				// Update SkyBox according to ship's position
				Point3D camLoc = ship.getCamera().getLocation();

				camTranslation = new Matrix3D();
				camTranslation.translate(camLoc.getX(), camLoc.getY(), camLoc.getZ());

				skyBox.setLocalTranslation(camTranslation);
				Matrix3D mat;
				Vector3D translateVec;
				physicsEngine.update(100.0f);
				for (SceneNode s : getGameWorld())
					{ 
					if (s.getPhysicsObject() != null)
					{
					mat = new Matrix3D(s.getPhysicsObject().getTransform());
					translateVec = mat.getCol(3);
					s.getLocalTranslation().setCol(3,translateVec);
					}
					}
				/* for (SceneNode s : getGameWorld())
					{
					if (s.getPhysicsObject()
					}
					*/
				// planetGrp.rotate(.5f, new Vector3D(0, 1, 0));
				super.update(elapsedTimeMS);
			}
		}

	public void setIsConnected(boolean b)
		{
		}

	public Vector3D getPlayerPosition()
		{
		Vector3D playerPositionVector = new Vector3D(ship.getLocation());
		// TODO Auto-generated method stub
		return playerPositionVector;
		}

	@Override
	public void shutdown()
		{
		super.shutdown();
		if (thisClient != null)
			{
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

	public void addPhysicsObject(Sphere laserObj)
		{
		float mass = 5.0f;
		Matrix3D goddammit = new Matrix3D();
		Vector3D bulletOrigin = ship.getLocationVec();
		bulletOrigin.setY(bulletOrigin.getY()-0.8);
		goddammit.setCol(3, bulletOrigin);
		laserP =  physicsEngine.addSphereObject(physicsEngine.nextUID(), mass, goddammit.getValues(), 1.0f);
		laserP.setBounciness(1.0f);
		float Xdir = (float) ship.getCamera().getViewDirection().getX();
		float Ydir = (float) ship.getCamera().getViewDirection().getY();
		float Zdir = (float) ship.getCamera().getViewDirection().getZ();
		float[] direction = {Xdir*3, Ydir*3,Zdir*3};
		laserP.setLinearVelocity(direction);
		laserP.setDamping(0,0);
		laserP.setFriction(0);
		laserObj.setPhysicsObject(laserP);
		
		}
	}
