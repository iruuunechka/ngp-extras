package ru.ifmo.ctd.ngp.theory.moearl;

import org.uncommons.maths.statistics.DataSet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Irene Petrova
 */
public class Runner {
    public static void main(String[] args) throws FileNotFoundException {
        int mu = 1;
        int maxFitnessCalc = 50000;
        int numberOfRuns = 1000;
        List<Pair<Integer, Integer>> ndVals = new ArrayList<>();
        for (int i = 100; i < 600; i += 100) {
            ndVals.add(new Pair<>(i, i/2));
        }
        boolean isNoDecrease = args.length > 0 && args[0].equals("no-decrease");
        ExecutorService es = Executors.newCachedThreadPool();
        HashMap<Pair<Integer, Integer>, List<String>> allRes = new HashMap<>();
        PrintWriter pw = new PrintWriter(new File("OMLearningSingle.txt"));
        for (Pair<Integer, Integer> p : ndVals) {
            Thread thread = new Thread(() -> {
                List<BitFitness> helpers = new ArrayList<>();
                int n = p.getKey();
                int d = p.getValue();
                helpers.add(new SwitchPointFitness(n, n / 2, true));
                helpers.add(new SwitchPointFitness(n, n / 2, false));
                BitMaskFitness target = new BitMaskFitness(n, d);
                double average = 0;
                long min = Integer.MAX_VALUE;
                long max = 0;
                int numberOfInf = 0;
                DataSet dataSet = new DataSet(numberOfRuns);
                List<String> res = new ArrayList<>();
                for (int i = 0; i < numberOfRuns; ++i) {
                    Agent agent = new GreedyQAgent(helpers.size() + 1, 0.6, 0.6, 0.1);
                    State state = new SingleState();
                    long fitnessCalculations = isNoDecrease
                            ? new EARLNoDecrease(mu, n, target, helpers, new OneBitMutation(), maxFitnessCalc, agent, state, n, true).run()
                            : new EARL(mu, n, target, helpers, new OneBitMutation(), maxFitnessCalc, agent, state, n).run();
                    dataSet.addValue(fitnessCalculations);
                    min = Math.min(min, fitnessCalculations);
                    max = Math.max(max, fitnessCalculations);
                    average += (double) (fitnessCalculations) / numberOfRuns;
                    if (fitnessCalculations >= maxFitnessCalc) {
                        numberOfInf++;
                    }
                    res.add(String.valueOf(fitnessCalculations));
                }

                res.add("mu: " + mu + " n: " + n + " d: " + d + " max: " + (max < maxFitnessCalc ? max : "infinity") +
                        " min: " + min + " average: " + (max < maxFitnessCalc ? average : "infinity") + " infs: " + numberOfInf +  " dev: " + dataSet.getStandardDeviation());
                allRes.put(new Pair<>(n, d), res);
            });
            es.execute(thread);
        }
        es.shutdown();
        //noinspection StatementWithEmptyBody: es shall terminate quickly
        while (!es.isTerminated());
        for (Pair p : allRes.keySet()) {
            String s = "n " + p.getKey() + "d " + p.getValue();
            System.out.println(s);
            pw.append(s).append("\na<-c(");
            for (int i = 0; i < allRes.get(p).size() - 2; ++i) {
                String line = allRes.get(p).get(i);
                System.out.print(line + ", ");
                pw.append(line).append(", ");
            }
            String line = allRes.get(p).get(allRes.get(p).size() - 2);
            System.out.print(line + ")" + "\n");
            pw.append(line).append(")\n");
            line = allRes.get(p).get(allRes.get(p).size() - 1);
            System.out.print(line + "\n");
            pw.append(line).append("\n");
        }
        pw.close();
    }



}
