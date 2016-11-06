package org.cyberpwn.fap.handler;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.material.PistonBaseMaterial;
import org.bukkit.material.PistonExtensionMaterial;
import org.bukkit.util.Vector;
import org.cyberpwn.fap.FAPController;
import org.cyberpwn.fap.FAPHandler;
import org.phantomapi.clust.Comment;
import org.phantomapi.clust.Keyed;
import org.phantomapi.lang.GList;
import org.phantomapi.lang.GSound;
import org.phantomapi.statistics.Monitorable;
import org.phantomapi.sync.TaskLater;
import org.phantomapi.world.Area;
import org.phantomapi.world.Blocks;
import org.phantomapi.world.Photon;
import org.phantomapi.world.W;

public class PistonHandler extends FAPHandler implements Monitorable
{
	@Comment("Use photon to relight chunks")
	@Keyed("pistons.use-photon")
	public boolean usePhoton = false;
	
	@Comment("Push entities like normal pistons?")
	@Keyed("pistons.push-entities")
	public boolean pushEntities = true;
	
	public PistonHandler()
	{
		super("piston");
	}
	
	@Override
	public String getMonitorableData()
	{
		return "?";
	}
	
	@Override
	public void onStart()
	{
		loadCluster(this, "handlers");
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void on(BlockPistonExtendEvent e)
	{
		if(!FAPController.inst().handlePistons)
		{
			return;
		}
		
		e.setCancelled(true);
		
		new GSound(Sound.PISTON_EXTEND, 1f, 1.5f).play(e.getBlock().getLocation());
		BlockFace face = e.getDirection();
		GList<Block> blocks = new GList<Block>(e.getBlocks());
		Block piston = e.getBlock();
		
		for(Block i : blocks)
		{
			FAPController.wq.set(i.getLocation(), Material.AIR);
		}
		
		if(pushEntities)
		{
			push(piston, face);
		}
		
		for(Block i : blocks)
		{
			if(pushEntities)
			{
				push(i, face);
				push(i.getRelative(BlockFace.DOWN), face);
				push(i.getRelative(BlockFace.UP), face);
			}
			
			FAPController.wq.set(i.getLocation().clone().add(face.getModX(), face.getModY(), face.getModZ()), i.getType(), i.getData());
		}
		
		PistonExtensionMaterial pem = new PistonExtensionMaterial(Material.PISTON_EXTENSION);
		PistonBaseMaterial pbm = (PistonBaseMaterial) piston.getState().getData();
		pbm.setPowered(true);
		pem.setSticky(pbm.isSticky());
		pem.setFacingDirection(pbm.getFacing());
		
		FAPController.wq.set(piston.getLocation(), pbm.getItemType(), pbm.getData());
		FAPController.wq.set(piston.getLocation().clone().add(face.getModX(), face.getModY(), face.getModZ()), Material.PISTON_EXTENSION, pem.getData());
		FAPController.wq.flush();
		
		new TaskLater(2)
		{
			@Override
			public void run()
			{
				for(Block i : blocks)
				{
					update(i.getLocation().clone().add(face.getModX(), face.getModY(), face.getModZ()).getBlock());
				}
			}
		};
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void on(BlockPistonRetractEvent e)
	{
		if(!FAPController.inst().handlePistons)
		{
			return;
		}
		
		e.setCancelled(true);
		
		new GSound(Sound.PISTON_RETRACT, 1f, 1.5f).play(e.getBlock().getLocation());
		BlockFace face = e.isSticky() ? e.getDirection().getOppositeFace() : e.getDirection();
		GList<Block> blocks = new GList<Block>(e.getBlocks());
		Block piston = e.getBlock();
		PistonBaseMaterial pbm = new PistonBaseMaterial(e.isSticky() ? Material.PISTON_STICKY_BASE : Material.PISTON_BASE);
		pbm.setPowered(false);
		pbm.setFacingDirection(face);
		FAPController.wq.set(piston.getLocation(), pbm.getItemType(), pbm.getData());
		FAPController.wq.set(piston.getLocation().clone().add(face.getModX(), face.getModY(), face.getModZ()), Material.AIR);
		face = face.getOppositeFace();
		
		for(Block i : blocks)
		{
			FAPController.wq.set(i.getLocation(), Material.AIR);
		}
		
		for(Block i : blocks)
		{
			FAPController.wq.set(i.getLocation().clone().add(face.getModX(), face.getModY(), face.getModZ()), i.getType(), i.getData());
		}
		
		FAPController.wq.flush();
		
		if(usePhoton)
		{
			Photon.relight(e.getBlock());
		}
		
		new TaskLater(2)
		{
			@Override
			public void run()
			{
				for(Block i : blocks)
				{
					update(i);
				}
			}
		};
	}
	
	public void push(Block b, BlockFace bf)
	{
		Location l = b.getRelative(bf).getLocation();
		Area a = new Area(l.clone().add(0.5, 0.5, 0.5), 0.9);
		
		for(Entity i : a.getNearbyEntities())
		{
			if(i instanceof LivingEntity)
			{
				i.setVelocity(new Vector(bf.getModX(), bf.getModY(), bf.getModZ()).multiply(0.6));
			}
		}
	}
	
	public void update(Block i)
	{
		if(i.getType().toString().contains("REDSTONE"))
		{
			Blocks.update(i);
		}
		
		boolean b = false;
		
		for(Block j : W.blockFaces(i))
		{
			if(j.isLiquid())
			{
				Blocks.update(j);
				b = true;
				continue;
			}
			
			if(j.getType().hasGravity())
			{
				Blocks.update(j);
				b = true;
				continue;
			}
			
			if(j.getType().toString().contains("REDSTONE"))
			{
				Blocks.update(j);
				b = true;
				continue;
			}
			
			if(j.getType().toString().contains("COMPARATOR"))
			{
				Blocks.update(j);
				b = true;
				continue;
			}
			
			if(j.getType().toString().contains("DIODE"))
			{
				Blocks.update(j);
				b = true;
				continue;
			}
		}
		
		if(b)
		{
			Blocks.update(i);
		}
	}
}