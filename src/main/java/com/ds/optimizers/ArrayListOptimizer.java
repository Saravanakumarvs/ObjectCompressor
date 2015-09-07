package com.ds.optimizers;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.ds.optimizer.Context;
import com.ds.optimizer.DataTypeMemoryOptimizer;
import com.ds.optimizer.DataTypeOptimizer;
import com.ds.util.OptimizerHelper;

public class ArrayListOptimizer implements DataTypeOptimizer
{
	private Map<String, Object> ARRAY_LIST_POOL = new ConcurrentHashMap<String, Object>();
	private DataTypeMemoryOptimizer memoryOptimizer;

	public Object optimize(Field field, Object value, Context context)
	{
		ArrayList<Object> list = (ArrayList<Object>) value;
		int size = list.size();
		if (0 == size)
		{
			// if no data, return the cached empty list shared list
			return getCachedArrayList(field, list);
		}
		else
		{
			// remote unused array elements
			list.trimToSize();
			for (int i = 0; i < size; i++)
			{
				// optimize individual data
				list.set(i,memoryOptimizer.optimize(list.get(i), context));
			}
		}
		return list;
	}

	private Object getCachedArrayList(Field field, ArrayList<?> list)
	{
		String fieldName = OptimizerHelper.getFieldName(field);
		Object object = ARRAY_LIST_POOL.get(fieldName);
		if (null == object)
		{
			// prepare list and add to cache
			list.trimToSize();
			object = list;
			ARRAY_LIST_POOL.put(fieldName, object);
		}
		return object;
	}

	@Override
	public void init(DataTypeMemoryOptimizer memoryOptimizer)
	{
		this.memoryOptimizer = memoryOptimizer;
	}

}
