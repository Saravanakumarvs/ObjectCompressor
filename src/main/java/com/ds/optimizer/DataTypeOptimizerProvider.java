package com.ds.optimizer;

import java.util.Map;

public interface DataTypeOptimizerProvider
{

	Map<Class<? extends Object>, DataTypeOptimizer> getDataTypeOptimizers();

}
