package org.cyberpwn.fap.handler;

import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.cyberpwn.fap.FAPHandler;
import org.phantomapi.construct.Ticked;
import org.phantomapi.statistics.Monitorable;
import org.phantomapi.util.C;
import org.phantomapi.util.F;

@Ticked(20)
public class HopperHandler extends FAPHandler implements Monitorable
{
	private int ups;
	private int upm;
	
	public HopperHandler()
	{
		super("hopper");
		
		ups = 0;
		upm = 0;
	}
	
	@Override
	public String getMonitorableData()
	{
		return "Updates: " + C.LIGHT_PURPLE + F.f(upm) + "/s";
	}
	
	@Override
	public void onTick()
	{
		upm = ups;
		ups = 0;
	}
	
	@Override
	public void onStart()
	{
		loadCluster(this, "handlers");
	}
	
	@EventHandler
	public void on(InventoryMoveItemEvent e)
	{
		ups++;
	}
}