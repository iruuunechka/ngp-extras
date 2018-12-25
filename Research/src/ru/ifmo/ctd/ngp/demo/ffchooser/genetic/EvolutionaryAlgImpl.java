package ru.ifmo.ctd.ngp.demo.ffchooser.genetic;

import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.*;
import org.uncommons.watchmaker.framework.operators.EvolutionPipeline;
import org.uncommons.watchmaker.framework.selection.TournamentSelection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Basic implementation of an incremental evolutionary algorithm, 
 * which is useful for machine learning methods.
 *  
 * @author Arina Buzdalova
 * @param <T> the type of an individual  
 * @see EvolutionaryAlgorithm
 */
public abstract class EvolutionaryAlgImpl<T> implements EvolutionaryAlgorithm<T> {
	
	protected int generationSize;
	
	protected final Random rng;
	protected final EvolutionaryOperator<T> pipeline;
	protected final EvolutionLogger observer;
	private final int targetEvaluatorIndex;
	private final List<FitnessEvaluator<? super T>> evaluators;
	private final int initEvalIndex;
	
	protected int eliteCount;	
	protected final SelectionStrategy<? super T> selectionStrategy;
	protected CandidateFactory<T> factory;
	protected FitnessEvaluator<? super T> currentEvaluator;
	private int curEvaluatorIndex;	
	protected List<T> seedPopulation;
	private List<T> initialPopulation;
	private List<Double> lastReported;
	protected T bestIndividual;
	protected int iterations;
	private final List<Printer<? super T>> printers;
	
	private final List<List<Double>> currentPoints;
	private boolean changed;
    private Double bestTargetValue;
    private double curBestTargetValue;
    private int curIteration;

    /**
     * <p>
     * Constructs EvolutionaryAlgImpl which evolves bit strings
     * with the specified list of possible fitness evaluators and the
     * indexes of the evaluators used to calculate the target and the current optimization parameters.
     * </p><p>
     * Selection strategy is initially set to {@link TournamentSelection} with 0.9 probability, generation size
     * is set to 100, elite count is set to 5. All these parameters can be changed by corresponding set methods.
     * </p>
     * @param factory the factory that is used to create candidates
     * @param targetCriterion the index of target optimization criterion in the {@code evaluators} list
     * @param curEvaluator the index of current optimization criterion in the {@code evaluators} list
     * @param evaluators the list of possible fitness evaluators
     * @param operators the genetic operators such as crossover and mutation
     */
	public EvolutionaryAlgImpl(
			CandidateFactory<T> factory,
			int targetCriterion, 
			int curEvaluator,
			List<? extends FitnessEvaluator<? super T>> evaluators,
			List<? extends EvolutionaryOperator<T>> operators)
	{		
		this.rng = new Random();
		
		this.factory = factory;
	
		this.pipeline = new EvolutionPipeline<>(new ArrayList<>(operators));
				
		this.observer = new EvolutionLogger();		
		this.targetEvaluatorIndex = targetCriterion;
		
		this.evaluators = new ArrayList<>(evaluators);
		
		this.curEvaluatorIndex = curEvaluator;
		this.currentEvaluator = evaluators.get(curEvaluatorIndex);	
		this.initEvalIndex = curEvaluatorIndex;
		
		this.seedPopulation = new ArrayList<>();
		this.initialPopulation = new ArrayList<>();
		this.lastReported = new ArrayList<>();
		this.iterations = 0;
		
		this.printers = new ArrayList<>();
		
		this.selectionStrategy = new TournamentSelection(new Probability(0.9));
		this.generationSize = 100;
		this.eliteCount = 5;
		
		this.currentPoints = new ArrayList<>();
		changed = true;
        this.bestTargetValue = null;
	}

    /**
	 * Sets the number of individuals in one generation
	 * @param generationSize the number of individuals in one generation
	 */
	public void setGenerationSize(int generationSize) {
		this.generationSize = generationSize;
	}
	
