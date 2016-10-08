package org.cyberpwn.fap;

import org.phantomapi.construct.Ghost;

public class FAP extends Ghost
{
	private FAPController fapController;
	
	@Override
	public void preStart()
	{
		fapController = new FAPController(this);
		
		register(fapController);
	}
	
	@Override
	public void onStart()
	{
		
	}

	@Override
	public void onStop()
	{
		
	}

	@Override
	public void postStop()
	{
		
	}
}
