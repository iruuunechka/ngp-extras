package ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria;

import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import org.uncommons.watchmaker.framework.FitnessEvaluator;
import org.uncommons.watchmaker.framework.factories.AbstractCandidateFactory;
import ru.ifmo.ctd.ngp.demo.ffchooser.genetic.EvolutionaryAlgorithm;
import ru.ifmo.ctd.ngp.demo.ffchooser.genetic.Printer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * {@link PesaII} algorithm for single-objective optimization with helper-objectives.
 * At each generation two objectives are used: the target one and the current helper objective.
 * @author Arina Buzdalova
 * @param <I> the type of an individual
 */
public class PesaIIWithHelpers<I> implements EvolutionaryAlgorithm<I> {
	private final PesaII<I> pesaii;
	private final List<FitnessEvaluator<I>> helpers;
	private final int target;
	private final List<Printer<? super I>> printers;
	private int currentHelper;
	private int iterations;
	
	/**
	 * Constructs {@link PesaIIWithHelpers} with the specified parameter.
	 * 
	 * @param targetCriterion the target objective
	 * @param helpers the helper-objectives
	 * @param mutation the mutation operator
	 * @param factory the factory of individuals
	 * @param rng the generator of randomness
	 */
	public PesaIIWithHelpers(
			FitnessEvaluator<I> targetCriterion,
			List<FitnessEvaluator<I>> helpers,
			EvolutionaryOperator<I> mutation,
			EvolutionaryOperator<I> crossover,
			double crossoverProbability,
			AbstractCandidateFactory<I> factory,
			int generationSize,
			int archiveSize,
			double[] gridSteps,
			Random rng) {
		currentHelper = rng.nextInt(helpers.size());
		target = 0;
		List<FitnessEvaluator<? super I>> criteria = new ArrayList<>();
		criteria.add(targetCriterion);
		criteria.add(helpers.get(currentHelper));
				//CollectionsEx.listOf(targetCriterion, helpers.get(currentHelper));
		this.pesaii = new PesaII<>(criteria, mutation, crossover, crossoverProbability, new BinaryTournament<>(), factory, generationSize, archiveSize, gridSteps, rng);
		this.helpers = helpers;
		this.iterations = 0;
		this.printers = new ArrayList<>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void changeCriterion(int index) {
		if (currentHelper == index) {
			return;
		}
		pesaii.deleteCriterion(helpers.get(currentHelper));
		pesaii.addCriterion(helpers.get(index));
		currentHelper = index;
    }

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Double> computeValues() {
		pesaii.iterate();
		iterations++;
		printAll();                                                                                                                                                                                                                                                      
		return computeMaxInGeneration();
	}
	
	private List<Double> computeMaxInGeneration() {
		List<EvaluatedIndividual<I>> evaluated = pesaii.getGeneration();
		EvaluatedIndividual<I> maxInd = evaluated.get(0);
		double max = 0;
		for (EvaluatedIndividual<I> ind : evaluated) {
			double val = ind.par().getCriteria()[target];
			if (val > max) {
				max = val;
				maxInd = ind;
			}
		}
		List<Double> values = new ArrayList<>(helpers.size());
		for (double val : maxInd.par().getCriteria()) {
			values.add(val);
		}
		return values;
	}
	
	private void printAll() {
		for (Printer<? super I> p : printers) {
			p.print(getCurrentBest(), null, iterations, currentHelper);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getTargetParameter() {
		return target;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getCurrentCriterion() {
		return currentHelper;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int parametersCount() {
		// 1 for the target parameter
		return helpers.size();
	}

	/**
	 * The maximal target value in the current generation.
	 */
	@Override
	public double getBestTargetValue() {
		List<EvaluatedIndividual<I>> generation = pesaii.getGeneration();
		double max = generation.get(0).par().getCriteria()[target];
		for (EvaluatedIndividual<I> ind : generation) {
			max = Math.max(max, ind.par().getCriteria()[target]);
		}
		return max;
	}

    @Override
    public double getTargetValueInCurrentBest() {
        return getBestTargetValue();
    }

    @Override
    public double getFinalBestTargetValue() {
        throw new UnsupportedOperationException();
    }

    /**
	 * {@inheritDoc}
	 */
	@Override
	public int getIterationsNumber() {
		return iterations;
	}

	/**
	 * Returns a list of one element -- maximal target value
	 * in the last evaluated generation
	 */
	@Override
	public List<Double> getCurrentBest() {
		return computeMaxInGeneration();
	}

	/**
	 * {@inheritDoc}
	 * Target criterion value goes first, then helpers values follow.
	 */
	@Override
	public List<List<Double>> getCurrentPoints() {
		Set<EvaluatedIndividual<I>> generation = pesaii.getArchive();
		List<List<Double>> res = new ArrayList<>();
		
		for (EvaluatedIndividual<I> ind : generation) {
			List<Double> values = new ArrayList<>();
			values.add(ind.par().getCriteria()[target]);
			for (FitnessEvaluator<? super I> eval : helpers) {
				values.add(eval.getFitness(ind.ind(), null));
			}
			res.add(values);
		}		
		return res;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void refresh() {
		pesaii.refresh();
		iterations = 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addPrinter(Printer<? super I> printer) {
		printers.add(printer);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removePrinter(Printer<? super I> printer) {
		printers.remove(printer);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setStartPopulation(List<I> seedPopulation) {
		pesaii.setGeneration(seedPopulation);
	}

	/**
	 * The operation is not supported.
	 * @throws UnsupportedOperationException when called
	 */
	@Override
	public void setLength(int length) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Sets the helper to the specified index
	 * @param index the specified index
	 * @param evaluator the helper to be set
	 */
	@Override
	public void setEvaluator(int index, FitnessEvaluator<? super I> evaluator) {
		throw new UnsupportedOperationException();
	}
	
}
