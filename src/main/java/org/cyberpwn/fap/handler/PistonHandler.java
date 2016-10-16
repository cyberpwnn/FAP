package org.cyberpwn.fap.handler;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.material.PistonBaseMaterial;
import org.bukkit.material.PistonExtensionMaterial;
import org.cyberpwn.fap.FAPController;
import org.cyberpwn.fap.FAPHandler;
import org.phantomapi.lang.GList;
import org.phantomapi.lang.GSound;
import org.phantomapi.statistics.Monitorable;
import org.phantomapi.world.Photon;

public class PistonHandler extends FAPHandler implements Monitorable
{
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
		e.setCancelled(true);
		
		new GSound(Sound.PISTON_EXTEND, 1f, 1.5f).play(e.getBlock().getLocation());
		BlockFace face = e.getDirection();
		GList<Block> blocks = new GList<Block>(e.getBlocks());
		Block piston = e.getBlock();
		
		for(Block i : blocks)
		{
			FAPController.wq.set(i.getLocation(), Material.AIR);
		}
		
		for(Block i : blocks)
		{
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
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void on(BlockPistonRetractEvent e)
	{
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
		Photon.relight(e.getBlock());
	}
}
