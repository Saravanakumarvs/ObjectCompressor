package com.ds.optimizer;

import java.util.HashSet;
import java.util.Set;

public class TraceContext implements Context
{

	private Set<Integer> traceId = new HashSet<Integer>();

	@Override
	public boolean contains(Object object)
	{
		return traceId.contains(System.identityHashCode(object));
	}

	@Override
	public void add(Object object)
	{
		traceId.add(System.identityHashCode(object));
	}

}
