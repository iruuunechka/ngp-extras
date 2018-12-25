package ru.ifmo.ctd.ngp.demo.ffchooser.royal;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.uncommons.maths.statistics.DataSet;
import org.uncommons.watchmaker.framework.FitnessEvaluator;
import org.uncommons.watchmaker.framework.operators.EvolutionPipeline;

import ru.ifmo.ctd.ngp.demo.ffchooser.GenerationsCounter;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.*;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.factory.EvaluatorsFactory;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.utils.ConfigurationVisitor;
import ru.ifmo.ctd.ngp.demo.ffchooser.genetic.ESCounter;
import ru.ifmo.ctd.ngp.demo.ffchooser.genetic.GACounter;
import ru.ifmo.ctd.ngp.demo.ffchooser.genetic.NoLearningCounter;
import ru.ifmo.ctd.ngp.demo.ffchooser.nonStationary.DynaWithStrategyConfiguration;
import ru.ifmo.ctd.ngp.demo.ffchooser.nonStationary.EpsQRLCDConfiguration;
import ru.ifmo.ctd.ngp.demo.ffchooser.nonStationary.PrioritizedSweepingConfiguration;
import ru.ifmo.ctd.ngp.demo.ffchooser.nonStationary.RLCDConfiguration;
import ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.OAEnvironment;
import ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.ReinforcementCounter;
import ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.reward.RewardCalculator;
import ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.FunctionalEvolutionStrategy;
import ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.FunctionalFitness;
import ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.FunctionalGeneticAlgorithm;
import ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.stat.StatisticsRunVisitor;
import ru.ifmo.ctd.ngp.demo.ffchooser.utils.GeneticUtils;
import ru.ifmo.ctd.ngp.demo.legacy_ds.string.impl.BitString;
import ru.ifmo.ctd.ngp.learning.reinforce.Agent;
import ru.ifmo.ctd.ngp.learning.reinforce.EnvironmentPrinterImpl;
import ru.ifmo.ctd.ngp.demo.util.Nil;

/**
 * Statistics collection runner that runs corresponding scenarios
 * for different types of configurations.
 *  
 * @author Arina Buzdalova
 */
public final class RunVisitor implements ConfigurationVisitor<RunArgument, Nil> {
    private static final RunVisitor instance = new RunVisitor();
    private RunVisitor() {}
    
    /**
     * Returns instance of this {@link StatisticsRunVisitor}
     * @return instance of this {@link StatisticsRunVisitor}
     */
    public static RunVisitor instance() {
        return instance;
    }
   
