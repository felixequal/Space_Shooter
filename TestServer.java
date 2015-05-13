package space_shooter;

import java.io.IOException;
import java.net.InetAddress;

public class TestServer
{
	GameServerTCP server;

	public static <K> void main(String[] args) throws IOException
	{ 
		System.out.println("Server address:" + InetAddress.getLocalHost());
		GameServerTCP server = new GameServerTCP(10001);
				
	}
}
