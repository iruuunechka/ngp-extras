package ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * Binary tournament selection operator.
 * Two individuals are chosen randomly, then the one with the lowest squeeze factor is taken.
 * 
 * @author Arina Buzdalova
 *
 * @param <T> the type of an individual
 * @see HyperBox
 */
public class BinaryTournament<T> implements Selection<T> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EvaluatedIndividual<T> select(
			Collection<EvaluatedIndividual<T>> population, Random rng) {
		List<EvaluatedIndividual<T>> list = new ArrayList<>(population);
		EvaluatedIndividual<T> first = list.get(rng.nextInt(list.size()));
		EvaluatedIndividual<T> second = list.get(rng.nextInt(list.size()));
		return first.getSqueezeFactor() < second.getSqueezeFactor() ? first : second;
	}

}
