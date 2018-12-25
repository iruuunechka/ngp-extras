package ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria.temp;

import ru.ifmo.ctd.ngp.demo.ffchooser.OptimizationAlgorithm;
import ru.ifmo.ctd.ngp.demo.ffchooser.TargetCondition;
import ru.ifmo.ctd.ngp.learning.reinforce.Environment;

import java.util.List;

/**
 * <p>
 * This is an interface for classes, which represent 
 * the {@link MulticriteriaOptimizationAlgorithm} as the {@link Environment}.
 * </p><p>
 * Such representation makes it possible to apply reinforcement learning 
 * algorithms to the problem of choosing the most appropriate
 * optimization criteria in the optimization algorithm.
 * </p>
 * @param <S> type of a state
 * @param <A> type of an action
 */
public interface MultiOptAlgEnvironment<S, A> extends Environment<S, A> {

    /**
     * Sets the target conditions for the {@link MulticriteriaOptimizationAlgorithm}
     * encapsulated by this {@link MultiOptAlgEnvironment}. When at least one
     * of these conditions is reached, this environment is decided to be
     * in the terminal state.
     *
     * @param conditions target conditions that determine terminal state
     */
    @SuppressWarnings("unchecked") // cannot do @SafeVarargs here
    void setTargetCondition(TargetCondition<OptimizationAlgorithm>... conditions);

    /**
     * Checks whether the encapsulated {@link MulticriteriaOptimizationAlgorithm}
     * satisfies the specified {@link TargetCondition} at the
     * current moment
     *
     * @param condition the specified target condition
     * @return <code>true</code> if the <code>condition</code> is satisfied,
     * <code>false</code> if not.
     */
    boolean checkTargetCondition(TargetCondition<OptimizationAlgorithm> condition);

    /**
     * Gets a view of the fitness evaluators values calculated just before the last ones
     * @return a view of the fitness evaluators values calculated just before the last ones
     */
    List<Double> getPrevValues();

    /**
     * Gets a view of the last calculated fitness evaluators values
     * @return a view of the last calculated fitness evaluators values
     */
    List<Double> getLastValues();

    /**
     * Gets the {@link MulticriteriaOptimizationAlgorithm} represented as this environment
     * @return the optimization algorithm represented as this environment
     */
    MulticriteriaOptimizationAlgorithm getAlgorithm();

    /**
     * Returns the best value of the target criterion from first to current iteration
     * @return the best value of the target criterion
     */
    double getFinalBestTargetValue();


}
