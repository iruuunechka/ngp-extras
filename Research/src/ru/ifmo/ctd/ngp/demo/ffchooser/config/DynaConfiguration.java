package ru.ifmo.ctd.ngp.demo.ffchooser.config;

import java.util.Properties;

import org.jetbrains.annotations.NotNull;

import ru.ifmo.ctd.ngp.demo.ffchooser.config.factory.EvaluatorsFactory;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.utils.ConfigurationVisitor;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.utils.PropertyMapped;
import ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.reward.RewardCalculator;
import ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.state.StateCalculator;
import ru.ifmo.ctd.ngp.learning.reinforce.Agent;
import ru.ifmo.ctd.ngp.learning.reinforce.DynaAgent;

/**
 * {@link Configuration}, which stores parameters' values for 
 * the Dyna algorithm.
 *  
 * @author Arina Buzdalova
 * @see DynaAgent
 */
public class DynaConfiguration extends LearnConfiguration {
	private static final long serialVersionUID = 2268079488960263951L;
	
	private final double probability;
	private final double discount;
	private final int k;
		
	private final String info;
	
	/**
	 * Constructs {@link DynaConfiguration} with the specified {@link Properties}.
	 * @param properties the specified properties
	 */
	public DynaConfiguration(Properties properties) {
		super(properties);
		
		this.probability = Double.parseDouble(properties.getProperty("probability"));
		this.discount = Double.parseDouble(properties.getProperty("discount"));
		this.k = Integer.parseInt(properties.getProperty("k"));		
		
		info = properties.containsKey("info") ? properties.getProperty("info") : "";
	}
	
	/**
	 * Constructs {@link DynaConfiguration} with the specified parameters
	 * 
	 * @param steps the maximum number of steps that can be made by the genetic algorithm
	 * @param crossover the probability of crossover
	 * @param mutation the probability of mutation
	 * @param length the length of the individual
	 * @param point the switch point for the piecewise functions
	 * @param divider the divider used in the <code> x / divider </code> fitness function
	 * @param probability the probability of exploration
	 * @param discount the discount factor
	 * @param k the number of updated state-action values
     * @param generationCount the number of individuals in a generation
     * @param eliteCount the elite count
	 */
	public DynaConfiguration(int steps, double crossover,
			double mutation, int length, int point, int divider, 
			double probability, double discount, int k, int generationCount, int eliteCount) {
		super(steps, crossover, mutation, length, point, divider, generationCount, eliteCount);
		
		this.probability = probability;
		this.discount = discount;
		this.k = k;
		
		this.info = "";
	}
	
	/**
	 * Constructs {@link DynaConfiguration} with the specified parameters
	 * 
	 * @param steps the maximum number of steps that can be made by the genetic algorithm
	 * @param crossover the probability of crossover
	 * @param mutation the probability of mutation
	 * @param length the length of the individual
	 * @param point the switch point for the piecewise functions
	 * @param divider the divider used in the <code> x / divider </code> fitness function
	 * @param probability the probability of exploration
	 * @param discount the discount factor
	 * @param k the number of updated state-action values
	 * @param evaluators the factory of fitness evaluators list
     * @param reward the {@link RewardCalculator}
     * @param state the {@link StateCalculator}
     * @param generationCount the number of individuals in a generation
     * @param eliteCount the elite count
	 */
	public DynaConfiguration(int steps, double crossover,
			double mutation, int length, int point, int divider, 
			double probability, double discount, int k,
			EvaluatorsFactory evaluators, RewardCalculator reward, StateCalculator<String, Integer> state,
			int generationCount, int eliteCount) {
		super(steps, crossover, mutation, length, point, divider, evaluators, reward, state,
				generationCount, eliteCount);
		
		this.probability = probability;
		this.discount = discount;
		this.k = k;
		
		this.info = "";
	}
	
	/**
	 * Constructs {@link DynaConfiguration} with the specified parameters
	 * and the specified additional information
	 * 
	 * @param steps the maximum number of steps that can be made by the genetic algorithm
	 * @param crossover the probability of crossover
	 * @param mutation the probability of mutation
	 * @param length the length of the individual
	 * @param point the switch point for the piecewise functions
	 * @param divider the divider used in the <code> x / divider </code> fitness function
	 * @param probability the probability of exploration
	 * @param discount the discount factor
	 * @param k the number of updated state-action values
	 * @param info the specified additional information
     * @param generationCount the number of individuals in a generation
     * @param eliteCount the elite count
	 */
	public DynaConfiguration(int steps, double crossover,
			double mutation, int length, int point, int divider, 
			double probability, double discount, int k, String info,
			int generationCount, int eliteCount) {
		super(steps, crossover, mutation, length, point, divider, generationCount, eliteCount);
		
		this.probability = probability;
		this.discount = discount;
		this.k = k;
		
		this.info = info;
	}
    
	/**
	 * Gets the additional information about this configuration
	 * @return the additional information about this configuration
	 */
    public String getInfo() {
        return info;
    }

    /**
     * {@inheritDoc}
     */
    @Override
	public Agent<String, Integer> createAgent() {
		return new DynaAgent<>(probability, discount, k);
	}
	
    /**
     * {@inheritDoc}
     */
	@Override
	public String listLearnValues() {
		return "probab " + probability + " discount " + discount + " k " + k;
	}
	
	/**
	 * {@inheritDoc}
	 */
    @Deprecated
	@Override
	public String generateName() {
		return "/probability" + probability + "discount" + discount + "k" + k;
	}
    
    /**
     * {@inheritDoc}
     */
	@Override
	public String getLabel() {
		return "dyna";
	}

	/**
	 * @return the probability of exploration
	 */
    @PropertyMapped
	public double getProbability() {
		return probability;
	}

	/**
	 * @return the discount factor
	 */
    @PropertyMapped
	public double getDiscount() {
		return discount;
	}

	/**
	 * @return the number of state-action values being updated
	 */
    @PropertyMapped
	public int getK() {
		return k;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return super.toString() + " " + info;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		long temp;
		temp = Double.doubleToLongBits(discount);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(k);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(probability);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		DynaConfiguration other = (DynaConfiguration) obj;
        return Double.doubleToLongBits(discount) == Double.doubleToLongBits(other.discount) &&
                Double.doubleToLongBits(k) == Double.doubleToLongBits(other.k) &&
                Double.doubleToLongBits(probability) == Double.doubleToLongBits(other.probability);
    }

	/**
	 * {@inheritDoc}
	 */
    @NotNull
    @Override
    public <A, R> R accept(@NotNull ConfigurationVisitor<A, R> visitor, @NotNull A argument) {
        return visitor.visitDynaConfiguration(this, argument);
    }
}
