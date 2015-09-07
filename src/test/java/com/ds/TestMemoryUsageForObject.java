package com.ds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

import com.ds.optimizers.DefaultDataTypeOptimizerProvider;

public class TestMemoryUsageForObject extends TestCase
{

	List list = new ArrayList();

	public void testCheckForMemoryUsage() throws InterruptedException
	{
		long nanoTime = System.nanoTime();
		for (int i = 0; i < 10000000; i++)
		{
			list.add(new Parent());
		}
		System.out.print("Object created in ");
		System.out.println(TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - nanoTime));
		Runtime runtime = Runtime.getRuntime();
		long heapSize = runtime.totalMemory() - runtime.freeMemory();
		System.gc();
		TimeUnit.SECONDS.sleep(5);
		nanoTime = System.nanoTime();
		System.out.println("Optimizing");
		MemoryOptimizerBuilder optimizerBuilder = new MemoryOptimizerBuilder();
		optimizerBuilder.addOptimizerProvider(new DefaultDataTypeOptimizerProvider());
		MemoryOptimizer memoryOptimizer = optimizerBuilder.build();
		memoryOptimizer.optimize(list);
		System.out.println(TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - nanoTime));
		System.out.println("Optimizion completed");
		System.gc();
		TimeUnit.SECONDS.sleep(5);
		System.out.println(runtime.totalMemory() + " : " + heapSize);
		assertTrue((runtime.totalMemory() - runtime.freeMemory()) < (heapSize) / 2);
	}

	class Parent
	{
		Sample s = new Sample();
	}

	class Sample
	{
		List<Integer> list = new ArrayList<Integer>();
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
	}

}
