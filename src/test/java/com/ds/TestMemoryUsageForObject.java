package com.ds;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.TestCase;

import com.ds.optimizers.DefaultDataTypeOptimizerProvider;

public class TestMemoryUsageForObject extends TestCase
{

	private static final int threadCount = 4;
	private static final int size = threadCount * 100000;
	List list = new ArrayList(size);

	public void testCheckForMemoryUsage() throws Exception
	{
		Field field = ArrayList.class.getDeclaredField("size");
		field.setAccessible(true);
		field.setInt(list, size);
		ExecutorService service = Executors.newFixedThreadPool(5);
		long startTime = System.nanoTime();
		final int count = size / threadCount;
		final AtomicInteger integer = new AtomicInteger();
		final CountDownLatch latch = new CountDownLatch(threadCount);
		Runnable runnable = new Runnable()
		{
			public void run()
			{
				try
				{
					for (int i = count * integer.getAndIncrement(), j = 0; j < count; j++, i++)
					{
						list.set(i, new Parent());
					}
				} catch (Throwable e)
				{
					e.printStackTrace();
				} finally
				{
					latch.countDown();
				}
			}
		};
		for (int i = 0; i < threadCount; i++)
			service.submit(runnable);
		latch.await();
		service.shutdownNow();
		System.out.println("Total Objects created : " + list.size());
		System.out.print("Object created in ");
		System.out.println(TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime));
		Runtime runtime = Runtime.getRuntime();
		long heapSize = runtime.totalMemory() - runtime.freeMemory();
		System.gc();
		TimeUnit.SECONDS.sleep(1);
		System.out.println("Optimizing");
		MemoryOptimizerBuilder optimizerBuilder = new MemoryOptimizerBuilder();
		optimizerBuilder.addOptimizerProvider(new DefaultDataTypeOptimizerProvider());
		MemoryOptimizer memoryOptimizer = optimizerBuilder.build();
		startTime = System.nanoTime();
		memoryOptimizer.optimize(list);
		System.out.println(TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime));
		System.out.println("Optimizion completed");
		System.gc();
		TimeUnit.SECONDS.sleep(10);
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
