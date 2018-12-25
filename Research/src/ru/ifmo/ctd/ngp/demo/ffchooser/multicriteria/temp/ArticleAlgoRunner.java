package ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria.temp;

import org.uncommons.maths.random.*;
import org.uncommons.maths.statistics.*;
import org.uncommons.watchmaker.framework.*;
import org.uncommons.watchmaker.framework.factories.*;
import ru.ifmo.ctd.ngp.demo.ffchooser.genetic.print.*;
import ru.ifmo.ctd.ngp.demo.ffchooser.jobshop.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

/**
 *  @author Irene Petrova
 */
public class ArticleAlgoRunner {
    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        Properties p = new Properties();
        try (FileReader fr = new FileReader("./misc/jobshop.properties")) {
            p.load(fr);
        }
        final DataFileReader reader = new DataFileReader(new File("./misc/jobshop.txt"));
        StringTokenizer sets = new StringTokenizer(p.getProperty("datasets.test"), ", ");
        ExecutorService ex = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        while (sets.hasMoreTokens()) {
            final String instance = sets.nextToken();
            final int result = Integer.parseInt(sets.nextToken());
            ArticleAlgoRunner.run(reader.get(instance), 200, result, instance, ex);
        }
        ex.shutdown();
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

//    private static void mesh(int[] helpers) {
//        Random rand = new Random();
//        for (int i = 0; i < 100; ++i){
//            int pos1 = rand.nextInt(helpers.length);
//            int pos2 = rand.nextInt(helpers.length);
//            int temp = helpers[pos1];
//            helpers[pos1] = helpers[pos2];
//            helpers[pos2] = temp;
//        }
//    }

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

    private static void run(DataFileReader.InputDataSet inputData, int runs, int result, String instance,
                            ExecutorService executor) throws IOException, InterruptedException, ExecutionException {
        final int evals = 200 * 100;
        final int population = 100;
        final int generations = evals / population;
        final int[][] times = inputData.getTimes();
        final int[][] machines = inputData.getMachines();
        final int jobs = times.length;
        final int max = jobs * JobShopUtils.sumTimes(times);
        int[] helpersCountArray = {2};

        final double[] flowtime = getMinFlowtime(times);
        final int[] sortedHelpers = sortedByFlowtime(flowtime);
        for (final int helpersCount : helpersCountArray) {
            final DataSet dataSet = new DataSet();
            final double crossoverProbability = 0.8;
            final String fileName = "./article"+
                    instance + "nsga" + crossoverProbability + population + helpersCount;

            List<Callable<Double>> tasks = new ArrayList<>();
            for (int i = 0; i < runs; i++) {
                final int runId = i;
                tasks.add(() -> {
                    try (PrintWriter pw = new PrintWriter(String.format("%s@%03d", fileName, runId))) {
                        List<FitnessEvaluator<List<Integer>>> evaluators;
                        evaluators = getEvaluators(helpersCount, sortedHelpers, max, times, machines);

                        AbstractCandidateFactory<List<Integer>> factory = new JobShopFactory(jobs, times[0].length);
                        FitnessEvaluator<List<Integer>> targetFitness = new FlowTimeFitness(max, times, machines);
                        EvolutionaryOperator<List<Integer>> mutation = new PositionBasedMutation();
                        MulticriteriaAlgorithm<List<Integer>> multicriteriaAlgo = new NSGA2MulticriteriaSlow<>(targetFitness,evaluators,
                                factory, mutation, new GeneralizedOrderCrossover(new Probability(crossoverProbability), jobs), crossoverProbability, population, new Random());

                        multicriteriaAlgo.addPrinter(new CompactPrinter<>());
                        multicriteriaAlgo.addPrinter(new CompactPrinter<>(pw));

                        int iterPerHelper = (generations + evaluators.size() - 1) / evaluators.size();
                        List<Double> last = new ArrayList<>();
                        for (int g = 0; g < generations; ++g) {
                            if (g % iterPerHelper == 0) {
                                multicriteriaAlgo.changeCriterion(g / iterPerHelper);
                            }
                            last = multicriteriaAlgo.computeValues();
                        }
                        double result1 = max - last.get(0);
                        for (double d : last) {
                            pw.print((max - d) + " ");
                        }
                        pw.println();
                        return result1;
                    }
                });
            }
            List<Future<Double>> results = executor.invokeAll(tasks);
            for (Future<Double> f : results) {
                dataSet.addValue(f.get());
            }

            double mean = dataSet.getArithmeticMean();
            double percent = (mean - result) / result * 100;
            String s = String.format("percent: %f average: %f min: %f max: %f dev: %f",
                    percent, dataSet.getArithmeticMean(), dataSet.getMinimum(), dataSet.getMaximum(), dataSet.getStandardDeviation());
            try (PrintWriter pw = new PrintWriter(fileName)) {
                pw.println(s);
            }
        }
    }
}
