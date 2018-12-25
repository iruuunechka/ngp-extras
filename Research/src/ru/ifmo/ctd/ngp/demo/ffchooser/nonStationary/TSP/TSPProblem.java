package ru.ifmo.ctd.ngp.demo.ffchooser.nonStationary.TSP;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Irene Petrova
 */
public class TSPProblem {
    public final double[][] tsp;
    public double solution;
    public final List<List<Integer>> sortedTsp; //for each vertice contains a list of vertices sorted by value in tsp (increasing order)

    private final double max;

    public TSPProblem(double[][] tsp, double solution) {
        this.tsp = tsp;
        this.solution = solution;
        this.max = countMax();
        sortedTsp = new ArrayList<>();
        for (int i = 0; i < tsp.length; ++i) {
            final Integer curV = i;
            List<Integer> cur = Stream.iterate(0, j -> j + 1).limit(tsp.length)
                                                             .sorted(Comparator.comparingDouble(a -> tsp[curV][a]))
                                                             .collect(Collectors.toList());
            cur.remove(curV);
            sortedTsp.add(cur);
        }
    }

    public void setSolution(double solution) {
        if (this.solution < 0) {
            this.solution = solution;
        }
    }

    private double countMax() {
        double max = 0;
        for (int i = 0; i < tsp.length; ++i) {
            for (int j = i; j < tsp.length; ++j) {
                max += tsp[i][j];
            }
        }
        return max;
    }

    public double getMax() {
        return max;
    }

    public int getSize() {
        return tsp.length;
    }

    public double getTSPValue(int c1, int c2) {
        return tsp[c1][c2];
    }

    /**
     *
     * @param c1 - current vertex
     * @param curInSorted - number in sorted neighbours list
     */
    public int getTSPNeighbour(int c1, int curInSorted) {
        return sortedTsp.get(c1).get(curInSorted);
    }
}
