package ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria;

import org.jetbrains.annotations.NotNull;
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import org.uncommons.watchmaker.framework.FitnessEvaluator;
import ru.ifmo.ctd.ngp.util.CollectionsEx;

import java.util.*;

/**
 * A pathologically-interfaced implementation of NSGA-II algorithm.
 *
 * @author Maxim Buzdalov
 */
public class NsgaII<T> {
    private final List<FitnessEvaluator<? super T>> criteria;
    private final EvolutionaryOperator<T> mutation;
    private final EvolutionaryOperator<T> crossover;
    private final Probability crossoverProbability;
    private final Random rng;

    public NsgaII(
            List<FitnessEvaluator<? super T>> criteria,
            EvolutionaryOperator<T> mutation,
            EvolutionaryOperator<T> crossover,
            double crossoverProbability,
            Random rng
    ) {
        this.criteria = criteria;
        this.mutation = mutation;
        this.crossover = crossover;
        this.crossoverProbability = new Probability(crossoverProbability);
        this.rng = rng;
    }

    private List<IndividualContainer<T>> computeFrontsAndCrowding(List<T> newIndividuals, List<IndividualContainer<T>> oldIndividuals) {
        List<IndividualContainer<T>> allRecords = new ArrayList<>(newIndividuals.size() + oldIndividuals.size());
        allRecords.addAll(oldIndividuals);
        for (int i = 0; i < newIndividuals.size(); ++i) {
            //TODO: We have a problem with collaborative fitness computation. Exactly here - V.
            allRecords.add(new IndividualContainer<>(newIndividuals.get(i), criteria, newIndividuals));
        }
        for (IndividualContainer<T> l : allRecords) {
            for (IndividualContainer<T> r : allRecords) {
                if (l.dominates(r)) {
                    r.betterThanMe++;
                    l.worseThanMe.add(r);
                } else if (r.dominates(l)) {
                    l.betterThanMe++;
                    r.worseThanMe.add(l);
                }
            }
        }
        List<IndividualContainer<T>> level = new ArrayList<>(allRecords.size());
        List<IndividualContainer<T>> next = new ArrayList<>(allRecords.size());
        int currentFront = 0;

        for (IndividualContainer<T> rec : allRecords) {
            if (rec.betterThanMe == 0) {
                level.add(rec);
            }
        }

        while (!level.isEmpty()) {
            computeCrowdDistance(level);

            for (IndividualContainer<T> rec : level) {
                rec.frontIndex = currentFront;
                for (IndividualContainer<T> sub : rec.worseThanMe) {
                    sub.betterThanMe--;
                    if (sub.betterThanMe == 0) {
                        next.add(sub);
                    }
                }
            }

            ++currentFront;
            List<IndividualContainer<T>> tmp = next;
            next = level;
            level = tmp;
            next.clear();
        }
        return allRecords;
    }

    private void computeCrowdDistance(List<IndividualContainer<T>> individuals) {
        @SuppressWarnings("unchecked")
        IndividualContainer<T>[] array = individuals.toArray(new IndividualContainer[0]);

        for (int c = 0; c < criteria.size(); ++c) {
            final int currentCriterion = c;
            Arrays.sort(array, Comparator.comparingDouble(o -> o.fitnesses[currentCriterion]));
            array[0].crowdingDistance = array[array.length - 1].crowdingDistance = Double.POSITIVE_INFINITY;
            double fitnessDiff = array[array.length - 1].fitnesses[c] - array[0].fitnesses[c];
            for (int i = 1; i + 1 < array.length; ++i) {
                array[i].crowdingDistance += (array[i + 1].fitnesses[c] - array[i - 1].fitnesses[c]) / fitnessDiff;
            }
        }
        for (int i = 0; i < array.length; ++i) {
            individuals.set(i, array[i]);
        }
    }

    private IndividualContainer<T> selectOne(List<IndividualContainer<T>> generation) {
        IndividualContainer<T> one = generation.get(rng.nextInt(generation.size()));
        IndividualContainer<T> two = generation.get(rng.nextInt(generation.size()));
        return one.compareTo(two) >= 0 ? one : two;
    }

    public void deleteCriterion(FitnessEvaluator<? super T> criterion) {
        criteria.remove(criterion);
    }

    public void addCriterion(FitnessEvaluator<? super T> criterion) {
        criteria.add(criterion);
    }

    public List<T> iterate(List<T> individuals, int times) {
        List<IndividualContainer<T>> current = computeFrontsAndCrowding(individuals, CollectionsEx.listOf());
        for (int time = 0; time < times; ++time) {
            List<T> next = new ArrayList<>(current.size());
            while (next.size() < current.size()) {
                List<T> ind = CollectionsEx.listOf(selectOne(current).individual, selectOne(current).individual);
                if (crossoverProbability.nextEvent(rng)) {
                    ind = crossover.apply(ind, rng);
                }
                next.addAll(mutation.apply(ind, rng));
            }
            List<IndividualContainer<T>> fronts = computeFrontsAndCrowding(next, current);
            Collections.sort(fronts);
            for (int i = 0, j = fronts.size() - 1; i < current.size(); ++i, --j) {
                current.set(i, fronts.get(j));
            }
        }
        List<T> rv = new ArrayList<>(current.size());
        for (IndividualContainer<T> indWrapper : current) {
            rv.add(indWrapper.individual);
        }
        return rv;
    }

    private static class IndividualContainer<T> implements Comparable<IndividualContainer<T>> {
        public final T individual;
        private int betterThanMe = 0;
        private final List<IndividualContainer<T>> worseThanMe = new ArrayList<>(1);
        private final double[] fitnesses;

        int frontIndex = -1;

        private double crowdingDistance = 0;

        private IndividualContainer(T individual, List<FitnessEvaluator<? super T>> fitnessEvs, List<T> all) {
            this.individual = individual;
            fitnesses = new double[fitnessEvs.size()];
            for (int i = 0; i < fitnesses.length; ++i) {
                fitnesses[i] = fitnessEvs.get(i).getFitness(individual, all);
            }
        }

        @Override
        public int compareTo(@NotNull IndividualContainer<T> o) {
            if (frontIndex != o.frontIndex) {
                return frontIndex - o.frontIndex;
            }
            return Double.compare(crowdingDistance, o.crowdingDistance);
        }

        boolean dominates(IndividualContainer<T> that) {
            for (int i = 0; i < fitnesses.length; ++i) {
                if (fitnesses[i] > that.fitnesses[i]) {
                    return true;
                }
            }
            return false;
        }
    }
}
