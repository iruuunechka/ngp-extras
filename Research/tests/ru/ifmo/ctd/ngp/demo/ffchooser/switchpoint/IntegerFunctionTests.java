package ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Some tests for {@link IntegerFunction}.
 * Mainly checks how it works with switch points.
 * 
 * @author Arina Buzdalova
 */
public class IntegerFunctionTests {

	@Test 
	public void minimalDomain() {				
		IntegerFunction intConvex = Functions.intDownConvex(0, 1);
		IntegerFunction intUpConvex = Functions.intUpConvex(0, 1);
	
		checkSwitchPoint(intUpConvex, intConvex, 0);
		checkSwitchPoint(intUpConvex, intConvex, 1);		
	}
	
	@Test
	public void typicalGAConditions() {
		IntegerFunction intConvex = Functions.intDownConvex(0, 1);
		IntegerFunction intUpConvex = Functions.intUpConvex(0, 1);
		
		int[] upperBound = {50, 100, 150, 200, 250};

        for (int bound : upperBound) {
            intConvex.setDomain(0, bound);
            intUpConvex.setDomain(0, bound);

            checkSwitchPoint(intUpConvex, intConvex, bound / 3);
            checkSwitchPoint(intUpConvex, intConvex, bound / 2);
            checkSwitchPoint(intUpConvex, intConvex, (int) (2 * ((float) bound / 3)));
        }
	}
	
	@Test
	public void withDifferentBounds() {
		IntegerFunction intConvex = Functions.intDownConvex(20, 200);
		IntegerFunction intUpConvex = Functions.intUpConvex(10, 250);
		
		checkSwitchPoint(intConvex, intUpConvex, 100);
	}
	
	public void checkSwitchPoint(IntegerFunction intUpConvex, IntegerFunction intConvex, int expectedSwitchPoint) {
		int sqrtTolerance = 20;
		int sqrtSwitchPoint = intUpConvex.changeSwitchPoint(expectedSwitchPoint, sqrtTolerance, 0, 3000);
		
		Assert.assertTrue(Math.abs(expectedSwitchPoint - sqrtSwitchPoint) <= sqrtTolerance);
		Assert.assertEquals(intUpConvex.findSwitchPoint(), sqrtSwitchPoint);
		
		int powTolerance = 10;
		int powSwitchPoint = intConvex.changeSwitchPoint(sqrtSwitchPoint, powTolerance, 0, 3000);
		
		Assert.assertTrue(Math.abs(powSwitchPoint - sqrtSwitchPoint) <= powTolerance);
		Assert.assertEquals(intConvex.findSwitchPoint(), powSwitchPoint);
	}
	
}
