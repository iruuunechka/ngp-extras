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
public class RLCDConfiguration extends LearnConfiguration {

    private final double probability;
    private final double discount;
    private final int k;
    private final Strategy strategy;
    private final double omega;
    private final double Emin;
    private final double rho;
    private final int M;

    private final String info;

    /**
     * Constructs {@link RLCDConfiguration} with the specified {@link java.util.Properties}.
     * @param properties the specified properties
     */
    public RLCDConfiguration(Properties properties) {
        super(properties);

        this.probability = Double.parseDouble(properties.getProperty("probability"));
        this.discount = Double.parseDouble(properties.getProperty("discount"));
        this.k = Integer.parseInt(properties.getProperty("k"));
        this.strategy = TextConstructor.constructFromString(Strategy.class,
                properties.getProperty("strategy"));
        this.omega = Double.parseDouble(properties.getProperty("omega"));
        this.Emin = Double.parseDouble(properties.getProperty("emin"));
        this.rho = Double.parseDouble(properties.getProperty("rho"));
        this.M = Integer.parseInt(properties.getProperty("m"));

        info = properties.containsKey("info") ? properties.getProperty("info") : "";
    }

    /**
     * Constructs {@link RLCDConfiguration} with the specified parameters
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
     * @param emin if energy of the model is less than emin, a new model is created
     * @param omega specifies the relative importance of rewards and transitions predictions(eR eT)
     * @param rho  is the adjustment coefficient for the quality
     * @param m is the number of stored experiments
     */
    public RLCDConfiguration(int steps, double crossover,
                             double mutation, int length, int point, int divider,
                             double probability, double discount, int k, int generationCount, int eliteCount, Strategy strategy, double omega, double emin, double rho, int m) {
        super(steps, crossover, mutation, length, point, divider, generationCount, eliteCount);

        this.probability = probability;
        this.discount = discount;
        this.k = k;
        this.strategy = strategy;
        this.omega = omega;
        Emin = emin;
        this.rho = rho;
        M = m;

        this.info = "";
    }

    /**
     * Constructs {@link RLCDConfiguration} with the specified parameters
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
     * @param emin if energy of the model is less than emin, a new model is created
     * @param omega specifies the relative importance of rewards and transitions predictions(eR eT)
     * @param rho  is the adjustment coefficient for the quality
     * @param m is the number of stored experiments
     */
    public RLCDConfiguration(int steps, double crossover,
                             double mutation, int length, int point, int divider,
                             double probability, double discount, int k,
                             EvaluatorsFactory evaluators, RewardCalculator reward, StateCalculator<String, Integer> state,
                             int generationCount, int eliteCount, Strategy strategy, double omega, double emin, double rho, int m) {
        super(steps, crossover, mutation, length, point, divider, evaluators, reward, state,
                generationCount, eliteCount);

        this.probability = probability;
        this.discount = discount;
        this.k = k;
        this.strategy = strategy;
        this.omega = omega;
        Emin = emin;
        this.rho = rho;
        M = m;

        this.info = "";
    }

    /**
     * Constructs {@link RLCDConfiguration} with the specified parameters
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
     * @param emin if energy of the model is less than emin, a new model is created
     * @param omega specifies the relative importance of rewards and transitions predictions(eR eT)
     * @param rho  is the adjustment coefficient for the quality
     * @param m is the number of stored experiments
     */
    public RLCDConfiguration(int steps, double crossover,
                             double mutation, int length, int point, int divider,
                             double probability, double discount, int k, String info,
                             int generationCount, int eliteCount, Strategy strategy, double omega, double emin, double rho, int m) {
        super(steps, crossover, mutation, length, point, divider, generationCount, eliteCount);

        this.probability = probability;
        this.discount = discount;
        this.k = k;

        this.info = info;
        this.strategy = strategy;
        this.omega = omega;
        Emin = emin;
        this.rho = rho;
        M = m;
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
        return new RLCD<>(probability, discount, k, strategy, omega, Emin, rho, M);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String listLearnValues() {
        return "probab " + probability + " discount " + discount + " k " + k + "omega" + omega + "rho" + rho;
    }

    /**
     * {@inheritDoc}
     */
    @Deprecated
    @Override
    public String generateName() {
        return "/probability" + probability + "discount" + discount + "k" + k + "omega" + omega + "rho" + rho;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLabel() {
        return "rlcd";
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

    @PropertyMapped
    public double getOmega() {
        return omega;
    }

    @PropertyMapped
    public double getRho() {
        return rho;
    }

    @PropertyMapped
    public double getEmin() {
        return Emin;
    }

    @PropertyMapped
    public double getM() {
        return M;
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
        temp = Double.doubleToLongBits(omega);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(Emin);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(rho);
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
        RLCDConfiguration other = (RLCDConfiguration) obj;
        return Double.doubleToLongBits(discount) == Double.doubleToLongBits(other.discount) &&
                Double.doubleToLongBits(k) == Double.doubleToLongBits(other.k) &&
                Double.doubleToLongBits(probability) == Double.doubleToLongBits(other.probability) &&
                Double.doubleToLongBits(omega) == Double.doubleToLongBits(other.omega) &&
                Double.doubleToLongBits(Emin) == Double.doubleToLongBits(other.Emin) &&
                Double.doubleToLongBits(rho) == Double.doubleToLongBits(other.rho);
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public <A, R> R accept(@NotNull ConfigurationVisitor<A, R> visitor, @NotNull A argument) {
        return visitor.visitRLCDConfiguration(this, argument);
    }
}