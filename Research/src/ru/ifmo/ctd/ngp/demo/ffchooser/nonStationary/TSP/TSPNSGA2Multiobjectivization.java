package ru.ifmo.ctd.ngp.demo.ffchooser.nonStationary.TSP;

import org.jetbrains.annotations.NotNull;
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import org.uncommons.watchmaker.framework.FitnessEvaluator;
import org.uncommons.watchmaker.framework.factories.AbstractCandidateFactory;
import ru.ifmo.ctd.ngp.demo.ffchooser.genetic.Printer;
import ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria.*;
import ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria.temp.MulticriteriaAlgorithm;
import ru.ifmo.ctd.ngp.util.CollectionsEx;

import java.util.*;

/**
 * @author Irene Petrova
 */

public class TSPNSGA2Multiobjectivization implements MulticriteriaAlgorithm<List<Integer>> {

    public class Individual<I> extends EvaluatedIndividual<List<Integer>> implements Comparable<Individual<List<Integer>>> {
        public double rank;

        public Individual(List<Integer> individual, Parameter parameter) {
            super(individual, parameter);
            rank = 0;
        }

        public void setRank(double r) {
            rank = r;
        }

        @Override
        public int compareTo(@NotNull Individual<List<Integer>> iIndividual) {
            if (rank < iIndividual.rank) {
                return 1;
            } else if (rank > iIndividual.rank) {
                return -1;
            }
            return 0;
        }
    }

    private double bestTargetValue = Double.NEGATIVE_INFINITY;
    private List<Individual<List<Integer>>> oldGeneration;
    private final List<FitnessEvaluator<List<Integer>>> helpers;
    private final AbstractCandidateFactory<List<Integer>> factory;
    protected final EvolutionaryOperator<List<Integer>> mutation;
    protected final EvolutionaryOperator<List<Integer>> crossover;
    protected final Probability crossoverProbability;
    private final int generationSize;
    protected final Selection<List<Integer>> selection;
    protected final Random rng;
    protected final List<FitnessEvaluator<? super List<Integer>>> criteria;
    private final List<Printer<? super List<Integer>>> printers;
    private final int target;
    private int currentHelper;
    private int iterations;
    private final List<List<Individual<List<Integer>>>> trainingGenerations;
    private int lastIterations;
    private double curBestTargetValue;
    private int lastIterationsPareto;
    private final TSPProblem problem;


    private List<List<Double>> curPareto;
//    private double mutationProbability = 1;


    public TSPNSGA2Multiobjectivization(
            FitnessEvaluator<List<Integer>> targetCriterion,
            List<FitnessEvaluator<List<Integer>>> helpers,
            AbstractCandidateFactory<List<Integer>> factory,
            EvolutionaryOperator<List<Integer>> mutation,
            EvolutionaryOperator<List<Integer>> crossover,
            double crossoverProbability,
            int generationSize,
            Random rng,
            TSPProblem problem
    ) {
        iterations = 0;
        lastIterations = 0;
        lastIterationsPareto = 0;
        target = 0;
        currentHelper = rng.nextInt(helpers.size());
        this.criteria = new ArrayList<>();
        this.criteria.add(targetCriterion);
        this.criteria.addAll(helpers);
        this.helpers = helpers;
        this.factory = factory;
        this.mutation = mutation;
        this.crossover = crossover;
        this.crossoverProbability = new Probability(crossoverProbability);
        this.generationSize = generationSize;
        this.rng = rng;
        this.selection = new BinaryTournament<>();
        this.oldGeneration = new ArrayList<>();
        this.trainingGenerations = new ArrayList<>(helpers.size());
        for (FitnessEvaluator<List<Integer>> ignored : helpers) {
            trainingGenerations.add(new ArrayList<>());
        }
        List<EvaluatedIndividual<List<Integer>>> gen = new ArrayList<>(Utils.evaluateAll(factory.generateInitialPopulation(generationSize, rng), criteria));
        for (EvaluatedIndividual<List<Integer>> i : gen) {
            this.oldGeneration.add(new Individual<>(i.ind(), i.par()));
        }
        this.printers = new ArrayList<>();
        this.problem = problem;
    }

