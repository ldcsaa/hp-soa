package com.lcb.hapi.demo.test;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import com.lcb.soa.misc.common.thread.TracingRecursiveTask;
import com.lcb.soa.misc.common.util.GeneralHelper;
import com.lcb.soa.misc.server.spring.main.AppStarter;

import static com.lcb.soa.misc.common.helper.CommonHelper.printStep;
import static com.lcb.soa.misc.common.helper.CommonHelper.printlnf;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AppStarter.class, webEnvironment = WebEnvironment.DEFINED_PORT)
public class TestThreadPool {
    @BeforeClass
    public static void beforeClass() throws Exception {

    }

    @AfterClass
    public static void afterClass() {
        GeneralHelper.waitFor(1, TimeUnit.SECONDS);
        printlnf("bye ~ bye ~");
    }

    @Test
    public void step_1() throws Exception {
        printStep(1, true);

        testForkJoin();

        printStep(1, false);
    }

	private void testForkJoin()
	{
		ForkJoinPool pool = new ForkJoinPool(20);
		
		MyTask task = new MyTask(1000);
		
		Long rs = pool.invoke(task);
		
		System.out.println(rs);
	}

	@SuppressWarnings("serial")
	private static class MyTask extends TracingRecursiveTask<Long>
	{
		private static AtomicInteger ID_GEN = new AtomicInteger(0);
		
		private long from;
		private long to;
		
		private int id;
		
		public MyTask(long size)
		{
			this(0, size);
		}
		
		public MyTask(long from, long to)
		{
			this.id = ID_GEN.incrementAndGet();
			
			this.from = from;
			this.to = to;
		}
		
		@Override
		protected Long doCompute()
		{
			long size = to - from;
			
			if(size <= 10)
			{
				long val = 0;
				for(long i = from; i <= to; i++)
					val += i;
				
				System.out.printf("(-%03d) %s : from - %d, to - %d, rs - %d\n", id, Thread.currentThread().getName(), from, to, val);
				
				return val;
			}
			else
			{
				System.out.printf("(+%03d) %s : from - %d, to - %d\n", id, Thread.currentThread().getName(), from, to);

				long mid = (from + to) / 2;

				MyTask left = new MyTask(from, mid);
				MyTask right = new MyTask(mid + 1, to);
				
				left.fork();
				
				long rval = right.doCompute();
				long lval = left.join();
				
				return lval + rval;
			}
		}
		
	}
}
