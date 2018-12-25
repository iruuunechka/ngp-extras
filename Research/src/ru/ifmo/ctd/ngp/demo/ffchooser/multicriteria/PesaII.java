package ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria;

import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import org.uncommons.watchmaker.framework.FitnessEvaluator;
import org.uncommons.watchmaker.framework.factories.AbstractCandidateFactory;
import ru.ifmo.ctd.ngp.util.CollectionsEx;

import java.util.*;

/**
 * The PESA-II multicriteria optimization evolutionary algorithm
 * with no crossover.
 * 
 * @author Arina Buzdalova
 * @param <T> the type of an individual
 */
public class PesaII<T> {
	private final List<FitnessEvaluator<? super T>> criteria;
	private final EvolutionaryOperator<T> mutation;
	private final EvolutionaryOperator<T> crossover;
	private final Probability crossoverProbability;
	private final Selection<T> selection;
	private final AbstractCandidateFactory<T> factory;
	private final Random rng;
	private final int generationSize;
	private final int archiveSize;
	private final double[] gridSteps;
	
	private List<EvaluatedIndividual<T>> internal;
	private Set<EvaluatedIndividual<T>> external;

	/**
	 * Constructs {@link PesaII} with the specified parameters.
	 * @param criteria criteria used to evaluate search points
	 * @param mutation the mutation operator
	 * @param selection the selection operator, in pure PESA-II it should be based on crowding
	 * @param factory the factory of individuals
	 * @param rng the generator of randomness
	 */
	public PesaII(List<FitnessEvaluator<? super T>> criteria,
			EvolutionaryOperator<T> mutation,
			EvolutionaryOperator<T> crossover,
			double crossoverProbability,
			Selection<T> selection, 
			AbstractCandidateFactory<T> factory, 
			int generationSize, 
			int archiveSize, 
			double[] gridSteps,
			Random rng) {
		
		if (gridSteps.length != criteria.size()) {
			throw new IllegalArgumentException("The number of grid steps and the number of criteria do not agree.");
		}	
		
		this.criteria = criteria;
		this.mutation = mutation;
		this.crossover = crossover;
		this.crossoverProbability = new Probability(crossoverProbability);
		this.selection = selection;
		this.factory = factory;
		this.generationSize = generationSize;
		this.archiveSize = archiveSize;
		this.gridSteps = gridSteps;
		this.rng = rng;
		refresh();
	}
	
	public void refresh() {
		internal = new ArrayList<>(Utils.evaluateAll(factory.generateInitialPopulation(generationSize, rng), criteria));
		external = new HashSet<>(Utils.getNotDominated(internal));
	}
	
	public Set<EvaluatedIndividual<T>> getArchive() {
		return Collections.unmodifiableSet(external);
	}
	
	public List<EvaluatedIndividual<T>> getGeneration() {
		return Collections.unmodifiableList(internal);
	}
	
	public void setGeneration(List<T> generation) {
		this.internal = new ArrayList<>(Utils.evaluateAll(generation, criteria));
	}
	
	public void deleteCriterion(FitnessEvaluator<? super T> criterion) {
		criteria.remove(criterion);
	}
	
	public void addCriterion(FitnessEvaluator<? super T> criterion) {
		criteria.add(criterion);
	}
	
	/**
	 * Runs PESA-II for the specified number of generation using only mutation operator and binary tournament selection
	 * (two hyper-boxes are taken at random, the one with the lowest squeeze factor is chosen, 
	 * then an individual is chosen from the latter randomly.)
	 * @param steps the specified number of generations
	 * @param generationSize the size of "internal" population
	 * @param archiveSize the size of "external" population
	 * @param gridSteps steps of hyper-grid, the array length must be equal to the criteria number
	 * @return the last approximation of the Pareto frontier consisting of the evaluated individuals
	 */
	public static <T> Collection<EvaluatedIndividual<T>> run(
			List<FitnessEvaluator<? super T>> criteria,
			EvolutionaryOperator<T> mutation,
			EvolutionaryOperator<T> crossover,
			double crossoverProbability,
			Selection<T> selection, 
			AbstractCandidateFactory<T> factory,
			int steps, 
			int generationSize, 
			int archiveSize, 
			double[] gridSteps,
			Random rng) {
		PesaII<T> pesaii = new PesaII<>(criteria, mutation, crossover, crossoverProbability, selection, factory, generationSize, archiveSize, gridSteps, rng);
		for (int i = 1; i < steps; i++) {
			pesaii.iterate();
		}		
		return pesaii.iterate();
	}	
	
	public Set<EvaluatedIndividual<T>> iterate() {
		internal.clear();
		while(internal.size() != generationSize) {
			T parent;
			if (crossoverProbability.nextEvent(rng)) {
                parent = crossover.apply(CollectionsEx.listOf(
                        selection.select(external, rng).ind(),
                        selection.select(external, rng).ind()
                ), rng).get(0);
			} else {
				parent = selection.select(external, rng).ind();
			}
			T iChild = mutation.apply(CollectionsEx.listOf(parent), rng).get(0);
			EvaluatedIndividual<T> child = new EvaluatedIndividual<>(iChild, Utils.evaluate(iChild, CollectionsEx.listOf(iChild), criteria));
			internal.add(child);
		}
		external.addAll(internal);
		//TODO: performance?
		external = Utils.normalize(new HashSet<>(Utils.getNotDominated(external)), archiveSize, gridSteps, rng);
		return external;
	}
}
