package ru.ifmo.ctd.ngp.demo.ffchooser.config;

import java.util.Properties;

import org.jetbrains.annotations.NotNull;

import ru.ifmo.ctd.ngp.demo.ffchooser.config.factory.EvaluatorsFactory;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.utils.ConfigurationVisitor;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.utils.PropertyMapped;
import ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.reward.RewardCalculator;
import ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.state.StateCalculator;
import ru.ifmo.ctd.ngp.learning.reinforce.Agent;
import ru.ifmo.ctd.ngp.learning.reinforce.RAgent;

/**
 * {@link Configuration}, which stores parameters' values for 
 * the R-Learning algorithm.
 * 
 * @author Arina Buzdalova
 * @see RAgent
 */
public class RConfiguration extends LearnConfiguration {
	private static final long serialVersionUID = 2268079488960263951L;
	
	private final double avrate;
	private final double rrate;
	private final double expl;
		
	private final String info;
	
	/**
	 * Constructs {@link RConfiguration} with the specified {@link Properties}.
	 * @param properties the specified properties
	 */
	public RConfiguration(Properties properties) {
		super(properties);
		
		this.avrate = Double.parseDouble(properties.getProperty("avrate"));
		this.rrate = Double.parseDouble(properties.getProperty("rrate"));
		this.expl = Double.parseDouble(properties.getProperty("expl"));		
		
		info = properties.containsKey("info") ? properties.getProperty("info") : "";
	}
	
	/**
	 * Constructs {@link RConfiguration} with the specified parameters
	 * 
	 * @param steps the maximum number of steps that can be made by the genetic algorithm
	 * @param crossover the probability of crossover
	 * @param mutation the probability of mutation
	 * @param length the length of the individual
	 * @param point the switch point for the piecewise functions
	 * @param divider the divider used in the <code> x / divider </code> fitness function
	 * @param avrate the period of refreshing Q-values
	 * @param rrate the rrate reward
	 * @param expl the discount factor
     * @param generationCount the number of individuals in a generation
     * @param eliteCount the elite count
	 */
	public RConfiguration(int steps, double crossover,
			double mutation, int length, int point, int divider, 
			double avrate, double rrate, double expl, int generationCount, int eliteCount) {
		super(steps, crossover, mutation, length, point, divider, generationCount, eliteCount);
		
		this.avrate = avrate;
		this.rrate = rrate;
		this.expl = expl;
		
		this.info = "";
	}
	
	/**
	 * Constructs {@link RConfiguration} with the specified parameters
	 * 
	 * @param steps the maximum number of steps that can be made by the genetic algorithm
	 * @param crossover the probability of crossover
	 * @param mutation the probability of mutation
	 * @param length the length of the individual
	 * @param point the switch point for the piecewise functions
	 * @param divider the divider used in the <code> x / divider </code> fitness function
	 * @param avrate the period of refreshing Q-values
	 * @param rrate the bonus reward
	 * @param expl the discount factor
	 * @param evaluators the factory of fitness evaluators list
     * @param reward the {@link RewardCalculator}
     * @param state the {@link StateCalculator}
     * @param generationCount the number of individuals in a generation
     * @param eliteCount the elite count
	 */
	public RConfiguration(int steps, double crossover,
			double mutation, int length, int point, int divider, 
			double avrate, double rrate, double expl,
			EvaluatorsFactory evaluators, RewardCalculator reward, StateCalculator<String, Integer> state,
			int generationCount, int eliteCount) {
		super(steps, crossover, mutation, length, point, divider, evaluators, reward, state,
				generationCount, eliteCount);
		
		this.avrate = avrate;
		this.rrate = rrate;
		this.expl = expl;
		
		this.info = "";
	}
	
	/**
	 * Constructs {@link RConfiguration} with the specified parameters
	 * and the specified additional information
	 * 
	 * @param steps the maximum number of steps that can be made by the genetic algorithm
	 * @param crossover the probability of crossover
	 * @param mutation the probability of mutation
	 * @param length the length of the individual
	 * @param point the switch point for the piecewise functions
	 * @param divider the divider used in the <code> x / divider </code> fitness function
	 * @param avrate the period of refreshing Q-values
	 * @param rrate the bonus reward
	 * @param expl the discount factor
	 * @param info the specified additional information
     * @param generationCount the number of individuals in a generation
     * @param eliteCount the elite count
	 */
	public RConfiguration(int steps, double crossover,
			double mutation, int length, int point, int divider, 
			double avrate, double rrate, double expl, String info,
			int generationCount, int eliteCount) {
		super(steps, crossover, mutation, length, point, divider, generationCount, eliteCount);
		
		this.avrate = avrate;
		this.rrate = rrate;
		this.expl = expl;
		
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
		return new RAgent<>(avrate, rrate, expl);
	}
	
    /**
     * {@inheritDoc}
     */
	@Override
	public String listLearnValues() {
		return "avrate " + avrate + " rrate " + rrate + " expl " + expl;
	}
	
	/**
	 * {@inheritDoc}
	 */
    @Deprecated
	@Override
	public String generateName() {
		return "/avrate" + avrate + "rrate" + rrate + "expl" + expl;
	}
    
    /**
     * {@inheritDoc}
     */
	@Override
	public String getLabel() {
		return "average";
	}

	/**
	 * @return the the period of refreshing Q-values
	 */
    @PropertyMapped
	public double getAvrate() {
		return avrate;
	}

	/**
	 * @return the period of refreshing average reward estimation
	 */
    @PropertyMapped
	public double getRrate() {
		return rrate;
	}

	/**
	 * @return the probability of exploration
	 */
    @PropertyMapped
	public double getExpl() {
		return expl;
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
		temp = Double.doubleToLongBits(rrate);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(expl);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(avrate);
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
		RConfiguration other = (RConfiguration) obj;
        return Double.doubleToLongBits(rrate) == Double.doubleToLongBits(other.rrate) &&
                Double.doubleToLongBits(expl) == Double.doubleToLongBits(other.expl) &&
                Double.doubleToLongBits(avrate) == Double.doubleToLongBits(other.avrate);
    }

	/**
	 * {@inheritDoc}
	 */
    @NotNull
    @Override
    public <A, R> R accept(@NotNull ConfigurationVisitor<A, R> visitor, @NotNull A argument) {
        return visitor.visitRConfiguration(this, argument);
    }
}
