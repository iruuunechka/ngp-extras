package ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria.temp;

import org.uncommons.maths.random.*;
import org.uncommons.watchmaker.framework.*;
import org.uncommons.watchmaker.framework.factories.*;
import ru.ifmo.ctd.ngp.demo.ffchooser.genetic.*;
import ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria.*;
import ru.ifmo.ctd.ngp.util.*;

import java.util.*;

/**
 * @author Irene Petrova
 */
public class NSGA2MulticriteriaSlow<I> implements MulticriteriaAlgorithm<I> {

    public class Individual<II> extends EvaluatedIndividual<II> {
        final List<Individual<II>> dominate = new ArrayList<>(2);
        int dominationCounter = 0;
        double crowding = 0;

        public Individual(II individual, Parameter parameter) {
            super(individual, parameter);
        }
    }

    private double bestTargetValue = Double.NEGATIVE_INFINITY;
    private List<Individual<I>> oldGeneration;
    private final List<FitnessEvaluator<I>> helpers;
    private final AbstractCandidateFactory<I> factory;
    private final EvolutionaryOperator<I> mutation;
    private final EvolutionaryOperator<I> crossover;
    private final Probability crossoverProbability;
    private final int generationSize;
    private final Selection<I> selection;
    private final Random rng;
    private final List<FitnessEvaluator<? super I>> criteria;
    private final List<Printer<? super I>> printers;
    private final int target;
    private int currentHelper;
    private int iterations;
    private final List<List<Individual<I>>> trainingGenerations;


    public NSGA2MulticriteriaSlow(
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


    private List<Individual<I>> genChildren(List<EvaluatedIndividual<I>> selected) {
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
            I iChild = mutation.apply(CollectionsEx.listOf(p), rng).get(0);
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
            i.dominate.clear();
            i.dominationCounter = 0;
            i.crowding = 0;
        }

        int numberOfCriteria = all.get(0).par().getCriteria().length;

        for (Individual<I> a : all) {
            for (Individual<I> b : all) {
                Parameter aa = a.par();
                Parameter bb = b.par();
                boolean atLeast = true;
                boolean greater = false;

                for (int c = 0; c < numberOfCriteria; ++c) {
                    double diff = aa.getCriteria(c) - bb.getCriteria(c);
                    atLeast &= diff > -1e-9;
                    greater |= diff > 1e-9;
                }
                if (atLeast && greater) {
                    a.dominate.add(b);
                    b.dominationCounter++;
                }
            }
        }

        List<Individual<I>> next = new ArrayList<>();
        List<Individual<I>> result = new ArrayList<>();
        List<Individual<I>> current = new ArrayList<>();

        for (Individual<I> a : all) {
            if (a.dominationCounter == 0) {
                next.add(a);
            }
        }

        List<Comparator<Individual<I>>> comparators = new ArrayList<>();
        for (int i = 0; i < numberOfCriteria; ++i) {
            comparators.add(new CriteriaComparator(i));
        }
        Comparator<Individual<I>> crowdComparator = (o1, o2) -> Double.compare(o2.crowding, o1.crowding);

        while (!next.isEmpty()) {
            List<Individual<I>> swap = next;
            next = current;
            current = swap;

            for (int i = 0; i < numberOfCriteria; ++i) {
                current.sort(comparators.get(i));
                double fMin = current.get(0).par().getCriteria(i);
                double fMax = current.get(current.size() - 1).par().getCriteria(i);
                if (fMax > fMin + 1e-9) {
                    current.get(0).crowding = Double.POSITIVE_INFINITY;
                    current.get(current.size() - 1).crowding = Double.POSITIVE_INFINITY;
                    for (int d = 1; d + 1 < current.size(); ++d) {
                        current.get(d).crowding += (current.get(d + 1).par().getCriteria(i) - current.get(d - 1).par().getCriteria(i)) / (fMax - fMin);
                    }
                }
            }
            current.sort(crowdComparator);
            result.addAll(current);
            next.clear();
            for (Individual<I> a : current) {
                for (Individual<I> b : a.dominate) {
                    b.dominationCounter--;
                    if (b.dominationCounter == 0) {
                        next.add(b);
                    }
                }
            }
        }

        for (Individual<I> i : all) {
            i.dominate.clear();
            i.dominationCounter = 0;
            i.crowding = 0;
        }

        return result.subList(0, Math.min(generationSize, result.size()));
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

    @Override
    public void refresh() {
        oldGeneration.clear();
        List<EvaluatedIndividual<I>> gen = new ArrayList<>(Utils.evaluateAll(factory.generateInitialPopulation(generationSize, rng), criteria));
        for (EvaluatedIndividual<I> i : gen) {
            this.oldGeneration.add(new Individual<>(i.ind(), i.par()));
        }
        bestTargetValue = Double.NEGATIVE_INFINITY;
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
        List<List<Double>> res = new ArrayList<>();

        for (EvaluatedIndividual<I> ind : generation) {
            List<Double> values = new ArrayList<>();
            values.add(ind.par().getCriteria()[target]);
            for (FitnessEvaluator<? super I> eval : helpers) {
                values.add(eval.getFitness(ind.ind(), null));
            }
            res.add(values);
        }
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
        printAll();
        getBestTargetValue();
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
        List<Individual<I>> generation = oldGeneration;
        double max = generation.get(0).par().getCriteria()[target];
        for (EvaluatedIndividual<I> ind : generation) {
            max = Math.max(max, ind.par().getCriteria()[target]);
        }
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
