package ru.ifmo.ctd.ngp.demo.ffchooser.nonStationary.TSP;

import org.uncommons.maths.statistics.DataSet;
import org.uncommons.watchmaker.framework.FitnessEvaluator;
import org.xml.sax.SAXException;
import ru.ifmo.ctd.ngp.demo.ffchooser.genetic.print.CompactPrinter;
import ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria.temp.MulticriteriaAlgorithm;
import ru.ifmo.ctd.ngp.util.FastRandom;

import javax.xml.parsers.ParserConfigurationException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * @author Irene Petrova
 */
public class TSPJahneRunner {
    private static final Random rand = FastRandom.threadLocal();
    private static final int m = 15;

    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException {
//        TSPProblem problem = TSPUtils.readJsonInstance("tspinstances/ran-20");
//        runTsp(problem, 100, 500000, "ran-20", 30);
//        System.out.println("------");

//        TSPProblem problem = TSPUtils.readJsonInstance("tspinstances/ran-50");
//        problem.setSolution(2.042);
//        runTsp(problem, 100, 500000, "ran-50", 30);
//        System.out.println("------");
//
//        TSPProblem problem = TSPUtils.readJsonInstance("tspinstances/euc-50");
//        problem.setSolution(TSPUtils.eucledianCostEstimate(problem.tsp));
//        runTsp(problem, 100, 500000, "euc-50", 30);
//        System.out.println("------");

//        TSPProblem problem = TSPUtils.readXMLInstance("tspinstances/kroB100.xml");
//        runTsp(problem, 100, 2000000, "kroB100", 30);
//        System.out.println("------");

//        TSPProblem problem = TSPUtils.readJsonInstance("tspinstances/euc-100");
//        problem.setSolution(TSPUtils.eucledianCostEstimate(problem.tsp));
//        runTsp(problem, 100, 2000000, "euc-100", 30);
//        System.out.println("------");

//
//        TSPProblem problem = TSPUtils.readXMLInstance("tspinstances/pr124.xml");
//        runTsp(problem, 100, 500000, "pr124", 30);
//        System.out.println("------");

//        TSPProblem problem = TSPUtils.readXMLInstance("tspinstances/pr136.xml");
//        runTsp(problem, 100, 500000, "pr136", 30);
//        System.out.println("------");
//
//        TSPProblem problem = TSPUtils.readXMLInstance("tspinstances/kroA150.xml");
//        runTsp(problem, 100, 500000, "kroA150", 30);
//        System.out.println("------");
//
//        TSPProblem problem = TSPUtils.readXMLInstance("tspinstances/kroB150.xml");
//        runTsp(problem, 100, 500000, "kroB150", 30);
//        System.out.println("------");

//        TSPProblem problem = TSPUtils.readXMLInstance("tspinstances/rat575.xml");
//        runTsp(problem, 100, 500000, "rat575", 30);
//        System.out.println("------");

//        TSPProblem problem = TSPUtils.readXMLInstance("tspinstances/bier127.xml");
//        runTsp(problem, 100, 500000, "bier127", 30);
//        System.out.println("------");

//        TSPProblem problem = TSPUtils.readXMLInstance("tspinstances/eil101.xml");
//        runTsp(problem, 100, 500000, "eil101", 30);
//        System.out.println("------");

//        TSPProblem problem = TSPUtils.readXMLInstance("tspinstances/pr152.xml");
//        runTsp(problem, 100, 500000, "pr152", 30);
//        System.out.println("------");

//        TSPProblem problem = TSPUtils.readXMLInstance("tspinstances/pr439.xml");
//        runTsp(problem, 100, 500000, "pr439", 30);
//        System.out.println("------");


//        TSPProblem problem = TSPUtils.readXMLInstance("tspinstances/kroD100.xml");
//        runTsp(problem, 100, 500000, "kroD100", 30);
//        System.out.println("------");
//        TSPProblem problem = TSPUtils.readXMLInstance("tspinstances/kroE100.xml");
//        runTsp(problem, 100, 500000, "kroE100", 30);
//        System.out.println("------");
        TSPProblem problem = TSPUtils.readXMLInstance("tspinstances/pr1002.xml");
        runTsp(problem, 100, 500000, "pr1002", 30);
        System.out.println("------");

    }

    private static int[] genPoints(int tspSize, int pointsCou) {
        int[] points = new int[pointsCou];
        for (int i = 0; i < pointsCou; ++i) {
            points[i] = rand.nextInt(tspSize);
        }
        Arrays.sort(points);
        return points;
    }

    private static int[] genStaticPoints(int tspSize, int pointsCou) {
        int[] points = new int[pointsCou];
        int dist = tspSize / (pointsCou + 1);
        for (int i = 0; i < pointsCou; ++i) {
            points[i] = dist * (i + 1);
        }
        return points;
    }

