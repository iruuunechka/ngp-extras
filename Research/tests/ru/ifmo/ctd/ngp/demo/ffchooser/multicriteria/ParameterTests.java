package ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria;

import junit.framework.Assert;
import org.junit.Test;

import java.util.*;

/**
 * Some basic tests for {@link Parameter}.
 * 
 * @author Arina Buzdalova
 */
public class ParameterTests {
	
	@Test (expected = IllegalArgumentException.class)
	public void emptyValues() {
		double[] values = new double[0];
		new Parameter(values);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void differentLengths() {
		Random rng = new Random();
		double[] values1 = randomValues(rng);
		Parameter p1 = new Parameter(values1);
		double[] values2 = new double[values1.length + 1];
		Parameter p2 = new Parameter(values2);
		p1.dominates(p2);
	}
	
	@Test
	public void equality() {
		Random rng = new Random();
		double[] values = randomValues(rng);
		Parameter p1 = new Parameter(values);
		Parameter p2 = new Parameter(values.clone());
		Assert.assertEquals(p1, p2);
		Assert.assertFalse(p1.dominates(p2));
		Assert.assertFalse(p1.notComparable(p2));
	}
	
	@Test 
	public void oneDimensionalRelations() {
		Parameter big = new Parameter(1);
		Parameter small = new Parameter(-1);
		checkRelations(small, big);		
	}
	
	@Test 
	public void twoDimensionalRelations() {
		Parameter big = new Parameter(1, 1);
		Parameter small = new Parameter(0, 1);
		checkRelations(small, big);		
	}
	
	@Test 
	public void manyDimensionalRelations() {
		Random rng = new Random();
		double[] values = randomValues(rng);
		Parameter big = new Parameter(values);
		for (int i = 0; i < values.length; i++) {
			if (rng.nextBoolean()) {
				values[i] = -1 * values[i];
			}
		}
		Parameter small = new Parameter(values);
		checkRelations(small, big);		
	}
	
	private void checkRelations(Parameter small, Parameter big) {
		Assert.assertTrue(big.dominates(small));
		Assert.assertFalse(big.notComparable(small));
		//noinspection SimplifiableJUnitAssertion
		Assert.assertFalse(big.equals(small));
		Assert.assertFalse(small.dominates(big));
		Assert.assertFalse(small.notComparable(big));
		//noinspection SimplifiableJUnitAssertion
		Assert.assertFalse(small.equals(big));
	}
	
	private double[] randomValues(Random rng) {
		int size = rng.nextInt(1000) + 1;
		double[] values = new double[size];
		for (int i = 0; i < size; i++) {
			values[i] = rng.nextDouble();
		}
		return values;
	}
}
