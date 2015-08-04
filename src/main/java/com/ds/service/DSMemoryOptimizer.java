package com.ds.service;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ds.ExcludeDataTypeOptimizer;
import com.ds.optimizer.Context;
import com.ds.optimizer.DataTypeMemoryOptimizer;
import com.ds.optimizer.DataTypeOptimizer;
import com.ds.optimizer.TraceContext;
import com.ds.util.OptimizerHelper;

public class DSMemoryOptimizer implements DataTypeMemoryOptimizer
{
	private Map<Class<? extends Object>, DataTypeOptimizer> optimizers = new HashMap<Class<? extends Object>, DataTypeOptimizer>();
	private final Logger logger;

	public DSMemoryOptimizer(Map<Class<? extends Object>, DataTypeOptimizer> optimizers, Logger logger)
	{
		this.logger = logger;
		for (DataTypeOptimizer optimizer : optimizers.values())
		{
			optimizer.init(this);
		}
		this.optimizers = optimizers;
	}

	public Object optimize(Object obj)
	{
		TraceContext context = new TraceContext();
		if (null != obj)
		{
			Class<? extends Object> klass = obj.getClass();
			DataTypeOptimizer optimizer = optimizers.get(klass);
			if (null != optimizer)
			{
				obj = optimizer.optimize(null, obj, context);
			}
			else if (null != klass.getCanonicalName())
			{
				optimize(obj, context);
			}
		}
		return obj;

	}

	public void optimize(Object obj, Context context)
	{
		if (needsToSkipObject(obj))
			return;
		context.add(obj);
		Field[] fields = OptimizerHelper.getAllFields(obj);
		for (Field field : fields)
		{
			if (needsToSkipField(field))
				continue;
			try
			{
				optimize(obj, field, context);
			} catch (Throwable t)
			{
				logger.log(Level.SEVERE, "Optimization of field failed :" + field, t);
			}
		}
	}

	private void optimize(Object obj, Field field, Context context) throws IllegalArgumentException, IllegalAccessException
	{
		field.setAccessible(true);
		Object value = field.get(obj);
		if (null != value && false == context.contains(value))
		{
			Class<? extends Object> klass = value.getClass();
			DataTypeOptimizer optimizer = optimizers.get(klass);
			if (null != optimizer)
			{
				Object optimizedValue = optimizer.optimize(field, value, context);
				field.set(obj, optimizedValue);
			}
			else if (null != klass.getCanonicalName())
			{
				optimize(value, context);
			}
		}
	}

	private static boolean needsToSkipField(Field field)
	{
		return Modifier.isFinal(field.getModifiers()) || Modifier.isStatic(field.getModifiers()) || field.isAnnotationPresent(ExcludeDataTypeOptimizer.class);
	}

	private static boolean needsToSkipObject(Object value)
	{
		return null == value || OptimizerHelper.isWrapperType(value)
		// consider enum as build in-type
				|| value instanceof Enum;
	}

}
