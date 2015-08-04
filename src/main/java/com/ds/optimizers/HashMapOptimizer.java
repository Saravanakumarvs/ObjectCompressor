package com.ds.optimizers;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.ds.optimizer.Context;
import com.ds.optimizer.DataTypeMemoryOptimizer;
import com.ds.optimizer.DataTypeOptimizer;
import com.ds.util.OptimizerHelper;

public class HashMapOptimizer implements DataTypeOptimizer {
	private Map<String, Map<?, ?>> MAP_POOL = new ConcurrentHashMap<String, Map<?, ?>>();
	private DataTypeMemoryOptimizer memoryOptimizer;

	public Object optimize(Field field, Object value, Context context) {
		Map<?, ?> map = (Map<?, ?>) value;
		if (0 == map.size()) {
			// if map is not having data then get it from pool.
			String fieldName = OptimizerHelper.getFieldName(field);
			Map<?, ?> cachedMap = MAP_POOL.get(fieldName);
			if (null == cachedMap) {
				cachedMap = map;
				cachedMap.clear();
				MAP_POOL.put(fieldName, cachedMap);
			}
			map = cachedMap;
		} else {
			// optimize individual key and its value
			for (Object obj : map.values()) {
				memoryOptimizer.optimize(obj, context);
			}
			for (Object obj : map.keySet()) {
				memoryOptimizer.optimize(obj, context);
			}
		}
		return map;
	}

	@Override
	public void init(DataTypeMemoryOptimizer memoryOptimizer) {
		this.memoryOptimizer = memoryOptimizer;
	}
}
