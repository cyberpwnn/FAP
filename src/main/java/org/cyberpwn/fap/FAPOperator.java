package org.cyberpwn.fap;

import org.phantomapi.lang.GList;

public abstract class FAPOperator implements Operation
{
	private final GList<Operation> operators;
	
	public FAPOperator()
	{
		this.operators = new GList<Operation>();
	}
	
	@Override
	public abstract void operate();

	@Override
	public void queue(Operation op)
	{
		operators.add(op);
	}

	@Override
	public GList<Operation> getOperators()
	{
		return operators;
	}
}
