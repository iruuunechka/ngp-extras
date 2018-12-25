package ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.stat;

import java.io.IOException;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.uncommons.watchmaker.framework.FitnessEvaluator;
import org.uncommons.watchmaker.framework.operators.EvolutionPipeline;

import ru.ifmo.ctd.ngp.demo.ffchooser.GenerationsCounter;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.*;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.factory.EvaluatorsFactory;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.utils.ConfigurationVisitor;
import ru.ifmo.ctd.ngp.demo.ffchooser.genetic.ESCounter;
import ru.ifmo.ctd.ngp.demo.ffchooser.genetic.GACounter;
import ru.ifmo.ctd.ngp.demo.ffchooser.genetic.NoLearningCounter;
import ru.ifmo.ctd.ngp.demo.ffchooser.genetic.Printer;
import ru.ifmo.ctd.ngp.demo.ffchooser.genetic.print.CompressingPrinter;
import ru.ifmo.ctd.ngp.demo.ffchooser.genetic.print.StatObserverPrinter;
import ru.ifmo.ctd.ngp.demo.ffchooser.nonStationary.DynaWithStrategyConfiguration;
import ru.ifmo.ctd.ngp.demo.ffchooser.nonStationary.EpsQRLCDConfiguration;
import ru.ifmo.ctd.ngp.demo.ffchooser.nonStationary.PrioritizedSweepingConfiguration;
import ru.ifmo.ctd.ngp.demo.ffchooser.nonStationary.RLCDConfiguration;
import ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.OAEnvironment;
import ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.ReinforcementCounter;
import ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.reward.RewardCalculator;
import ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.state.StateCalculator;
import ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.FunctionalEvolutionStrategy;
import ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.FunctionalFitness;
import ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.FunctionalGeneticAlgorithm;
import ru.ifmo.ctd.ngp.demo.ffchooser.utils.GeneticUtils;
import ru.ifmo.ctd.ngp.demo.legacy_ds.string.impl.BitString;
import ru.ifmo.ctd.ngp.learning.reinforce.Agent;

/**
 * Statistics collection runner that runs corresponding scenarios
 * for different types of configurations.
 *  
 * @author Arina Buzdalova
 */
public final class StatisticsRunVisitor implements ConfigurationVisitor<StatisticsRunArgument, Integer> {
    private static final StatisticsRunVisitor instance = new StatisticsRunVisitor();
    private StatisticsRunVisitor() {}
    
    /**
     * Returns instance of this {@link StatisticsRunVisitor}
     * @return instance of this {@link StatisticsRunVisitor}
     */
    public static StatisticsRunVisitor instance() {
        return instance;
    }
   
    /**
     * Runs the statistics collection of the performance on the specified configuration.
     * Necessary parameters are specified by the given {@link StatisticsRunArgument}.
     * @param config the specified configuration
     * @param arg the specified argument
     * @return number of fitness evaluators or -1 if some problems occur
     */
    public Integer run(Configuration config, StatisticsRunArgument arg) {
        return config.accept(StatisticsRunVisitor.instance(), arg);
    }
    
