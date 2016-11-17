package org.cyberpwn.fap.object;

import org.bukkit.block.Hopper;
import org.phantomapi.clust.DataCluster;
import org.phantomapi.stack.StackedInventory;

public class HopperStatus
{
	private DataCluster inventory;
	private byte direction;
	
	@SuppressWarnings("deprecation")
	public HopperStatus(Hopper hopper)
	{
		try
		{
			inventory = new DataCluster(new StackedInventory(hopper.getInventory()).toData());
			direction = hopper.getRawData();
		}
		
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public DataCluster getInventory()
	{
		return inventory;
	}
	
	public byte getDirection()
	{
		return direction;
	}
}
