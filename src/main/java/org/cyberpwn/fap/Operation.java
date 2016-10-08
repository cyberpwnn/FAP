package org.cyberpwn.fap;

import org.phantomapi.lang.GList;

public interface Operation
{
	public void operate();
	
	public void queue(Operation op);
	
	public GList<Operation> getOperators();
}
