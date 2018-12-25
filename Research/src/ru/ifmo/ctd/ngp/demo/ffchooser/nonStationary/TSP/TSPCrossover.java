package ru.ifmo.ctd.ngp.demo.ffchooser.nonStationary.TSP;

import java.util.*;

import org.uncommons.watchmaker.framework.EvolutionaryOperator;

/**
 * @author Irene Petrova
 */

public class TSPCrossover implements EvolutionaryOperator<List<Integer>> {

    public final double crossoverProbability;

    /**
     * Constructs the {@link TSPCrossover} operator with the
     * specified parameters.
     *
     * @param crossoverProbability the crossover probability.
     */
    public TSPCrossover(double crossoverProbability) {
        this.crossoverProbability = crossoverProbability;
    }

    public List<Integer> mate(List<Integer> parent1, List<Integer> parent2, Random rng) {
        List<Integer> offspring = new ArrayList<>();
        List<Set<Integer>> edgeTable = getEdgeTable(parent1, parent2);
        int curPoint = getNextPoint(edgeTable, rng);
        offspring.add(curPoint - 1);
        clearEdgeTable(edgeTable, curPoint);
        for (int i = 1; i < parent1.size(); ++i) {
            int nextPoint = getNextInOffspring(edgeTable, curPoint, rng);
            edgeTable.set(curPoint, null);
            if (nextPoint == -1) {
                nextPoint = getNextPoint(edgeTable, rng);
            }
            offspring.add(nextPoint - 1);
            clearEdgeTable(edgeTable, nextPoint);
            curPoint = nextPoint;
        }
        return offspring;
    }

    private void clearEdgeTable(List<Set<Integer>> edgeTable, int nextPoint) {
        for (int inEntry : edgeTable.get(nextPoint)) {
            if (inEntry < 0) {
                inEntry = -inEntry;
            }
            edgeTable.get(inEntry).remove(nextPoint);
            edgeTable.get(inEntry).remove(-nextPoint);
        }
    }

    private int getNextPoint (List<Set<Integer>> edgeTable, Random rng) {
        int minConnections = Integer.MAX_VALUE;
        for (int i = 1; i < edgeTable.size(); ++i) {
            if (edgeTable.get(i) == null) {
                continue;
            }
            minConnections = Math.min(minConnections, edgeTable.get(i).size());
        }
        List<Integer> candidates = new ArrayList<>();
        for (int i = 1; i < edgeTable.size(); ++i) {
            if (edgeTable.get(i) == null) {
                continue;
            }
            if (edgeTable.get(i).size() == minConnections) {
                candidates.add(i);
            }
        }
        return candidates.get(rng.nextInt(candidates.size()));
    }

    private List<Set<Integer>> getEdgeTable(List<Integer> parent1, List<Integer> parent2) {
        int len = parent1.size();
        List<Set<Integer>> edgeList = new ArrayList<>(len + 1);
        for (int i = 0; i < len + 1; ++i) {
            //this works in Java 8 not in Java 7
            //noinspection Convert2Diamond
            edgeList.add(new HashSet<Integer>());
        }
        for (int i = 0; i < len; ++i) {
            Set<Integer> connections = edgeList.get(parent1.get(i) + 1);
            connections.add(parent1.get((i + 1) % len) + 1);
            connections.add(parent1.get((i - 1) >= 0 ? i - 1 : i - 1 + len) + 1);
        }
        for (int i = 0; i < len; ++i) {
            int left = parent2.get((i - 1) >= 0 ? i - 1 : i - 1 + len) + 1;
            int right = parent2.get((i + 1) % len) + 1;
            Set<Integer> curEntry = edgeList.get(parent2.get(i) + 1);
            if (curEntry.contains(left)) {
                curEntry.remove(left);
                curEntry.add(-left);
            } else {
                curEntry.add(left);
            }
            if (curEntry.contains(right)) {
                curEntry.remove(right);
                curEntry.add(-right);
            } else {
                curEntry.add(right);
            }
        }
        return edgeList;
    }

    private int getNextInOffspring(List<Set<Integer>> edgeTable, int cur, Random rng) {
        int minConnections = Integer.MAX_VALUE;
        int negativeCount = 0;
        for (int connection : edgeTable.get(cur)) {
            if (connection < 0) {
                negativeCount++;
                minConnections = Math.min(minConnections, edgeTable.get(-connection).size());
                continue;
            }
            minConnections = Math.min(minConnections, edgeTable.get(connection).size());
        }
        if (negativeCount == 1) {
            for (int connection : edgeTable.get(cur)) {
                if (connection < 0) {
                    return -connection;
                }
            }
        }
        List<Integer> candidates = new ArrayList<>();
        for (int connection : edgeTable.get(cur)) {
            connection = connection >= 0 ? connection : -connection;
            if (edgeTable.get(connection).size() == minConnections) {
                candidates.add(connection);
            }
        }
        return candidates.size() == 0 ? -1 : candidates.get(rng.nextInt(candidates.size()));
    }

    @Override
    public List<List<Integer>> apply(List<List<Integer>> selectedCandidates, Random rng) {
        List<List<Integer>> selectionClone = new ArrayList<>(selectedCandidates);
        selectionClone.addAll(new ArrayList<>(selectedCandidates));
        Collections.shuffle(selectionClone, rng);
        List<List<Integer>> result = new ArrayList<>(selectedCandidates.size());
        Iterator<List<Integer>> iterator = selectionClone.iterator();
        while (iterator.hasNext()) {
            List<Integer> parent1 = iterator.next();
            if (iterator.hasNext()) {
                List<Integer> parent2 = iterator.next();
                if (crossoverProbability > rng.nextDouble()) {
                    result.add(mate(parent1, parent2, rng));
                } else {
                    if (rng.nextBoolean()) {
                        result.add(parent1);
                    } else {
                        result.add(parent2);
                    }
                }
            } else {
                result.add(parent1);
            }
        }
        return result;
    }
}
