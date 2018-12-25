package ru.ifmo.ctd.ngp.demo.ffchooser.config;

import java.util.Properties;

import ru.ifmo.ctd.ngp.demo.ffchooser.config.factory.EvaluatorsFactory;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.utils.PropertyMapped;
import ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.reward.ComplexFixedReward;
import ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.reward.RewardCalculator;
import ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.state.StateCalculator;
import ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.state.VectorState;
import ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.evaluators.SwitchPointEvaluatorsFactory;
import ru.ifmo.ctd.ngp.learning.reinforce.Agent;
import ru.ifmo.ctd.ngp.demo.util.textconstructor.TextConstructor;

/**
 * Configuration for algorithms that use reinforcement learning.
 * It provides with {@link Agent} and switch point.
 * 
 * @author Arina Buzdalova
 */
public abstract class LearnConfiguration extends DivisorConfiguration {
    private static final long serialVersionUID = -6800049024062690881L;    
    private final int point;
	//private final EvaluatorsFactory evaluators;
	private final RewardCalculator reward;
	private final StateCalculator<String, Integer> state;

    /**
     * Constructs {@link LearnConfiguration} with the specified parameters
	 * @param steps the maximum number of steps that can be made by the genetic algorithm
	 * @param crossover the probability of crossover
	 * @param mutation the probability of mutation
	 * @param length the length of the individual
     * @param point the switch point where supporting fitness functions change their behavior 
     * @param divider the divisor used in fitness function
     * @param generationCount the number of individuals in a generation
     * @param eliteCount the elite count
     */
	public LearnConfiguration(int steps, double crossover,
			double mutation, int length, int point, int divider, int generationCount, int eliteCount) {
		this(steps, crossover, mutation, length, point, divider, 
				new SwitchPointEvaluatorsFactory(divider, length, point),
				new ComplexFixedReward(0, 0.5, 1, 0.5),
				new VectorState(3), generationCount, eliteCount);
	}
	
    /**
     * Constructs {@link LearnConfiguration} with the specified parameters
	 * @param steps the maximum number of steps that can be made by the genetic algorithm
	 * @param crossover the probability of crossover
	 * @param mutation the probability of mutation
	 * @param length the length of the individual
     * @param point the switch point where supporting fitness functions change their behavior 
     * @param divider the divisor used in fitness function
     * @param evaluators the factory of fitness evaluators list
     * @param reward the {@link RewardCalculator}
     * @param state the {@link StateCalculator}
     * @param generationCount the number of individuals in a generation
     * @param eliteCount the elite count
     */
	public LearnConfiguration(int steps, double crossover,
			double mutation, int length, int point, int divider, 
			EvaluatorsFactory evaluators, RewardCalculator reward, StateCalculator<String, Integer> state,
			int generationCount, int eliteCount
    ) {
		super(steps, crossover, mutation, length, divider, generationCount, eliteCount, evaluators);
		this.point = point;
		//this.evaluators = evaluators;
		this.reward = reward;
		this.state = state;
	}
	
	/**
     * Constructs {@link LearnConfiguration} with the parameters 
     * provided by the specified properties
     * @param properties the specified properties
     */
	public LearnConfiguration(Properties properties) {
		super(properties);
		
		this.point = Integer.parseInt(properties.getProperty("point"));
		
//		this.evaluators = TextConstructor.constructFromString(EvaluatorsFactory.class,
//				properties.getProperty("evaluators"));
		
		this.reward = TextConstructor.constructFromString(RewardCalculator.class,
				properties.getProperty("reward"));

        //noinspection unchecked
        this.state = TextConstructor.constructFromString(StateCalculator.class,
				(properties.getProperty("state")));
	}
	
	/**
	 * Returns the {@link Agent} corresponding to the learning mode of this configuration 
	 * @return the {@link Agent} corresponding to the learning mode of this configuration
	 */
	public abstract Agent<String, Integer> createAgent();
	
	/**
	 * Gets the switch point
	 * @return the switch point
	 */
    @PropertyMapped
	public int getPoint() {
		return point;		
	}
    
//    /**
//	 * @return the factory of fitness evaluators list
//	 */
//    @PropertyMapped
//	public EvaluatorsFactory getEvaluators() {
//		return this.evaluators;
//	}
    
    /**
	 * @return the {@link RewardCalculator}
	 */
    @PropertyMapped
	public RewardCalculator getReward() {
		return reward;
	}
    
    /**
	 * @return the {@link StateCalculator}
	 */
    @PropertyMapped
	public StateCalculator<String, Integer> getState() {
		return state;
	}
	
    /**
     * {@inheritDoc}
     */
	public String generatePath() {
		return String.format("%spoint%s-%s-%s-%s", super.generatePath(), point, 
				this.getEvaluators().getName(), reward.getName(), state.getName());
    }
	
	/**
	 * Returns the string representation of the learning parameters values
	 * @return the string representation of the learning parameters values
	 */
	protected abstract String listLearnValues();
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return String.format("%s switchPoint %d %s %s %s %s", super.toString(), point, listLearnValues(), 
				this.getEvaluators().getName(), reward.getName(), state.getName());
	}	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((getEvaluators() == null) ? 0 : getEvaluators().hashCode());
		result = prime * result + point;
		result = prime * result + ((reward == null) ? 0 : reward.hashCode());
		result = prime * result + ((state == null) ? 0 : state.hashCode());
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
		LearnConfiguration other = (LearnConfiguration) obj;
		if (getEvaluators() == null) {
			if (other.getEvaluators() != null) {
				return false;
			}
		} else if (!getEvaluators().equals(other.getEvaluators())) {
			return false;
		}
		if (point != other.point) {
			return false;
		}
		if (reward == null) {
			if (other.reward != null) {
				return false;
			}
		} else if (!reward.equals(other.reward)) {
			return false;
		}
		if (state == null) {
			return other.state == null;
		} else {
			return state.equals(other.state);
		}
	}
}
