package ru.ifmo.ctd.ngp.demo.ffchooser.config;

import org.jetbrains.annotations.NotNull;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.factory.EvaluatorsFactory;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.utils.ConfigurationVisitor;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.utils.PropertyMapped;
import ru.ifmo.ctd.ngp.demo.util.textconstructor.TextConstructor;

import java.io.Serializable;
import java.util.Properties;

/**
 * Abstract configuration for normal and learning-based evolutionary algorithms.
 * 
 * @author Arina Buzdalova
 *
 */
public abstract class Configuration implements Serializable {
	private static final long serialVersionUID = 2862625465616620181L;	
	/**
	 * Maximal number of generations
	 */
	protected final int steps;
	/**
	 * Probability of crossover
	 */
	protected final double crossover;
	/**
	 * Probability of mutation
	 */
	protected final double mutation;
	/**
	 * Length of an individual
	 */
	protected final int length;	 
	
	private final int generationCount;
	
	private final int eliteCount;
    private final EvaluatorsFactory evaluators;

    /**
	 * Constructs {@link Configuration} with the specified {@link Properties}
	 * @param properties the specified properties
	 */
	public Configuration(Properties properties) {
		steps = Integer.parseInt(properties.getProperty("steps"));
		
		crossover = Double.parseDouble(properties.getProperty("crossover"));
		mutation = Double.parseDouble(properties.getProperty("mutation"));
		
		length = Integer.parseInt(properties.getProperty("length"));
		
		generationCount = Integer.parseInt(properties.getProperty("gensize"));
		
		eliteCount = Integer.parseInt(properties.getProperty("elite"));
        evaluators = TextConstructor.constructFromString(EvaluatorsFactory.class, properties.getProperty("evaluators"));

    }
	
	/**
	 * Constructs {@link Configuration} with the specified parameters.
	 * @param steps the maximum number of steps that can be made by the genetic algorithm
	 * @param crossover the probability of crossover
	 * @param mutation the probability of mutation
	 * @param length the length of the individual
	 * @param generationCount the number of individuals in a generation
	 * @param eliteCount the elite count
	 */
	public Configuration(int steps, double crossover, 
			double mutation, int length, int generationCount, int eliteCount, EvaluatorsFactory evaluators) {
		this.steps = steps;
		this.crossover = crossover;
		this.mutation = mutation;
		this.length = length;
		this.generationCount = generationCount;
		this.eliteCount = eliteCount;
        this.evaluators = evaluators;
	}
	
	/**
	 * {@inheritDoc}
	 * Returns string, which describes all the parameters' values
	 */
	@Override
	public String toString() {
		return 
		"stepsLimit " + steps + " crossover " + crossover + " mutation " + mutation + " length " + length;
	}
	
	/**
	 * Generates string representation of the hierarchy which
	 * can be used to store the log files of the runs
	 * @return string representation of the hierarchy for storing the log files
	 */
	public String generatePath() {
		return String.format("len%d%s/mutation%scrossover%s", length, getLabel(), mutation, crossover);
    }
	
	/**
	 * Generates the names of the log files	
	 * @return the names of the log files	
	 */
	public abstract String generateName();
	
	/**
	 * Generates name of the configuration's log with the full path
	 * @return name of the configuration's log with the full path
	 */
	public String generateFullName() {
		return generatePath() + generateName();
	}
    
    /**
	 * Generates string analog of the learning mode that is used in log names
	 * @return string analog of the learning mode that is used in log names	
	 */
	public abstract String getLabel(); 
    
    /**
     * @return the divider
     */
    public abstract int getDivider();

	/**
	 * @return the stepsLimit
	 */
    @PropertyMapped
	public int getSteps() {
		return steps;
	}

    /**
     *
     * @return evaluators
     */
    @PropertyMapped
    public EvaluatorsFactory getEvaluators() {
        return evaluators;
    }
	/**
	 * @return the crossover
	 */
    @PropertyMapped
	public double getCrossover() {
		return crossover;
	}

	/**
	 * @return the mutation
	 */
    @PropertyMapped
	public double getMutation() {
		return mutation;
	}

	/**
	 * @return the length
	 */
    @PropertyMapped
	public int getLength() {
		return length;
	}
    
    /**
     * @return the number of individuals in a generation
     */
    @PropertyMapped("gensize")
    public int getGenerationCount() {
    	return generationCount;
    }
    
    /**
     * @return the elite count
     */
    @PropertyMapped("elite")
    public int getEliteCount() {
    	return eliteCount;
    }

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(crossover);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + eliteCount;
		result = prime * result + generationCount;
		result = prime * result + length;
		temp = Double.doubleToLongBits(mutation);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + steps;
		return result;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Configuration other = (Configuration) obj;
        return Double.doubleToLongBits(crossover) == Double.doubleToLongBits(other.crossover)
                && eliteCount == other.eliteCount
                && generationCount == other.generationCount
                && length == other.length
                && Double.doubleToLongBits(mutation) == Double.doubleToLongBits(other.mutation)
                && steps == other.steps;
    }

	/**
	 * Accepts {@link ConfigurationVisitor}
	 * @param <A> the type of the visitor's argument
	 * @param <R> the return type of the visitor
	 * @param visitor the visitor to be accepted
	 * @param argument the argument to be passed to the visitor
	 * @return the result provided by <code>visitor</code>
	 */
	@NotNull
	public abstract <A, R> R accept(@NotNull ConfigurationVisitor<A, R> visitor, @NotNull A argument);
}
