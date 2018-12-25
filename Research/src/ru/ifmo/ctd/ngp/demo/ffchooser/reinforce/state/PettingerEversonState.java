package ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.state;

import java.util.List;

import ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.OptAlgEnvironment;
import ru.ifmo.ctd.ngp.demo.util.textconstructor.ParamDef;

/**
 * Calculates state as a vector of the generation interval number, the fitness interval number
 * and the entropy interval number. 
 * 
 * @author Dmitriy Meynster
 */
public class PettingerEversonState implements StateCalculator<String, Integer> {
	private static final long serialVersionUID = -1990089853405467396L;
	private int time = 0;
    private final int total;
    private double firstSum;
    private final int timeIntervals;
    private final int fitnessIntervals;
    private final double timeInterval;
    private final double fitnessInterval;
    private final double entropyInterval;

    public PettingerEversonState(@ParamDef(name = "timeIntervals") int timeIntervals, @ParamDef(name = "fitnessIntervals") int fitnessIntervals, 
    		@ParamDef(name = "entropyInterval") double entropyInterval, @ParamDef(name = "steps") int steps) {
        this.timeIntervals = timeIntervals;
        this.fitnessIntervals = fitnessIntervals;
        this.entropyInterval = entropyInterval;
        this.total = steps;

        timeInterval = 1.0 / timeIntervals;
        fitnessInterval = 1.0 / fitnessIntervals;
    }

    private double sum(List<Double> values) {
        double ans = 0;
        for (Double value : values) {
            ans += value;
        }
        return ans;
    }

    private int fitnessRate(OptAlgEnvironment<String, Integer> environment) {
        if (time == 0) {
            firstSum = sum(environment.getLastValues());
        }
        double currentSum = sum(environment.getLastValues());
        double ratio = firstSum / currentSum;
        return (int) Math.floor(ratio / fitnessInterval);
    }

    private int entropyRate(OptAlgEnvironment<String, Integer> environment) {
        List<Double> values = environment.getLastValues();
        double sum = sum(values);
        double H = 0;
        for (Double value : values) {
            double p = value / sum;
            H += p * Math.log(p) / Math.log(2);
        }
        //TODO: what about uniform/nonuniform division?
        return (int) Math.floor(H / entropyInterval);
    }

    private int timeRate(OptAlgEnvironment<String, Integer> environment) {
        double ratio = Math.log(time) / Math.log(total);
        return (int) Math.floor(ratio / timeInterval);
    }

    @Override
    public String calculate(OptAlgEnvironment<String, Integer> environment) {
        String ans = String.valueOf(fitnessRate(environment)) +
                '|' + entropyRate(environment) +
                '|' + timeRate(environment);
        time++;
        return ans;
    }

    @Override
    public String getName() {
        return "PettingerEvreson_" + timeIntervals + "_" + fitnessIntervals + "_" + entropyInterval;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return 1;
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
        return false;
    }
}

