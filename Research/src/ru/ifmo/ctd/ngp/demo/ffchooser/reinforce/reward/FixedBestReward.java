package ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.reward;

import java.util.List;

import ru.ifmo.ctd.ngp.demo.ffchooser.OptimizationAlgorithm;
import ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.OptAlgEnvironment;
import ru.ifmo.ctd.ngp.demo.util.textconstructor.ParamDef;

/**
 * Implementation of the {@link RewardCalculator}
 * with fixed rewards for the negative, zero and positive
 * changes of the target parameter.
 * It takes into account the best individual only.
 * 
 * @author Arina Buzdalova
 *
 */
public class FixedBestReward implements RewardCalculator {
	private static final long serialVersionUID = -7760457070329945724L;
	private final double negative;
	private final double zero;
	private final double positive;

	/**
	 * Constructs {@link FixedBestReward} reward calculator with the
	 * specified rewards for different kind of the target parameter's change
	 * 
	 * @param negative reward for a negative change of the target parameter
	 * @param zero reward for a zero change of the target parameter
	 * @param positive reward for a positive change of the target parameter
	 */
	public FixedBestReward(@ParamDef(name = "negative") double negative, @ParamDef(name = "zero") double zero, 
			@ParamDef(name = "positive") double positive) {
		this.negative = negative;
		this.zero = zero;
		this.positive = positive;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public double calculate(OptAlgEnvironment<?, ?> environment) {
		OptimizationAlgorithm algorithm = environment.getAlgorithm();
		int target = algorithm.getTargetParameter();
		List<Double> prevValues = algorithm.getCurrentBest();
		List<Double> newValues = algorithm.computeValues();
		double delta = newValues.get(target) -	prevValues.get(target);
		
		if (delta < 0) {
			return negative;
		}
		
		if (delta == 0) {
			return zero;
		}
		
		if (delta > 0) {
			return positive;
		}
		
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return String.format("fixed-best%.2fn%.2fz%.2fp", negative, zero, positive);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(negative);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(positive);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(zero);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FixedBestReward other = (FixedBestReward) obj;
        return Double.doubleToLongBits(negative) == Double.doubleToLongBits(other.negative)
                && Double.doubleToLongBits(positive) == Double.doubleToLongBits(other.positive)
                && Double.doubleToLongBits(zero) == Double.doubleToLongBits(other.zero);
    }
}
