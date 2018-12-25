package ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.state;

import java.util.Arrays;
import java.util.List;

import ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.OptAlgEnvironment;
import ru.ifmo.ctd.ngp.demo.util.textconstructor.ParamDef;

/**
 * State reflects the changes in parameters values.
 * 0 for decrease, 1 for stable and 2 for increase.
 * 
 * @author Arina Buzdalova
 *
 */
@SuppressWarnings({"unused"})
public class AverageDiffState implements StateCalculator<String, Integer> {
 	private static final long serialVersionUID = -1541762883217713521L;
 	private final LimitedQueue[] q;
 	private final double[] average;
 	private final int len;
 	
	public AverageDiffState(@ParamDef(name = "period") int period, @ParamDef(name = "evaluators") int evaluators) {
 		this(period, evaluators, evaluators);
 	}
	
	public AverageDiffState(int period, int evaluators, int len) {
 		q = new LimitedQueue[evaluators];
 		this.len = len;
 				
 		for (int i = 0; i < evaluators; i++) {
 			q[i] = new LimitedQueue(period);
 		}
 		
 		average = new double[evaluators];
 		Arrays.fill(average, 0);
 	}
 	
	@Override
	public String calculate(OptAlgEnvironment<String, Integer> environment) {
		return calc(environment.getLastValues()).substring(0, len);
	}
	
	private String calc(List<Double> values) {
		int evaluators = average.length; 
		
		int i = 0;
		for (double d : values) {
			q[i].add(d);
			i++;
		}
		
		StringBuilder b = new StringBuilder(evaluators);
		
		for (int j = 0; j < evaluators; j++) {
			if (q[j].getAverage() < average[j]) {
				b.append("0");
			}
			if (q[j].getAverage() == average[j]) {
				b.append("1");
			}
			if (q[j].getAverage() > average[j]) {
				b.append("2");
			}
			average[j] = q[j].getAverage();
		}
		return b.toString();
	}

	@Override
	public String getName() {
		return "average-diff";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(q);
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
		AverageDiffState other = (AverageDiffState) obj;
        return Arrays.equals(q, other.q);
    }
}
