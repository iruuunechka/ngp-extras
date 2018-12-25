package ru.ifmo.ctd.ngp.demo.paramchooser.controller.agents.adaptive.earpc;

import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.Clusterable;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.Bounded;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.State;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.Transition;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.agents.BaseTransition;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.agents.adaptive.AdaptiveAction;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.agents.utree.UState;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.util.SimpleBounded;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Arkadii Rost
 */
public class EarpcState<S extends State, T extends BaseTransition<AdaptiveAction, S>>
        implements UState<AdaptiveAction, S, T>
{
    protected final Bounded[] bounds;
	protected final double eps;
	protected final List<T> transitions;
    private double effectiveQ;
    private final double[] splitPoints;
    private final double[] prob;

    public EarpcState(Bounded[] bounds, double eps) {
        this.bounds = bounds;
	    this.eps = eps;
	    transitions = new ArrayList<>();
        splitPoints = new double[bounds.length];
        prob = new double[bounds.length];
    }

    @Override
    public double getEffectiveQ() {
        return effectiveQ;
    }

    @Override
    public Collection<T> getTransitions() {
        return transitions;
    }

    @Override
    public AdaptiveAction get(Random rand) {
        Bounded[] bounded = new SimpleBounded[bounds.length];
        double[] values = new double[bounded.length];
        for (int i = 0; i < bounded.length; i++) {
            double toss = rand.nextDouble();
            double splitPoint = splitPoints[i];
            if (toss < prob[i]) { // first
                double lowerBound = bounds[i].getLowerBound();
                bounded[i] = new SimpleBounded(lowerBound, splitPoint);
                values[i] = lowerBound + rand.nextDouble() * (splitPoint - lowerBound);
            } else {
                double upperBound = bounds[i].getUpperBound();
                bounded[i] = new SimpleBounded(splitPoint, upperBound);
                values[i] = splitPoint + rand.nextDouble() * (upperBound - splitPoint);
            }
        }
        return new AdaptiveAction(bounded, values);
    }

    @Override
    public void updateTransitions(Random rand, T newTransition) {
        transitions.add(newTransition);
        if (transitions.size() < 2) {
            effectiveQ = transitions.stream().mapToDouble(Transition::getReward).average().orElse(0);
           for (int i = 0; i < bounds.length; i++)
               splitPoints[i] = bounds[i].getLowerBound();
            Arrays.fill(prob, 0);
            return;
        }
	    effectiveQ = 0;

        for (int i = 0; i < bounds.length; i++) {
            final int v = i;
	        KMeansPlusPlusClusterer<TransitionWrapper> clusterer = new KMeansPlusPlusClusterer<>(2, 1000);
	        Collection<TransitionWrapper> wrapped = wrap(transitions, v);
	        List<CentroidCluster<TransitionWrapper>> centers = clusterer.cluster(wrapped);
	        CentroidCluster<TransitionWrapper> c1 = centers.get(0);
	        CentroidCluster<TransitionWrapper> c2 = centers.get(1);
            double[] vs = transitions.stream().mapToDouble(t -> t.getFromState().getParameters()[v]).toArray();
            Arrays.sort(vs);
            double minEntropy = Double.POSITIVE_INFINITY;
            double bestSplit = 0;
            for (int j = 0; j < vs.length - 1; j++) {
                double splitPoint = vs[j] / 2 + vs[j + 1] / 2;
                double h = infoEntropy(v, splitPoint, c1, c2);
                if (h < minEntropy) {
                    minEntropy = h;
                    bestSplit = splitPoint;
                }
            }
            final double split = bestSplit;
            double q1 = getQ(transitions.stream().filter(t -> t.getFromState().getParameters()[v] < split));
            double q2 = getQ(transitions.stream().filter(t -> t.getFromState().getParameters()[v] > split));
            splitPoints[i] = split;
	        if (q1 == 0) {
		        if (q2 == 0) {
			        prob[i] = 0.5;
		        } else {
					prob[i] = eps;
		        }
	        } else if (q2 == 0) {
		        prob[i] = 1 - eps;
	        } else {
		        prob[i] = q1 / (q1 + q2);
	        }
            effectiveQ += (prob[i] * q1 + (1 - prob[i]) * q2) / bounds.length;
        }
    }

	protected double[] getSplitPoints() {
		return Arrays.copyOf(splitPoints, splitPoints.length);
	}

    protected double infoEntropy(int param, double splitPoint, CentroidCluster<TransitionWrapper> c1, CentroidCluster<TransitionWrapper> c2) {
        int c1v1 = (int)c1.getPoints().stream().filter(t -> getParameterValue(t, param) < splitPoint).count();
        int c1v2 = c1.getPoints().size() - c1v1;
        int c2v1 = (int)c2.getPoints().stream().filter(t -> getParameterValue(t, param) < splitPoint).count();
        int c2v2 = c2.getPoints().size() - c2v1;
        int cxv1 = c1v1 + c2v1;
        int cxv2 = c1v2 + c2v2;
        int cx = cxv1 + cxv2;
        double e1 = getE(c1v1, cxv1) + getE(c2v1, cxv1);
        double e2 = getE(c1v2, cxv2) + getE(c2v2, cxv2);
        return cxv1 * e1 / cx + cxv2 * e2 / cx;
    }

	private double getParameterValue(TransitionWrapper t, int param) {
		return t.getTransition().getFromState().getParameters()[param];
	}

	private double getQ(Stream<T> transitions) {
        return Math.max(0, transitions.mapToDouble(Transition::getReward).average().orElse(0));
    }

    private double getE(int c, int cx) {
        if (c == 0 || cx ==0)
            return 0;
        double p = 1. * c / cx;
        return -p * Math.log(p);
    }

	private Collection<TransitionWrapper> wrap(Collection<T> transitions, int param) {
		return transitions.stream().map(t -> new TransitionWrapper(t, param)).collect(Collectors.toList());
	}

	private class TransitionWrapper implements Clusterable {
		private final T transition;
		private final double[] point;


		private TransitionWrapper(T transition, int param) {
			this.transition = transition;
			point = new double[] {transition.getFromState().getParameters()[param]};
		}

		@Override
		public double[] getPoint() {
			return point;
		}

		public T getTransition() {
			return transition;
		}
	}
}
