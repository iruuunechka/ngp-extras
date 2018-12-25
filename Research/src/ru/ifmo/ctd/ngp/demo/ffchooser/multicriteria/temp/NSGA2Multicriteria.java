package ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria.temp;

import org.jetbrains.annotations.NotNull;
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import org.uncommons.watchmaker.framework.FitnessEvaluator;
import org.uncommons.watchmaker.framework.factories.AbstractCandidateFactory;
import ru.ifmo.ctd.ngp.demo.ffchooser.genetic.Printer;
import ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria.*;
import ru.ifmo.ctd.ngp.util.CollectionsEx;

import java.util.*;

/**
 * @author Irene Petrova
 */

public class NSGA2Multicriteria<I> implements MulticriteriaAlgorithm<I> {

    public class Individual<II> extends EvaluatedIndividual<II> implements Comparable<Individual<II>> {
        double rank;

        public Individual(II individual, Parameter parameter) {
            super(individual, parameter);
            rank = 0;
        }

        void setRank(double r) {
            rank = r;
        }

        @Override
        public int compareTo(@NotNull Individual<II> iIndividual) {
            if (rank < iIndividual.rank) {
                return 1;
            } else if (rank > iIndividual.rank) {
                return -1;
            }
            return 0;
        }
    }

    private double bestTargetValue = Double.NEGATIVE_INFINITY;
    private List<Individual<I>> oldGeneration;
    private final List<FitnessEvaluator<I>> helpers;
    private final AbstractCandidateFactory<I> factory;
    protected final EvolutionaryOperator<I> mutation;
    protected final EvolutionaryOperator<I> crossover;
    protected final Probability crossoverProbability;
    private final int generationSize;
    protected final Selection<I> selection;
    protected final Random rng;
    protected final List<FitnessEvaluator<? super I>> criteria;
    private final List<Printer<? super I>> printers;
    private final int target;
    private int currentHelper;
    private int iterations;
    private final List<List<Individual<I>>> trainingGenerations;
    private int lastIterations;
    private double curBestTargetValue;
    private int lastIterationsPareto;


    private List<List<Double>> curPareto;
//    private double mutationProbability = 1;


    public NSGA2Multicriteria(
            FitnessEvaluator<I> targetCriterion,
            List<FitnessEvaluator<I>> helpers,
            AbstractCandidateFactory<I> factory,
            EvolutionaryOperator<I> mutation,
            EvolutionaryOperator<I> crossover,
            double crossoverProbability,
            int generationSize,
            Random rng
    ) {
        iterations = 0;
        lastIterations = 0;
        lastIterationsPareto = 0;
        target = 0;
        currentHelper = rng.nextInt(helpers.size());
        this.criteria = new ArrayList<>();
        this.criteria.add(targetCriterion);
        this.criteria.add(helpers.get(currentHelper));
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
        for (FitnessEvaluator<I> ignored : helpers) {
            trainingGenerations.add(new ArrayList<>());
        }
        List<EvaluatedIndividual<I>> gen = new ArrayList<>(Utils.evaluateAll(factory.generateInitialPopulation(generationSize, rng), criteria));
        for (EvaluatedIndividual<I> i : gen) {
            this.oldGeneration.add(new Individual<>(i.ind(), i.par()));
        }
        this.printers = new ArrayList<>();
    }


    public List<Individual<I>> genChildren(List<EvaluatedIndividual<I>> selected) {
        List<Individual<I>> generatedIndividuals = new ArrayList<>();
        for (int i = 0; i < selected.size(); ++i) {
            I p;
            if (crossoverProbability.nextEvent(rng)) {
                p = crossover.apply(CollectionsEx.listOf(
                        selection.select(selected, rng).ind(),
                        selection.select(selected, rng).ind()
                ), rng).get(0);
            } else {
                p = selection.select(selected, rng).ind();
            }
//            I iChild = p;
//            if (rng.nextDouble() < mutationProbability) {
            I  iChild = mutation.apply(CollectionsEx.listOf(p), rng).get(0);
//            iChild = mutation.apply(CollectionsEx.listOf(p), rng).get(0);
//            iChild = mutation.apply(CollectionsEx.listOf(p), rng).get(0);

//            }
            Individual<I> child = new Individual<>(iChild, Utils.evaluate(iChild, CollectionsEx.listOf(iChild), criteria));
            generatedIndividuals.add(child);
        }
        return generatedIndividuals;
    }

