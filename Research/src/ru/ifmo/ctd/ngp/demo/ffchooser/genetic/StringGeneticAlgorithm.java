package ru.ifmo.ctd.ngp.demo.ffchooser.genetic;

import java.util.ArrayList;
import java.util.List;

import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import org.uncommons.watchmaker.framework.FitnessEvaluator;

import ru.ifmo.ctd.ngp.demo.util.strings.GStringXMutation;
import ru.ifmo.ctd.ngp.demo.util.strings.ObjStringFactory;
import ru.ifmo.ctd.ngp.demo.util.strings.ShiftCrossover;
import ru.ifmo.ctd.ngp.demo.generators.SetMemberGenerator;
import ru.ifmo.ctd.ngp.demo.legacy_ds.string.impl.BitString;
import ru.ifmo.ctd.ngp.util.CollectionsEx;

/**
 * Incremental {@link GeneticAlgorithm}, in which individuals are represented
 * as {@link BitString}s.
 * 
 * @author Arina Buzdalova
 */
public class StringGeneticAlgorithm extends GeneticAlgorithm<BitString> {
    private static final List<Boolean> TRUE_FALSE = CollectionsEx.listOf(false, true);

    /**
	 * Constructs {@link StringGeneticAlgorithm} with the specified parameters
	 * 
	 * @param length the length of an individual
	 * @param targetCriterion the index of target optimization criterion in the evaluators list
	 * @param curEvaluator the index of current optimization criterion in the evaluators list
	 * @param evaluators the list of possible fitness evaluators
	 * @param operators the genetic operators such as crossover and mutation
	 */ 
	protected StringGeneticAlgorithm(
			int length,
			int targetCriterion, 
			int curEvaluator,
			List<? extends FitnessEvaluator<? super BitString>> evaluators,
			List<EvolutionaryOperator<BitString>> operators) {
		
		super(ObjStringFactory.create(
                length,
                SetMemberGenerator.newGen(TRUE_FALSE),
                BitString.empty()
        ), targetCriterion, curEvaluator, evaluators, operators);
	}
	
	/**
	 * Constructs {@link StringGeneticAlgorithm} with the specified parameters.
	 * Operators are set to {@link ShiftCrossover} with 0.7 probability and
	 * {@link ru.ifmo.ctd.ngp.demo.util.strings.GStringXMutation} with 0.03 probability.
	 * 
	 * @param length the length of an individual
	 * @param targetCriterion the index of target optimization criterion in the evaluators list
	 * @param curEvaluator the index of current optimization criterion in the evaluators list
	 * @param evaluators the list of possible fitness evaluators
     * @return the constructed algorithm
	 */
	public static StringGeneticAlgorithm newStringGA(
			int length,
			int targetCriterion, 
			int curEvaluator,
			List<? extends FitnessEvaluator<? super BitString>> evaluators) {
		
		List<EvolutionaryOperator<BitString>> operators = new ArrayList<>();
		
		operators.add(new ShiftCrossover<>(new Probability(0.7)));
		
		operators.add(new GStringXMutation<>(
                SetMemberGenerator.newGen(CollectionsEx.listOf(false, true)), new Probability(0.003)));
		
		return new StringGeneticAlgorithm(length, targetCriterion, curEvaluator, evaluators, operators);
	}
	
	/**
	 * Constructs {@link StringGeneticAlgorithm} with the specified parameters
	 * 
	 * @param length the length of an individual
	 * @param targetCriterion the index of target optimization criterion in the evaluators list
	 * @param curEvaluator the index of current optimization criterion in the evaluators list
	 * @param evaluators the list of possible fitness evaluators
	 * @param crossoverProbability the probability of {@link ShiftCrossover} 
	 * @param mutationProbability the probability of {@link ru.ifmo.ctd.ngp.demo.util.strings.GStringXMutation}
     * @return the constructed algorithm
	 */
	public static StringGeneticAlgorithm newStringGA(
			int length,
			int targetCriterion, 
			int curEvaluator,
			List<? extends FitnessEvaluator<BitString>> evaluators,
			double crossoverProbability,
			double mutationProbability) {
		
		List<EvolutionaryOperator<BitString>> operators = new ArrayList<>();
		
		operators.add(new ShiftCrossover<>(
                new Probability(crossoverProbability))
        );
		
		operators.add(new GStringXMutation<>(
                SetMemberGenerator.newGen(CollectionsEx.listOf(false, true)), new Probability(mutationProbability)));
		
		return new StringGeneticAlgorithm(length, targetCriterion, curEvaluator, evaluators, operators);
	}
	
	/**
	 * Constructs {@link StringGeneticAlgorithm} with the specified parameters
	 * 
	 * @param length the length of an individual
	 * @param targetCriterion the index of target optimization criterion in the evaluators list
	 * @param curEvaluator the index of current optimization criterion in the evaluators list
	 * @param evaluators the list of possible fitness evaluators
	 * @param operators the genetic operators such as crossover and mutation
     * @return the constructed algorithm
	 */
	public static StringGeneticAlgorithm newStringGA(
			int length,
			int targetCriterion, 
			int curEvaluator,
			List<? extends FitnessEvaluator<BitString>> evaluators,
			List<EvolutionaryOperator<BitString>> operators) {
		return new StringGeneticAlgorithm(length, targetCriterion, curEvaluator, evaluators, operators);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected BitString emptyCandidate() {
		return BitString.empty();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLength(int length) {
		this.factory = ObjStringFactory.create(
                length,
                SetMemberGenerator.newGen(CollectionsEx.listOf(false, true)),
                BitString.empty()
        );
		refresh();
	}
}
