package ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria.temp.reward;

import ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria.temp.MultiOptAlgEnvironment;
import ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria.temp.MulticriteriaOptimizationAlgorithm;
import ru.ifmo.ctd.ngp.demo.util.textconstructor.ParamDef;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;

/**
 * @author Irene Petrova
 */
public class MultiPositiveFullReward implements MultiRewardCalculator {
    private static final long serialVersionUID = -7611829354390001113L;
    /**
     * Discount factor for supporting fitness functions
     */
    protected final double discount;
    private transient Writer writer;

    /**
     * Constructs {@link MultiPositiveFullReward} with the factor for the supporting fitness evaluators
     * @param discount the factor for the supporting fitness evaluators
     */
    public MultiPositiveFullReward(@ParamDef(name = "discount") double discount) {
        this.discount = discount;
    }

    /**
     * Sets the writer to which the reward values are passed
     * @param writer the writer to which the reward values are passed
     */
    public void setWriter(Writer writer) {
        this.writer = writer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double calculate(MultiOptAlgEnvironment<?, ?> environment) {
        MulticriteriaOptimizationAlgorithm alg = environment.getAlgorithm();
        int eval = environment.actionsCount();

        double[] prevSum = sumPerEvaluator(alg.getCurrentParetoFront(), eval);
        alg.computeValues();
        double[] newSum = sumPerEvaluator(alg.getCurrentParetoFront(), eval);

        return diff(prevSum, newSum, alg.getTargetParameter(), eval);
    }

    protected double diff(double[] prevSum, double[] newSum, int target, int eval) {
        double diff = 0;

        for (int i = 0; i < eval; i++) {
            double value = (newSum[i] - prevSum[i]) / Math.max(1, Math.abs(prevSum[i]));

            if (i == target && value < 0) {
                return 0;
            }

            double added = i == target ? value : discount * value;

            if (writer != null) {
                try {
                    writer.append(String.format("%.3f ", added));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


            diff += added;
        }

        if (writer != null) {
            try {
                writer.append(String.format("Sum: %.3f\n", diff));
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return diff > 0 ? diff : 0;
    }

    private double[] sumPerEvaluator(List<List<Double>> generation, int eval) {
        double[] sum = new double[eval];
        Arrays.fill(sum, 0.0);
        for (List<Double> ind : generation) {
            for (int i = 0; i < eval; i++) {
                sum[i] += ind.get(i);
            }
        }
        int size = generation.size();
        for (int i = 0; i < eval; i++) {
            sum[i] /= Math.max(1, size);
        }
        return sum;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return String.format("full-positive%.3f", discount);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(discount);
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
        MultiPositiveFullReward other = (MultiPositiveFullReward) obj;
        return Double.doubleToLongBits(discount) == Double.doubleToLongBits(other.discount);
    }
}
