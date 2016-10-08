package org.cyberpwn.fap;

import org.phantomapi.clust.ConfigurableController;

public class FAPHandler extends ConfigurableController implements Handler
{
	public FAPHandler(String codeName)
	{
		super(FAPController.inst(), codeName);
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
	public void queue(Operation op)
	{
		FAPController.inst().queueOperation(op);
	}
}
