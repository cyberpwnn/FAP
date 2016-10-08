package org.cyberpwn.fap.operation;

import org.cyberpwn.fap.FAPOperator;
import org.cyberpwn.fap.Operation;
import org.phantomapi.lang.GList;

public class OperationSupertick extends FAPOperator
{
	private GList<Operation> operators;
	
	public OperationSupertick()
	{
		this.operators = new GList<Operation>();
	}
	
	@Override
	public void operate()
	{
		for(Operation i : operators)
		{
			i.operate();
			
			for(Operation j : i.getOperators())
			{
				queue(j);
			}
		}
	}
	
	public void add(Operation op)
	{
		operators.add(op);
	}
}
