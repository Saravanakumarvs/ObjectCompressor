package com.ds;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.junit.Test;

import com.ds.optimizers.DefaultDataTypeOptimizerProvider;

public class TestHashMapMemoryOptimization extends TestCase {

	@Test
	public void testCheckForResusingOfEmptyHashMap() {
		MemoryOptimizerBuilder optimizerBuilder = new MemoryOptimizerBuilder();
		optimizerBuilder
				.addOptimizerProvider(new DefaultDataTypeOptimizerProvider());
		MemoryOptimizer memoryOptimizer = optimizerBuilder.build();
		A a = new A();
		a.map = new HashMap();
		B b = new B();
		b.map = new HashMap();
		memoryOptimizer.optimize(a);
		memoryOptimizer.optimize(b);
		assertSame(a.map, b.map);
	}

	@Test
	public void testCheckForNotResusingHashMapHavingData() {
		MemoryOptimizerBuilder optimizerBuilder = new MemoryOptimizerBuilder();
		optimizerBuilder
				.addOptimizerProvider(new DefaultDataTypeOptimizerProvider());
		MemoryOptimizer memoryOptimizer = optimizerBuilder.build();
		A a = new A();
		a.map = new HashMap();
		B b = new B();
		b.map = new HashMap();
		b.map.put(1,1);
		memoryOptimizer.optimize(a);
		memoryOptimizer.optimize(b);
		assertNotSame(a.map, b.map);
	}

	@Test
	public void testCheckForNotResusingHashMapHavingDifferentType() {
		MemoryOptimizerBuilder optimizerBuilder = new MemoryOptimizerBuilder();
		optimizerBuilder
				.addOptimizerProvider(new DefaultDataTypeOptimizerProvider());
		MemoryOptimizer memoryOptimizer = optimizerBuilder.build();
		C c = new C();
		c.map = new HashMap<Integer,Integer>();
		D d = new D();
		d.map = new HashMap<Double,Double>();
		memoryOptimizer.optimize(c);
		memoryOptimizer.optimize(d);
		assertNotSame(c.map, d.map);
	}

	@Test
	public void testCheckForResusingHashMapHavingDifferentTypeGeneric() {
		MemoryOptimizerBuilder optimizerBuilder = new MemoryOptimizerBuilder();
		optimizerBuilder
				.addOptimizerProvider(new DefaultDataTypeOptimizerProvider());
		MemoryOptimizer memoryOptimizer = optimizerBuilder.build();
		G<Integer> g1 = new G<Integer>();
		g1.map = new HashMap<Integer,Integer>();
		G<Double> g2 = new G<Double>();
		g2.map = new HashMap<Double,Double>();
		memoryOptimizer.optimize(g1);
		memoryOptimizer.optimize(g2);
		assertSame(g1.map, g2.map);
	}

	class A {
		Map map;
	}

	class B {
		Map map;
	}

	class C {
		Map<Integer,Integer> map;
	}

	class D {
		Map<Double,Double> map;
	}

	class G<T> {
		Map<T,T> map;
	}

}
