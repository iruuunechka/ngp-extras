package ru.ifmo.ctd.ngp.demo.ffchooser.nonStationary;

import org.jetbrains.annotations.NotNull;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.LearnConfiguration;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.factory.EvaluatorsFactory;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.utils.ConfigurationVisitor;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.utils.PropertyMapped;
import ru.ifmo.ctd.ngp.demo.ffchooser.nonStationary.strategy.Strategy;
import ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.reward.RewardCalculator;
import ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.state.StateCalculator;
import ru.ifmo.ctd.ngp.demo.util.textconstructor.TextConstructor;
import ru.ifmo.ctd.ngp.learning.reinforce.Agent;

import java.util.Properties;

/**
 * @author Irene Petrova
 */
public class EpsQRLCDConfiguration extends LearnConfiguration {
    private static final long serialVersionUID = 2268079488960263951L;

    private final double epsilon;
    private final double alpha;
    private final double gamma;
    private final double mindiff;
    private final Strategy strategy;

    private final String info;

    /**
     * Constructs {@link EpsQRLCDConfiguration} with the specified {@link java.util.Properties}.
     * @param properties the specified properties
     */
    public EpsQRLCDConfiguration(Properties properties) {
        super(properties);

        this.epsilon = Double.parseDouble(properties.getProperty("epsq"));
        this.alpha = Double.parseDouble(properties.getProperty("alpha"));
        this.gamma = Double.parseDouble(properties.getProperty("gamma"));
        this.mindiff = Double.parseDouble(properties.getProperty("mindiff"));
        this.strategy = TextConstructor.constructFromString(Strategy.class,
                properties.getProperty("strategy"));

        info = properties.containsKey("info") ? properties.getProperty("info") : "";
    }

    /**
     * Constructs {@link EpsQRLCDConfiguration} with the specified parameters
     *
     * @param steps the maximum number of steps that can be made by the genetic algorithm
     * @param crossover the probability of crossover
     * @param mutation the probability of mutation
     * @param length the length of the individual
     * @param point the switch point for the piecewise functions
     * @param divider the divider used in the <code> x / divider </code> fitness function
     * @param eps the probability of exploration
     * @param alpha the learning rate
     * @param gamma the discount factor
     * @param mindiff minimal difference for Q
     * @param generationCount the number of individuals in a generation
     * @param eliteCount the elite count
     * @param strategy strategy of exploration
     */
    public EpsQRLCDConfiguration(int steps, double crossover,
                                         double mutation, int length, int point, int divider,
                                         double eps, double alpha, double gamma, double mindiff, int generationCount, int eliteCount, Strategy strategy) {
        super(steps, crossover, mutation, length, point, divider, generationCount, eliteCount);

        this.epsilon = eps;
        this.alpha = alpha;
        this.gamma = gamma;
        this.strategy = strategy;
        this.mindiff = mindiff;

        this.info = "";
    }

    /**
     * Constructs {@link EpsQRLCDConfiguration} with the specified parameters
     *
     * @param steps the maximum number of steps that can be made by the genetic algorithm
     * @param crossover the probability of crossover
     * @param mutation the probability of mutation
     * @param length the length of the individual
     * @param point the switch point for the piecewise functions
     * @param divider the divider used in the <code> x / divider </code> fitness function
     * @param eps the probability of exploration
     * @param alpha the learning rate
     * @param gamma the discount factor
     * @param mindiff minimal difference for Q
     * @param evaluators the factory of fitness evaluators list
     * @param reward the {@link ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.reward.RewardCalculator}
     * @param state the {@link ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.state.StateCalculator}
     * @param generationCount the number of individuals in a generation
     * @param eliteCount the elite count
     * @param strategy strategy of exploration
     */
    public EpsQRLCDConfiguration(int steps, double crossover,
                                         double mutation, int length, int point, int divider,
                                         double eps, double alpha, double gamma, double mindiff,
                                         EvaluatorsFactory evaluators, RewardCalculator reward, StateCalculator<String, Integer> state,
                                         int generationCount, int eliteCount, Strategy strategy) {
        super(steps, crossover, mutation, length, point, divider, evaluators, reward, state,
                generationCount, eliteCount);

        this.epsilon = eps;
        this.alpha = alpha;
        this.gamma = gamma;
        this.mindiff = mindiff;
        this.strategy = strategy;

        this.info = "";
    }

    /**
     * Constructs {@link EpsQRLCDConfiguration} with the specified parameters
     * and the specified additional information
     *
     * @param steps the maximum number of steps that can be made by the genetic algorithm
     * @param crossover the probability of crossover
     * @param mutation the probability of mutation
     * @param length the length of the individual
     * @param point the switch point for the piecewise functions
     * @param divider the divider used in the <code> x / divider </code> fitness function
     * @param eps the probability of exploration
     * @param alpha the learning rate
     * @param gamma the discount factor
     * @param mindiff minimal difference for Q
     * @param info the specified additional information
     * @param generationCount the number of individuals in a generation
     * @param eliteCount the elite count
     * @param strategy strategy of exploration
     */
    public EpsQRLCDConfiguration(int steps, double crossover,
                                         double mutation, int length, int point, int divider,
                                         double eps, double alpha, double gamma, double mindiff, String info,
                                         int generationCount, int eliteCount, Strategy strategy) {
        super(steps, crossover, mutation, length, point, divider, generationCount, eliteCount);

        this.epsilon = eps;
        this.alpha = alpha;
        this.gamma = gamma;
        this.mindiff = mindiff;

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
        return new EpsQRLCD<>(alpha, gamma, epsilon, mindiff, strategy, length / divider);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String listLearnValues() {
        return "alpha " + alpha + " gamma " + gamma + " eps " + epsilon + " mindiff " + mindiff;
    }

    /**
     * {@inheritDoc}
     */
    @Deprecated
    @Override
    public String generateName() {
        return "/alpha" + alpha + "gamma" + gamma + "eps" + epsilon + "mindiff" + mindiff;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLabel() {
        return "eqrlcd";
    }

    /**
     * @return the eps
     */
    @PropertyMapped
    public double getEpsq() {
        return epsilon;
    }

    /**
     * @return the alpha
     */
    @PropertyMapped
    public double getAlpha() {
        return alpha;
    }

    /**
     * @return the gamma
     */
    @PropertyMapped
    public double getGamma() {
        return gamma;
    }

    /**
     * @return the mindiff
     */
    @PropertyMapped
    public double getMindiff() {
        return mindiff;
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
        temp = Double.doubleToLongBits(alpha);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(epsilon);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(gamma);
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
        EpsQRLCDConfiguration other = (EpsQRLCDConfiguration) obj;
        return Double.doubleToLongBits(alpha) == Double.doubleToLongBits(other.alpha) &&
                Double.doubleToLongBits(epsilon) == Double.doubleToLongBits(other.epsilon) &&
                Double.doubleToLongBits(gamma) == Double.doubleToLongBits(other.gamma);
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public <A, R> R accept(@NotNull ConfigurationVisitor<A, R> visitor, @NotNull A argument) {
        return visitor.visitEpsQRLCDConfiguration(this, argument);
    }
}
