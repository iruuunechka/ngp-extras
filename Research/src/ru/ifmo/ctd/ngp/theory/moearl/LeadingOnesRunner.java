package ru.ifmo.ctd.ngp.theory.moearl;

import org.uncommons.maths.statistics.DataSet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Arkadii Rost
 */
public class LeadingOnesRunner {
    public static void main(String[] args) throws FileNotFoundException {
        int mu = 1;
        long maxFitnessCalc = 50000;
        int numberOfRuns = 1000;
        List<Integer> nVals = new ArrayList<>();
        for (int i = 1; i < 200; i += 20) {
            nVals.add(i);
        }
        boolean isNoDecrease = args.length > 0 && args[0].equals("no-decrease");

        PrintWriter pw = new PrintWriter(new File("LeadingOnesLearningSingle.txt"));
        ExecutorService es = Executors.newCachedThreadPool();
        TreeMap<Integer, List<String>> allRes = new TreeMap<>();
        for (int n : nVals) {
            Thread thread = new Thread(() -> {
                List<String> res = new ArrayList<>();
                List<BitFitness> helpers = new ArrayList<>();
                helpers.add(new SwitchPointFitness(n, n - 2, true));
                helpers.add(new SwitchPointFitness(n, n - 2, false));
                BitFitness target = new LeadingOnesFitness();
                double average = 0;
                long min = Integer.MAX_VALUE;
                long max = 0;
                int numberOfInf = 0;
                DataSet dataSet = new DataSet(numberOfRuns);
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
                    System.out.println(n + " " + i + " " + fitnessCalculations);
                    res.add(String.valueOf(fitnessCalculations));
                }

                res.add("mu: " + mu + " n: " + n  + " max: " + (max < maxFitnessCalc ? max : "infinity") +
                        " min: " + min + " average: " + (max < maxFitnessCalc ? average : "infinity") + " infs: " + numberOfInf + " dev: " + dataSet.getStandardDeviation());
                allRes.put(n, res);
            });
            es.execute(thread);
        }
        es.shutdown();
        //noinspection StatementWithEmptyBody: a busy wait, since es shall not shutdown for too long
        while (!es.isTerminated());
        for (Integer len : allRes.keySet()) {
            String s = "n: " + len;
            System.out.println(s);
            pw.append(s).append("\na<-c(");
            for (int i = 0; i < allRes.get(len).size() - 2; ++i) {
                String line = allRes.get(len).get(i);
                System.out.print(line + ", ");
                pw.append(line).append(", ");
            }
            String line = allRes.get(len).get(allRes.get(len).size() - 2);
            System.out.print(line + ")" + "\n");
            pw.append(line).append(")\n");
            line = allRes.get(len).get(allRes.get(len).size() - 1);
            System.out.print(line + "\n");
            pw.append(line).append("\n");
        }
        pw.close();
    }
}
