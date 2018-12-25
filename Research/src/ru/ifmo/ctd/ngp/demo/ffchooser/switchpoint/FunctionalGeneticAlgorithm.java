package ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint;

import java.util.ArrayList;
import java.util.List;

import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;

import ru.ifmo.ctd.ngp.demo.util.strings.BitInversionMutation;
import ru.ifmo.ctd.ngp.demo.util.strings.GStringXMutation;
import ru.ifmo.ctd.ngp.demo.util.strings.ObjStringFactory;
import ru.ifmo.ctd.ngp.demo.util.strings.ShiftCrossover;
import ru.ifmo.ctd.ngp.demo.ffchooser.genetic.GeneticAlgorithm;
import ru.ifmo.ctd.ngp.demo.generators.SetMemberGenerator;
import ru.ifmo.ctd.ngp.demo.legacy_ds.string.impl.BitString;

import static ru.ifmo.ctd.ngp.util.CollectionsEx.listOf;

/**
 * {@link GeneticAlgorithm} with {@link ru.ifmo.ctd.ngp.demo.legacy_ds.string.impl.BitString}s as individuals
 * and {@link FunctionalFitness}.
 * 
 * @author Arina Buzdalova
 */
public class FunctionalGeneticAlgorithm extends GeneticAlgorithm<BitString> {
    /**
	 * Constructs {@link FunctionalGeneticAlgorithm} with the specified parameters
	 * 
	 * @param length the length of an individual
	 * @param targetCriterion the index of target optimization criterion in the evaluators list
	 * @param curEvaluator the index of current optimization criterion in the evaluators list
	 * @param evaluators the list of possible fitness evaluators
	 * @param operators the genetic operators such as crossover and mutation
	 */
	protected FunctionalGeneticAlgorithm(
			int length,
			int targetCriterion, 
			int curEvaluator,
			List<FunctionalFitness> evaluators,
			List<EvolutionaryOperator<BitString>> operators) {
		super(ObjStringFactory.create(length, SetMemberGenerator.newGen(listOf(true, false)), BitString.empty()),
				targetCriterion, curEvaluator, evaluators, operators);
	}
	
	/**
	 * Returns genetic operators, which used to create {@link FunctionalGeneticAlgorithm} by default
	 * @param crossoverProbability the probability of {@link ShiftCrossover}
	 * @param mutationProbability the probability of {@link ru.ifmo.ctd.ngp.demo.util.strings.GStringXMutation}
	 * @return default genetic operators
	 */
	public static List<EvolutionaryOperator<BitString>> getDefaultOperators(
			double crossoverProbability,
			double mutationProbability) {
		List<EvolutionaryOperator<BitString>> operators = new ArrayList<>();
		operators.add(new ShiftCrossover<>(new Probability(crossoverProbability)));
		operators.add(new GStringXMutation<>(
				SetMemberGenerator.newGen(listOf(true, false)), new Probability(mutationProbability))
        );
		return operators;
	}
	

	/**
	 * Returns genetic operators, which used to create {@link FunctionalGeneticAlgorithm} by default
	 * @return default genetic operators
	 */
	public static List<EvolutionaryOperator<BitString>> getDefaultMutation() {
		List<EvolutionaryOperator<BitString>> operators = new ArrayList<>();
		operators.add(new BitInversionMutation(1));
		return operators;
	}


	/**
	 * Constructs {@link FunctionalGeneticAlgorithm} with the specified parameters
	 * 
	 * @param length the length of an individual
	 * @param targetCriterion the index of target optimization criterion in the evaluators list
	 * @param curEvaluator the index of current optimization criterion in the evaluators list
	 * @param evaluators the list of possible fitness evaluators
	 * @param crossoverProbability the probability of {@link ShiftCrossover} 
	 * @param mutationProbability the probability of {@link ru.ifmo.ctd.ngp.demo.util.strings.GStringXMutation}
     * @return the algorithm created
	 */
	public static FunctionalGeneticAlgorithm newFGA(
			int length,
			int targetCriterion, 
			int curEvaluator,
			List<FunctionalFitness> evaluators,
			double crossoverProbability,
			double mutationProbability) {
		return new FunctionalGeneticAlgorithm(
                length, targetCriterion, curEvaluator, evaluators,
				FunctionalGeneticAlgorithm.getDefaultOperators(crossoverProbability, mutationProbability));
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
		this.factory = ObjStringFactory.create(length, SetMemberGenerator.newGen(listOf(true, false)), BitString.empty());
		refresh();
	}
}
