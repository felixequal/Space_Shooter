package space_shooter;

public class NPCcontroller {
	private NPC[] NPClist;
	private int numNPCs;

	public void setupNPCs()
	{
		NPClist = new NPC[5];
	}
	
	public void updateNPCs()
	{
		numNPCs = NPClist.length;
		for(int i=0; i<numNPCs; i++)
		{
			NPClist[i].updateLocation();
		}
	}
	
	public int getNumOfNPCs()
	{
		return numNPCs;
	}
}
