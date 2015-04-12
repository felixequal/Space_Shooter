package a3;

import java.io.IOException;
import java.net.InetAddress;
import java.util.UUID;
import sage.networking.server.GameConnectionServer;
import sage.networking.server.IClientInfo;
public class GameServerTCP extends GameConnectionServer<UUID>
{
	public GameServerTCP(int localPort) throws IOException
	{ 
		super(localPort, ProtocolType.TCP); 
	}

	@Override
	public void acceptClient(IClientInfo ci, Object o) // override
	{ 
		
		String message = (String)o;
	String[] messageTokens = message.split(",");
	if(messageTokens.length > 0)
	{ if(messageTokens[0].compareTo("join") == 0) // received “join”
	{ // format: join,localid
		UUID clientID = UUID.fromString(messageTokens[1]);
		System.out.println("client joined:" + clientID.toString());
		super.addClient(ci, clientID);
		sendJoinedMessage(clientID, true);
		
	} 
	} 
	}

	@Override
	public void processPacket(Object o, InetAddress senderIP, int sndPort)
	{
		String message = (String) o;
		String[] msgTokens = message.split(",");
		if(msgTokens.length > 0)
		{ if(msgTokens[0].compareTo("bye") == 0) // receive “bye”
		{ // format: bye,localid
	
			UUID clientID = UUID.fromString(msgTokens[1]);
			System.out.println("Client leaving - recieved bye message, sending bye messages to others ");
			sendByeMessages(clientID);
			super.removeClient(clientID);
			System.out.println("removed client from clientList - ");
			
				}
			
		}
		if(msgTokens[0].compareTo("create") == 0) // receive “create”
		{ // format: create,localid,x,y,z
			UUID clientID = UUID.fromString(msgTokens[1]);
			String[] pos = {msgTokens[2], msgTokens[3], msgTokens[4]};
			sendCreateMessages(clientID, pos);
			sendWantsDetailsMessages(clientID);
		}
		if(msgTokens[0].compareTo("dsfr") == 0) // receive “details for”
		{ // etc….. 
		}
		if(msgTokens[0].compareTo("move") == 0) // receive “move”
		{ 
			UUID clientID = UUID.fromString(msgTokens[1]);
			String[] pos = {msgTokens[2], msgTokens[3], msgTokens[4]};
			sendMoveMessages(clientID, pos);
			//sendWantsDetailsMessages(clientID);
		}
		
	}

	public void sendJoinedMessage(UUID clientID, boolean success)
	{ // format: join, success or join, failure
		try
		{ String message = new String("join,");
		if(success) { message += "success"; System.out.println("Server sending joined message");}
		else message += "failure";
		sendPacket(message, clientID);
		}
		catch (IOException e) { e.printStackTrace();
		} 
	}

	public void sendCreateMessages(UUID clientID, String[] position)
	{ // format: create, remoteId, x, y, z
		try
		{ String message = new String("create," + clientID.toString());
		message += "," + position[0];
		message += "," + position[1];
		message += "," + position[2];
		System.out.println("server received create request from: " + clientID.toString()+ " sending create messages");
		forwardPacketToAll(message, clientID);
		}
		catch (IOException e) { e.printStackTrace();
		} 
	}

	public void sndDetailsMsg(UUID clientID, UUID remoteId, String[] position)
	{ 
		// etc….. 
	}

	public void sendWantsDetailsMessages(UUID clientID)
	{ 
		// etc….. 
	}


	public void sendMoveMessages(UUID clientID, String[] position)
	{ 
		try
			{ String message = new String("move," + clientID.toString());
			message += "," + position[0];
			message += "," + position[1];
			message += "," + position[2];
			System.out.println("server sending move messages");
			forwardPacketToAll(message, clientID);
			}
			catch (IOException e) { e.printStackTrace();
			}  
	}


	public void sendByeMessages(UUID clientID)
	{ // etc….. 
		try
			{ String message = new String("bye," + clientID.toString());
			System.out.println("server sending bye messages");
			forwardPacketToAll(message, clientID);
			}
			catch (IOException e) { e.printStackTrace();
			}
	}

}