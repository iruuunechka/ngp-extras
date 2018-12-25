package ru.ifmo.ctd.ngp.demo.ffchooser.mh_iff;

import java.util.List;

import org.uncommons.watchmaker.framework.FitnessEvaluator;

import ru.ifmo.ctd.ngp.demo.legacy_ds.string.GString;

/**
 * Fitness function that calculates number of matches with the
 * 101010...10 mask.
 * 
 * @author Arina Buzdalova
 * @param <S> the type of an individual
 *
 */
public class AlternatingMaskFitness<S extends GString<Boolean>> implements FitnessEvaluator<S> {
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getFitness(S candidate,
			List<? extends S> population) {
		
		int matches = 0;
		for (int i = 0; i < candidate.length(); i++) {
			if (candidate.charAt(i) == (i % 2 == 0)) {
				matches++;
			}
		}
		
		return matches;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isNatural() {
		return true;
	}
}
