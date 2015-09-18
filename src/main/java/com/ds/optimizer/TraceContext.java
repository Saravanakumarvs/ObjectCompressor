package com.ds.optimizer;

import java.util.IdentityHashMap;
import java.util.Map;

public class TraceContext implements Context
{

	private Map<Object,Object> traceId = new IdentityHashMap<Object,Object>(1<<16);

	@Override
	public boolean contains(Object object)
	{
		return traceId.containsKey(object);
	}

	@Override
	public void add(Object object)
	{
		traceId.put(object,null);
	}

}
