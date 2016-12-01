package org.cyberpwn.fap;

import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.cyberpwn.fap.handler.HopperHandler;
import org.cyberpwn.fap.handler.PistonHandler;
import org.cyberpwn.fap.handler.TNTHandler;
import org.phantomapi.async.A;
import org.phantomapi.clust.AsyncConfig;
import org.phantomapi.clust.Comment;
import org.phantomapi.clust.ConfigurableController;
import org.phantomapi.clust.Keyed;
import org.phantomapi.construct.Controllable;
import org.phantomapi.construct.Ticked;
import org.phantomapi.event.PlayerMoveChunkEvent;
import org.phantomapi.lang.GList;
import org.phantomapi.sync.TaskLater;
import org.phantomapi.util.M;
import org.phantomapi.world.PhantomWorldQueue;
import org.phantomapi.world.Photon;
import org.phantomapi.world.W;

@Ticked(20)
@AsyncConfig
public class FAPController extends ConfigurableController
{
	private long lrl;
	
	@Comment("Use more than one thread to maximize work")
	@Keyed("fap.max-threads")
	public int threads = 3;
	
	@Comment("Should tnt be handled with the tnt handler? Set to false for bukkit.")
	@Keyed("fap.feature.tnt")
	public boolean handleTNT = true;
	
	@Comment("Should pistons be handled with the piston handler? Set to false for bukkit.")
	@Keyed("fap.feature.pistons")
	public boolean handlePistons = true;
	
	@Comment("Should FAP attempt to fix missing chunks?")
	@Keyed("fap.feature.fix-missing-chunks")
	public boolean fixMissingChunks = true;
	
	@Comment("How long should a player have to stay in the same chunk to\ncause fap to update their view and fix missing chunks.")
	@Keyed("fap.feature.wait-missing-chunk-ticks")
	public int waitTicks = 150;
	
	public static PhantomWorldQueue wq;
	private GList<Operation> queued;
	private static FAPController inst;
	private TNTHandler tntHandler;
	private PistonHandler pistonHandler;
	private HopperHandler hopperHandler;
	private boolean running;
	
	public FAPController(Controllable parentController)
	{
		super(parentController, "config");
		
		wq = new PhantomWorldQueue();
		queued = new GList<Operation>();
		inst = this;
		running = false;
		
		tntHandler = new TNTHandler();
		pistonHandler = new PistonHandler();
		hopperHandler = new HopperHandler();
		lrl = M.ms();
		
		register(tntHandler);
		register(pistonHandler);
		register(hopperHandler);
	}
	
	public void queueOperation(Operation operation)
	{
		queued.add(operation);
	}
	
	@Override
	public void onReadConfig()
	{
		
	}
	
	@Override
	public void onStart()
	{
		loadCluster(this);
	}
	
	@Override
	public void onStop()
	{
		
	}
	
	@EventHandler
	public void on(PlayerMoveChunkEvent e)
	{
		if(!fixMissingChunks)
		{
			return;
		}
		
		Chunk c = e.getToChunk();
		
		new TaskLater(waitTicks)
		{
			@Override
			public void run()
			{
				if(c.equals(e.getPlayer().getLocation().getChunk()))
				{
					for(Chunk i : W.chunkRadius(e.getToChunk(), 3))
					{
						Photon.relight(i);
					}
				}
			}
		};
	}
	
	@EventHandler
	public void on(PlayerTeleportEvent e)
	{
		if(!fixMissingChunks)
		{
			return;
		}
		
		if(M.ms() - lrl < 200)
		{
			return;
		}
		
		lrl = M.ms();
		
		Photon.relight(e.getTo());
	}
	
	@Override
	public void onTick()
	{
		wq.flush();
		
		if(!queued.isEmpty() && !running)
		{
			running = true;
			GList<Operation> o = queued.copy();
			queued.clear();
			
			new A()
			{
				@Override
				public void async()
				{
					for(Operation i : o)
					{
						i.operate();
					}
					
					running = false;
				}
			};
		}
	}
	
	public static FAPController inst()
	{
		return inst;
	}
}
