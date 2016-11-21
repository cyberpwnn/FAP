package org.cyberpwn.fap.handler;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.cyberpwn.fap.FAPController;
import org.cyberpwn.fap.FAPHandler;
import org.cyberpwn.fap.operation.OperationTNT;
import org.phantomapi.clust.AsyncConfig;
import org.phantomapi.lang.GList;
import org.phantomapi.statistics.Monitorable;
import org.phantomapi.util.C;
import org.phantomapi.util.F;

@AsyncConfig
public class TNTHandler extends FAPHandler implements Monitorable
{
	public static int threads = 0;
	
	public TNTHandler()
	{
		super("tnt");
	}
	
	@Override
	public void onStart()
	{
		loadCluster(this, "handlers");
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void on(EntityExplodeEvent e)
	{
		if(e.isCancelled() || e.blockList().isEmpty() || !FAPController.inst().handleTNT)
		{
			return;
		}
		
		GList<Block> blocks = new GList<Block>();
		
		for(Block i : new GList<Block>(e.blockList()))
		{
			if(!i.getType().equals(Material.TNT) && !i.getType().hasGravity())
			{
				blocks.add(i);
				e.blockList().remove(i);
			}
		}
		
		new OperationTNT(e.getEntity().getLocation(), e.getYield(), blocks).operate();
	}
	
	@Override
	public String getMonitorableData()
	{
		return "Threads: " + C.LIGHT_PURPLE + F.f(threads);
	}
}
