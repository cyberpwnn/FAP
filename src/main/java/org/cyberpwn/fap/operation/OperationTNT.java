package org.cyberpwn.fap.operation;

import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.cyberpwn.fap.FAPController;
import org.cyberpwn.fap.FAPOperator;
import org.phantomapi.sync.S;
import org.phantomapi.world.W;

public class OperationTNT extends FAPOperator
{
	private final Location location;
	private final float power;
	private final List<Block> blocks;
	
	public OperationTNT(Location location, float power, List<Block> blocks)
	{
		this.location = location;
		this.power = power;
		this.blocks = blocks;
	}
	
	@Override
	public void operate()
	{
		World w = W.getAsyncWorld(location.getWorld().getName());
		
		for(Block i : blocks)
		{
			Block b = w.getBlockAt(i.getX(), i.getY(), i.getZ());
			
			if(b.getType().equals(Material.CHEST) || b.getType().equals(Material.TRAPPED_CHEST))
			{
				new S()
				{
					@Override
					public void sync()
					{
						W.toSync(b).breakNaturally(new ItemStack(Material.DIAMOND_AXE));
					}
				};
			}
			
			else
			{
				FAPController.wq.set(b.getLocation(), Material.AIR);
			}
		}
	}
	
	public Location getLocation()
	{
		return location;
	}
	
	public float getPower()
	{
		return power;
	}
}