	/**
	 * Sets the number of candidates kept by elitism
	 * @param eliteCount number of candidates kept by elitism
	 */
	public void setEliteCount(int eliteCount) {
		this.eliteCount = eliteCount;
	}
	
	/**
	 * Adds the {@link Printer} used to observe this algorithm running
	 * @param printer the printer used to observe this algorithm running
	 */
	@Override
	public void addPrinter(Printer<? super T> printer) {
		printers.add(printer);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removePrinter(Printer<? super T> printer) {
		printers.remove(printer);
	}
	
	/**
	 * Sets the population, which will be used to generate individuals 
	 * at the next iteration of the algorithm
	 * @param seedPopulation the initial generation to be set
	 */
	public void setStartPopulation(List<T> seedPopulation) {
		this.initialPopulation = seedPopulation;
		this.seedPopulation = seedPopulation;
		populationChanged();
	}
	
	/**
	 * Makes GA forgetting about already evolved individuals.
	 * GA will start from scratch after the next call of {@link #computeValues()}.
	 * 
	 * @param currentEvaluator the index of current fitness evaluator
	 */
	public void refresh(int currentEvaluator) {
		this.seedPopulation = initialPopulation;
		populationChanged();
		this.curEvaluatorIndex = currentEvaluator;
		this.currentEvaluator = evaluators.get(curEvaluatorIndex);		
		this.bestIndividual = emptyCandidate();
        if (initialPopulation.isEmpty()) {
            this.lastReported = null;
        } else {
            this.lastReported = evaluate(initialPopulation.get(0));
        }
		this.iterations = 0;
        this.bestTargetValue = null;
        curIteration = 0;
        curBestTargetValue = 0;
	}
	
	/**
	 * Returns an empty candidate
	 * @return an empty candidate
	 */
	protected abstract T emptyCandidate();
	
	/**
	 * Makes GA forgetting about already evolved individuals.
	 * GA will start from scratch after the next call of {@link #computeValues()}.
	 * Current fitness evaluator is set to the initial one.
	 */
	public void refresh() {
		refresh(initEvalIndex);
	}
	
	/**
	 * Sets the length of an individual and refreshes the algorithm
	 * using the {@link #refresh} method
	 * @param length the length of an individual
	 */
	public abstract void setLength(int length);
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void changeCriterion(int index) {
		FitnessEvaluator<? super T> newEvaluator = evaluators.get(index);
		if (currentEvaluator != newEvaluator) {
			curEvaluatorIndex = index;
			currentEvaluator = newEvaluator;
        }
    }

	/**
	 * {@inheritDoc}
	 * Iterates evolution for the next step and
	 * gets the values of the best individual's fitness 
	 * calculated by the predefined fitness evaluators.
	 */
	@Override
	public List<Double> computeValues() {
		lastReported = evaluate(iterateEvolution().getCandidate());
		printInfo(lastReported);				
		return lastReported;
	}
	
	/**
     * Evaluates all fitness functions on the specified individual
     * @param individual the specified individual
     * @return list of fitness values of the {@code individual}
     */
	private List<Double> evaluate(T individual) {
		return evaluators.stream().map(evaluator -> evaluator.getFitness(individual, seedPopulation)).collect(Collectors.toList());
	}
	
	/**
	 * {@inheritDoc}
	 * Gets the index of the fitness evaluator, which calculates 
	 * the most important parameter
	 * @return the index of the target parameter
	 */
	@Override
	public int getTargetParameter() {
		return targetEvaluatorIndex;
	}
	
	/**
	 * {@inheritDoc}
	 * Gets the index of the evaluator currently used to 
	 * calculate the basic fitness which influences the evolution
	 * @return the index of the current fitness evaluator
	 */
	@Override
	public int getCurrentCriterion() {
		return curEvaluatorIndex;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int parametersCount() {
		return evaluators.size();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getTargetValueInCurrentBest() {
		return evaluators.get(targetEvaluatorIndex).getFitness(getBestIndividual(), seedPopulation);
	}

    /**
     * {@inheritDoc}
     */
    @Override
    public double getBestTargetValue() {
        if (curIteration == iterations) {
            return curBestTargetValue;
        }
        curIteration = iterations;
        double best;
        best = Double.NEGATIVE_INFINITY;
        double cur;
        for (T ind : seedPopulation) {
            cur = evaluators.get(getTargetParameter()).getFitness(ind, seedPopulation);
            best = Math.max(cur, best);
        }
        bestTargetValue = bestTargetValue == null ? bestTargetValue = best : Math.max(bestTargetValue, best);
        curBestTargetValue = best;
        return best;
    }

    @Override
    public double getFinalBestTargetValue() {
        return bestTargetValue;
    }

    /**
     * Gets the individual with the best fitness function
     * among the individuals in the last evolved population.
     * @return the individual with the best fitness function
     */
	public T getBestIndividual() {
		return bestIndividual;
	}
	
	/**
	 * Gets the values of the parameters corresponding to the
	 * last {@link #computeValues()} query
	 * @return values of the parameters after the last {@link #computeValues()} call
	 */
	@Override
	public List<Double> getCurrentBest() {
		return lastReported;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public List<List<Double>> getCurrentPoints() {
		if (changed) {
			currentPoints.clear();
			for (T ind : seedPopulation) {
				currentPoints.add(evaluate(ind));
			}
			changed = false;
		}
		return Collections.unmodifiableList(currentPoints);
	}
	
	/**
	 * Takes the last evaluated population and evolves it over the next generation
	 * @return the fittest candidate in the evolved population
	 */
	public EvaluatedCandidate<T> iterateEvolution() {
		EvaluatedCandidate<T> rv = iterate();
		populationChanged();
		return rv;
	}
	
	protected abstract EvaluatedCandidate<T> iterate();
	
	/**
	 * Gets list of individuals from the list of {@link EvaluatedCandidate}s.
	 * @param evaluated the list of evaluated candidates
	 * @return the list of individuals
	 */
	protected List<T> getPopulation(List<EvaluatedCandidate<T>> evaluated) {
		List<T> population = new ArrayList<>();
        for (EvaluatedCandidate<T> e : evaluated) {
        	population.add(e.getCandidate());
        }
        return population;
	}
	
	/**
	 * Prints information about current state of evolution
	 * @param values 	the values to be printed,
	 * 					typically list of best individual's fitness calculated by 
	 * 					different evaluators
	 */
	private void printInfo(List<Double> values) {
		for (Printer<? super T> printer : printers) {
			printer.print(values, bestIndividual, iterations, curEvaluatorIndex);
		}
	}
		    
	/**
	 * Evolution observer for providing information at the end
	 * of each generation.
	 */
	@SuppressWarnings("WeakerAccess") // see ru.ifmo.ctd.ngp.demo.ffchooser.nonStationary.TSP.GeneticAlgorithmTSP
	public class EvolutionLogger implements EvolutionObserver<T> {
		private EvaluatedCandidate<T> bestCandidate;
	    
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void populationUpdate(PopulationData<? extends T> data) {
	   		T individual = data.getBestCandidate();
	   		double fitness = data.getBestCandidateFitness();
	   		bestCandidate = new EvaluatedCandidate<>(individual, fitness);
		}
	   	
		/**
		 * Gets the best candidate of the last evolved generation
		 * @return the best candidate of the last evolved generation
		 */
		public EvaluatedCandidate<T> getLastBestCandidate() {
		   return bestCandidate;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getIterationsNumber() {
		return iterations;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setEvaluator(int index,
			FitnessEvaluator<? super T> evaluator) {
		evaluators.set(index, evaluator);
	}	
	
	private void populationChanged() {
		changed = true;
	}
}
