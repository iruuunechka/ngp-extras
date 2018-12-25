package ru.ifmo.ctd.ngp.demo.ffchooser.config.factory;

import java.io.Serializable;
import java.util.List;

import org.uncommons.watchmaker.framework.FitnessEvaluator;

import ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.FunctionalFitness;

/**
 * Factory of {@link FitnessEvaluator} lists.
 * 
 * @author Arina Buzdalova
 */
public interface EvaluatorsFactory extends Serializable {
	/**
	 * Returns an unmodifiable view of evaluators list
	 * @return an unmodifiable view of evaluators list
	 */
	List<FunctionalFitness> getEvaluators();
	/**
	 * The index of the target fitness evaluator in the list returned by this factory
	 * @return the index of the target fitness evaluator
	 */
	int targetIndex();
	/**
	 * Gets a short mnemonic name of this factory
	 * @return a short mnemonic name of this factory
	 */
	String getName();
    /**
     * The best possible fitness of the target function
     * @return the best possible fitness of the target function
     */
    double bestFitness();
	/**
	 * {@inheritDoc}
	 */
	@Override
	boolean equals(Object obj);
	/**
	 * {@inheritDoc}
	 */
	@Override
	int hashCode();
}
