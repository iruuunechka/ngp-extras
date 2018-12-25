package ru.ifmo.ctd.ngp.demo.paramchooser.controller.agents.utree;

import org.apache.commons.math3.stat.inference.KolmogorovSmirnovTest;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.Action;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.Agent;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.State;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.Transition;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.util.splitter.SplitResult;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.util.splitter.Splittable;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.util.RandomWrapper;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.util.splitter.Splittables;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Arkadii Rost
 */
public abstract class UTreeAgent<A extends Action, S extends State, T extends Transition<A, S>,
    U extends UState<A, S, T>, L> implements Agent<A, S, L>
{
    protected final double gamma;
    protected final double obsResolution;
    protected final double minSplitPercent;
    protected final int splitLimit;

    protected TreeNode<U> root;

    protected UTreeAgent(double gamma, double obsResolution, double minSplitPercent, int splitLimit) {
        this.gamma = gamma;
        this.obsResolution = obsResolution;
        this.minSplitPercent = minSplitPercent;
        this.splitLimit = splitLimit;
    }

    @Override
    public A getParameterValues(Random rand, S state) {
        return getUState(state).get(rand);
    }

	@Override
	public void init(Random rand) {
		U rootState = createUState(rand, null, Collections.emptyList());
		root = new TreeNode<>(rootState);
		registerState(rootState);
	}

    @Override
    public void update(Random rand, double reward, S lastState, A lastAction, S state) {
        TreeNode<U> node = root.findLeaf(lastState.getObservables());
        U uState = node.getValue();
        updateTransitions(rand, reward, lastState, lastAction, state, uState);
        if (uState.getTransitions().size() > splitLimit && rand.nextDouble() < 0.1)
            trySplit(rand, node);
    }

    protected abstract T createTransition(double reward, S lastState, A lastAction, S state);

    protected void updateTransitions(Random rand, double reward, S lastState, A lastAction, S state, U uState) {
        uState.updateTransitions(rand, createTransition(reward, lastState, lastAction, state));
    }

    protected U getUState(S state) {
        return root.findLeaf(state.getObservables()).getValue();
    }

    protected void trySplit(Random rand, TreeNode<U> node) {
        if (!node.isLeaf())
            return;
        KolmogorovSmirnovTest splitter = new KolmogorovSmirnovTest(new RandomWrapper(rand));
        U state = node.getValue();
        Collection<T> transitions = state.getTransitions();
        int obsCount = getObsCount(transitions);
        int offset = (int)(minSplitPercent * transitions.size());
        int bestObs = -1;
	    SplitResult bestSplitResult = null;
        for (int obs = 0; obs < obsCount; obs++) {
	        SplitResult splitResult = Splittables.split(splitter, wrap(transitions, obs), offset, obsResolution);
	        if (splitResult != null
		          && (bestSplitResult == null || splitResult.getpValue() < bestSplitResult.getpValue()))
	        {
				bestSplitResult = splitResult;
		        bestObs = obs;
	        }
        }
	    if (bestObs < 0)
		    return;
        List<T> leftTransitions = new ArrayList<>();
        List<T> rightTransition = new ArrayList<>();
        for (T t : transitions) {
            if (t.getFromState().getObservables()[bestObs] < bestSplitResult.getSplitPoint()) {
                leftTransitions.add(t);
            } else {
                rightTransition.add(t);
            }
        }
        U leftState = createUState(rand, node.getValue(), leftTransitions);
        U rightState = createUState(rand, node.getValue(), rightTransition);
        TreeNode<U> left = new TreeNode<>(leftState);
        TreeNode<U> right = new TreeNode<>(rightState);
        deregisterState(node.getValue());
        node.split(bestObs, bestSplitResult.getSplitPoint(), left, right);
        registerState(leftState);
        registerState(rightState);
    }

	private List<Splittable> wrap(Collection<T> transitions, int obs) {
		return transitions.stream().map(t -> new SplittableByObsValue(t, obs)).collect(Collectors.toList());
	}

	protected abstract U createUState(Random rand, U prev, List<T> transitions);
    protected abstract void registerState(U state);
    protected abstract void deregisterState(U state);


    private int getObsCount(Iterable<T> transitions) {
        Iterator<T> iterator = transitions.iterator();
        return iterator.hasNext() ? iterator.next().getFromState().getObservables().length : 0;
    }

    private class SplittableByObsValue implements Splittable {
        private final double y;
        private final double x;

        private SplittableByObsValue(T transition, int obs) {
	        x = transition.getFromState().getObservables()[obs];
	        y = transition.getReward() + gamma * root.findLeaf(transition.getToState().getObservables()).getValue().getEffectiveQ();
        }
	    @Override
	    public double getX() {
		    return x;
	    }

	    @Override
	    public double getY() {
		    return y;
	    }
    }
}
