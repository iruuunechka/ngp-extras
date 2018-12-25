package ru.ifmo.ctd.ngp.theory.moearl;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.uncommons.maths.statistics.DataSet;

/**
 * @author Irene Petrova (original implementation)
 * @author Maxim Buzdalov (parameterized rewrite)
 */
public class XdivKRunner {
    private static final int mu = 1;
    private static final int numberOfRuns = 1000;
    private static final long maxFitnessCalc = 1000000000;

    private static final Map<Integer, List<Integer>> nkMap = new HashMap<>();
    static {
        nkMap.put(3, new ArrayList<>(Arrays.asList(60, 72, 84, 96, 108, 120)));
//        nkMap.put(2, new ArrayList<>(Arrays.asList(40, 48, 56, 64, 72, 80)));
//        nkMap.put(2, new ArrayList<>(Arrays.asList(40, 56, 72)));
//        nkMap.put(3, new ArrayList<>(Arrays.asList(60, 84, 108)));
//        nkMap.put(4, new ArrayList<>(Arrays.asList(80, 96, 112, 128, 144, 160)));
//        nkMap.put(5, new ArrayList<>(Arrays.asList(100, 120, 140, 160, 180, 200)));
    }

    private static final BiFunction<Integer, Integer, BitFitness> oneMax = (n, k) -> new BitMaskFitness(n, 0);
    private static final BiFunction<Integer, Integer, BitFitness> zeroMax = (n, k) -> new BitMaskFitness(n, n);

    private static final BiFunction<Integer, Integer, BitFitness> switchEndOneMax = (n, k) -> new SwitchPointFitness(n, n - k + 1, true);
    private static final BiFunction<Integer, Integer, BitFitness> switchEndZeroMax = (n, k) -> new SwitchPointFitness(n, n - k + 1, false);

    private static final Function<Integer, Agent> epsGreedy = n -> new GreedyQAgent(n, 0.6, 0.6, 0.1);
    private static final Function<Integer, Agent> greedy = n -> new GreedyQAgent(n, 0.6, 0.6, 0.0);

    private static final Supplier<State> singleState = SingleState::new;
    private static final Supplier<State> targetState = MaxFitnessState::new;

    private static final Supplier<BitMutation> oneBitMutation = OneBitMutation::new;

    enum EARLFlavor {
        Vanilla("EA+RL") {
            @Override
            public long runAlgorithm(int n, int optimum, BitFitness target, List<BitFitness> helpers, State state, Agent agent) {
                return new EARL(mu, n, target, helpers, oneBitMutation.get(), maxFitnessCalc, agent, state, optimum).run();
            }
        }, NoLearningOnMistakes("EA+RLnM") {
            @Override
            public long runAlgorithm(int n, int optimum, BitFitness target, List<BitFitness> helpers, State state, Agent agent) {
                return new EARLNoDecrease(mu, n, target, helpers, oneBitMutation.get(), maxFitnessCalc, agent, state, optimum, false).run();
            }
        }, LearningOnMistakes("EA+RLM") {
            @Override
            public long runAlgorithm(int n, int optimum, BitFitness target, List<BitFitness> helpers, State state, Agent agent) {
                return new EARLNoDecrease(mu, n, target, helpers, oneBitMutation.get(), maxFitnessCalc, agent, state, optimum, true).run();
            }
        };

        private final String earlName;

        EARLFlavor(String earlName) {
            this.earlName = earlName;
        }

        public abstract long runAlgorithm(
                int n, int optimum, BitFitness target, List<BitFitness> helpers, State state, Agent agent
        );

        public String earlName() {
            return earlName;
        }
    }

    static class RunResult {
        final int n;
        final int k;
        final long iterations;

        RunResult(int n, int k, long iterations) {
            this.n = n;
            this.k = k;
            this.iterations = iterations;
        }
    }

