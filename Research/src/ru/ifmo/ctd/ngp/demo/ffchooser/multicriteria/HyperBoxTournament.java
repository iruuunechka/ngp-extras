package ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria;

import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * Binary tournament selection operator that uses hyper-boxes. 
 * Two hyper-boxes are chosen randomly, then the one with the
 * lowest squeeze factor is chosen. An individual is taken arbitrarily 
 * form the latter.
 * 
 * @author Arina Buzdalova
 *
 * @param <T> the type of an individual
 * @see HyperBox
 */
public class HyperBoxTournament<T> implements Selection<T> {
	private final double[] gridSteps;

	/**
	 * Constructs {@link HyperBoxTournament} selection operator 
	 * @param gridSteps the step sizes of hyper-grid
	 */
	public HyperBoxTournament (double[] gridSteps) {
		this.gridSteps = gridSteps;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public EvaluatedIndividual<T> select(
			Collection<EvaluatedIndividual<T>> population, Random rng) {
		List<HyperBox<T>> grid = Utils.getHyperGrid(population, gridSteps);
		HyperBox<T> box1 = grid.get(rng.nextInt(grid.size()));
		HyperBox<T> box2 = grid.get(rng.nextInt(grid.size()));
		HyperBox<T> min = box1.compareTo(box2) < 0 ? box1 : box2;
		return min.getRandom(rng);
	}

}
