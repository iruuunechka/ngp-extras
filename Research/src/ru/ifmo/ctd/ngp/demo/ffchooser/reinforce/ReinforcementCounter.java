package ru.ifmo.ctd.ngp.demo.ffchooser.reinforce;

import java.util.List;

import org.uncommons.watchmaker.framework.FitnessEvaluator;

import ru.ifmo.ctd.ngp.demo.ffchooser.*;
import ru.ifmo.ctd.ngp.demo.ffchooser.genetic.EvolutionaryAlgorithm;
import ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.reward.RewardCalculator;
import ru.ifmo.ctd.ngp.learning.reinforce.Agent;

/**
 * Class for counting number of steps performed by a reinforcement learning method
 * running on the environment derived from an {@link EvolutionaryAlgorithm}.
 *  
 * @author Arina Buzdalova
 * @param <I> the type of an individual 
 * @see GenerationsCounter
 */
public class ReinforcementCounter<I> implements GenerationsCounter<FitnessEvaluator<? super I>, I> {
	private final int stepsLimit;
	private final Agent<String, Integer> agent;
	private final EvolutionaryAlgorithm<I> algorithm;
	private final OptAlgEnvironment<String, Integer> environment;
	private double idealFitness;
	
	/**
	 * Constructs ReinforcementCounter with the specified agent, 
	 * reward calculator and the evolutionary algorithm, which 
	 * determines the environment
	 * 
	 * @param stepsLimit maximal number of iterations made by <code>algorithm</code>
	 * @param agent the learning agent
	 * @param calculator the reward calculator
	 * @param algorithm the evolutionary algorithm, which determines the environment
	 */
	public ReinforcementCounter(
			int stepsLimit,
			Agent<String, Integer> agent, 
			RewardCalculator calculator, 
			EvolutionaryAlgorithm<I> algorithm) {
		
		this.stepsLimit = stepsLimit;
		this.agent = agent;
		this.algorithm = algorithm;
		this.environment = new OAEnvironment(algorithm, calculator);
	}
	
	/**
	 * Constructs ReinforcementCounter with the specified agent, 
	 * reward calculator and the evolutionary algorithm, which 
	 * determines the environment
	 * 
	 * TODO: get the <code>algorithm</code> from the environment.
	 *
     * @param stepsLimit maximal number of iterations made by <code>algorithm</code>
     * @param agent the learning agent
     * @param algorithm the evolutionary algorithm, which is the base of the <code>environment</code>
     * @param environment the environment based on some optimization algorithm
     */
	public ReinforcementCounter(
            int stepsLimit,
            Agent<String, Integer> agent,
            EvolutionaryAlgorithm<I> algorithm,
            OptAlgEnvironment<String, Integer> environment) {
		this.stepsLimit = stepsLimit;
		this.agent = agent;
		this.algorithm = algorithm;
		this.environment = environment;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int countGenerations() {
		TargetCondition<OptimizationAlgorithm> valueCond = new ValueCondition(idealFitness);
		TargetCondition<OptimizationAlgorithm> stepsCond = new StepsCondition(stepsLimit);

		//noinspection unchecked (could not do @SafeVarargs)
		environment.setTargetCondition(valueCond, stepsCond);
		algorithm.refresh();
		agent.refresh();		
		int steps = agent.learn(environment);
		
		return environment.checkTargetCondition(valueCond) ? steps : -1;
	}

	/**
	 * {@inheritDoc}
	 * Sets the evaluator associated with the target criteria
	 * of the optimization algorithm encapsulated by the
	 * environment
	 */
	@Override
	public void setEvaluator(FitnessEvaluator<? super I> evaluator,
			double idealFitness) {		
		this.idealFitness = idealFitness;
		algorithm.setEvaluator(algorithm.getTargetParameter(), evaluator);
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLength(int length) {
		algorithm.setLength(length);
		algorithm.refresh();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setStartPopulation(List<I> population) {
		algorithm.setStartPopulation(population);
		algorithm.refresh();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getEvaluatorsCount() {
		return environment.actionsCount();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getTargetIndex() {
		return algorithm.getTargetParameter();
	}
}