    public List<Individual<List<Integer>>> genChildren(List<EvaluatedIndividual<List<Integer>>> selected) {
        List<Individual<List<Integer>>> generatedIndividuals = new ArrayList<>();
        for (int i = 0; i < selected.size(); ++i) {
            List<Integer> p;
            if (crossoverProbability.nextEvent(rng)) {
                p = crossover.apply(CollectionsEx.listOf(
                        selection.select(selected, rng).ind(),
                        selection.select(selected, rng).ind()
                ), rng).get(0);
            } else {
                p = selection.select(selected, rng).ind();
                p = mutation.apply(CollectionsEx.listOf(p), rng).get(0);
            }
            TSPUtils.apply2Opt(p, problem);
           Individual<List<Integer>> child = new Individual<>(p, Utils.evaluate(p, CollectionsEx.listOf(p), criteria));
            generatedIndividuals.add(child);
        }
        return generatedIndividuals;
    }

    public List<Individual<List<Integer>>> makeChildren(List<Individual<List<Integer>>> generation) {
        List<EvaluatedIndividual<List<Integer>>> cur = new ArrayList<>();
        for (int i = 0; i < generationSize; ++i) {
            cur.add(generation.get(Math.min(rng.nextInt(generation.size()), rng.nextInt(generation.size()))));       //???
        }
        return genChildren(cur);

    }

    public List<Individual<List<Integer>>> removeFitnessDuplicate(List<Individual<List<Integer>>> ind) {
        List<Individual<List<Integer>>> filtered = new ArrayList<>();
        for (Individual<List<Integer>> i : ind) {
            boolean notInFiltered = true;
            for (Individual<List<Integer>> f : filtered) {
                if (i.par().equals(f.par())) {
                    notInFiltered = false;
                    break;
                }
            }
            if (notInFiltered) {
                filtered.add(i);
            }
        }
        return filtered;
    }

    private class CriteriaComparator implements Comparator<Individual<List<Integer>>> {
        private final int criterion;

        public CriteriaComparator(int criterion) {
            this.criterion = criterion;
        }

        @Override
        public int compare(Individual<List<Integer>> lhs, Individual<List<Integer>> rhs) {
            if (lhs.par().getCriteria(criterion) < rhs.par().getCriteria(criterion)) {
                return -1;
            } else if (lhs.par().getCriteria(criterion) > rhs.par().getCriteria(criterion)) {
                return 1;
            }
            return 0;
        }
    }
    public List<Individual<List<Integer>>> hookedNewGenerationSelector(List<Individual<List<Integer>>> generation) {
        List<Individual<List<Integer>>> newGeneration = makeChildren(generation);
        List<Individual<List<Integer>>> all = new ArrayList<>(generation.size() + newGeneration.size());
        all.addAll(generation);
        all.addAll(newGeneration);
        all = removeFitnessDuplicate(all);
        for (Individual<List<Integer>> i : all) {
            i.setRank(0);
        }
        int numberOfCriteria = all.get(0).par().getCriteria().length;
        for (int i = 1; i < numberOfCriteria; ++i) {
            all.sort(new CriteriaComparator(i));
            int idx = 0;
            for (int j = 1; j < all.size(); ++j) {
                if (all.get(j).par().getCriteria(i) != all.get(j - 1).par().getCriteria(i)) {
                    idx += 1;
                }
                all.get(j).setRank(all.get(j).rank + idx);
            }
        }
        Collections.sort(all);
        assert (all.get(0).rank >= all.get(1).rank);
        assert (all.get(5).rank >= all.get(20).rank);
        return all.subList(0, Math.min(generationSize, all.size()));
    }

    @Override
    public void refresh() {
//        mutationProbability = 1;
        oldGeneration.clear();
        List<EvaluatedIndividual<List<Integer>>> gen = new ArrayList<>(Utils.evaluateAll(factory.generateInitialPopulation(generationSize, rng), criteria));
        for (EvaluatedIndividual<List<Integer>> i : gen) {
            this.oldGeneration.add(new Individual<>(i.ind(), i.par()));
        }
        bestTargetValue = Double.NEGATIVE_INFINITY;
        lastIterations = 0;
        lastIterationsPareto = 0;
        iterations = 0;
        curBestTargetValue = 0;
        curPareto = null;
    }

    @Override
    public void addPrinter(Printer<? super List<Integer>> printer) {
        printers.add(printer);
    }

    @Override
    public void removePrinter(Printer<? super List<Integer>> printer) {
        printers.remove(printer);
    }

