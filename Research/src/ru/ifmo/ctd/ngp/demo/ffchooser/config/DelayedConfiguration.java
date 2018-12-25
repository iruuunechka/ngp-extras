package ru.ifmo.ctd.ngp.demo.ffchooser.config;

import java.util.Properties;

import org.jetbrains.annotations.NotNull;

import ru.ifmo.ctd.ngp.demo.ffchooser.config.factory.EvaluatorsFactory;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.utils.ConfigurationVisitor;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.utils.PropertyMapped;
import ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.reward.RewardCalculator;
import ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.state.StateCalculator;
import ru.ifmo.ctd.ngp.learning.reinforce.Agent;
import ru.ifmo.ctd.ngp.learning.reinforce.q.DelayedAgent;
import ru.ifmo.ctd.ngp.learning.reinforce.q.DelayedParameters;

/**
 * {@link Configuration}, which stores parameters' values for 
 * the Delayed Q-Learning algorithm.
 * 
 * @author Arina Buzdalova
 * @see DelayedParameters
 */
public class DelayedConfiguration extends LearnConfiguration {
	private static final long serialVersionUID = 2268079488960263951L;
	
	private final double period;
	private final double bonus;
	private final double factor;
		
	private final String info;
	
	/**
	 * Constructs {@link DelayedConfiguration} with the specified {@link Properties}.
	 * @param properties the specified properties
	 */
	public DelayedConfiguration(Properties properties) {
		super(properties);
		
		this.period = Double.parseDouble(properties.getProperty("period"));
		this.bonus = Double.parseDouble(properties.getProperty("bonus"));
		this.factor = Double.parseDouble(properties.getProperty("factor"));		
		
		info = properties.containsKey("info") ? properties.getProperty("info") : "";
	}
	
	/**
	 * Constructs {@link DelayedConfiguration} with the specified parameters
	 * 
	 * @param steps the maximum number of steps that can be made by the genetic algorithm
	 * @param crossover the probability of crossover
	 * @param mutation the probability of mutation
	 * @param length the length of the individual
	 * @param point the switch point for the piecewise functions
	 * @param divider the divider used in the <code> x / divider </code> fitness function
	 * @param period the period of refreshing Q-values
	 * @param bonus the bonus reward
	 * @param factor the discount factor
     * @param generationCount the number of individuals in a generation
     * @param eliteCount the elite count
	 */
	public DelayedConfiguration(int steps, double crossover,
			double mutation, int length, int point, int divider, 
			double period, double bonus, double factor, int generationCount, int eliteCount) {
		super(steps, crossover, mutation, length, point, divider, generationCount, eliteCount);
		
		this.period = period;
		this.bonus = bonus;
		this.factor = factor;
		
		this.info = "";
	}
	
	/**
	 * Constructs {@link DelayedConfiguration} with the specified parameters
	 * 
	 * @param steps the maximum number of steps that can be made by the genetic algorithm
	 * @param crossover the probability of crossover
	 * @param mutation the probability of mutation
	 * @param length the length of the individual
	 * @param point the switch point for the piecewise functions
	 * @param divider the divider used in the <code> x / divider </code> fitness function
	 * @param period the period of refreshing Q-values
	 * @param bonus the bonus reward
	 * @param factor the discount factor
	 * @param evaluators the factory of fitness evaluators list
     * @param reward the {@link RewardCalculator}
     * @param state the {@link StateCalculator}
     * @param generationCount the number of individuals in a generation
     * @param eliteCount the elite count
	 */
	public DelayedConfiguration(int steps, double crossover,
			double mutation, int length, int point, int divider, 
			double period, double bonus, double factor,
			EvaluatorsFactory evaluators, RewardCalculator reward, StateCalculator<String, Integer> state,
			int generationCount, int eliteCount) {
		super(steps, crossover, mutation, length, point, divider, evaluators, reward, state,
				generationCount, eliteCount);
		
		this.period = period;
		this.bonus = bonus;
		this.factor = factor;
		
		this.info = "";
	}
	
	/**
	 * Constructs {@link DelayedConfiguration} with the specified parameters
	 * and the specified additional information
	 * 
	 * @param steps the maximum number of steps that can be made by the genetic algorithm
	 * @param crossover the probability of crossover
	 * @param mutation the probability of mutation
	 * @param length the length of the individual
	 * @param point the switch point for the piecewise functions
	 * @param divider the divider used in the <code> x / divider </code> fitness function
	 * @param period the period of refreshing Q-values
	 * @param bonus the bonus reward
	 * @param factor the discount factor
	 * @param info the specified additional information
     * @param generationCount the number of individuals in a generation
     * @param eliteCount the elite count
	 */
	public DelayedConfiguration(int steps, double crossover,
			double mutation, int length, int point, int divider, 
			double period, double bonus, double factor, String info,
			int generationCount, int eliteCount) {
		super(steps, crossover, mutation, length, point, divider, generationCount, eliteCount);
		
		this.period = period;
		this.bonus = bonus;
		this.factor = factor;
		
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
		return new DelayedAgent<>(0, factor, (int) period, bonus);
	}
	
    /**
     * {@inheritDoc}
     */
	@Override
	public String listLearnValues() {
		return "period " + period + " bonus " + bonus + " factor " + factor;
	}
	
	/**
	 * {@inheritDoc}
	 */
    @Deprecated
	@Override
	public String generateName() {
		return "/period" + period + "bonus" + bonus + "discount" + factor;
	}
    
    /**
     * {@inheritDoc}
     */
	@Override
	public String getLabel() {
		return "delayed";
	}

	/**
	 * @return the period
	 */
    @PropertyMapped
	public double getPeriod() {
		return period;
	}

	/**
	 * @return the bonus
	 */
    @PropertyMapped
	public double getBonus() {
		return bonus;
	}

	/**
	 * @return the factor (discount factor)
	 */
    @PropertyMapped
	public double getFactor() {
		return factor;
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
		temp = Double.doubleToLongBits(bonus);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(factor);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(period);
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
		DelayedConfiguration other = (DelayedConfiguration) obj;
        return Double.doubleToLongBits(bonus) == Double.doubleToLongBits(other.bonus) &&
                Double.doubleToLongBits(factor) == Double.doubleToLongBits(other.factor) &&
                Double.doubleToLongBits(period) == Double.doubleToLongBits(other.period);
    }

	/**
	 * {@inheritDoc}
	 */
    @NotNull
    @Override
    public <A, R> R accept(@NotNull ConfigurationVisitor<A, R> visitor, @NotNull A argument) {
        return visitor.visitDelayedConfiguration(this, argument);
    }
}