    @NotNull
    @Override
	public Integer visitIdealConfiguration(@NotNull IdealConfiguration config,
                                           @NotNull StatisticsRunArgument argument) {
    	try {
			return runSimple(config, argument, 0);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return -1;
	}
    
    @NotNull
    @Override
    public Integer visitNoLearnConfiguration(@NotNull NoLearnConfiguration config,
                                             @NotNull StatisticsRunArgument argument) {
    	try {
			return runSimple(config, argument, config.getEvaluators().targetIndex());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return -1;
    }
	
    @NotNull
    @Override
    public Integer visitGreedyConfiguration(@NotNull GreedyConfiguration config,
                                            @NotNull StatisticsRunArgument argument) {
		try {
			return runWithLearning(config, argument);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return -1;
    }
	
    @NotNull
    @Override
	public Integer visitDelayedConfiguration(@NotNull DelayedConfiguration config,
                                             @NotNull StatisticsRunArgument argument) {
    	try {
			return runWithLearning(config, argument);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return -1;
    }
    
    @NotNull
    @Override
	public Integer visitRConfiguration(@NotNull RConfiguration config,
                                       @NotNull StatisticsRunArgument argument) {
    	try {
			return runWithLearning(config, argument);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return -1;
	}
    
	@NotNull
    @Override
	public Integer visitDynaConfiguration(@NotNull DynaConfiguration config,
                                          @NotNull StatisticsRunArgument argument) {
    	try {
			return runWithLearning(config, argument);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return -1;
	}

    @NotNull
    @Override
    public Integer visitDynaWithStrategyConfiguration(@NotNull DynaWithStrategyConfiguration config,
                                                      @NotNull StatisticsRunArgument argument) {
        try {
            return runWithLearning(config, argument);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }


    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public Integer visitPrioritizedSweepingConfiguration(@NotNull PrioritizedSweepingConfiguration config,
                                                         @NotNull StatisticsRunArgument argument) {
        try {
            runWithLearning(config, argument);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public Integer visitRLCDConfiguration(@NotNull RLCDConfiguration config,
                                          @NotNull StatisticsRunArgument argument) {
        try {
            runWithLearning(config, argument);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public Integer visitEpsQRLCDConfiguration(@NotNull EpsQRLCDConfiguration config,
                                          @NotNull StatisticsRunArgument argument) {
        try {
            runWithLearning(config, argument);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private int runSimple(Configuration conf, StatisticsRunArgument arg, int helperIndex) throws IOException {
		int stepsLimit = conf.getSteps();
		
		double crossover = conf.getCrossover();
		double mutation = conf.getMutation();
		
		int length = conf.getLength();
		//int divider = conf.getDivider();
        EvaluatorsFactory factory = conf.getEvaluators();

		NoLearningCounter counter = arg.es
                ? new ESCounter(stepsLimit, arg.generationSize, arg.eliteCount,
                    new EvolutionPipeline<>(FunctionalGeneticAlgorithm.getDefaultOperators(crossover, mutation)), true, 5)
				: new GACounter(stepsLimit, arg.generationSize, arg.eliteCount,
                    new EvolutionPipeline<>(FunctionalGeneticAlgorithm.getDefaultOperators(crossover, mutation)));
										 	 
		
		counter.setLength(length);
		counter.setEvaluator(factory.getEvaluators().get(helperIndex), factory.bestFitness());//(new FunctionalFitness(new IntegerFunctionImpl(new X(), 1.0 / divider, 0, length)), length / divider);
		counter.setStartPopulation(GeneticUtils.zeroPopulation(length, arg.generationSize));
		
		
		CompressingPrinter<BitString> printer = new CompressingPrinter<>(arg.writer, counter.getEvaluatorsCount());
		
		counter.addPrinter(printer);
		
		StatObserverPrinter<BitString> statPrinter = null;
		
		if (arg.observer != null) {
			statPrinter = new StatObserverPrinter<>(arg.observer, conf, arg.updatePeriod, arg.times);
			counter.addPrinter(statPrinter);
		}

		arg.printers.forEach(counter::addPrinter);
		
		return printStatistics(printer, arg.printers, conf, counter, statPrinter, arg.times, stepsLimit, 
				counter.getEvaluatorsCount(), counter.getTargetIndex());
    }
	    
	private int runWithLearning(LearnConfiguration conf, StatisticsRunArgument arg) throws IOException {
		int stepsLimit = conf.getSteps();
			
		double crossover = conf.getCrossover();
		double mutation = conf.getMutation();
			
		int length = conf.getLength();
		//int divider = conf.getDivider();
			
		RewardCalculator reward = conf.getReward();
        StateCalculator<String, Integer> state = conf.getState();
		
		EvaluatorsFactory factory = conf.getEvaluators();
		List<FunctionalFitness> evaluators = factory.getEvaluators();
		int targetIndex = factory.targetIndex();

		FunctionalGeneticAlgorithm alg = arg.es
                ? new FunctionalEvolutionStrategy(length, targetIndex, targetIndex, evaluators, mutation, crossover, true, 5)
				: FunctionalGeneticAlgorithm.newFGA(length, targetIndex, targetIndex, evaluators, crossover, mutation);
		alg.setGenerationSize(arg.generationSize);
		alg.setStartPopulation(GeneticUtils.zeroPopulation(length, arg.generationSize));
		alg.setEliteCount(arg.eliteCount);
		
		OAEnvironment env = new OAEnvironment(alg, reward, state);

		Agent<String, Integer> agent = 	conf.createAgent();
			
		ReinforcementCounter<BitString> counter = new ReinforcementCounter<>(stepsLimit, agent, alg, env);
			
		counter.setLength(length);
		counter.setEvaluator(factory.getEvaluators().get(targetIndex), factory.bestFitness());//new IntFitness(new BitCountFitness(1.0 / divider, 0)), length / divider);
			
		CompressingPrinter<BitString> printer = new CompressingPrinter<>(arg.writer, counter.getEvaluatorsCount());
			
		alg.addPrinter(printer);
		
		StatObserverPrinter<BitString> statPrinter = null;
		
		if (arg.observer != null) {
			statPrinter = new StatObserverPrinter<>(arg.observer, conf, arg.updatePeriod, arg.times);
			alg.addPrinter(statPrinter);
		}

		arg.printers.forEach(alg::addPrinter);
		
		return printStatistics(printer, arg.printers, conf, counter, 
				statPrinter, arg.times, stepsLimit, counter.getEvaluatorsCount(), counter.getTargetIndex());
	}
	
	private int printStatistics(CompressingPrinter<BitString> printer,
                                List<? extends Printer<? super BitString>> printers,
                                Configuration conf,
                                GenerationsCounter<FitnessEvaluator<? super BitString>, BitString> counter,
                                StatObserverPrinter<BitString> statPrinter,
                                int times, int stepsLimit, int evaluators, int target) throws IOException {
		String head = String.format("times %d evaluators %d target %d %s", times, evaluators, target, conf.toString());
		printer.println(head);
		
		for (Printer<? super BitString> p : printers) {
			p.println(head);
		}
		
		for (int i = 0; i < times; i++) {
			int steps = counter.countGenerations();
			if (statPrinter != null) {
				if (steps > 0 && steps < stepsLimit) {
					statPrinter.addCompleted(stepsLimit - steps);
				}
			}
	        printer.println("=====");
			printer.dumpAll();
		}
			
		printer.close();		
		return evaluators;
	}
}
