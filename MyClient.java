package a3;

import graphicslib3D.Vector3D;

import java.io.IOException;
import java.net.InetAddress;
import java.util.UUID;
import java.util.Vector;

import sage.networking.client.GameConnectionClient;


public class MyClient extends GameConnectionClient
{
	private MyNetworkingClient game;
	private UUID id;
	private Vector<GhostAvatar> ghostAvatars;
	private Object ghostPosition;
	
	public MyClient(InetAddress remAddr, int remPort, ProtocolType pType, MyNetworkingClient game) throws IOException
	{
		super(remAddr, remPort, pType);
		this.game = game;
		this.id = UUID.randomUUID();
		this.ghostAvatars = new Vector<GhostAvatar>();
	}
	

	@Override
	protected void processPacket(Object msg)
	{
		String message = (String) msg;
		String[] msgTokens = message.split(",");
		if(msgTokens[0].compareTo("join") ==0)
			// receive “join”
		{
			// format: join, success or join, failure
			if(msgTokens[1].compareTo("success") == 0)
			{
				game.setIsConnected(true);
				System.out.println("successfully joined server, sending create message");
				sendCreateMessage(game.getPlayerPosition());
			}
			if(msgTokens[1].compareTo("failure") == 0)
				game.setIsConnected(false);
		}
		if(msgTokens[0].compareTo("bye") == 0)
			
		{
			// format: bye, remoteId
			UUID ghostID = UUID.fromString(msgTokens[1]);
			System.out.println("received bye message from: " + ghostID);
			//removeGhostAvatar(ghostID);
		}
		if
		(msgTokens[0].compareTo("create") == 0)
			// receive “details for”
		{
			System.out.println("Received create message from server");
			// format: create, remoteId, x,y,z or dsfr, remoteId, x,y,z
			UUID ghostID = UUID.fromString(msgTokens[1]);
			double x = Double.parseDouble(msgTokens[2]);
			double y = Double.parseDouble(msgTokens[3]);
			double z = Double.parseDouble(msgTokens[4]);
			Vector3D ghostVector = new Vector3D(x,y,z); 
			// extract ghost x,y,z, position from message, then:
			createGhostAvatar(ghostID, ghostPosition);
		}
		if(msgTokens[0].compareTo("wsds") == 0)
			// receive “create...”
		{
			//etc.....
		}
		if(msgTokens[0].compareTo("wsds") == 0)
			// receive “wants...”
		{
			//etc.....
		}
		if(msgTokens[0].compareTo("move") == 0)
			// receive “move”
		{
			System.out.println("Received move message from server");
			// format: create, remoteId, x,y,z or dsfr, remoteId, x,y,z
			UUID ghostID = UUID.fromString(msgTokens[1]);
			double x = Double.parseDouble(msgTokens[2]);
			double y = Double.parseDouble(msgTokens[3]);
			double z = Double.parseDouble(msgTokens[4]);
			Vector3D ghostVector = new Vector3D(x,y,z); 
			// extract ghost x,y,z, position from message, then:
			for(GhostAvatar check: ghostAvatars)
				{
					if (check.getGhostID() == ghostID)
						check.moveAvatar(ghostVector);
				}
			//ghostAvatars.
			//createGhostAvatar(ghostID, ghostPosition);
		}
	}

private void createGhostAvatar(UUID ghostID, Object ghostPosition2) {
	
	
	Vector3D initPos = new Vector3D();
	
		GhostAvatar ghost = new GhostAvatar(ghostID, initPos);
		ghostAvatars.add(ghost);
		game.addGameWorldObject(ghost.getAvatar());
		//game.addGameWorldObject();
		
	}
private void removeGhostAvatar(UUID ghostID) {
		// TODO Auto-generated method stub
		
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
public void sendDetailsForMessage(UUID remId, Vector3D pos)
{ // etc….. 
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
}