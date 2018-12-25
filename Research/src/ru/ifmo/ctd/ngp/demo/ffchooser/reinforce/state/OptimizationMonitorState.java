package ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.state;

import ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.*;

import java.util.*;

public class OptimizationMonitorState implements StateCalculator<String, Integer> {
	private static final long serialVersionUID = 4498304925447739570L;
	private final double[] optimalValues;
	
	public OptimizationMonitorState(double[] optimalValues) {
		this.optimalValues = optimalValues;
	}

	@Override
	public String calculate(OptAlgEnvironment<String, Integer> environment) {
		List<List<Double>> points = environment.getAlgorithm().getCurrentPoints();
		int len = points.get(0).size();
		if (len != optimalValues.length) {
			throw new IllegalStateException("Different number of criteria expected.");
		}
		boolean[] optimized = new boolean[len];
		Arrays.fill(optimized, false);
		for (List<Double> point : points) {
			for (int i = 0; i < len; i++) {
				if (point.get(i) >= optimalValues[i]) {
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
		return "opt-monitor";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(optimalValues);
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
		OptimizationMonitorState other = (OptimizationMonitorState) obj;
        return Arrays.equals(optimalValues, other.optimalValues);
    }
}
