package ru.ifmo.ctd.ngp.demo.ffchooser.config;

import org.jetbrains.annotations.NotNull;

import ru.ifmo.ctd.ngp.demo.ffchooser.config.factory.EvaluatorsFactory;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.utils.ConfigurationVisitor;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.utils.PropertyMapped;
import ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.reward.RewardCalculator;
import ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.state.StateCalculator;
import ru.ifmo.ctd.ngp.learning.reinforce.Agent;
import ru.ifmo.ctd.ngp.learning.reinforce.q.EGreedyAgent;

import java.util.Properties;

/**
 * <p>
 * {@link Configuration}, which stores parameters' values for 
 * the epsilon-greedy Q-Learning algorithm.
 * 
 * @author Arina Buzdalova
 */
public class GreedyConfiguration extends LearnConfiguration {
	private static final long serialVersionUID = -8561424063665889026L;
	
	private final double epsilon;
	private final double alpha;
	private final double gamma;
	
	/**
	 * Constructs {@link GreedyConfiguration} with the specified {@link Properties}
	 * @param properties the specified properties
	 */
	public GreedyConfiguration(Properties properties) {
		super(properties);
		
		this.epsilon = Double.parseDouble(properties.getProperty("epsilon"));
		this.alpha = Double.parseDouble(properties.getProperty("alpha"));
		this.gamma = Double.parseDouble(properties.getProperty("gamma"));		
	}
	
	/**
	 * Constructs {@link GreedyConfiguration} with the specified parameters.
	 * 
	 * @param steps the maximum number of steps that can be made by the genetic algorithm
	 * @param crossover the probability of crossover
	 * @param mutation the probability of mutation
	 * @param length the length of the individual
	 * @param point the switch point for the piecewise functions
	 * @param divider the divider used in the <code> x / divider </code> fitness function
	 * @param eps the probability of exploration
	 * @param alpha the learning rate
	 * @param gamma the discount factor
	 * @param generationCount the number of individuals in a generation
     * @param eliteCount the elite count
	 */
	public GreedyConfiguration(int steps, double crossover,
			double mutation, int length, int point, int divider,
			double eps, double alpha, double gamma, int generationCount, int eliteCount) {
		super(steps, crossover, mutation, length, point, divider, generationCount, eliteCount);
		
		this.epsilon = eps;
		this.alpha = alpha;
		this.gamma = gamma;
	}
	
	/**
	 * Constructs {@link GreedyConfiguration} with the specified parameters.
	 * 
	 * @param steps the maximum number of steps that can be made by the genetic algorithm
	 * @param crossover the probability of crossover
	 * @param mutation the probability of mutation
	 * @param length the length of the individual
	 * @param point the switch point for the piecewise functions
	 * @param divider the divider used in the <code> x / divider </code> fitness function
	 * @param eps the probability of exploration
	 * @param alpha the learning rate
	 * @param gamma the discount factor
     * @param evaluators the factory of fitness evaluators list
     * @param reward the {@link RewardCalculator}
     * @param state the {@link StateCalculator}
     * @param generationCount the number of individuals in a generation
     * @param eliteCount the elite count
	 */
	public GreedyConfiguration(int steps, double crossover,
			double mutation, int length, int point, int divider,
			double eps, double alpha, double gamma,
			EvaluatorsFactory evaluators, RewardCalculator reward, StateCalculator<String, Integer> state,
			int generationCount, int eliteCount) {
		super(steps, crossover, mutation, length, point, divider, evaluators, reward, state, 
				generationCount, eliteCount);
		
		this.epsilon = eps;
		this.alpha = alpha;
		this.gamma = gamma;
	}

	/**
	 * {@inheritDoc}
	 */
    @Override
	public Agent<String, Integer> createAgent() {
		return new EGreedyAgent<>(epsilon, 1.0, alpha, gamma);
	}
    
    /**
	 * {@inheritDoc}
	 */
	@Deprecated
	@Override
	public String generateName() {
		return "/eps" + epsilon + "alpha" + alpha + "gamma" + gamma;
	}
	
	/**
     * {@inheritDoc}
     */
	@Override
	public String getLabel() {
		return "greedy";
	}
	
    /**
     * {@inheritDoc}
     */
	@Override
	protected String listLearnValues() {
		return "epsilon " + epsilon + " alpha " + alpha + " gamma " + gamma;
	}	
	
	/**
	 * @return the eps
	 */
    @PropertyMapped
	public double getEpsilon() {
		return epsilon;
	}

	/**
	 * @return the alpha
	 */
    @PropertyMapped
	public double getAlpha() {
		return alpha;
	}

	/**
	 * @return the gamma
	 */
    @PropertyMapped
	public double getGamma() {
		return gamma;
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		long temp;
		temp = Double.doubleToLongBits(alpha);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(epsilon);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(gamma);
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
		GreedyConfiguration other = (GreedyConfiguration) obj;
        return Double.doubleToLongBits(alpha) == Double.doubleToLongBits(other.alpha) &&
                Double.doubleToLongBits(epsilon) == Double.doubleToLongBits(other.epsilon) &&
                Double.doubleToLongBits(gamma) == Double.doubleToLongBits(other.gamma);
    }

	/**
	 * {@inheritDoc}
	 */
    @NotNull
    @Override
    public <A, R> R accept(@NotNull ConfigurationVisitor<A, R> visitor, @NotNull A argument) {
        return visitor.visitGreedyConfiguration(this, argument);
    }
}
