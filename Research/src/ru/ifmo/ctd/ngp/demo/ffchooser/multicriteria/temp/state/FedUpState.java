package ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria.temp.state;

import ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria.temp.MultiOptAlgEnvironment;

import java.util.Arrays;
import java.util.List;

public class FedUpState implements MultiStateCalculator<String, Integer> {
    private static final long serialVersionUID = 8980709462561863592L;
    final double[] minValues;

    public FedUpState(double[] minValues) {
        this.minValues = minValues.clone();
    }

    @Override
    public String calculate(MultiOptAlgEnvironment<String, Integer> environment) {
        List<List<Double>> points = environment.getAlgorithm().getCurrentParetoFront();
        int len = points.get(0).size();
        if (len != minValues.length) {
            throw new IllegalStateException("Different number of criteria expected.");
        }
        
        double[] values = new double[len];
       
        for (List<Double> point : points) {
            for (int i = 1; i < len; i++) {
               values[i] += point.get(i);
                //System.out.print(point.get(i) + " ");
            }
        }

        for (int i = 1; i < len; i++) {
        	values[i] /= points.size();
        }
        
        int maxi = 1;
        double max = (minValues[1] - values[1]) / minValues[1];
        
        for (int i = 2; i < len; i++) {
        	double current = (minValues[i] - values[i]) / minValues[i];
            //System.out.println(current);
            //System.err.println(current);
        	//System.err.println(min);
        	if (current > max) {
        		maxi = i;
        		max = current;
        	}
        	//System.err.println(mini);
        }
//        System.out.println(minValues[1]);
//        System.out.println(values[1]);
//        System.out.println("mini" + maxi);
        //System.err.println(mini);
        return String.valueOf(maxi);
    }

    @Override
    public String getName() {
        return "FedUp state";
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
