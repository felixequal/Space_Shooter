package space_shooter;
//Start point
public class Starter {
	public static void main(String[] args) 
		{
			String address = args[0];
			int port = Integer.parseInt(args[1]);
			MyNetworkingClient game = new MyNetworkingClient(address, port);
			game.start();
		}
}