    private static void run(
            String filename,
            EARLFlavor flavor,
            Supplier<State> stateFunction,
            Function<Integer, Agent> agentFunction,
            List<BiFunction<Integer, Integer, BitFitness>> helperFunctions,
            int cores
    ) throws IOException, InterruptedException, ExecutionException {
        try (PrintWriter pw = new PrintWriter(filename)) {
            List<Callable<RunResult>> tasks = new ArrayList<>();

            for (int k : nkMap.keySet()) {
                for (int n : nkMap.get(k)) {
                    AtomicInteger runNumber = new AtomicInteger();
                    for (int nr = 0; nr < numberOfRuns; ++nr) {
                        tasks.add(() -> {
                            BitFitness target = new XdivKFitness(n, k);
                            List<BitFitness> helpers = helperFunctions
                                    .stream()
                                    .map(f -> f.apply(n, k))
                                    .collect(Collectors.toList());
                            Agent agent = agentFunction.apply(helpers.size() + 1);
                            State state = stateFunction.get();
                            long fitnessCalculations = flavor.runAlgorithm(n, n / k, target, helpers, state, agent);
                            System.out.println(filename + ": " + n + " " + runNumber.getAndIncrement() + " " + fitnessCalculations);
                            return new RunResult(n, k, fitnessCalculations);
                        });
                    }
                }
            }

            ExecutorService service = Executors.newFixedThreadPool(cores);
            List<Future<RunResult>> resultFutures = service.invokeAll(tasks);
            List<RunResult> results = new ArrayList<>(resultFutures.size());
            for (Future<RunResult> resultFuture : resultFutures) {
                results.add(resultFuture.get());
            }
            service.shutdown();

            HashMap<Pair<Integer, Integer>, List<Long>> groupedResults = new HashMap<>();
            for (RunResult result : results) {
                groupedResults.computeIfAbsent(new Pair<>(result.n, result.k), p -> new ArrayList<>()).add(result.iterations);
            }

            HashMap<Pair<Integer, Integer>, List<String>> allRes = new HashMap<>();

            for (Map.Entry<Pair<Integer, Integer>, List<Long>> e : groupedResults.entrySet()) {
                List<Long> runs = e.getValue();
                List<String> newContent = new ArrayList<>();
                DataSet dataSet = new DataSet(runs.size());
                int numberOfInf = 0;
                for (long run : runs) {
                    dataSet.addValue(run);
                    if (run >= maxFitnessCalc) {
                        ++numberOfInf;
                    }
                    newContent.add(String.valueOf(run));
                }
                newContent.add("mu: " + mu
                        + " n: " + e.getKey().getKey()
                        + " k: " + e.getKey().getValue()
                        + " max: " + (dataSet.getMaximum() < maxFitnessCalc ? dataSet.getMaximum() : "infinity")
                        + " min: " + dataSet.getMinimum()
                        + " average: " + (dataSet.getMaximum() < maxFitnessCalc ? dataSet.getArithmeticMean() : "infinity")
                        + " infs: " + numberOfInf
                        + " dev: " + (dataSet.getMaximum() < maxFitnessCalc ? dataSet.getStandardDeviation() : "infinity")
                );
                allRes.put(e.getKey(), newContent);
            }

            for (Pair p : allRes.keySet()) {
                String s = "n " + p.getKey() + " k " + p.getValue();
                System.out.print(s + "\nc(");
                pw.append(s).append("\nc(");
                for (int i = 0; i < allRes.get(p).size() - 2; ++i) {
                    String line = allRes.get(p).get(i);
                    System.out.print(line + ", ");
                    pw.append(line).append(", ");
                }
                String line = allRes.get(p).get(allRes.get(p).size() - 2);
                System.out.print(line + ")\n");
                pw.append(line).append(")\n");
                line = allRes.get(p).get(allRes.get(p).size() - 1);
                System.out.print(line + "\n");
                pw.append(line).append("\n");
            }
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        int cpusIndex = -1;
        String prefix = "--cpus=";
        for (int i = 0; i < args.length; ++i) {
            if (args[i].startsWith(prefix)) {
                cpusIndex = i;
            }
        }
        int cores = cpusIndex == -1 ? Runtime.getRuntime().availableProcessors() : Integer.parseInt(args[cpusIndex].substring(prefix.length()));
        List<BiFunction<Integer, Integer, BitFitness>> helpers = Arrays.asList(oneMax, zeroMax);
        for (EARLFlavor flavor : EARLFlavor.values()) {
            run("XdivK-" + flavor.earlName() + "-ss01", flavor, singleState, epsGreedy, helpers, cores);
            run("XdivK-" + flavor.earlName() + "-ts01", flavor, targetState, epsGreedy, helpers, cores);
            run("XdivK-" + flavor.earlName() + "-ts00", flavor, targetState, greedy, helpers, cores);
        }
    }
}
