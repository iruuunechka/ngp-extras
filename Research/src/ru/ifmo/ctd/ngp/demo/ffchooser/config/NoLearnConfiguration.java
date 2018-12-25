package ru.ifmo.ctd.ngp.demo.ffchooser.config;

import java.util.Properties;

import org.jetbrains.annotations.NotNull;

import ru.ifmo.ctd.ngp.demo.ffchooser.config.factory.EvaluatorsFactory;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.utils.ConfigurationVisitor;


/**
 * Configuration for evolutionary algorithms designed to be
 * compared with learning-based algorithms.
 * 
 * It provides with a divisor that can be used in the fitness function.
 * 
 * @author Arina Buzdalova
 *
 */
public class NoLearnConfiguration extends DivisorConfiguration {
    private static final long serialVersionUID = -5547179743896539487L;
    /**
     * Constructs {@link NoLearnConfiguration} with the specified parameters
     * @param steps the maximum number of steps that can be made by the genetic algorithm
     * @param crossover the probability of crossover
     * @param mutation the probability of mutation
     * @param length the length of the individual
     * @param divider the divisor used in fitness function
     * @param generationCount the number of individuals in a generation
     * @param eliteCount the elite count
     */
    public NoLearnConfiguration(int steps, double crossover,
                                double mutation, int length, int divider, int generationCount, int eliteCount,
								EvaluatorsFactory evaluators) {
		super(steps, crossover, mutation, length, divider, generationCount, eliteCount, evaluators);
    }
    
    /**
     * Constructs {@link NoLearnConfiguration} with the parameters 
     * provided by the specified properties
     * @param properties the specified properties
     */
	public NoLearnConfiguration(Properties properties){
		super(properties);
	}

	/**
	 * {@inheritDoc}
	 */
	@Deprecated
	@Override
	public String generateName() {
		return "";
	}
	
	/**
     * {@inheritDoc}
     */
	@Override
	public String getLabel() {
		return "none";
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
        return this == obj || obj != null && getClass() == obj.getClass() && super.equals(obj);
    }
	
	/**
	 * {@inheritDoc}
	 */
	@NotNull
    @Override
    public <A, R> R accept(@NotNull ConfigurationVisitor<A, R> visitor, @NotNull A argument) {
        return visitor.visitNoLearnConfiguration(this, argument);
    }
}
