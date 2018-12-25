package ru.ifmo.ctd.ngp.demo.ffchooser.jobshop;

import org.junit.Test;
import junit.framework.Assert;

import ru.ifmo.ctd.ngp.util.CollectionsEx;

public class ScheduleBuildingTests {
	
	@Test
	public void testMinimal() {
		int time = 10;
		int[][] times = {{time}};
		int[][] machines = {{0}};
 		int[] fitness = JobShopUtils.evalFlowTimes(CollectionsEx.listOf(0), JobShopUtils.createJobsList(times, machines));
 		Assert.assertEquals(time, fitness[0]);
	}
	
	@Test
	public void testSimple() {
		int[][] times = {{15, 15}, {20, 20}};
		int[][] machines = {{0, 0}, {1, 1}};
 		int[] fitness = JobShopUtils.evalFlowTimes(CollectionsEx.listOf(0, 0, 1, 1), JobShopUtils.createJobsList(times, machines));
 		Assert.assertEquals(30, fitness[0]);
 		Assert.assertEquals(40, fitness[1]);
	}
	
	@Test
	public void testFromArticle() {
		int[][] times = {{5, 8, 7, 5},
                         {6, 3, 9, 6},
						 {4, 2, 4, 8}};
		
		int[][] machines = {{0, 1, 3, 2},
						 	{1, 0, 2, 3},
							{2, 3, 1, 0}};
		
 		int[] fitness = JobShopUtils.evalFlowTimes(
 				CollectionsEx.listOf(0, 1, 2, 0, 1, 1, 0, 1, 0, 2, 2, 2), 
 				JobShopUtils.createJobsList(times, machines));
 		Assert.assertEquals(26, fitness[0]);
 		Assert.assertEquals(27, fitness[1]);
 		Assert.assertEquals(26, fitness[2]);
	}

}
