package ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria;

import java.util.Collection;
import java.util.Random;

/**
 * The selection operator interface.
 *  
 * @author Arina Buzdalova
 * 
 * @param <T> type of an individual
 */
public interface Selection<T> {
	
	/**
	 * Selects one individual from the specified population
	 * using the implemented selection strategy.
	 *  
	 * @param population the specified population
	 * @param rng the source of randomness
	 * @return an individual selected from the {@code population}
	 */
    EvaluatedIndividual<T> select(Collection<EvaluatedIndividual<T>> population, Random rng);
}
