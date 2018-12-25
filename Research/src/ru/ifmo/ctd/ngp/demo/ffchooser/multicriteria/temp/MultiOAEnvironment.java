package ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria.temp;

import ru.ifmo.ctd.ngp.demo.ffchooser.OptimizationAlgorithm;
import ru.ifmo.ctd.ngp.demo.ffchooser.TargetCondition;
import ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria.temp.reward.MultiRewardCalculator;
import ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria.temp.state.MultiStateCalculator;
import ru.ifmo.ctd.ngp.learning.reinforce.EnvironmentPrinter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * Implementation of {@link ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.OptAlgEnvironment} for
 * {@link ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria.temp.MulticriteriaAlgorithm} used in reinforcement learning.
 * </p><p>
 * The states are associated with the order of the parameters' values in the sorted list.
 * The action is switching to some optimization criteria. The actions are encoded by
 * the indexes of the parameters.
 * </p>
 */

public class MultiOAEnvironment implements MultiOptAlgEnvironment<String, Integer>{
    private final MulticriteriaOptimizationAlgorithm algorithm;
    private final MultiRewardCalculator rewardCalc;
    private final MultiStateCalculator<String, Integer> stateCalc;
    private final List<Integer> actions;
    private List<TargetCondition<OptimizationAlgorithm>> targetConditions;
    private List<Double> prevValues;
    private List<Double> lastValues;
    private final List<EnvironmentPrinter<String, Integer>> printers;
    private double lastReward;
    private int lastAction;

    /**
     * Constructs {@link MultiOAEnvironment} with the specified {@link OptimizationAlgorithm},
     * {@link MultiRewardCalculator} that defines the kind of the reward and
     * {@link MultiStateCalculator} that defines the kind of the states.
     * @param algorithm the specified algorithm
     * @param reward the specified reward calculator
     * @param state the specified state calculator
     */
    public MultiOAEnvironment(MulticriteriaOptimizationAlgorithm algorithm, MultiRewardCalculator reward, MultiStateCalculator<String, Integer> state) {
        this(algorithm, reward, state, allActionsAllowed(algorithm));
    }

    /**
     * Constructs {@link MultiOAEnvironment} with the specified {@link OptimizationAlgorithm},
     * {@link MultiRewardCalculator} that defines the kind of the reward and
     * {@link MultiStateCalculator} that defines the kind of the states.
     * @param algorithm the specified algorithm
     * @param reward the specified reward calculator
     * @param state the specified state calculator
     * @param actions list of allowed actions
     */
    public MultiOAEnvironment(MulticriteriaOptimizationAlgorithm algorithm, MultiRewardCalculator reward, MultiStateCalculator<String, Integer> state,
                              List<Integer> actions) {
        this.algorithm = algorithm;
        this.rewardCalc = reward;
        this.stateCalc = state;
        this.actions = actions;

        this.targetConditions = new ArrayList<>();

        lastValues = algorithm.computeValues();
        prevValues = lastValues;

        printers = new ArrayList<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double applyAction(Integer action) {
        algorithm.changeCriterion(action);
        double reward = rewardCalc.calculate(this);
        refresh(algorithm.getCurrentBest());
        lastReward = reward;
        lastAction = action;

        for (EnvironmentPrinter<String, Integer> p : printers) {
            try {
                p.print(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return reward;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Integer> getActions() {
        return Collections.unmodifiableList(actions);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer firstAction() {
        return actions.get(0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCurrentState() {
//        String s = stateCalc.calculate(this);
//        System.out.println("getCS" + s);
//        return s;
        return stateCalc.calculate(this);

    }

    /**
     * Replaces {@link #lastValues} with the specified values and calculates state
     * @param values the specified values
     */
    private void refresh(List<Double> values) {
        prevValues = lastValues;
        lastValues = values;
        //state = getState(lastValues);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isInTerminalState() {
        for (TargetCondition<OptimizationAlgorithm> c : targetConditions) {
            if (checkTargetCondition(c)) {
                return true;
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @SafeVarargs
    @Override
    public final void setTargetCondition(TargetCondition<OptimizationAlgorithm>... conditions) {
        this.targetConditions = Arrays.asList(conditions);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean checkTargetCondition(TargetCondition<OptimizationAlgorithm> condition) {
        return condition.targetReached(algorithm);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Double> getLastValues() {
        return Collections.unmodifiableList(lastValues);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Double> getPrevValues() {
        return Collections.unmodifiableList(prevValues);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MulticriteriaOptimizationAlgorithm getAlgorithm() {
        return algorithm;
    }

    @Override
    public double getFinalBestTargetValue() {
        return algorithm.getFinalBestTargetValue();
    }

    private static List<Integer> allActionsAllowed(OptimizationAlgorithm algorithm) {
        List<Integer> actions = new ArrayList<>();
        for (int i = 0, size = algorithm.parametersCount(); i < size; i++) {
            actions.add(i);
        }
        return actions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int actionsCount() {
        return actions.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addPrinter(EnvironmentPrinter<String, Integer> printer) {
        printers.add(printer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getLastReward() {
        return lastReward;
    }

	@Override
	public Integer getLastAction() {
		return lastAction;
	}

	@Override
	public double getTargetValue() {
		return algorithm.getBestTargetValue();
	}

    @Override
    public double getBestTargetValue() {
        return algorithm.getBestTargetValue();
    }


}

