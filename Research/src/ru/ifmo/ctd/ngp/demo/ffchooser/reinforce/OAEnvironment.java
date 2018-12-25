package ru.ifmo.ctd.ngp.demo.ffchooser.reinforce;

import ru.ifmo.ctd.ngp.demo.ffchooser.OptimizationAlgorithm;
import ru.ifmo.ctd.ngp.demo.ffchooser.TargetCondition;
import ru.ifmo.ctd.ngp.demo.ffchooser.genetic.EvolutionaryAlgorithm;
import ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.reward.RewardCalculator;
import ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.state.StateCalculator;
import ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.state.VectorState;
import ru.ifmo.ctd.ngp.learning.reinforce.EnvironmentPrinter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * Implementation of {@link OptAlgEnvironment} used in reinforcement learning. 
 * </p><p> 
 * The states are associated with the order of the parameters' values in the sorted list. 
 * The action is switching to some optimization criteria. The actions are encoded by 
 * the indexes of the parameters.
 * </p>
 * @author Arina Buzdalova
 */
public class OAEnvironment implements OptAlgEnvironment<String, Integer> {
	private final EvolutionaryAlgorithm algorithm;	    //OptimizationAlgorithm was earlier
	private final RewardCalculator rewardCalc;
	private final StateCalculator<String, Integer> stateCalc;
	private final List<Integer> actions;
	private List<TargetCondition<OptimizationAlgorithm>> targetConditions;
	private List<Double> prevValues;
	private List<Double> lastValues;
	private final List<EnvironmentPrinter<String, Integer>> printers;
	private double lastReward;
	private int lastAction;

	/**
	 * Constructs {@link OAEnvironment} with the specified {@link OptimizationAlgorithm} and
	 * {@link RewardCalculator} that defines the kind of the reward. The {@link StateCalculator}
	 * is {@link VectorState} by default. The number of vector's elements equals to the number
	 * of fitness evaluators.
	 * @param algorithm the specified algorithm
	 * @param reward the specified reward calculator
	 */
	public OAEnvironment(EvolutionaryAlgorithm algorithm, RewardCalculator reward) {
		this(algorithm, reward, new VectorState(algorithm.parametersCount()), allActionsAllowed(algorithm));
	}
	
	/**
	 * Constructs {@link OAEnvironment} with the specified {@link OptimizationAlgorithm},
	 * {@link RewardCalculator} that defines the kind of the reward and 
	 * {@link StateCalculator} that defines the kind of the states.
	 * @param algorithm the specified algorithm
	 * @param reward the specified reward calculator
	 * @param state the specified state calculator
	 */
	public OAEnvironment(EvolutionaryAlgorithm algorithm, RewardCalculator reward, StateCalculator<String, Integer> state) {
		this(algorithm, reward, state, allActionsAllowed(algorithm));
	}
	
	/**
	 * Constructs {@link OAEnvironment} with the specified {@link OptimizationAlgorithm},
	 * {@link RewardCalculator} that defines the kind of the reward and 
	 * {@link StateCalculator} that defines the kind of the states.
	 * @param algorithm the specified algorithm
	 * @param reward the specified reward calculator
	 * @param state the specified state calculator
	 * @param actions list of allowed actions
	 */
	public OAEnvironment(EvolutionaryAlgorithm algorithm, RewardCalculator reward, StateCalculator<String, Integer> state,
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
	public OptimizationAlgorithm getAlgorithm() {
		return algorithm;
	}

    @Override
    public double getBestTargetValue() {
        return algorithm.getBestTargetValue();
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
		return algorithm.getTargetValueInCurrentBest();
	}
}
