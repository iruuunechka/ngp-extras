package ru.ifmo.ctd.ngp.demo.ffchooser.config;

import ru.ifmo.ctd.ngp.demo.ffchooser.config.factory.EvaluatorsFactory;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.utils.PropertyMapped;

import java.util.Properties;

/**
 * Abstract configuration for normal and learning-based 
 * evolutionary algorithms.
 * It provides with divisor that can be used in fitness function.
 * 
 * @author Arina Buzdalova
 */
public abstract class DivisorConfiguration extends Configuration {
    private static final long serialVersionUID = 272915312458036898L;

    /**
     * The divisor used in the fitness function
     */
    protected final int divider;
	
    /**
     * Constructs {@link DivisorConfiguration} with the specified parameters
	 * @param steps the maximum number of steps that can be made by the genetic algorithm
	 * @param crossover the probability of crossover
	 * @param mutation the probability of mutation
	 * @param length the length of the individual
     * @param divider the divisor used in fitness function
     * @param generationCount the number of individuals in a generation
     * @param eliteCount the elite count
     */
	public DivisorConfiguration(int steps, double crossover,
			double mutation, int length, int divider, int generationCount, int eliteCount, EvaluatorsFactory evaluators)
    {
		super(steps, crossover, mutation, length, generationCount, eliteCount, evaluators);
		this.divider = divider;			
	}
	
	/**
     * Constructs {@link DivisorConfiguration} with the parameters 
     * provided by the specified properties
     * @param properties the specified properties
     */
	public DivisorConfiguration(Properties properties) {
		super(properties);
		divider = Integer.parseInt(properties.getProperty("divider"));		
	}

	/**
	 * @return the divider used in the fitness function
	 */
    @PropertyMapped
	public int getDivider() {
		return divider;
	}
	
    /**
	 * {@inheritDoc}
	 */
	public String generatePath() {
		return String.format("%s/div%s", super.generatePath(), divider);
    }
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return String.format("%s divider %d", super.toString(), divider);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + divider;
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
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		DivisorConfiguration other = (DivisorConfiguration) obj;
        return divider == other.divider;
    }
}