    /**
     * Runs the statistics collection of the performance on the specified configuration.
     * Necessary parameters are specified by the given {@link RunArgument}.
     * @param config the specified configuration
     * @param arg the specified argument
     */
    public void run(Configuration config, RunArgument arg) {
        config.accept(RunVisitor.instance(), arg);
	}
    
    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
	public Nil visitIdealConfiguration(@NotNull IdealConfiguration config, @NotNull RunArgument argument) {
    	try {
			runSimple(config, argument);
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return Nil.value();
	}
    
    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public Nil visitNoLearnConfiguration(@NotNull NoLearnConfiguration config, @NotNull RunArgument argument) {
    	try {
			runSimple(config, argument);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Nil.value();
    }
	
    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public Nil visitGreedyConfiguration(@NotNull GreedyConfiguration config, @NotNull RunArgument argument) {
		try {
			runWithLearning(config, argument);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Nil.value();
    }
	
    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
	public Nil visitDelayedConfiguration(@NotNull DelayedConfiguration config, @NotNull RunArgument argument) {
    	try {
			runWithLearning(config, argument);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Nil.value();
    }
    
    /**
     * {@inheritDoc}
     */
	@NotNull
    @Override
	public Nil visitDynaConfiguration(@NotNull DynaConfiguration config, @NotNull RunArgument argument) {
    	try {
			runWithLearning(config, argument);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Nil.value();
	}

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public Nil visitRConfiguration(@NotNull RConfiguration config, @NotNull RunArgument argument) {
        try {
            runWithLearning(config, argument);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Nil.value();
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public Nil visitDynaWithStrategyConfiguration(@NotNull DynaWithStrategyConfiguration config,
                                                  @NotNull RunArgument argument) {
        try {
            runWithLearning(config, argument);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Nil.value();
    }


    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public Nil visitPrioritizedSweepingConfiguration(@NotNull PrioritizedSweepingConfiguration config,
                                                     @NotNull RunArgument argument) {
        try {
            runWithLearning(config, argument);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Nil.value();
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public Nil visitRLCDConfiguration(@NotNull RLCDConfiguration config,
                                      @NotNull RunArgument argument) {
        try {
            runWithLearning(config, argument);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Nil.value();
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public Nil visitEpsQRLCDConfiguration(@NotNull EpsQRLCDConfiguration config,
                                      @NotNull RunArgument argument) {
        try {
            runWithLearning(config, argument);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Nil.value();
    }


    private void runSimple(Configuration conf, RunArgument arg) throws IOException {
		int stepsLimit = conf.getSteps();
		
		double crossover = conf.getCrossover();
		double mutation = conf.getMutation();
		
		int length = conf.getLength();
		
		NoLearningCounter counter = arg.es
                ? new ESCounter(stepsLimit, arg.generationSize, arg.eliteCount,
                    new EvolutionPipeline<>(FunctionalGeneticAlgorithm.getDefaultMutation()), true, 1)
				: new GACounter(stepsLimit, arg.generationSize, arg.eliteCount,
                    new EvolutionPipeline<>(FunctionalGeneticAlgorithm.getDefaultOperators(crossover, mutation)));
										 	 
		counter.setLength(length);
		counter.setEvaluator(new RoyalRoadFitness(8), length);
		
		StatisticsPrinter<BitString> printer = new StatisticsPrinter<>(0);
		counter.addPrinter(printer);
		
		printStatistics(printer, counter, arg, conf);
    }
	    
	private void runWithLearning(LearnConfiguration conf, RunArgument arg) throws IOException {
		int stepsLimit = conf.getSteps();
			
		double crossover = conf.getCrossover();
		double mutation = conf.getMutation();
			
		int length = conf.getLength();
			
		RewardCalculator reward = conf.getReward();
		
		EvaluatorsFactory factory = conf.getEvaluators();
		List<FunctionalFitness> evaluators = factory.getEvaluators();
		int targetIndex = factory.targetIndex();

		FunctionalGeneticAlgorithm alg = arg.es
                ? new FunctionalEvolutionStrategy(length, targetIndex, targetIndex, evaluators, true, 1)
                : FunctionalGeneticAlgorithm.newFGA(length, targetIndex, targetIndex, evaluators, crossover, mutation);
		alg.setGenerationSize(arg.generationSize);
		alg.setEliteCount(arg.eliteCount);
		alg.setStartPopulation(GeneticUtils.randomPopulation(length, arg.generationSize));
		
		OAEnvironment env = new OAEnvironment(alg, reward);
		
		env.addPrinter(new EnvironmentPrinterImpl<>(new PrintWriter(System.out)));
			
		Agent<String, Integer> agent = 	conf.createAgent();
			
		ReinforcementCounter<BitString> counter = new ReinforcementCounter<>(stepsLimit, agent, alg, env);
			
		counter.setLength(length);
		counter.setEvaluator(new RoyalRoadFitness(8), length);		
		
		StatisticsPrinter<BitString> printer = new StatisticsPrinter<>(targetIndex);
		alg.addPrinter(printer);
		
		printStatistics(printer,counter, arg, conf);
	}
	
	private void printStatistics(StatisticsPrinter<BitString> printer,
                                 GenerationsCounter<FitnessEvaluator<? super BitString>, BitString> counter,
                                 RunArgument arg, Configuration conf) throws IOException {
		arg.writer.append(String.format("times %d %s\n", arg.times, conf.toString()));
		DataSet fit = new DataSet();
		DataSet gen = new DataSet();
		
		for (int i = 0; i < arg.times; i++) {
			int generations = counter.countGenerations();
			arg.writer.append("generations: ").append(String.valueOf(generations)).append("\n");
			
			if (generations > 0) {
				gen.addValue(generations);
			}
			
			double bestFitness = printer.getBestFitness();
			Set<BitString> bests = printer.getBestsIndividuals();

			for (int j = 0; j < bests.size(); j++) {
				fit.addValue(bestFitness);
			}
			printer.refresh();
			
			arg.writer.append(String.format("%f\n", bestFitness));
			arg.writer.flush();
			
			System.out.println("==========================");
		}
		
		if (fit.getSize() != 0) {
			arg.writer.append(String.format("Fitness. max: %f average: %f deviation: %f\n", fit.getMaximum(),
					fit.getArithmeticMean(), fit.getStandardDeviation()));
		}
		
		if (gen.getSize() != 0) {
		arg.writer.append(String.format("Generations. %d of %d successful: min: %f max: %f average: %f deviation: %f\n", 
				gen.getSize(), arg.times,gen.getMinimum(), gen.getMaximum(),
				gen.getArithmeticMean(), gen.getStandardDeviation()));
		}
		
		arg.writer.flush();
		//arg.writer.close();
	}
}
