# Memory optimization in Java


Memory usage optimization is the most important type of optimization in Java. Current systems are limited by memory access times rather than by CPU frequency. It means that by reducing your application memory footprint you will most likely improve your program data processing speed by making your CPU to wait for smaller amount of data. In Server scale applications cache while utilize most of the memory. There are many cache elimination techniques available to release the memory and it come with its own cost. The object which is present in the cache generally reserve volume of memory for its feature expansion.

For optimizing memory we use below techniques:

 * Use primitive data type
 * Avoid using java collection f/w and use trove collection


	When i try to optimize memory also. i did the above approach. Problem with this approach is, we needs to modify the complete code base. If we use any third party classes we will hit a road block. 

	When i analyzed my heap dump for the cached objects it has below issues. And it's from a 3rd party objects.

* Under utilization of memory by collection framework
	* Collection has reserved 16 buckets and used only a single bucket.
* Empty Collections
	* Though these collections are empty, it has reserved space for feature use.
	

I have decided to release the unused memory and also apply fly weight over the empty collections for read only objects. After writting this f/w for optimizing memory server cache size is reduced by 80% :) 

And here's the test code! :+1:

```java
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
		long heapSize = runtime.totalMemory() - runtime.freeMemory();
		System.gc();
		TimeUnit.SECONDS.sleep(15);
		nanoTime = System.nanoTime();



		MemoryOptimizerBuilder optimizerBuilder = new MemoryOptimizerBuilder();
		optimizerBuilder.addOptimizerProvider(new DefaultDataTypeOptimizerProvider());
		MemoryOptimizer memoryOptimizer = optimizerBuilder.build();
		for (Object object : list)
		{
			memoryOptimizer.optimize(object);
		}


		System.out.println(TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - nanoTime));
		System.gc();
		TimeUnit.SECONDS.sleep(15);
		assertTrue((runtime.totalMemory() - runtime.freeMemory()) < (heapSize) / 3);
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
```


##### My cache object has read attributes and read/write attributes, ExcludeDataTypeOptimizer annotation has been applied for the read/write attributes.


