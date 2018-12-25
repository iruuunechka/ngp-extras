package ru.ifmo.ctd.ngp.demo.ffchooser.genetic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.uncommons.watchmaker.framework.*;
import org.uncommons.watchmaker.framework.termination.GenerationCount;
import org.uncommons.watchmaker.framework.termination.TargetFitness;
import ru.ifmo.ctd.ngp.demo.util.strings.ObjStringFactory;
import ru.ifmo.ctd.ngp.demo.ffchooser.GenerationsCounter;
import ru.ifmo.ctd.ngp.demo.ffchooser.xvector.BitCountFitness;
import ru.ifmo.ctd.ngp.demo.generators.SetMemberGenerator;
import ru.ifmo.ctd.ngp.demo.legacy_ds.string.impl.BitString;
import ru.ifmo.ctd.ngp.util.CollectionsEx;
import ru.ifmo.ctd.ngp.util.FastRandom;

/**
 * <p>
 * A class for counting number of generations needed 
 * for evolutionary algorithm to evolve the ideal individual 
 * in the "strings" problem without learning.
 * </p><p>
 * It uses normal, non-incremental, evolutionary algorithms from Watchmaker library.
 * </p>
 * @author Arina Buzdalova
 */
public abstract class NoLearningCounter implements GenerationsCounter<FitnessEvaluator<? super BitString>, BitString> {
	protected final Random rng;
	private final int maxSteps;
	private final int generationSize;
	private final int eliteCount;
	private final EvolutionLogger observer;
	private List<BitString> seedPopulation;

	final EvolutionaryOperator<BitString> pipeline;

	protected FitnessEvaluator<? super BitString> evaluator;
	private double idealFitness;
	
	protected CandidateFactory<BitString> factory;
	
	private final List<Printer<? super BitString>> printers;

	/**
     * Constructs {@link NoLearningCounter} with the specified steps limit,
     * size of generation, elite count and evolutionary operators.
     * If the {@code stepsLimit} is reached, counting stops.
     * @param stepsLimit the maximal number of generations that can be evolved
     * @param generationSize the size of a generation
     * @param eliteCount the number of candidates kept by elitism
     * @param pipeline the evolutionary operators
     */
	NoLearningCounter(
			int stepsLimit, 
			int generationSize, 
			int eliteCount, 
			EvolutionaryOperator<BitString> pipeline
	) {
        this.rng = FastRandom.threadLocal();
        this.maxSteps = stepsLimit;
		this.generationSize = generationSize;		
		this.eliteCount = eliteCount;		
		this.pipeline = pipeline;		
		this.observer = new EvolutionLogger();
		this.seedPopulation = new ArrayList<>();
		this.printers = new ArrayList<>();
	}
	
	/**
	 * Adds the specified printer to observe the evolutionary algorithm running
	 * @param printer the specified printer to observe the evolutionary algorithm running
	 */
	public void addPrinter(Printer<? super BitString> printer) {
		printers.add(printer);
	}
	
	/**
	 * Removes the specified printer so it stops observing the evolutionary algorithm
	 * @param printer the specified printer to be removed
	 */
	public void removePrinter(Printer<? super BitString> printer) {
		printers.remove(printer);
	}
	
	/**
     * Sets the fitness evaluator and the corresponding ideal fitness value
     * @param evaluator the fitness evaluator
     * @param idealFitness the ideal fitness value corresponding to {@code evaluator}
     */
	public void setEvaluator(FitnessEvaluator<? super BitString> evaluator, double idealFitness) {
		this.evaluator = evaluator;
		this.idealFitness = idealFitness;
	}
	
	/**
	 * Sets the length of an individual
	 * @param length the length of an individual
	 */
	public void setLength(int length) {
		this.factory = ObjStringFactory.create(
                length,
                SetMemberGenerator.newGen(CollectionsEx.listOf(false, true)),
                BitString.empty()
        );
	}
	
	/**
	 * Sets the population, from which the algorithm starts. 
	 * If it is empty, then random individuals will be used.
	 * @param population the start population
	 */
	public void setStartPopulation(List<BitString> population) {
		this.seedPopulation = population;
	}
	
	/**
     * <p>
     * Counts number of generations to be evolved in order to reach the current ideal fitness
     * using the current fitness evaluator.
     * </p><p>
     * Fitness evaluator and ideal fitness are set by {@link #setEvaluator} method.
     * The length of each individual is set by {@link #setLength} method.
     * These two methods should be called at least once before trying to count number of generations.
     * </p>
     * @return number of generations to be evolved in order to reach the ideal fitness, or {@code -1}
     * if the ideal fitness wasn't reached after maximal number of generations
     */
	public int countGenerations() {
		
		if (factory == null) {
			throw new RuntimeException("Length of the individual is not set. Use setLength method.");
		}
		
		if (evaluator == null) {
			throw new RuntimeException("Fitness evaluator is not set. Use setEvaluator method.");
		}
		
		AbstractEvolutionEngine<BitString> engine = getEngine();
        
        engine.addEvolutionObserver(observer);       

        engine.setSingleThreaded(false);
        
        engine.evolve(
        		generationSize, 
        		eliteCount, 
        		seedPopulation, 
        		new GenerationCount(maxSteps), 
        		new TargetFitness(idealFitness, true));
        
        if (observer.getBestFitness() == idealFitness) {
        	return observer.getGenerationsNumber();
        } else {
        	return -1;
        }
	}
	
	protected abstract AbstractEvolutionEngine<BitString> getEngine();
	
	/**
	 * Gets the number of evaluators this logger uses to print different values
	 * @return the number of evaluators this logger uses to print different values
	 */
	@Override
	public int getEvaluatorsCount() {
		return 2;
	}
	
	/**
	 * Gets the index of the target evaluator
	 * @return the index of the target evaluator
	 */
	@Override
	public int getTargetIndex() {
		return 0;
	}
	
	/**
	 * Evolution observer for printing information at the end
	 * of each generation and counting the number of evolved generations
	 */
	private class EvolutionLogger implements EvolutionObserver<BitString> {
		private int genNum;	  
		private double bestFitness;
		
		@Override
		public void populationUpdate(PopulationData<? extends BitString> data) {
			genNum = data.getGenerationNumber();
	   		bestFitness = data.getBestCandidateFitness();
	   		BitString cand = data.getBestCandidate();
	   		for (Printer<? super BitString> printer : printers) {
	   			printer.print(Arrays.asList(bestFitness, (new BitCountFitness(1.0, 0.0)).getFitness(cand, null)), 
	   					cand, genNum + 1, 0);
	   		}
		}

		/**
		 * Returns the number of the last evolved generation
		 * @return the number of the last evolved generation
		 */
		int getGenerationsNumber() {
			return genNum;
		}

		/**
		 * Returns the best fitness in the last evolved generation
		 * @return the best fitness in the last evolved generation
		 */
		double getBestFitness() {
			return bestFitness;
		}
	}
}
