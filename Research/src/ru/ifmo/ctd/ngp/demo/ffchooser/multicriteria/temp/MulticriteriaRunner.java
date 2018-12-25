package ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria.temp;

import org.uncommons.maths.random.Probability;
import org.uncommons.maths.statistics.DataSet;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import org.uncommons.watchmaker.framework.FitnessEvaluator;
import org.uncommons.watchmaker.framework.factories.AbstractCandidateFactory;
import ru.ifmo.ctd.ngp.demo.ffchooser.StepsCondition;
import ru.ifmo.ctd.ngp.demo.ffchooser.ValueCondition;
import ru.ifmo.ctd.ngp.demo.ffchooser.genetic.print.CompactPrinter;
import ru.ifmo.ctd.ngp.demo.ffchooser.jobshop.*;
import ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria.temp.agent.NonstationaryAgent;
import ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria.temp.reward.MultiRewardCalculator;
import ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria.temp.reward.MultiSingleDiffReward;
import ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria.temp.state.MultiStateCalculator;
import ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria.temp.state.MulticriteriaTripleState;
import ru.ifmo.ctd.ngp.learning.reinforce.Agent;
import ru.ifmo.ctd.ngp.learning.reinforce.EnvironmentPrinterImpl;
import ru.ifmo.ctd.ngp.util.FastRandom;

import java.io.*;
import java.util.*;

public class MulticriteriaRunner {
    public static void main(String[] args) throws IOException {
        Properties p = new Properties();
        try (FileReader in = new FileReader("./misc/jobshop.properties")) {
            p.load(in);
        }
        DataFileReader reader = new DataFileReader(new File("./misc/jobshop.txt"));
        StringTokenizer sets = new StringTokenizer(p.getProperty("datasets.test"), ", ");
        while (sets.hasMoreTokens()) {
            String instance = sets.nextToken();
            int result = Integer.parseInt(sets.nextToken());
            System.out.println(instance);
            run(reader.get(instance), 100, result, instance);
        }
    }

    private static double[] getMinFlowtime(int[][] times) {
        int jobs = times.length;
        double[] flowtime = new double[jobs + 1];
        int sum = 0;
        for (int j = 0; j < jobs; j++) {
            for (int k = 0; k < times[j].length; k++) {
                flowtime[j + 1] += times[j][k];
                sum += times[j][k];
            }
        }
        flowtime[0] = sum;
        return flowtime;
    }

    private static int[] sortedByFlowtime(double[] flowtime) {
        int[] sortedHelpers = new int[flowtime.length - 1];
        boolean[] usedHelper = new boolean[sortedHelpers.length];
        for (int i = 0; i < sortedHelpers.length; ++i) {
            double min = Double.MAX_VALUE;
            int pos = 0;
            for (int j = 1; j < flowtime.length; ++j) {
                if (flowtime[j] < min && !usedHelper[j - 1]) {
                    min = flowtime[j];
                    pos = j - 1;
                }
            }
            sortedHelpers[i] = pos;
            usedHelper[pos] = true;
        }
        return sortedHelpers;
    }

    private static void mesh(int[] helpers) {
        Random rand = new Random();
        for (int i = 0; i < 100; ++i){
            int pos1 = rand.nextInt(helpers.length);
            int pos2 = rand.nextInt(helpers.length);
            int temp = helpers[pos1];
            helpers[pos1] = helpers[pos2];
            helpers[pos2] = temp;
        }
    }

    private static double[] getOptima(double [] flowtime, int jobs, int helpersCount, int max) {
        int jobPerHelper = jobs / helpersCount;
        double[] optima = new double[helpersCount + 1];
        Arrays.fill(optima, max);
        optima[0] = max - flowtime[0];
        for (int i = 0; i < helpersCount; ++i) {
            for (int j = i * jobPerHelper; j < (i + 1) * jobPerHelper + (i == helpersCount - 1 ? jobs - jobPerHelper * helpersCount : 0); j++) {
                optima[i + 1] -= flowtime[j];
            }
        }
        return optima;
    }

