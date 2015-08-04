package com.ds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.ds.optimizers.DefaultDataTypeOptimizerProvider;

import junit.framework.TestCase;

public class TestMemoryUsageForObject extends TestCase {

	List list = new ArrayList();

	public void testCheckForMemoryUsage() throws InterruptedException {
		long nanoTime = System.nanoTime();
		for (int i = 0; i < 10000000; i++) {
			list.add(new Parent());
		}
		System.out.println("Object created");
		System.out.println(TimeUnit.NANOSECONDS.toMillis(System.nanoTime()
				- nanoTime));
		Runtime runtime = Runtime.getRuntime();
		long heapSize = runtime.totalMemory() - runtime.freeMemory();
		System.gc();
		TimeUnit.SECONDS.sleep(15);
		nanoTime = System.nanoTime();
		System.out.println("Optimizing");
		MemoryOptimizerBuilder optimizerBuilder = new MemoryOptimizerBuilder();
		optimizerBuilder
				.addOptimizerProvider(new DefaultDataTypeOptimizerProvider());
		MemoryOptimizer memoryOptimizer = optimizerBuilder.build();
		for (Object object : list) {
			memoryOptimizer.optimize(object);
		}
		System.out.println(TimeUnit.NANOSECONDS.toMillis(System.nanoTime()
				- nanoTime));
		System.out.println("Optimizen completed");
		System.gc();
		TimeUnit.SECONDS.sleep(15);
		System.out.println(runtime.totalMemory() + " : " + heapSize);
		assertTrue((runtime.totalMemory() - runtime.freeMemory()) < (heapSize) / 2);
	}

	class Parent {
		Sample s = new Sample();
	}

	class Sample {
		List<Integer> list = new ArrayList<Integer>();
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
	}

}