    private static List<List<Integer>> genJahnePointsPartition(int tspSize) {
        List<Integer> p = new ArrayList<>();
        List<Integer> pc = new ArrayList<>();
        for (int i = 0; i < tspSize; ++i) {
            if (rand.nextDouble() < 0.5) {
                p.add(i);
            } else {
                pc.add(i);
            }
        }
        List<List<Integer>> pointSets = new ArrayList<>();
        pointSets.add(p);
        pointSets.add(pc);
        return pointSets;
    }

    private static List<List<Integer>> genKnowlesPointsPartition(int tspSize, int helpersCount) {
        List<List<Integer>> pointsSets = new ArrayList<>();
        for (int i = 0; i < helpersCount; ++i) {
            List<Integer> ab = new ArrayList<>();
            int first = rand.nextInt(tspSize);
            int second = rand.nextInt(tspSize);
            while (second == first) {
                second = rand.nextInt(tspSize);
            }
            ab.add(first);
            ab.add(second);
            pointsSets.add(ab);
            List<Integer> ba = new ArrayList<>();
            ba.add(second);
            ba.add(first);
            pointsSets.add(ba);
        }
        return pointsSets;
    }

    private static List<FitnessEvaluator<List<Integer>>> getJahneEvaluators(TSPProblem problem, List<List<Integer>> pointsSets) {
        List<FitnessEvaluator<List<Integer>>> evaluators = new ArrayList<>();
        for (List<Integer> pointsSet : pointsSets) {
            evaluators.add(new TSPJahneFitness(problem, pointsSet));
        }
        return evaluators;
    }

    private static List<FitnessEvaluator<List<Integer>>> getKnowlesEvaluators(TSPProblem problem, List<List<Integer>> pointsSets) {
        List<FitnessEvaluator<List<Integer>>> evaluators = new ArrayList<>();
        for (List<Integer> pointsSet : pointsSets) {
            evaluators.add(new TSPKnowlesFitness(problem, pointsSet.get(0), pointsSet.get(1)));
        }
        return evaluators;
    }

    private static int couEvals(TSPProblem problem) {
        return (int) Math.sqrt(Math.pow(problem.getSize(), 3)) * m;
    }

    public static void runTsp (TSPProblem problem, int populationSize, int stepsCount, String instance, int runs) throws FileNotFoundException {
        //final int evals = stepsCount == 0 ? couEvals(problem) : stepsCount;
        final int evals = couEvals(problem);
        final int generations = evals / populationSize;
        int tspSize = problem.tsp.length;
        System.out.println(String.format("Optimal answer is %.3f", problem.solution));

        List<List<Integer>> pointsSets = //genKnowlesPointsPartition(tspSize, helpersCount);
                genJahnePointsPartition(tspSize);
        PrintWriter pw = null;
        DataSet dataSet = new DataSet();
        double crossoverProbability = 0.4;
        for (int i = 0; i < runs; i++) {
            System.out.println("Run:" + i);
            List<FitnessEvaluator<List<Integer>>> evaluators = //new ArrayList<>();//getKnowlesEvaluators(problem, pointsSets);
                    getJahneEvaluators(problem, pointsSets);
            FitnessEvaluator<List<Integer>> targetFitness = new TSPFitness(problem);
            TSPFactory factory = new TSPFactory(problem);
            TSPMutation mutation = new TSPMutation();
            TSPCrossover crossover = new TSPCrossover(crossoverProbability);
            MulticriteriaAlgorithm<List<Integer>> algo =  new TSPNSGA2Multiobjectivization(targetFitness, evaluators, factory, mutation, crossover, crossoverProbability, populationSize, rand, problem);

            if (pw == null) {
                pw = new PrintWriter("../TSP/multi/" + instance + "Jahne");
            }

            algo.addPrinter(new CompactPrinter<>());
            algo.addPrinter(new CompactPrinter<>(pw));

            for (int g = 0; g < generations; ++g) {
                algo.computeValues();
            }
            double result = problem.getMax() - algo.getBestTargetValue();
            pw.println();

            dataSet.addValue(result);
            System.out.println();
            pw.println();
        }
        double mean = dataSet.getArithmeticMean();
        double percent = (mean - problem.solution) / problem.solution * 100;
        String s = String.format("percent: %f average: %f min: %f max: %f dev: %f",
                percent, dataSet.getArithmeticMean(), dataSet.getMinimum(), dataSet.getMaximum(), dataSet.getStandardDeviation());
        System.out.println(s);
        if (pw != null) {
            pw.println(s);
            pw.flush();
        }
    }

}
