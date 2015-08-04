package com.ds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.ds.optimizer.DataTypeOptimizer;
import com.ds.optimizer.DataTypeOptimizerProvider;
import com.ds.service.DSMemoryOptimizer;

public class MemoryOptimizerBuilder
{

	private Map<Class<? extends Object>, DataTypeOptimizer> optimizers = new HashMap<Class<? extends Object>, DataTypeOptimizer>();
	private List<DataTypeOptimizerProvider> optimizerProviders = new ArrayList<DataTypeOptimizerProvider>();

	private Logger logger = Logger.getLogger("MemoryOptimizer");

	public void setLogger(Logger logger)
	{
		this.logger = logger;
	}

	/**
	 * @param dataType
	 *            field data type
	 * @param optimizer
	 *            optimizer for the field
	 * @return
	 */
	public MemoryOptimizerBuilder addOptimizer(Class<?> dataType, DataTypeOptimizer optimizer)
	{
		optimizers.put(dataType, optimizer);
		return this;
	}

	public MemoryOptimizer build()
	{
		Map<Class<? extends Object>, DataTypeOptimizer> finalOptimizers = new HashMap<Class<? extends Object>, DataTypeOptimizer>();
		for (DataTypeOptimizerProvider dataTypeOptimizerProvider : optimizerProviders)
		{
			finalOptimizers.putAll(dataTypeOptimizerProvider.getDataTypeOptimizers());
		}

		// override with individual optimizer
		finalOptimizers.putAll(optimizers);

		DSMemoryOptimizer dsMemoryOptimizer = new DSMemoryOptimizer(finalOptimizers, logger);
		return dsMemoryOptimizer;
	}

	public void addOptimizerProvider(DataTypeOptimizerProvider dataTypeOptimizerProvider)
	{
		optimizerProviders.add(dataTypeOptimizerProvider);
	}
}
