package com.ds.optimizers;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.ds.optimizer.Context;
import com.ds.optimizer.DataTypeMemoryOptimizer;
import com.ds.optimizer.DataTypeOptimizer;
import com.ds.util.OptimizerHelper;

public class HashMapOptimizer implements DataTypeOptimizer
{
	private Map<String, Map<Object,Object>> MAP_POOL = new ConcurrentHashMap<String, Map<Object,Object>>();
	private DataTypeMemoryOptimizer memoryOptimizer;

	public Object optimize(Field field, Object value, Context context)
	{
		Map<Object,Object> map = (Map<Object,Object>) value;
		if (0 == map.size())
		{
			// if map is not having data then get it from pool.
			String fieldName = OptimizerHelper.getFieldName(field);
			Map<Object,Object> cachedMap = MAP_POOL.get(fieldName);
			if (null == cachedMap)
			{
				cachedMap = map;
				cachedMap.clear();
				MAP_POOL.put(fieldName, cachedMap);
			}
			map = cachedMap;
		}
		else
		{
			Set<Entry<Object,Object>> entrySet = map.entrySet();
			// optimize individual key and its value
			for (Entry<Object,Object> entry : entrySet)
			{
				Object object = entry.getValue();
				entry.setValue(memoryOptimizer.optimize(object, context));
			}
			//do not optimize the root of key it may affect the hashcode.
			for (Object obj : map.keySet())
			{
				memoryOptimizer.optimize(obj, context);
			}
		}
		return map;
	}

	@Override
	public void init(DataTypeMemoryOptimizer memoryOptimizer)
	{
		this.memoryOptimizer = memoryOptimizer;
	}
}
