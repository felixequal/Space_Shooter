package space_shooter;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.UUID;
import sage.networking.server.GameConnectionServer;
import sage.networking.server.IClientInfo;


public class GameServerTCP extends GameConnectionServer<UUID>
{

int numberOfClients = 0;

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
		System.out.println("client joined:" + ci + " -  " + clientID.toString());
		super.addClient(ci, clientID);
		numberOfClients++;
		System.out.println("Server: Total clients connected: " + numberOfClients);
		try
			{
			sendJoinedMessage(clientID, true);
		} catch (SocketException e) {}
	}
	}
	
	
	}

	@Override
	public void processPacket(Object o, InetAddress senderIP, int sndPort)
	{
	
		String message = (String) o;
		String[] msgTokens = message.split(",");
		UUID clientID = UUID.fromString(msgTokens[1]);
		
		if(msgTokens.length > 0)
			{ 
			if(msgTokens[0].compareTo("bye") == 0) // receive “bye”
				{
				//UUID clientID = UUID.fromString(msgTokens[1]);
				System.out.println("Server: Client leaving - recieved bye message, sending bye messages to others ");
				sendByeMessages(clientID);
				super.removeClient(clientID);
				numberOfClients--;
				System.out.println("removed client from clientList - ");
				System.out.println("Server: Total clients connected: " + numberOfClients);
				}
			}
		
		if(msgTokens[0].compareTo("create") == 0) //receive “create”
		{ // format: create,localid,x,y,z
			//UUID clientID = UUID.fromString(msgTokens[1]);
			String[] pos = {msgTokens[2], msgTokens[3], msgTokens[4]};
			try{
			sendCreateMessages(clientID, pos);
			if(numberOfClients > 1)
				{
				System.out.println("Server: client sent create message, requesting details from others (sending wsds)");
				sendWantsDetailsMessages(clientID);
				}
			}catch (Exception e) {e.printStackTrace();  
			//super.removeClient(clientID);
			//try{
			//super.getServerSocket().close();
			//} catch (IOException f) {f.printStackTrace();}
			
			}
		}
		
		if(msgTokens[0].compareTo("dsfr") == 0) // receive “details for”
		{
		System.out.println("Server: received details-for message, sending it to recipient");
		
		UUID from = UUID.fromString(msgTokens[1]);
		UUID to = UUID.fromString(msgTokens[2]);
		System.out.println("from: " + from.toString() + " to:" + to.toString());
		String[] pos = {msgTokens[3], msgTokens[4], msgTokens[5]};
		try{
		sndDetailsMsg(from, to, pos);
		}catch (Exception e) 
			{
			e.printStackTrace(); 
			//super.removeClient(clientID);
			//try{
			//super.getServerSocket().close();
			//} catch (IOException f) {f.printStackTrace();}
			}
		}
		
		if(msgTokens[0].compareTo("move") == 0) // receive “move”
		{ 
		//	UUID clientID = UUID.fromString(msgTokens[1]);
			String[] pos = {msgTokens[2], msgTokens[3], msgTokens[4]};
			try{
			sendMoveMessages(clientID, pos);
			}catch (SocketException e) {
			//e.printStackTrace();
			System.out.println("Server: Throwing error in Process Packet: ");
			System.out.println(e.toString());
			System.out.println("Server: END THROW: Process Packet: ");
			//super.removeClient(clientID);
			//try{
			//super.getServerSocket().close();
			//} catch (IOException f) {f.printStackTrace();}
			
			}
		}
	
		
	}

	public void sendJoinedMessage(UUID clientID, boolean success) throws SocketException
	{ // format: join, success or join, failure
		try
		{ String message = new String("join,");
		if(success) { message += "success"; System.out.println("Server sending joined message");}
		else message += "failure";
		sendPacket(message, clientID);
		}
		catch (IOException e) { e.printStackTrace();}
	}

	public void sendCreateMessages(UUID clientID, String[] position) throws SocketException
	{ // format: create, remoteId, x, y, z
		try
			{ 
			String message = new String("create," + clientID.toString());
			message += "," + position[0];
			message += "," + position[1];
			message += "," + position[2];
			System.out.println("server received create request from: " + clientID.toString()+ " sending create messages");
			forwardPacketToAll(message, clientID);
			}
		catch (IOException e) { e.printStackTrace();} 
	}

	public void sndDetailsMsg(UUID clientID, UUID remoteID, String[] position) throws SocketException
		{ 
		try
			{ 
			String message = new String("dsfr," + clientID.toString());
			message += "," + position[0];
			message += "," + position[1];
			message += "," + position[2];
			sendPacket(message, remoteID);
			}
		catch (IOException e) { e.printStackTrace(); }
		}

	public void sendWantsDetailsMessages(UUID clientID) throws SocketException
		{ 
		try
			{ 
			String message = new String("wsdf," + clientID.toString());
			forwardPacketToAll(message, clientID);
			} 
		catch (IOException e) { e.printStackTrace(); } 
		}


	public void sendMoveMessages(UUID clientID, String[] position) throws SocketException
	{ 
		try
			{ 
			String message = new String("move," + clientID.toString());
			message += "," + position[0];
			message += "," + position[1];
			message += "," + position[2];
			//System.out.println("server sending move messages" + message);
			try
				{
				forwardPacketToAll(message, clientID);
				}
			catch (SocketException e) 
				{
				e.printStackTrace(); 
				super.removeClient(clientID);
				super.getServerSocket().close();
				}
			}
		catch (IOException e) { e.printStackTrace();}  
	}


	public void sendByeMessages(UUID clientID)
	{ // etc….. 
		try
			{ 
			String message = new String("bye," + clientID.toString());
			System.out.println("server sending bye messages");
			try
				{
				forwardPacketToAll(message, clientID);
				}
			catch (SocketException e) 
				{
				e.printStackTrace(); 
				super.removeClient(clientID);
				super.getServerSocket().close();
				}
			}
			catch (IOException e) { e.printStackTrace();}
			}

}