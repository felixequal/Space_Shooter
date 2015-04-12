package a3;

import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Version;
import net.java.games.input.Component;

public class FindComponents
{
	public void listControllers()
	{
		System.out.println("JInput version: " + Version.getVersion());
		ControllerEnvironment ce = ControllerEnvironment.getDefaultEnvironment();
		
		Controller[] cs = ce.getControllers();
		
		for(int i = 0; i < cs.length; i++)
		{
			System.out.println("\nController #" + i);
			listComponents(cs[i]);
		}
	}
	
	private void listComponents(Controller contr)
	{
		System.out.println("Name: " + contr.getName() + ". Type ID: " + contr.getType());
		
		Component[] comps = contr.getComponents();
		for(int i = 0; i<comps.length; i++)
		{
			System.out.println(" name: " + comps[i].getName() + " ID: " + comps[i].getIdentifier());
		}
		
		Controller[] subCtrls = contr.getControllers();
		for(int j=0; j<subCtrls.length; j++)
		{
			System.out.println(" " + contr.getName() + "subcontroller # " + j);
			listComponents(subCtrls[j]);
		}
	}
}
