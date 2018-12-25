package ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.state;

import junit.framework.Assert;

import org.junit.Test;

public class LimitedQueueTest {
	
	@Test
	public void test() {
		for (int limit = 1; limit < 10; limit++) {
			LimitedQueue q = new LimitedQueue(limit);
			double sum = 0;
			
			for (int i = 0; i < limit; i++) {
				Assert.assertEquals(i, q.size());
				q.add((double) i);
				sum += i;
				Assert.assertEquals(String.format("Error at i = %d limit = %d", i, limit), sum / q.size(), q.getAverage());
				Assert.assertTrue(q.contains((double) i));
			}
			
			for (int i = limit; i < limit * 2; i++) {
				Assert.assertEquals(limit, q.size());
				Assert.assertEquals(String.format("Error at i = %d limit = %d", limit, i), sum / limit, q.getAverage());
				double old = q.getFirst();
				q.add((double) i);
				sum += i;
				sum -= i - limit;
				Assert.assertTrue(q.contains((double) i));
				Assert.assertFalse(q.contains(old));
			}
		}		
	}

}
