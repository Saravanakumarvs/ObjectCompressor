package com.ds;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

import com.ds.optimizers.DefaultDataTypeOptimizerProvider;

public class TestArrayListMemoryOptimization extends TestCase
{

	@Test
	public void testCheckForResusingOfEmptyArrayList()
	{
		MemoryOptimizerBuilder optimizerBuilder = new MemoryOptimizerBuilder();
		optimizerBuilder.addOptimizerProvider(new DefaultDataTypeOptimizerProvider());
		MemoryOptimizer memoryOptimizer = optimizerBuilder.build();
		A a = new A();
		a.list = new ArrayList();
		B b = new B();
		b.list = new ArrayList();
		memoryOptimizer.optimize(a);
		memoryOptimizer.optimize(b);
		assertSame(a.list, b.list);
	}
	@Test
	public void testCheckForResusingOfEmptyArrayListInArrayList()
	{
		MemoryOptimizerBuilder optimizerBuilder = new MemoryOptimizerBuilder();
		optimizerBuilder.addOptimizerProvider(new DefaultDataTypeOptimizerProvider());
		MemoryOptimizer memoryOptimizer = optimizerBuilder.build();
		ArrayList list = new ArrayList();
		list.add(new ArrayList());
		list.add(new ArrayList());
		memoryOptimizer.optimize(list);
		assertSame(list.get(0), list.get(1));
	}

	@Test
	public void testCheckForNotResusingArrayListHavingData()
	{
		MemoryOptimizerBuilder optimizerBuilder = new MemoryOptimizerBuilder();
		optimizerBuilder.addOptimizerProvider(new DefaultDataTypeOptimizerProvider());
		MemoryOptimizer memoryOptimizer = optimizerBuilder.build();
		A a = new A();
		a.list = new ArrayList();
		B b = new B();
		b.list = new ArrayList();
		b.list.add(1);
		memoryOptimizer.optimize(a);
		memoryOptimizer.optimize(b);
		assertNotSame(a.list, b.list);
	}

	@Test
	public void testCheckForReleasingUnusedSpaceInArrayList() throws Exception
	{
		MemoryOptimizerBuilder optimizerBuilder = new MemoryOptimizerBuilder();
		optimizerBuilder.addOptimizerProvider(new DefaultDataTypeOptimizerProvider());
		MemoryOptimizer memoryOptimizer = optimizerBuilder.build();
		A a = new A();
		a.list = new ArrayList(16);
		Field declaredField = ArrayList.class.getDeclaredField("elementData");
		declaredField.setAccessible(true);
		Object object = declaredField.get(a.list);
		assertEquals(16, ((Object[]) object).length);
		memoryOptimizer.optimize(a);
		object = declaredField.get(a.list);
		assertEquals(0, ((Object[]) object).length);
	}

	@Test
	public void testCheckForNotResusingArrayListHavingDifferentType()
	{
		MemoryOptimizerBuilder optimizerBuilder = new MemoryOptimizerBuilder();
		optimizerBuilder.addOptimizerProvider(new DefaultDataTypeOptimizerProvider());
		MemoryOptimizer memoryOptimizer = optimizerBuilder.build();
		C c = new C();
		c.list = new ArrayList<Integer>();
		D d = new D();
		d.list = new ArrayList<Double>();
		memoryOptimizer.optimize(c);
		memoryOptimizer.optimize(d);
		assertNotSame(c.list, d.list);
	}

	@Test
	public void testCheckForResusingArrayListHavingDifferentTypeGeneric()
	{
		MemoryOptimizerBuilder optimizerBuilder = new MemoryOptimizerBuilder();
		optimizerBuilder.addOptimizerProvider(new DefaultDataTypeOptimizerProvider());
		MemoryOptimizer memoryOptimizer = optimizerBuilder.build();
		G<Integer> g1 = new G<Integer>();
		g1.list = new ArrayList<Integer>();
		G<Double> g2 = new G<Double>();
		g2.list = new ArrayList<Double>();
		memoryOptimizer.optimize(g1);
		memoryOptimizer.optimize(g2);
		assertSame(g1.list, g2.list);
	}

	class A
	{
		List list;
	}

	class B
	{
		List list;
	}

	class C
	{
		List<Integer> list;
	}

	class D
	{
		List<Double> list;
	}

	class G<T>
	{
		List<T> list;
	}

}
