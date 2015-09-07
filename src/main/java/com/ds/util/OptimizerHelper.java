package com.ds.util;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public abstract class OptimizerHelper
{

	// can be static, As class can not be redefined.
	private static Map<Class<? extends Object>, Field[]> fieldCache = new ConcurrentHashMap<Class<? extends Object>, Field[]>();

	// return generic name of the based on field type
	public static String getFieldName(Field field)
	{
		StringBuilder genericName = new StringBuilder("");
		// if its generic validate for parameterized type and form real field
		// name.
		if (null != field && field.getGenericType() instanceof ParameterizedType)
		{
			Type genericType = field.getGenericType();
			ParameterizedType parameterizedType = (ParameterizedType) genericType;
			for (Type type : parameterizedType.getActualTypeArguments())
			{
				genericName.append(type);
			}
		}
		return genericName.toString();
	}

	private static final Set<Class<?>> WRAPPED_TYPES = new HashSet<Class<?>>();
	static
	{
		WRAPPED_TYPES.add(Boolean.class);
		WRAPPED_TYPES.add(Character.class);
		WRAPPED_TYPES.add(Byte.class);
		WRAPPED_TYPES.add(Short.class);
		WRAPPED_TYPES.add(Integer.class);
		WRAPPED_TYPES.add(Long.class);
		WRAPPED_TYPES.add(Float.class);
		WRAPPED_TYPES.add(Double.class);
		WRAPPED_TYPES.add(Void.class);
	}

	public static boolean isWrapperType(Object value)
	{
		return WRAPPED_TYPES.contains(value);
	}

	// get all fields of a class by navigating through cross hierarchy.
	public static Field[] getAllFields(Object obj)
	{
		Field[] fields = fieldCache.get(obj.getClass());
		if (null == fields)
		{
			synchronized (OptimizerHelper.class)
			{
				fields = fieldCache.get(obj.getClass());
				if (null == fields)
				{
					List<Field> fieldList = new ArrayList<Field>();
					for (Class<? extends Object> klass = obj.getClass(); null != klass; klass = klass.getSuperclass())
					{
						fieldList.addAll(Arrays.asList(klass.getDeclaredFields()));
					}
					fields = fieldList.toArray(new Field[fieldList.size()]);
					fieldCache.put(obj.getClass(), fields);
				}
			}
		}
		return fields;
	}
}
