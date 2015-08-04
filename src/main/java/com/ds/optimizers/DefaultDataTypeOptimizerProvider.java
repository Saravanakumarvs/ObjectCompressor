package com.ds.optimizers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.ds.optimizer.DataTypeOptimizer;
import com.ds.optimizer.DataTypeOptimizerProvider;

public class DefaultDataTypeOptimizerProvider implements
		DataTypeOptimizerProvider {

	@Override
	public Map<Class<? extends Object>, DataTypeOptimizer> getDataTypeOptimizers() {
		Map<Class<? extends Object>, DataTypeOptimizer> optimizers = new HashMap<Class<? extends Object>, DataTypeOptimizer>();
		// default optimizers
		optimizers.put(ArrayList.class, new ArrayListOptimizer());
		optimizers.put(HashMap.class, new HashMapOptimizer());
		optimizers.put(ConcurrentHashMap.class, new HashMapOptimizer());
		// Optimizer stringOptimizer = (field, object, trace) -> {
		// return ((String) object).intern();
		// };
		// optimizers.put(String.class, stringOptimizer);
		// optimizers.put(String.class, new StringOptimizer());
		return optimizers;
	}

}
