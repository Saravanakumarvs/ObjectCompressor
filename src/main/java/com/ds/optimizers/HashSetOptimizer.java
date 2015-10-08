package com.ds.optimizers;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.ds.optimizer.Context;
import com.ds.optimizer.DataTypeMemoryOptimizer;
import com.ds.optimizer.DataTypeOptimizer;
import com.ds.util.OptimizerHelper;

public class HashSetOptimizer implements DataTypeOptimizer
{
	private Map<String, Set<Object>> SET_POOL = new ConcurrentHashMap<String, Set<Object>>();
	private DataTypeMemoryOptimizer memoryOptimizer;

	public Object optimize(Field field, Object value, Context context)
	{
		Set<Object> set = (Set<Object>) value;
		if (0 == set.size())
		{
			// if map is not having data then get it from pool.
			String fieldName = OptimizerHelper.getFieldName(field);
			Set<Object> cachedSet = SET_POOL.get(fieldName);
			if (null == cachedSet)
			{
				cachedSet = set;
				cachedSet.clear();
				SET_POOL.put(fieldName, cachedSet);
			}
			set = cachedSet;
		}
		else
		{
			// optimize individual key and its value
			//do not optimize the root of key it may affect the hashcode.
			for (Object obj : set)
			{
				memoryOptimizer.optimize(obj, context);
			}
		}
		return set;
	}

	@Override
	public void init(DataTypeMemoryOptimizer memoryOptimizer)
	{
		this.memoryOptimizer = memoryOptimizer;
	}
}
