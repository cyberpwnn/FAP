package org.cyberpwn.fap.handler;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.cyberpwn.fap.FAPController;
import org.cyberpwn.fap.FAPHandler;
import org.cyberpwn.fap.operation.OperationTNT;
import org.phantomapi.async.A;
import org.phantomapi.clust.AsyncConfig;
import org.phantomapi.clust.Comment;
import org.phantomapi.clust.Keyed;
import org.phantomapi.lang.GList;
import org.phantomapi.statistics.Monitorable;
import org.phantomapi.util.C;
import org.phantomapi.util.F;
import org.phantomapi.world.Photon;

@AsyncConfig
public class TNTHandler extends FAPHandler implements Monitorable
{
	public static int threads = 0;
	
	@Comment("Should we ask photon to relight blown up areas?\nLighting is handled async, and has very little performance impace.")
	@Keyed("tnt.use-photon")
	public boolean photon = true;
	
	@Comment("Turning this off will make tnt no longer multicore\nbut it still is faster, and uses FAP mechanics.")
	@Keyed("tnt.asyncronous-processing")
	public boolean async = true;
	
	public TNTHandler()
	{
		super("tnt");
	}
	
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
		
		OperationTNT o = new OperationTNT(e.getEntity().getLocation(), e.getYield(), new GList<Block>(e.blockList()));
		
		for(Block i : new GList<Block>(e.blockList()))
		{
			if(photon)
			{
				Photon.relight(i);
			}
			
			if(!i.getType().equals(Material.TNT) && !i.getType().hasGravity())
			{
				e.blockList().remove(i);
			}
		}
		
		if(async)
		{
			new A()
			{
				@Override
				public void async()
				{
					o.operate();
				}
			};
		}
		
		else
		{
			o.operate();
		}
	}

	@Override
	public String getMonitorableData()
	{
		return "Threads: " + C.LIGHT_PURPLE + F.f(threads);
	}
}
