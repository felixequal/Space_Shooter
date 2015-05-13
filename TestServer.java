package space_shooter;

import java.io.IOException;
import java.net.InetAddress;

public class TestServer
{
	GameServerTCP server;
	private NPCcontroller npcCtrl;
	private long startTime, lastUpdateTime;
	private static GameServerTCP TCPServer;
	
	public TestServer(){
		startTime = System.nanoTime();
		lastUpdateTime = startTime;
		npcCtrl = new NPCcontroller();
		npcCtrl.setupNPCs();
		npcLoop();
	}
	
	public void npcLoop()
	{
		while(true)
		{
			long frameStartTime = System.nanoTime();
			float elapMilSecs = (frameStartTime - lastUpdateTime)/(1000000.0f);
			if(elapMilSecs >= 50.0f)
			{
				lastUpdateTime = frameStartTime;
				npcCtrl.updateNPCs();
				TCPServer.sendNPCinfo();
			}
			Thread.yield();
		}
	}
	
	public static <K> void main(String[] args) throws IOException
	{ 
		System.out.println("Server address:" + InetAddress.getLocalHost());
		TCPServer = new GameServerTCP(10001);
	}
}
