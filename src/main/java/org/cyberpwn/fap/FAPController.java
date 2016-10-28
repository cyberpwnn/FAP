package org.cyberpwn.fap;

import org.cyberpwn.fap.handler.PistonHandler;
import org.cyberpwn.fap.handler.TNTHandler;
import org.phantomapi.async.A;
import org.phantomapi.clust.AsyncConfig;
import org.phantomapi.clust.Comment;
import org.phantomapi.clust.ConfigurableController;
import org.phantomapi.clust.Keyed;
import org.phantomapi.construct.Controllable;
import org.phantomapi.construct.Ticked;
import org.phantomapi.lang.GList;
import org.phantomapi.world.PhantomWorldQueue;

@Ticked(5)
@AsyncConfig
public class FAPController extends ConfigurableController
{
	@Comment("Use more than one thread to maximize work")
	@Keyed("fap.max-threads")
	public int threads = 3;
	
	@Comment("Should tnt be handled with the tnt handler? Set to false for bukkit.")
	@Keyed("fap.feature.tnt")
	public boolean handleTNT = true;
	
	@Comment("Should fire be handled with the fire handler? Set to false for bukkit.")
	@Keyed("fap.feature.fire")
	public boolean handleFire = true;
	
	@Comment("Should pistons be handled with the piston handler? Set to false for bukkit.")
	@Keyed("fap.feature.pistons")
	public boolean handlePistons = true;
	
	public static PhantomWorldQueue wq;
	private GList<Operation> queued;
	private static FAPController inst;
	private TNTHandler tntHandler;
	private PistonHandler pistonHandler;
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
		
		register(tntHandler);
		register(pistonHandler);
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
