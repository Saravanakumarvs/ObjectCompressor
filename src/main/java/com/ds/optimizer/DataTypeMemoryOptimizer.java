package com.ds.optimizer;

import com.ds.MemoryOptimizer;

public interface DataTypeMemoryOptimizer extends MemoryOptimizer
{
	public void optimize(Object obj, Context context);
}