    private static List<FitnessEvaluator<List<Integer>>> getEvaluators(int helpersCount, int[] helpers,
                                                                       int max, int[][] times, int[][] machines) {
        List<FitnessEvaluator<List<Integer>>> evaluators = new ArrayList<>();
        int jobs = times.length;
        int jobPerHelper = jobs / helpersCount;
        for (int j = 0; j < helpersCount; ++j) {
            evaluators.add(new MultiFlowTimeFitness(
                    Arrays.copyOfRange(helpers, j * jobPerHelper,
                            (j + 1) * jobPerHelper + (j == helpersCount - 1 ? jobs - jobPerHelper * helpersCount : 0)),
                    max, times, machines));
        }
        return evaluators;
    }

    private static void run(DataFileReader.InputDataSet inputData, int runs, int result, String instance) throws FileNotFoundException {
        final int evals = 200 * 100;
        final int population = 150;
        final int generations = evals / population;
        int[][] times = inputData.getTimes();
        int[][] machines = inputData.getMachines();
        int jobs = times.length;
        int max = jobs * JobShopUtils.sumTimes(times);
        int[] helpersCountArray = {2};
        System.out.println(max);
        DataSet dataSet = new DataSet();

        double[] flowtime = getMinFlowtime(times);
        int[] sortedHelpers = sortedByFlowtime(flowtime);
        //mesh(sortedHelpers);
        for (int helpersCount : helpersCountArray) {
            PrintWriter pw = null;
            //double[] optima = getOptima(flowtime, jobs, helpersCount, max);
            for (int i = 0; i < runs; i++) {
                System.out.println("run: " + i);
                List<FitnessEvaluator<List<Integer>>> evaluators;
                evaluators = getEvaluators(helpersCount, sortedHelpers, max, times, machines);

                //double[] grid = new double[]{jobs, jobs};

                AbstractCandidateFactory<List<Integer>> factory = new JobShopFactory(jobs, times[0].length);
                FitnessEvaluator<List<Integer>> targetFitness = new FlowTimeFitness(max, times, machines);
                EvolutionaryOperator<List<Integer>> mutation = new PositionBasedMutation();

                //evaluators.add(0, targetFitness);
//                evaluators.remove(1);
//                evaluators.remove(2);
//                GeneticAlgorithmJobShop multicriteriaAlgo = GeneticAlgorithmJobShop.newGAJS(0, 1, evaluators, new GeneralizedOrderCrossover(new Probability(0.8), jobs), mutation, factory);
                MulticriteriaAlgorithm<List<Integer>> multicriteriaAlgo = new NSGA2MulticriteriaSlow<>(targetFitness,evaluators,
                            factory, mutation, new GeneralizedOrderCrossover(new Probability(0.8), jobs), 0.8, population, FastRandom.threadLocal());
    //           MulticriteriaAlgorithm<List<Integer>> multicriteriaAlgo =
    //                    new PesaIIMulticriteria<>(targetFitness, evaluators,
    //                            mutation, new GeneralizedOrderCrossover(new Probability(0.8), jobs), 0.8, factory, 150, 100, grid, new Random());

                multicriteriaAlgo.addPrinter(new CompactPrinter<>());


                MultiStateCalculator<String, Integer> monitorState = new MulticriteriaTripleState(evals / population + 1, (int) (max - flowtime[0]));
//                MultiStateCalculator<String, Integer> monitorState = new TimeIntervalState(150, 3);

//                MultiStateCalculator<String, Integer> monitorState = new HelperState();
//                MultiStateCalculator<String, Integer> monitorState = new FitnessIntervalState(result, max, 5);
                //MultiStateCalculator<String, Integer> monitorState = new FedUpState(optima);
                //MultiStateCalculator<String, Integer> monitorState = new VectorParetoTargMaxState(multicriteriaAlgo.parametersCount(), multicriteriaAlgo);
//                MultiStateCalculator<String, Integer> monitorState = new SingleState();
//                MultiRewardCalculator reward = new MultiFixedBestTargetReward(-1, 0, 1);
                MultiRewardCalculator reward = new MultiSingleDiffReward();
//                MultiRewardCalculator reward = new MultiPositiveFullReward(0);
                MultiOptAlgEnvironment<String, Integer> env =
                        new MultiOAEnvironment(multicriteriaAlgo, reward, monitorState);
//
//                StateCalculator<String, Integer> state = new TripleState(evals / population + 1, max - flowtime[0]);
//                RewardCalculator reward = new SingleDiffReward();
//                OAEnvironment env = new OAEnvironment(multicriteriaAlgo, reward, state);

                //Agent<String, Integer> agent = new EGreedyAgent<String, Integer>(0.25, 1.0, 0.6, 0.01);
                //Agent<String, Integer> agent = new RandomAgent<String, Integer>();
    //            Agent<String, Integer> agent = new RAgent<>(0.2, 0.3, 0.01);
//                Agent<String, Integer> agent = new DelayedAgent<>(0, 0.7, 5, 0.1);


//                Agent<String, Integer> agent = new SoftMaxAgent<>(helpersCount, 0.6, 0.7, multicriteriaAlgo);
//                Agent<String, Integer> agent = new EqualProbabilityAgent<>(helpersCount, multicriteriaAlgo);
//                Agent<String, Integer> agent = new RandomAgent<>(helpersCount, multicriteriaAlgo);
                Agent<String, Integer> agent = new NonstationaryAgent<>(0.9, 0.7, 0.1, 0.5);
                //Agent<String, Integer> agent = new MyEGreedyAgent<>(helpersCount, 0.6, 0.7, 0.25, multicriteriaAlgo);
//                Agent<String, Integer> agent = new EpsQRLCDJS<>(0.6, 0.7, 0.3, 0.001, new FreezeStrategy(0.95, 40, 1), (int) (max - flowtime[0]), evals / population + 1);
                //Agent<String, Integer> agent = new DynaAgent<>(0.25, 0.7, 50);

                if (pw == null) {
                    pw =  new PrintWriter("../JobShop/test"+
                            "times7"+instance + multicriteriaAlgo.getName() + reward.getName() + monitorState.getName() + agent.toString());
                }
//                if (pw == null) {
//                    pw =  new PrintWriter("./Research/src/ru/ifmo/ctd/ngp/demo/ffchooser/multicriteria/temp/results/onlyTarget"+
//                            instance + "GA" + state.getName() + agent.toString());
//                }
                env.addPrinter(new EnvironmentPrinterImpl<>(new PrintWriter(System.out)));
                env.addPrinter(new EnvironmentPrinterImpl<>(pw));
                multicriteriaAlgo.addPrinter(new CompactPrinter<>(pw));
                env.setTargetCondition(new ValueCondition(max), new StepsCondition(generations));
                int steps = agent.learn(env);
                System.out.println("Steps: " + steps);
                pw.println("Steps: " + steps);
                dataSet.addValue(max - env.getLastValues().get(0));
                for (double d : env.getLastValues()) {
                    System.out.print((max - d) + " ");
                    pw.print((max - d) + " ");
                }
                System.out.println();
                pw.println();
            }
            double mean = dataSet.getArithmeticMean();
            double percent = (mean - result) / result * 100;
            String s = String.format("percent: %f average: %f min: %f max: %f dev: %f",
                    percent, dataSet.getArithmeticMean(), dataSet.getMinimum(), dataSet.getMaximum(), dataSet.getStandardDeviation());
            System.out.println(s);
            if (pw != null) {
                pw.println(s);
                pw.flush();
            }
        }
    }
}
