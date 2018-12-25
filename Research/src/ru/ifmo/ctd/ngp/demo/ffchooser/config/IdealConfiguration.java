package ru.ifmo.ctd.ngp.demo.ffchooser.config;

import java.util.Properties;

import org.jetbrains.annotations.NotNull;

import ru.ifmo.ctd.ngp.demo.ffchooser.config.factory.EvaluatorsFactory;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.utils.ConfigurationVisitor;

/**
 * Configuration that corresponds to a normal evolutionary algorithm.
 * 
 * @author Arina Buzdalova
 */
public class IdealConfiguration extends Configuration {
	private static final long serialVersionUID = 2898306426676116116L;

	/**
	 * Constructs {@link IdealConfiguration} with the specified parameters.
	 * 
	 * @param steps the maximum number of steps that can be made by the genetic algorithm
	 * @param crossover the probability of crossover
	 * @param mutation the probability of mutation
	 * @param length the length of the individual
	 * @param generationCount the number of individuals in a generation
     * @param eliteCount the elite count
	 */
	public IdealConfiguration(int steps, double crossover,
			double mutation, int length, int generationCount, int eliteCount, EvaluatorsFactory evaluators
    ) {
		super(steps, crossover, mutation, length, generationCount, eliteCount, evaluators);
	}
	
	/**
	 * Constructs {@link IdealConfiguration} with the specified {@link Properties}. 
	 * @param properties the specified properties
	 */
	public IdealConfiguration(Properties properties) {
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
		return "ideal";
	}
	
	/**
	 * Constant value 1, as there is no divisor in the ideal fitness function
	 */
	@Override
	public int getDivider() {
		return 1;
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
        return visitor.visitIdealConfiguration(this, argument);
    }
}
