package com.ds.optimizer;

import java.lang.reflect.Field;

public interface DataTypeOptimizer {

	/**
	 * @param field
	 *            field contained object
	 * @param value
	 *            to be optimized
	 * @param context
	 *            optimization context
	 * @return optimized value
	 */
	Object optimize(Field field, Object value, Context context);

	void init(DataTypeMemoryOptimizer memoryOptimizer);

}
