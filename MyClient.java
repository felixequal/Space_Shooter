package space_shooter;

import graphicslib3D.Vector3D;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;
import java.util.Vector;

import sage.networking.client.GameConnectionClient;
import sage.scene.SceneNode;
import sage.scene.TriMesh;


public class MyClient extends GameConnectionClient
{
	private MyNetworkingClient game;
	private UUID id, ghostID;
	GhostAvatar gA;
	private ArrayList<GhostAvatar> ghostAvatars;
	//private Object ghostPosition;
	private double x, y, z;
	//GhostAvatar ghost;
	private boolean removalFlag;
	
	public MyClient(InetAddress remAddr, int remPort, ProtocolType pType, MyNetworkingClient game) throws IOException
	{
		super(remAddr, remPort, pType);
		this.game = game;
		this.id = UUID.randomUUID();
		this.ghostAvatars = new ArrayList<GhostAvatar>();
	}
	

	@Override
	protected void processPacket(Object msg)
	{
		String message = (String) msg;
		String[] msgTokens = message.split(",");
//------------------------ RECEIVE “join”----------------------------------------------------------
		if(msgTokens[0].compareTo("join") ==0)
		{
			if(msgTokens[1].compareTo("success") == 0)
			{
				game.setIsConnected(true);
				System.out.println("successfully joined server, sending create message");
				sendCreateMessage(game.getPlayerPosition());
			}
			if(msgTokens[1].compareTo("failure") == 0)
				game.setIsConnected(false);
		}
		
		
//------------------------ RECEIVE “create”----------------------------------------------------------
		if(msgTokens[0].compareTo("create") == 0)
		{
			System.out.println("Received create message from server");
			ghostID = UUID.fromString(msgTokens[1]);
			x = Double.parseDouble(msgTokens[2]);
			y = Double.parseDouble(msgTokens[3]);
			z = Double.parseDouble(msgTokens[4]);
			Vector3D ghostVector = new Vector3D(x,y,z); 
			createGhostAvatar(ghostID, ghostVector);
			for(GhostAvatar list: ghostAvatars)
				{
			System.out.println("ghost avatar UUID: " + list.getGhostID() + " \nPosition: " + list.getPositionVec());
				}
		}
		
		if(msgTokens[0].compareTo("wsdf") == 0)
			{
			System.out.println("Client: received wants-details-for message, send details to server (dsfr)");
			ghostID = UUID.fromString(msgTokens[1]);
			sendDetails(id, ghostID, game.getPlayerPosition());
			}
			
//------------------------ RECEIVE “dsfr”----------------------------------------------------------
		if(msgTokens[0].compareTo("dsfr") == 0)
			{
			System.out.println("Received dsfr message from server");
			ghostID = UUID.fromString(msgTokens[1]);
			x = Double.parseDouble(msgTokens[2]);
			y = Double.parseDouble(msgTokens[3]);
			z = Double.parseDouble(msgTokens[4]);
			Vector3D dsfrVector = new Vector3D(x,y,z);
			createGhostAvatar(ghostID, dsfrVector);
			for(GhostAvatar list: ghostAvatars)
				{
				System.out.println("ghost avatar UUID: " + list.getGhostID() + " \nPosition: " + list.getPositionVec());
				}
			}
//------------------------ RECEIVE “move”----------------------------------------------------------
		if(msgTokens[0].compareTo("move") == 0) 
			{
			ghostID = UUID.fromString(msgTokens[1]);
			//System.out.println("Received move message from server from: " + ghostID);
			x = Double.parseDouble(msgTokens[2]);
			y = Double.parseDouble(msgTokens[3]);
			z = Double.parseDouble(msgTokens[4]);
			Vector3D ghostcrVector = new Vector3D(x,y,z);
			//System.out.println("move vector from other client: " + ghostVector.toString());			
			for(GhostAvatar check: ghostAvatars)
				{
				if(check.getGhostID().equals(ghostID))
					{
					//System.out.println("found correct ghost. Moving: " + ghostID);
					check.moveAvatar(ghostcrVector);
					}
				}
			}
//------------------------ RECEIVE “rot”----------------------------------------------------------
		if(msgTokens[0].compareTo("rot") == 0) 
			{
			ghostID = UUID.fromString(msgTokens[1]);
			//System.out.println("Received move message from server from: " + ghostID);
			x = Double.parseDouble(msgTokens[2]);
			y = Double.parseDouble(msgTokens[3]);
			z = Double.parseDouble(msgTokens[4]);
			Vector3D ghostcrVector = new Vector3D(x,y,z);
			//System.out.println("move vector from other client: " + ghostVector.toString());			
			for(GhostAvatar check: ghostAvatars)
				{
				if(check.getGhostID().equals(ghostID))
					{
					//System.out.println("found correct ghost. Moving: " + ghostID);
					check.rotAvatar(ghostcrVector);
					}
				}
			}
//------------------------ RECEIVE “bye”----------------------------------------------------------		
		if(msgTokens[0].compareTo("bye") == 0)
			{
				ghostID = UUID.fromString(msgTokens[1]);
				System.out.println("received bye message from: " + ghostID);
				removeGhostAvatar(ghostID);
			}
	}

private void createGhostAvatar(UUID ghostID, Vector3D ghostPosition2) {
	
	
	Vector3D initPos = ghostPosition2;
	
		gA = new GhostAvatar(ghostID, initPos);
		ghostAvatars.add(gA);
		game.addGameWorldObject(gA.getAvatar());
		
	}
private void removeGhostAvatar(UUID ghostID) 
	{
		int gAIndex = -1;
	/*	Iterator iter = game.getGameWorld().iterator();
		while(iter.hasNext())
			{
				if (iter.next() instanceof GhostAvatar)
					GhostAvatar instance = node;
					if (instance.getGhostID() == ghostID)
		game.getGameWorld().iterator().remove();
			}
			*/
		for(GhostAvatar findGhost: ghostAvatars)
			{
				gAIndex++;
				if (findGhost.getGhostID() == ghostID)
					{
					break;
					}
			}
		
		System.out.println("gAIndex: " + gAIndex);
		gA = ghostAvatars.get(gAIndex);
		//removalFlag = true;
		//gAIndex = -1;
		game.removeGameWorldObject(gA.getAvatar());
		ghostAvatars.remove(ghostID);
		
	}

public void setRemovalFlag(boolean b)
{
	removalFlag = b;
}

public boolean checkAvatarsToDelete()
{
	if (removalFlag == false)
		{
			gA = null;
			return removalFlag;
		}
	return removalFlag;
}

public SceneNode getGhostToRemove()
{
	removalFlag = false;
	System.out.println("removeGhost:" + gA.getGhostID());
	return gA;
}

public void sendCreateMessage(Vector3D pos)
{ // format: (create, localId, x,y,z)
	try
	{ String message = new String("create," + id.toString());
	message += "," + pos.getX()+"," + pos.getY() + "," + pos.getZ();
	sendPacket(message);
	}
	catch (IOException e) { e.printStackTrace(); }
}

public void sendJoinMessage()
{ // format: join, localId
	try
	{ sendPacket(new String("join," + id.toString())); }
	catch (IOException e) { e.printStackTrace(); }
}

public void sendByeMessage()
{
	System.out.println("Client: quitting - sending bye message");
	try
	{ sendPacket(new String("bye," + id.toString())); }


	catch (IOException e) { e.printStackTrace(); } 
}

public void sendDetails(UUID localID, UUID remID, Vector3D pos)
{
System.out.println("Client:sending details to " + remID.toString() + " from " + localID.toString());
try
	{ String message = new String("dsfr," + id.toString() + "," + remID.toString());
	message += "," + pos.getX()+"," + pos.getY() + "," + pos.getZ();
	sendPacket(message);
	}
	catch (IOException e) { e.printStackTrace(); }
}

public void sendMoveMessage(Vector3D pos)
{ 
	try
		{ String message = new String("move," + id.toString());
		message += "," + pos.getX()+"," + pos.getY() + "," + pos.getZ();
		sendPacket(message);
		}
		catch (IOException e) { e.printStackTrace(); }
}

public void sendRotMessage(Vector3D rot)
{ 
System.out.println("In MyClient: sending rot message");
	try
		{ String message = new String("rot," + id.toString());
		message += "," + rot.getX()+"," + rot.getY() + "," + rot.getZ();
		sendPacket(message);
		}
		catch (IOException e) { e.printStackTrace(); }
}

}