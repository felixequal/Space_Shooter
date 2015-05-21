package space_shooter;
/* This is the main game client. It inits a physics handler and a network hander (CLASS:MyClient) which is attached and runs the network part.
 * If there is no server, it ignores network functionality and just runs standalone.
 * 
 */
import java.awt.Color;
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
import sage.physics.JBullet.JBulletBoxObject;
import sage.renderer.IRenderer;
import sage.scene.Group;
import sage.scene.HUDImage;
import sage.scene.HUDString;
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

	private static final int bulletspeed = 20;
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
	private ArrayList<Cube> physCubeList;
	private Cube physCube;
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
	private IPhysicsObject shipBall,laserP;
	private Matrix3D camTranslation;
	private HUDImage cockpit, screen, healthImage, speedImage;
	private HUDImage spd_Image;
	
	double x;
	double z;
	double theta;
	float a = 0.1f;
	float b = 0.1f;
	float r = 1.0f;
	
	IAudioManager audioMgr;
	Sound thrusterSound;
	AudioResource resource1;
	public MyNetworkingClient(String serverAddr, int serverPrt)
	{
		super();
		this.serverAddress = serverAddr;
		this.serverPort = serverPrt;
		System.out.println("Server Addr: " + serverAddress);
		
		x = 0;
		z = 0;
		theta = 45;
	}

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
			thisClient = new MyClient(InetAddress.getByName(serverAddress), serverPort, ProtocolType.TCP, this);
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
			System.out.println("Name =" + f.getEngineName() + " language =" + f.getLanguageName() + " extensions =" + f.getExtensions());
			}

		// Add all game objects including skybox

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
		// elapsedTime = System.currentTimeMillis();
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
		String engine = "sage.physics.JBullet.JBulletPhysicsEngine";
		physicsEngine = PhysicsEngineFactory.createPhysicsEngine(engine);
		physicsEngine.initSystem();
		float[] gravity = { 0, 0, 0 };
		physicsEngine.setGravity(gravity);
		}

	private void createSagePhysicsWorld()
		{
		float massCube = 1000000.0f;
		float[] size = {1,1,1};
		//double[] scale = cube.getLocalScale().getValues();
		//float[] scale2 = new float[scale.length];
		//for (int x = 0; x < scale.length; x++)
		//	{
		//	scale2[x] = (float) scale[x];
		//	System.out.println("scale: " + scale[x] + "scale2:" + scale2[x]);
		//	}
		//cube.updateWorldBound();
		// IPhysicsObject box = new IPhysicsObject();

		//cubeP = physicsEngine.addBoxObject(physicsEngine.nextUID(), massCube, cube.getWorldTransform().getValues(), scale2);
		//cube.setPhysicsObject(cubeP);

		for (Cube pcube : physCubeList)
			{
			IPhysicsObject cubeP = physicsEngine.addBoxObject(physicsEngine.nextUID(), massCube, pcube.getWorldTransform().getValues(),size);
			cubeP.setBounciness(1.0f);
			pcube.setPhysicsObject(cubeP);
			}

		float massShip = 5.0f;
		shipBall = physicsEngine.addSphereObject(physicsEngine.nextUID(), massShip, ship.getWorldTransform().getValues(), 1.0f);
		shipBall.setBounciness(1.0f);
		ship.setPhysicsObject(shipBall);

		}

	public void initGameObjects()
		{
		physCubeList = new ArrayList<Cube>();
		// Add SkyBox w/ ZBuffer disabled
		skyBox = new Space(renderer);
		skyBox.scale(500.0f, 500.0f, 500.0f);
		addGameWorldObject(skyBox);
		// Now enabled the ZBuffer
		skyBox.getBuf().setDepthTestingEnabled(true);
		//cube = new Cube();
		// Matrix3D scale = cube.getLocalScale();
		// cube.scale(3, 3, 3);
		// cube.setLocalScale(scale);
		// cube.translate(1,0,1);

		//physCube = new PhysCube(x,y,z,cube);
		//physCubeList.add(physCube);
		//addGameWorldObject(cube);
	//	cube.updateLocalBound();
		//cube.updateWorldBound();

		for (int p = 0; p < 15; p++)
			{
			Random r = new Random();
			double x = rangeMin + (rangeMax - rangeMin) * r.nextDouble();
			double y = rangeMin + (rangeMax - rangeMin) * r.nextDouble();
			double z = rangeMin + (rangeMax - rangeMin) * r.nextDouble();
			//float x = (float) (a / 3) * 1.5f;
			//float y = (float) (b / 4) * 1.5f;
			//float z = (float) (c / 3) * 1.5f;
			Matrix3D localTranslation = new Matrix3D();
			localTranslation.translate(x, y, z);
			physCube = new Cube();
			//cube.scale(x, y, z);
			physCube.setWorldTranslation(localTranslation);
			physCube.updateWorldBound();
			//physCube = new PhysCube(x, y, z, cube);
			physCubeList.add(physCube);
			addGameWorldObject(physCube);
			}
		// planet = new Planet();
		// planetGrp = planet.loadObject();
		// planetGrp.translate(0, 0, 0);
		// addGameWorldObject(planetGrp);
		 planet = new Planet();
		 planetGrp = planet.loadObject();
		 planetGrp.translate(0, 0, 0);
		 addGameWorldObject(planetGrp);
		// Add other objects
		ship = new SpaceShip(renderer, display);

		// Add Space Station
		station = new SpaceStation();
		addGameWorldObject(station.loadObject());

		map = new Map();
		addGameWorldObject(map.loadWall1());
		addGameWorldObject(map.loadWall2());
		addGameWorldObject(map.loadWall3());
		addGameWorldObject(map.loadWall4());
		addGameWorldObject(map.loadCargoShip());
		// Load terrain
		terrain = new Terrain(this);
		tBlock = terrain.getTerrain();
		tBlock.translate(0, -5, 0);
		addGameWorldObject(tBlock);
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
		PitchDownAction pitchDown = new PitchDownAction(thisClient, ship.getCamera(), ship);
		YawRightAction yawRight = new YawRightAction(thisClient,ship.getCamera(), ship);
		YawLeftAction yawLeft = new YawLeftAction(thisClient,ship.getCamera(), ship);
		TiltRightAction tiltRight = new TiltRightAction(thisClient,ship.getCamera(), ship);
		TiltLeftAction tiltLeft = new TiltLeftAction(thisClient,ship.getCamera(), ship);
		
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
	
	public void initHUD(){
		cockpit = new HUDImage("Textures/cockpit.png");
		cockpit.rotateImage(180.0f);
		cockpit.scale(2.0f, 0.8f, 1.0f);
		cockpit.setLocation(0,-0.6);
		cockpit.setRenderMode(sage.scene.SceneNode.RENDER_MODE.ORTHO);
		cockpit.setCullMode(sage.scene.SceneNode.CULL_MODE.NEVER);
		ship.getCamera().addToHUD(cockpit);
		
		screen = new HUDImage("Textures/screen.jpg");
		screen.setLocation(0, -0.7);
		screen.scale(0.45f, 0.9f, 0.7f);
		screen.setRenderMode(sage.scene.SceneNode.RENDER_MODE.ORTHO);
		screen.setCullMode(sage.scene.SceneNode.CULL_MODE.NEVER);
		ship.getCamera().addToHUD(screen);
		
		healthImage = new HUDImage("Textures/healthImage.png");
		healthImage.rotateImage(180.0f);
		healthImage.scale(0.2f,  0.2f, 1.0f);
		healthImage.setLocation(-0.1, -0.4);
		healthImage.setRenderMode(sage.scene.SceneNode.RENDER_MODE.ORTHO);
		healthImage.setCullMode(sage.scene.SceneNode.CULL_MODE.NEVER);
		ship.getCamera().addToHUD(healthImage);
		
		speedImage = new HUDImage("Textures/speedImage.png");
		speedImage.rotateImage(180.0f);
		speedImage.scale(0.2f,  0.2f, 1.0f);
		speedImage.setLocation(-0.1, -0.6);
		speedImage.setRenderMode(sage.scene.SceneNode.RENDER_MODE.ORTHO);
		speedImage.setCullMode(sage.scene.SceneNode.CULL_MODE.NEVER);
		ship.getCamera().addToHUD(speedImage);
		
		healthImage = new HUDImage("Textures/healthImage_5.jpg");
		//healthImage_5.rotateImage(180.0f);
		healthImage.scale(0.1f,  0.1f, 1.0f);
		healthImage.setLocation(0.075, -0.39);
		healthImage.setRenderMode(sage.scene.SceneNode.RENDER_MODE.ORTHO);
		healthImage.setCullMode(sage.scene.SceneNode.CULL_MODE.NEVER);
		ship.getCamera().addToHUD(healthImage);
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
				checkLaserTTL();
				ship.move();
				if (thisClient != null)
					{
						thisClient.processPackets();
						thisClient.sendMoveMessage(ship.getLocationVec());
					}
				station.rotateStation();
				// Update SkyBox according to ship's position
				Point3D camLoc = ship.getCamera().getLocation();

				camTranslation = new Matrix3D();
				camTranslation.translate(camLoc.getX(), camLoc.getY(), camLoc.getZ());

				skyBox.setLocalTranslation(camTranslation);
				Matrix3D mat;
				Vector3D translateVec;
				physicsEngine.update(200.0f);
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
				planetGrp.rotate(.5f, new Vector3D(0, 1, 0));
				
				/*if(ship.getHealth() == 6){
					healthImage.setImage("textures/healthImage_5.jpg");
				}else{
					if(ship.getHealth() == 5){
						healthImage.setImage("textures/healthImage_4.jpg");
					}else{
						if(ship.getHealth() == 4){
							healthImage.setImage("textures/healthImage_3.jpg");
						}else{
							if(ship.getHealth() == 3){
								healthImage.setImage("textures/healthImage_2.jpg");
							}else{
								if(ship.getHealth() == 2){
									healthImage.setImage("textures/healthImage_1.jpg");
								}else{
									healthImage.setImage("textures/healthImage_0.jpg");
								}
							}
						}
					}
				}*/
				
				////Basic NPC///////
				
			/*	theta = theta + Math.toRadians(10);
				x = a + r*Math.cos(theta);
				z = b + r*Math.sin(theta);*/
				
				/*if(z >= -5) {
					z = z - 0.001f;
					map.loadCargoShip().translate(0, 0, -.001f);
				}*/
				
				
				super.update(elapsedTimeMS);

			}
		}

	public void setIsConnected(boolean b)
		{}


	public Vector3D getPlayerPosition()
		{
		Vector3D playerPositionVector = new Vector3D(ship.getLocation());
		return playerPositionVector;
		}

	@Override
	public void shutdown()
		{
		audioMgr.shutdown();
		super.shutdown();
		if (thisClient != null)
			{
			thisClient.sendByeMessage();

			try
				{
				thisClient.shutdown();
				} catch (IOException e1)
				{
				e1.printStackTrace();
				}
			try
				{
				Thread.sleep(4000);
				} catch (InterruptedException e)
				{
				e.printStackTrace();
				}
			}
		}

	public void addPhysicsObject(Sphere laserObj)
		{
		float mass = 5.0f;
		Matrix3D bulletMat = new Matrix3D();
		Vector3D bulletOrigin = ship.getLocationVec();
		bulletOrigin.setY(bulletOrigin.getY() - 0.2);
		bulletMat.setCol(3, bulletOrigin);
		laserP = physicsEngine.addSphereObject(physicsEngine.nextUID(), mass, bulletMat.getValues(), 1.0f);
		laserP.setBounciness(1.0f);
		float Xdir = (float) ship.getCamera().getViewDirection().getX();
		float Ydir = (float) ship.getCamera().getViewDirection().getY();
		float Zdir = (float) ship.getCamera().getViewDirection().getZ();
		float[] direction = { Xdir * bulletspeed, Ydir * bulletspeed, Zdir * bulletspeed };
		laserP.setLinearVelocity(direction);
		laserP.setDamping(0, 0);
		laserP.setFriction(0);
		laserObj.setPhysicsObject(laserP);
		}

	public void checkLaserTTL()
		{
		Vector<Laser> laserStorage = ship.getLaserStorage();
		for (Laser las : laserStorage)
			{
			if (las.expired()) removeGameWorldObject(las.getLaser());
			}
		}
	}
