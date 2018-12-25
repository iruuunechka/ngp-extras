package ru.ifmo.ctd.ngp.demo.ffchooser.nonStationary;

import org.jetbrains.annotations.NotNull;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.LearnConfiguration;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.factory.EvaluatorsFactory;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.utils.ConfigurationVisitor;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.utils.PropertyMapped;
import ru.ifmo.ctd.ngp.demo.ffchooser.nonStationary.strategy.Strategy;
import ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.reward.RewardCalculator;
import ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.state.StateCalculator;
import ru.ifmo.ctd.ngp.learning.reinforce.Agent;
import ru.ifmo.ctd.ngp.demo.util.textconstructor.TextConstructor;

import java.util.Properties;

/**
 * @author Irene Petrova
 */
public class DynaWithStrategyConfiguration extends LearnConfiguration {
    private static final long serialVersionUID = 2268079488960263951L;

    private final double probability;
    private final double discount;
    private final int k;
    private final Strategy strategy;

    private final String info;

    /**
     * Constructs {@link DynaWithStrategyConfiguration} with the specified {@link java.util.Properties}.
     * @param properties the specified properties
     */
    public DynaWithStrategyConfiguration(Properties properties) {
        super(properties);

        this.probability = Double.parseDouble(properties.getProperty("probability"));
        this.discount = Double.parseDouble(properties.getProperty("discount"));
        this.k = Integer.parseInt(properties.getProperty("k"));
        this.strategy = TextConstructor.constructFromString(Strategy.class,
                properties.getProperty("strategy"));

        info = properties.containsKey("info") ? properties.getProperty("info") : "";
    }

    /**
     * Constructs {@link DynaWithStrategyConfiguration} with the specified parameters
     *
     * @param steps the maximum number of steps that can be made by the genetic algorithm
     * @param crossover the probability of crossover
     * @param mutation the probability of mutation
     * @param length the length of the individual
     * @param point the switch point for the piecewise functions
     * @param divider the divider used in the <code> x / divider </code> fitness function
     * @param probability the probability of exploration
     * @param discount the discount factor
     * @param k the number of updated state-action values
     * @param generationCount the number of individuals in a generation
     * @param eliteCount the elite count
     * @param strategy strategy of exploration
     */
    public DynaWithStrategyConfiguration(int steps, double crossover,
                                         double mutation, int length, int point, int divider,
                                         double probability, double discount, int k, int generationCount, int eliteCount, Strategy strategy) {
        super(steps, crossover, mutation, length, point, divider, generationCount, eliteCount);

        this.probability = probability;
        this.discount = discount;
        this.k = k;
        this.strategy = strategy;

        this.info = "";
    }

    /**
     * Constructs {@link DynaWithStrategyConfiguration} with the specified parameters
     *
     * @param steps the maximum number of steps that can be made by the genetic algorithm
     * @param crossover the probability of crossover
     * @param mutation the probability of mutation
     * @param length the length of the individual
     * @param point the switch point for the piecewise functions
     * @param divider the divider used in the <code> x / divider </code> fitness function
     * @param probability the probability of exploration
     * @param discount the discount factor
     * @param k the number of updated state-action values
     * @param evaluators the factory of fitness evaluators list
     * @param reward the {@link ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.reward.RewardCalculator}
     * @param state the {@link ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.state.StateCalculator}
     * @param generationCount the number of individuals in a generation
     * @param eliteCount the elite count
     * @param strategy strategy of exploration
     */
    public DynaWithStrategyConfiguration(int steps, double crossover,
                                         double mutation, int length, int point, int divider,
                                         double probability, double discount, int k,
                                         EvaluatorsFactory evaluators, RewardCalculator reward, StateCalculator<String, Integer> state,
                                         int generationCount, int eliteCount, Strategy strategy) {
        super(steps, crossover, mutation, length, point, divider, evaluators, reward, state,
                generationCount, eliteCount);

        this.probability = probability;
        this.discount = discount;
        this.k = k;
        this.strategy = strategy;

        this.info = "";
    }

    /**
     * Constructs {@link DynaWithStrategyConfiguration} with the specified parameters
     * and the specified additional information
     *
     * @param steps the maximum number of steps that can be made by the genetic algorithm
     * @param crossover the probability of crossover
     * @param mutation the probability of mutation
     * @param length the length of the individual
     * @param point the switch point for the piecewise functions
     * @param divider the divider used in the <code> x / divider </code> fitness function
     * @param probability the probability of exploration
     * @param discount the discount factor
     * @param k the number of updated state-action values
     * @param info the specified additional information
     * @param generationCount the number of individuals in a generation
     * @param eliteCount the elite count
     * @param strategy strategy of exploration
     */
    public DynaWithStrategyConfiguration(int steps, double crossover,
                                         double mutation, int length, int point, int divider,
                                         double probability, double discount, int k, String info,
                                         int generationCount, int eliteCount, Strategy strategy) {
        super(steps, crossover, mutation, length, point, divider, generationCount, eliteCount);

        this.probability = probability;
        this.discount = discount;
        this.k = k;

        this.info = info;
        this.strategy = strategy;
    }

    /**
     * Gets the additional information about this configuration
     * @return the additional information about this configuration
     */
    public String getInfo() {
        return info;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Agent<String, Integer> createAgent() {
        return new DynaAgentWithStrategy<>(probability, discount, k, strategy);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String listLearnValues() {
        return "probab " + probability + " discount " + discount + " k " + k;
    }

    /**
     * {@inheritDoc}
     */
    @Deprecated
    @Override
    public String generateName() {
        return "/probability" + probability + "discount" + discount + "k" + k;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLabel() {
        return "dynaWithStrategy";
    }

    /**
     * @return the probability of exploration
     */
    @PropertyMapped
    public double getProbability() {
        return probability;
    }

    /**
     * @return the discount factor
     */
    @PropertyMapped
    public double getDiscount() {
        return discount;
    }

    /**
     * @return the number of state-action values being updated
     */
    @PropertyMapped
    public int getK() {
        return k;
    }

    @PropertyMapped
    public Strategy getStrategy() {
        return strategy;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return super.toString() + " " + info;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        long temp;
        temp = Double.doubleToLongBits(discount);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(k);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(probability);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        DynaWithStrategyConfiguration other = (DynaWithStrategyConfiguration) obj;
        return Double.doubleToLongBits(discount) == Double.doubleToLongBits(other.discount) &&
                Double.doubleToLongBits(k) == Double.doubleToLongBits(other.k) &&
                Double.doubleToLongBits(probability) == Double.doubleToLongBits(other.probability);
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public <A, R> R accept(@NotNull ConfigurationVisitor<A, R> visitor, @NotNull A argument) {
        return visitor.visitDynaWithStrategyConfiguration(this, argument);
    }
}
