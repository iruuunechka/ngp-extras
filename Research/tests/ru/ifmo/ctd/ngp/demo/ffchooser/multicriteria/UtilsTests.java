package ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;

import ru.ifmo.ctd.ngp.demo.util.Nil;

/**
 * Some basic tests for {@link Utils}.
 * 
 * @author Arina Buzdalova
 */
public class UtilsTests {

	@Test
	public void isNotDominated() {
		Collection<EvaluatedIndividual<Nil>> coll = simpleCollection();
		Assert.assertTrue(Utils.isNotDominated(new Parameter(1, 1), coll));
		Assert.assertTrue(Utils.isNotDominated(new Parameter(2, -1), coll));
		Assert.assertFalse(Utils.isNotDominated(new Parameter(0, 1), coll));
		Assert.assertFalse(Utils.isNotDominated(new Parameter(1, 0), coll));
		Assert.assertFalse(Utils.isNotDominated(new Parameter(0, 0), coll));
	}
	
	@Test
	public void getAllNotDominated() {
		Collection<EvaluatedIndividual<Nil>> coll = Utils.getNotDominated(simpleCollection());
		Assert.assertEquals(2, coll.size());
		Assert.assertTrue(coll.contains(construct(1, 1)));
		Assert.assertTrue(coll.contains(construct(2, -1)));
	}
	
	@Test
	public void getHyperGrid() {
		List<HyperBox<Nil>> grid = Utils.getHyperGrid(smallCollection(), new double[]{1.1, 1.05});
		Assert.assertEquals(2, grid.size());
		List<EvaluatedIndividual<Nil>> list1 = grid.get(0).getAll();
		List<EvaluatedIndividual<Nil>> list2 = grid.get(1).getAll();		
		Assert.assertEquals(3, list1.size() + list2.size());
		List<EvaluatedIndividual<Nil>> smallList = list1.size() < list2.size() ? list1 : list2;	
		Assert.assertTrue(smallList.contains(construct(0.5, 1.5)));
	}
	
	@Test
	public void normalize() {
		Set<EvaluatedIndividual<Nil>> population = new HashSet<>(smallCollection());
		Utils.normalize(population, 2, new double[]{1, 1}, new Random());
		Assert.assertEquals(2, population.size());
		Assert.assertTrue(population.contains(construct(0.5, 1.5)));
	}
	
	private Collection<EvaluatedIndividual<Nil>> smallCollection() {
		Collection<EvaluatedIndividual<Nil>> coll = new ArrayList<>();
		coll.add(construct(0.5, 1.5));
		coll.add(construct(1.3, 0.5));
		coll.add(construct(1.7, 0.5));
		return coll;
	}
	
	private Collection<EvaluatedIndividual<Nil>> simpleCollection() {
		Collection<EvaluatedIndividual<Nil>> coll = new ArrayList<>();
		coll.add(construct(1, 1));
		coll.add(construct(2, -1));
		coll.add(construct(0, 1));
		coll.add(construct(1, 0));
		coll.add(construct(0, 0));
		return coll;
	}
	
	private EvaluatedIndividual<Nil> construct(double... values) {
		return new EvaluatedIndividual<>(Nil.value(), new Parameter(values));
	}
}
