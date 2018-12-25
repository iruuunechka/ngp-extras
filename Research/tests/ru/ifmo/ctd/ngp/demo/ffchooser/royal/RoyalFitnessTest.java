package ru.ifmo.ctd.ngp.demo.ffchooser.royal;

import junit.framework.Assert;
import org.junit.Test;
import ru.ifmo.ctd.ngp.demo.legacy_ds.string.impl.*;

public class RoyalFitnessTest {
	
	@Test
	public void testRoyalRoadFitness() {
		RoyalRoadFitness ff1 = new RoyalRoadFitness(1);
		Assert.assertEquals(0.0, ff1.getFitness(BitString.of(false), null));
		Assert.assertEquals(1.0, ff1.getFitness(BitString.of(true), null));	
		
		RoyalRoadFitness ff2 = new RoyalRoadFitness(2);
		Assert.assertEquals(0.0, ff2.getFitness(BitString.of(true), null));
		Assert.assertEquals(0.0, ff2.getFitness(BitString.of(true, false), null));
		Assert.assertEquals(2.0, ff2.getFitness(BitString.of(true, true), null));
		Assert.assertEquals(2.0, ff2.getFitness(BitString.of(true, true, true), null));
		Assert.assertEquals(2.0, ff2.getFitness(BitString.of(true, true, false), null));
		Assert.assertEquals(0.0, ff2.getFitness(BitString.of(false, true, false), null));
	}
}