    private List<Individual<I>> makeChildren(List<Individual<I>> generation) {
        List<EvaluatedIndividual<I>> cur = new ArrayList<>();
        for (int i = 0; i < generationSize; ++i) {
            cur.add(generation.get(Math.min(rng.nextInt(generation.size()), rng.nextInt(generation.size()))));       //???
        }
        return genChildren(cur);

    }

    private List<Individual<I>> removeFitnessDuplicate(List<Individual<I>> ind) {
        List<Individual<I>> filtered = new ArrayList<>();
        for (Individual<I> i : ind) {
            boolean notInFiltered = true;
            for (Individual<I> f : filtered) {
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

    private class CriteriaComparator implements Comparator<Individual<I>> {
        private final int criterion;

        CriteriaComparator(int criterion) {
            this.criterion = criterion;
        }

        @Override
        public int compare(Individual<I> lhs, Individual<I> rhs) {
            if (lhs.par().getCriteria(criterion) < rhs.par().getCriteria(criterion)) {
                return -1;
            } else if (lhs.par().getCriteria(criterion) > rhs.par().getCriteria(criterion)) {
                return 1;
            }
            return 0;
        }
    }
    private List<Individual<I>> hookedNewGenerationSelector(List<Individual<I>> generation) {
        List<Individual<I>> newGeneration = makeChildren(generation);
        List<Individual<I>> all = new ArrayList<>(generation.size() + newGeneration.size());
        all.addAll(generation);
        all.addAll(newGeneration);
        all = removeFitnessDuplicate(all);
        for (Individual<I> i : all) {
            i.setRank(0);
        }
        int numberOfCriteria = all.get(0).par().getCriteria().length;
        for (int i = 0; i < numberOfCriteria; ++i) {
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
        List<EvaluatedIndividual<I>> gen = new ArrayList<>(Utils.evaluateAll(factory.generateInitialPopulation(generationSize, rng), criteria));
        for (EvaluatedIndividual<I> i : gen) {
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
    public void addPrinter(Printer<? super I> printer) {
        printers.add(printer);
    }

    @Override
    public void removePrinter(Printer<? super I> printer) {
        printers.remove(printer);
    }

    @Override
    public void setStartPopulation(List<I> seedPopulation) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setLength(int length) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setEvaluator(int index, FitnessEvaluator<? super I> evaluator) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getName() {
        return "nsga";
    }


    private List<List<Double>> getParetoFront(List<Individual<I>> generation) {
        if ((lastIterationsPareto == iterations) && (iterations != 0)) {
            return curPareto;
        }
        List<List<Double>> res = new ArrayList<>();

        for (EvaluatedIndividual<I> ind : generation) {
            List<Double> values = new ArrayList<>();
            values.add(ind.par().getCriteria()[target]);
            for (FitnessEvaluator<? super I> eval : helpers) {
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
        List<Individual<I>> generation= trainingGenerations.get(gen);
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

    private List<Double> computeMaxInGeneration(List<? extends EvaluatedIndividual<I>> evaluated) {
        EvaluatedIndividual<I> maxInd = evaluated.get(0);
        double max = 0;
        for (EvaluatedIndividual<I> ind : evaluated) {
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
        for (Printer<? super I> p : printers) {
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
        List<Individual<I>> generation = oldGeneration;
        double max = generation.get(0).par().getCriteria()[target];
        for (EvaluatedIndividual<I> ind : generation) {
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
