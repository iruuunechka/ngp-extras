package ru.ifmo.ctd.ngp.learning.reinforce;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import ru.ifmo.ctd.ngp.learning.util.Map2;
import ru.ifmo.ctd.ngp.util.FastRandom;

/**
 * Action chooser that implements softmax, or Boltzmann, exploration strategy.
 * 
 * @author Arina Buzdalova
 *
 * @param <S> a state type
 * @param <A> an action type
 */
public class SoftmaxActionChooser<S, A> {
	private double t;
	private final double tRate;
	
	/**
	 * Constructs {@link SoftmaxActionChooser} that implements Boltzmann exploration. 
	 * Exploration gives way for exploitation as the "temperature" decreases.
	 * 
	 * @param temperature initial temperature value
	 * @param tRate parameter that influences the rate of temperature decrease
	 */
	public SoftmaxActionChooser( 
			double temperature, 
			double tRate) {
		this.t = temperature;
		this.tRate = tRate;
	}

	public A chooseAction(S state, Map2<S, A, Double> Q, List<A> actions) {
		double rand = FastRandom.threadLocal().nextDouble();
		
		List<Pair<A>> pairs = constructActionSums(state, Q, actions);
		
		int left = 0, right = pairs.size();
		while (right - left > 1) {
			int mid = (left + right) >>> 1;
			if (pairs.get(mid - 1).getValue() > rand) {
	            right = mid;
	        } else {
	            left = mid;
	        }
		}
		
		decreaseT();
		return pairs.get(left).getObject();
	}
	
	/**
	 * Sorts actions according to their probability of being chosen
	 * @param state the current state of the environment
	 * @param Q current quality of state-action pairs
	 * @param actions the actions of the agent
	 * @return sorted list of pairs (probability, action)
	 */
	private List<Pair<A>> constructActionSums(S state, Map2<S, A, Double> Q, List<A> actions) {
		List<Pair<A>> sums = new ArrayList<>();
		List<Pair<A>> normalized = normalize(state, Q, actions);
		
		double partialSum = 0;
		double sumExp = sumExp(state, Q, actions);
		
		for (Pair<A> pair : normalized) {
			double prob = Math.exp(pair.getValue() / t) / sumExp;
			sums.add(new Pair<>(partialSum += prob, pair.getObject()));
		}
//		Collections.sort(pairs);
		return sums;
	}
	
	private void decreaseT() {
		t /= tRate;
	}
	
	private double sumExp(S state, Map2<S, A, Double> Q, List<A> actions) {
		double sumExp = 0;
		for (A a : actions) {
			sumExp += Math.exp(Q.get(state, a) / t);
		}
		return sumExp;
	}
	
	private List<Pair<A>> normalize(S state, Map2<S, A, Double> Q,  List<A> actions) {
		double d1 = 0.1;
		double d2 = 1;
		
		double min = Q.get(state, actions.get(0));
		double max = min;
		
		for (A action : actions) {
			double q = Q.get(state, action);
			min = Math.min(min, q);
			max = Math.max(max, q);
		}
		
		double a = (d2 - d1) / (max - min);
		double b = (d1 * max - d2 * min) / (max - min);
		
		List<Pair<A>> normalized = new ArrayList<>(actions.size());
		for (A action : actions) {
			normalized.add(new Pair<>(a * Q.get(state, action) + b, action));
		}
	
		return normalized;
	}

    private static class Pair<T> implements Comparable<Pair<T>> {
        private final double value;
        private final T object;

        public Pair(double value, T object) {
            this.value = value;
            this.object = object;
        }

        public double getValue() {
            return value;
        }

        public T getObject() {
            return object;
        }

        @Override
        public int compareTo(@NotNull Pair<T> pair) {
            return Double.compare(value, pair.getValue());
        }
    }
}
