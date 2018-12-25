package ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria.temp.state;

import ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria.temp.MultiOptAlgEnvironment;

import java.util.Arrays;
import java.util.List;

public class FedUpVectorState implements MultiStateCalculator<String, Integer> {
    private static final long serialVersionUID = 8980709462561863592L;
    final double[] minValues;

    public FedUpVectorState(double[] optimalValues) {
        this.minValues = optimalValues;
    }

    @Override
    public String calculate(MultiOptAlgEnvironment<String, Integer> environment) {
        List<List<Double>> points = environment.getAlgorithm().getCurrentInternalGeneration();
        int len = points.get(0).size();
        if (len != minValues.length) {
            throw new IllegalStateException("Different number of criteria expected.");
        }
        boolean[] optimized = new boolean[len];
        Arrays.fill(optimized, false);
        for (List<Double> point : points) {
            for (int i = 0; i < len; i++) {
                if (point.get(i) >= minValues[i]) {
                    optimized[i] = true;
                }
            }
        }

        StringBuilder rv = new StringBuilder();
        for (boolean b : optimized) {
            rv.append(b ? 1 : 0);
        }
        return rv.toString();
    }

    @Override
    public String getName() {
        return "Internal state";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(minValues);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        FedUpState other = (FedUpState) obj;
        return Arrays.equals(minValues, other.minValues);
    }
}
