package com.ds.optimizer;

import com.ds.MemoryOptimizer;

public interface DataTypeMemoryOptimizer extends MemoryOptimizer
{
	Object optimize(Object obj, Context context);
}