    @Override
    public void setStartPopulation(List<List<Integer>> seedPopulation) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setLength(int length) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setEvaluator(int index, FitnessEvaluator<? super List<Integer>> evaluator) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getName() {
        return "nsga";
    }


    private List<List<Double>> getParetoFront(List<Individual<List<Integer>>> generation) {
        if ((lastIterationsPareto == iterations) && (iterations != 0)) {
            return curPareto;
        }
        List<List<Double>> res = new ArrayList<>();

        for (EvaluatedIndividual<List<Integer>> ind : generation) {
            List<Double> values = new ArrayList<>();
            values.add(ind.par().getCriteria()[target]);
            for (FitnessEvaluator<? super List<Integer>> eval : helpers) {
                values.add(eval.getFitness(ind.ind(), null));
            }
            res.add(values);
        }
        curPareto = res;
        lastIterationsPareto = iterations;
        return res;
    }

    @Override
    public List<List<Double>> computeParetoOfGeneration(int gen) {
        List<Individual<List<Integer>>> generation= trainingGenerations.get(gen);
        return getParetoFront(generation);
    }

    @Override
    public List<List<Double>> getCurrentParetoFront() {
        return getParetoFront(oldGeneration);
    }

    @Override
    public List<List<Double>> getCurrentInternalGeneration() {
        return getCurrentParetoFront();
    }

    @Override
    public void changeCriterion(int index) {
        if (currentHelper == index) {
            return;
        }
        criteria.remove(helpers.get(currentHelper));
        criteria.add(helpers.get(index));
        currentHelper = index;
    }

    @Override
    public List<Double> computeGenerationQuality(int generation) {
        return computeMaxInGeneration(trainingGenerations.get(generation));
    }

    @Override
    public void genGeneration(int helper) {
        changeCriterion(helper);
        trainingGenerations.set(helper, hookedNewGenerationSelector(oldGeneration));
    }


    @Override
    public void setPopulation(int population) {
        oldGeneration = trainingGenerations.get(population);
    }

    @Override
    public void computeValuesOfGeneration(int generation) {
        trainingGenerations.set(generation, hookedNewGenerationSelector(trainingGenerations.get(generation)));
        iterations++;
        printAll();
    }

    @Override
    public double getFinalBestTargetValue() {
        return bestTargetValue;
    }

    @Override
    public List<Double> computeValues() {
        oldGeneration = hookedNewGenerationSelector(oldGeneration);
        iterations++;
//        if (iterations % 20 == 0) {
//            mutationProbability = Math.max(0, mutationProbability - 0.1);
//        }
        getBestTargetValue();
        printAll();
        return computeMaxInGeneration(oldGeneration);
    }

    private List<Double> computeMaxInGeneration(List<? extends EvaluatedIndividual<List<Integer>>> evaluated) {
        EvaluatedIndividual<List<Integer>> maxInd = evaluated.get(0);
        double max = 0;
        for (EvaluatedIndividual<List<Integer>> ind : evaluated) {
            double val = ind.par().getCriteria()[target];
            if (val > max) {
                max = val;
                maxInd = ind;
            }
        }
        List<Double> values = new ArrayList<>(helpers.size());
        for (double val : maxInd.par().getCriteria()) {
            values.add(val);
        }
        return values;
    }

    private void printAll() {
        for (Printer<? super List<Integer>> p : printers) {
            p.print(getCurrentBest(), null, iterations, currentHelper);
        }
    }


    @Override
    public int getTargetParameter() {
        return target;
    }

    @Override
    public int getCurrentCriterion() {
        return currentHelper;
    }

    @Override
    public int parametersCount() {
        return helpers.size();
    }

    @Override
    public double getBestTargetValue() {
        if (lastIterations == iterations) {
            return curBestTargetValue;
        }
        lastIterations = iterations;
        List<Individual<List<Integer>>> generation = oldGeneration;
        double max = generation.get(0).par().getCriteria()[target];
        for (EvaluatedIndividual<List<Integer>> ind : generation) {
            max = Math.max(max, ind.par().getCriteria()[target]);
        }
        curBestTargetValue = max;
        bestTargetValue = Math.max(bestTargetValue, max);
        return max;
    }

    @Override
    public int getIterationsNumber() {
        return iterations;
    }

    @Override
    public List<Double> getCurrentBest() {
        return computeMaxInGeneration(oldGeneration);
    }

    @Override
    public List<List<Double>> getCurrentPoints() {
        return getCurrentParetoFront();
    }
}
