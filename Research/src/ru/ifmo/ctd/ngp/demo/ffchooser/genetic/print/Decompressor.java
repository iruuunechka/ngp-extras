package ru.ifmo.ctd.ngp.demo.ffchooser.genetic.print;

import java.io.*;
import java.util.*;

import org.jetbrains.annotations.NotNull;

/**
 * Decompressor for the {@link CompressingPrinter}.
 * 
 * This class contains useful methods for working with
 * logs created by the {@link CompressingPrinter}.
 * 
 * @author Arina Buzdalova
 */
public class Decompressor {
	
	/**
	 * data[number of run][fitness function index] = list of data units that describe ff values in the specified run
	 */
	private final List<Unit>[][] data;
	private final int evaluators;
	private final Map<String, String> parameters;
	private final int times;
	private final int actualTimes;
	private StopHandler stopHandler;

    private static final String dataExpr = "[\\d.\\s]+";
	/**
	 * Constructs {@link Decompressor} with the specified input reader
	 * and {@link StopHandler}
	 * Number of run times is taken from the reader.
	 * @param reader the specified input reader. 
	 * @param stopHandler the specified {@link StopHandler}
	 * @throws IOException if some problems with the <code>reader</code> occur
	 */
	public Decompressor(Reader reader, StopHandler stopHandler) throws IOException {
		this(reader);
		this.stopHandler = stopHandler;
	}
	
	/**
	 * Constructs {@link Decompressor} with the specified input reader.
	 * Number of run times is taken from the reader.
	 * @param reader the specified input reader. 
	 * @throws IOException if some problems with the <code>reader</code> occur
	 */
	public Decompressor(Reader reader) throws IOException {
		this.parameters = new HashMap<>();
		BufferedReader bufReader = readParameters(reader);
		
		this.evaluators = Integer.parseInt(parameters.get("evaluators"));
		
		this.times = Integer.parseInt(parameters.get("times"));
        //noinspection unchecked
        this.data = new List[times][evaluators + 1];
		
		for (int i = 0; i < times; i++) {
			for (int j = 0; j <= evaluators; j++) {
				data[i][j] = new ArrayList<>();
			}
		}
		
		actualTimes = readData(bufReader);
		reader.close();
	}
	
	/**
	 * Gets the number of runs specified by the compressed source
	 * @return the number of runs specified by the compressed source
	 */
	public int getTimes() {
		return times;
	}
	
	/**
	 * Gets the actual number of runs
	 * @return the actual number of runs
	 */
	public int getActualTimes() {
		return actualTimes;
	}
	
//	/**
//	 * <p>
//	 * Returns whether the specified fitness evaluator is constant on the
//	 * interval that contains the specified value
//	 * </p><p>
//	 * TODO: Maybe this method should be in Configuration... Anyway, it should be
//	 * linked with the type of evaluators used to solve the task.
//	 * </p>
//	 * @param evaluator the specified fitness evaluator
//	 * @param value the specified value
//	 * @param time the number of run
//	 * @param generation the number of generation
//	 * @return <code>true</code> if the <code>evaluator</code> is constant; <code>false</code> if it is not
//	 */
//	public boolean isConstant(int evaluator, double value, int time, int generation) {
//		String mode = getParameterValue("learnMode");
//		
//		if (mode.equals("greedy") || mode.equals("delayed")) {
//			if (evaluator == 0 && value == switchPoint) {
//				return true;
//			}
//			
//			if (evaluator == 1 && value == switchPoint) {
//				return getFitness(time, 0, generation) != switchPoint;
//			}
//		}		
//		return false;
//	}

    /**
	 * Gets the value of the specified parameter that is used in the current set of runs
	 * @param parameter the specified parameter
	 * @return the value of the <code>parameter</code>
	 */
	public String getParameterValue(String parameter) {
		return parameters.get(parameter);
	}
	
	/**
	 * Gets the interval containing the value of the specified evaluator (fitness function) in the
	 * specified run and generation
	 * @param time the number of run
	 * @param fitnessIndex the number of fitness evaluator, or evaluators count for the choice of learning
	 * @param generation the number of generation
	 * @return the interval containing the value of the specified evaluator in the
	 * specified run and generation
	 */
	public Unit getContainingInterval(int time, int fitnessIndex, int generation) {
		return search(0, data[time][fitnessIndex].size() - 1, time, fitnessIndex, generation);
	}
	
	/**
	 * Gets the best value of the specified evaluator (fitness function) in the specified run and generation
	 * @param time the specified number of run
	 * @param fitnessIndex the number of the evaluator (fitness function)
	 * @param generation the specified number of generation
	 * @return the best value of the evaluator with <code>fitnessIndex</code> 
	 * in the generation number <code>generation</code> of the run number <code>run</code>
	 */
	public double getFitness(int time, int fitnessIndex, int generation) {
		return getContainingInterval(time, fitnessIndex, generation).getValue();
	}
	
	/**
	 * Gets the number of evaluator (fitness function) chosen by the learning agent in the specified run and generation
	 * @param time the specified number of run
	 * @param generation the specified number of generation
	 * @return the number of evaluator (fitness function) chosen by the learning agent in the specified run and generation
	 */
	public int getChoice(int time, int generation) {
		return (int) getFitness(time, evaluators, generation);
	}

    public String getCouGoodChoices(String label, File statFile) throws FileNotFoundException {
        PrintWriter pw = new PrintWriter(new FileOutputStream(statFile, true));
        int targetIndex = Integer.valueOf(getParameterValue("target"));
        List<Integer> switchPoints = new ArrayList<>();
        if (getParameterValue("nonstationary") != null) {
            String[] switches = getParameterValue("nonstationary").split("_");
            for (String s : switches) {
                switchPoints.add(Integer.valueOf(s));
            }
            switchPoints.add(Integer.valueOf(getParameterValue("length")));
        } else {
            switchPoints.add(Integer.valueOf(getParameterValue("switchPoint")));
        }

        int[][] good = new int[times][switchPoints.size() + 1]; //in the last the sum of time
        int[][] target = new int[times][switchPoints.size() + 1];
        int[][] bad = new int[times][switchPoints.size() + 1];
        double fitness = 0;
        double[] fitnessArr = new double[times];
        int lastGeneration = 0;
        int couReached = 0;
        double[] goodPcnt = new double[times];
        double[] targetPcnt = new double[times];
        double[] badPcnt = new double[times];
        double minFitness = Double.POSITIVE_INFINITY;
        double maxFitness = Double.NEGATIVE_INFINITY;
        StringBuilder stat = new StringBuilder(label + "<-c(");
        for (int i = 0; i < times; ++i) {
            int last = data[i][evaluators].get(data[i][evaluators].size() - 1).getStart();
            double curFitness = getFitness(i, targetIndex, last);
            fitness += curFitness;
            stat.append(curFitness).append(i == times - 1 ? "" : ",");
            minFitness = Math.min(minFitness, curFitness);
            maxFitness = Math.max(maxFitness, curFitness);
            fitnessArr[i] = getFitness(i, targetIndex, last);
            if (curFitness == Integer.valueOf(getParameterValue("length")) / Integer.valueOf(getParameterValue("divider"))) {
                couReached++;
                lastGeneration += last;
            }
            double goodCou = 0;
            double targetCou = 0;
            double badCou = 0;
            int stop = data[i][evaluators].get(data[i][evaluators].size() - 1).getStop();
            for (int j = data[i][evaluators].get(0).getStart(); j < data[i][evaluators].get(data[i][evaluators].size() - 1).getStop(); ++j) {
                for (int point = 0; point < switchPoints.size(); ++point) {
                    double f = getFitness(i, targetIndex, j);
                    if (f <= switchPoints.get(point) / Integer.valueOf(getParameterValue("divider"))) {
                        int helper = getChoice(i, j);
                        if ((point % 2 == 0) && (helper == 0)) {
                            good[i][point]++;
                            goodCou++;
                        } else if  ((point % 2 == 1) && (helper == 1)) {
                            good[i][point]++;
                            goodCou++;
                        } else if (helper == 2) {
                            target[i][point]++;
                            targetCou++;
                        } else {
                            bad[i][point]++;
                            badCou++;
                        }
                        break;
                    }
                }
            }
            goodPcnt[i] = goodCou / stop;
            targetPcnt[i] = targetCou / stop;
            badPcnt[i] = badCou / stop;
        }

        stat.append(")\n");
        fitness /= times;
        double devFitness = 0;
        for (int i = 0; i < times; ++i) {
            devFitness += Math.pow(fitnessArr[i] - fitness, 2);
        }
        devFitness = Math.sqrt(devFitness / times);

        int goodTotal = 0;
        int targetTotal = 0;
        int badTotal = 0;
        StringBuilder s = new StringBuilder("Good: ");
        for (int i = 0; i < good.length; ++i) {
            for (int j = 0; j < good[i].length - 1; ++j) {
                good[i][good[i].length - 1] += good[i][j];
            }
            goodTotal += good[i][good[i].length - 1];
        }
        for (int i = 0; i < switchPoints.size(); ++i) {
            int sum = 0;
			for (int[] g : good) {
				sum += g[i];
			}
            s.append(" ").append(sum / times);
        }
        int midGood = goodTotal / times;
        double devGood = 0;
        for (int i = 0; i < times; ++i) {
            devGood += Math.pow(good[i][good[i].length - 1] - midGood, 2);
        }

        devGood = Math.sqrt(devGood / times);
        s.append(" Target: ");
        for (int i = 0; i < target.length; ++i) {
            for (int j = 0; j < target[i].length - 1; ++j) {
                target[i][target[i].length - 1] += target[i][j];
            }
            targetTotal += target[i][target[i].length - 1];
        }
        for (int i = 0; i < switchPoints.size(); ++i) {
            int sum = 0;
            for (int[] t : target) {
                sum += t[i];
            }
            s.append(" ").append(sum / times);
        }
        int midTarget = targetTotal / times;
        double devTarget = 0;
        for (int i = 0; i < times; ++i) {
            devTarget += Math.pow(target[i][target[i].length - 1] - midTarget, 2);
        }
        devTarget = Math.sqrt(devTarget / times);
        for (int[] b : bad) {
            for (int j = 0; j < b.length - 1; ++j) {
                badTotal += b[j];
            }
        }
        badTotal /= times;

        StringBuilder statGood = new StringBuilder(label + "Good" + "<-c(");
        double midGoodPcnt = 0;
        double midTargetPcnt = 0;
        double midBadPcnt = 0;
        for (int i = 0; i < times; ++i) {
            midGoodPcnt += goodPcnt[i];
            midTargetPcnt += targetPcnt[i];
            midBadPcnt += badPcnt[i];
            statGood.append(String.format("%.4f", goodPcnt[i])).append(i == times - 1 ? "" : ",");
        }
        statGood.append(")\n");
        midGoodPcnt /= times;
        midTargetPcnt /= times;
        midBadPcnt /= times;

        double devGoodPcnt = 0;
        double devTargetPcnt = 0;
        double devBadPcnt = 0;

        for (int i = 0; i < times; ++i) {
            devGoodPcnt += Math.pow(goodPcnt[i] - midGoodPcnt, 2);
            devTargetPcnt += Math.pow(targetPcnt[i] - midTargetPcnt, 2);
            devBadPcnt += Math.pow(badPcnt[i] - midBadPcnt, 2);
        }
        devGoodPcnt = Math.sqrt(devGoodPcnt / times);
        devTargetPcnt = Math.sqrt(devTargetPcnt / times);
        devBadPcnt = Math.sqrt(devBadPcnt / times);
        pw.append(stat);
        pw.append(statGood);
        pw.flush();
        pw.close();
        return s.toString() + ' ' + "Good total: " + midGood + " Dev: " + String.format("%.4f", devGood) + ' ' + "Target total: " + midTarget +  " Dev: " + String.format("%.4f", devTarget) + " Bad total: " + badTotal +
                " Reached: " + couReached + " Generations: " + (couReached == 0 ? "" : lastGeneration / couReached) + " Fitness: " + fitness +  " Dev: " + String.format("%.4f", devFitness) +
                " Good%: " + String.format("%.4f", midGoodPcnt) + " Dev: " + String.format("%.4f", devGoodPcnt) + " Target%: " + String.format("%.4f", midTargetPcnt) +
                " Dev: " + String.format("%.4f", devTargetPcnt) + " Bad%: " + String.format("%.4f", midBadPcnt) + " Dev: " + String.format("%.4f", devBadPcnt) +
                "Max: " + String.format("%.4f", maxFitness) + "Min: " + String.format("%.4f", minFitness);

    }

	/**
	 * Gets unmodifiable view of data units that describes values of the specified evaluator in the specified run
	 * @param time the number of run
	 * @param evaluator the number of fitness evaluator, or (evaluators count) for the choice of learning
	 * @return data units that describe values of the <code>evaluator</code> in the run number <code>time</code>
	 */
	public List<Unit> getCompressedDescription(int time, int evaluator) {
		return Collections.unmodifiableList(data[time][evaluator]);
	}
	
//	public double getBestFitness(int time, int evaluator) {
//		List<Unit> list = new ArrayList<Unit>(getCompressedDescription(time, evaluator));
//		Collections.sort(list);
//		return list.get(list.size() - 1).getValue();	
//	}


	private int readData(BufferedReader buffReader) throws IOException {
		int runs = 0;
		int eval = 0;
		
		String line;
		do {
			line = buffReader.readLine();
		} while (!line.matches(dataExpr));
		
		String start = "1 ";
		
		while (true) {			
			
			boolean gap = false;
			while (!(line == null) && !line.matches(dataExpr)) {
				gap = true;
				line = buffReader.readLine();
			}
			
			if (line == null) {
				fillTheRest(runs, eval);
				break;
			}
			
			if (gap) {
				fillTheRest(runs, eval);
				eval++;
				start = "1 ";
			}
			
			if (eval > evaluators) {
				eval = 0;
				runs++;
			}
			
			Unit unit = new Unit(start + line);
			data[runs][eval].add(unit);
			start = (unit.getStop() + 1) + " ";
			
			line = buffReader.readLine();
		}
	
//		if (runs != times - 1) {
//			throw new RuntimeException("Wrong input format. Runs expected: " + times + ". Runs performed: " + (runs + 1));
//		}
		
		buffReader.close();
		return runs + 1;
	}
	
	/**
	 * If the run ended too early, fills the rest with the last value
	 * @param runs the number of the run
	 * @param eval the number of the fitness evaluator
	 */
	private void fillTheRest(int runs, int eval) {
		int stepsLimit = Integer.parseInt(parameters.get("stepsLimit"));
		int lastIndex = data[runs][eval].size() - 1;
		Unit lastUnit = data[runs][eval].get(lastIndex);
		data[runs][eval].remove(lastIndex);
		data[runs][eval].add(new Unit(lastUnit.getStart(), stepsLimit, lastUnit.getValue()));
		
		if (stopHandler != null) {
			stopHandler.handle(lastUnit.getStop(), lastUnit.getValue());
		}
	}
	
	private BufferedReader readParameters(Reader reader) throws IOException {
		BufferedReader bufReader = new BufferedReader(reader);
		String[] split = bufReader.readLine().split(" ");
		
		for(int i = 0; i < split.length - 2; i += 2) {
			parameters.put(split[i], split[i + 1]);
		}
		
		return bufReader;
	}
	
	private Unit search(int low, int high, int time, int fitnessIndex, int generation) {
		int index = low + (high - low) / 2;		
		
		Unit candidate = data[time][fitnessIndex].get(index);
		
		if (candidate.getStart() > generation) {
			return search(low, index - 1, time, fitnessIndex, generation);
		}
		
		if (candidate.getStop() < generation) {
			return search(index + 1, high, time, fitnessIndex, generation);
		}
		
		return candidate;
	}
	
	/**
	 * An unmodifiable class that represents a unit of data -- one line from the log 
	 * @author Arina Buzdalova
	 */
	public class Unit implements Comparable<Unit> {
		private int start;
		private int stop;
		private double value;
		private final String string;
		
		/**
		 * Constructs {@link Unit} with the specified start, stop and value,
		 * which was constant from start to stop
		 * @param start the specified start
		 * @param stop the specified stop
		 * @param value the specified value
		 */
		public Unit(int start, int stop, double value) {
			this.start = start;
			this.stop = stop;
			this.value = value;
			string = start + " " + stop + " " + value;
		}
		
		/**
		 * Constructs {@link Unit} with from the specified string.
		 * It should have format "start stop value".
		 * @param string the specified string
		 */
		public Unit(String string) {
			//TODO: check string format
			this.string = string;
			start = -1;
			stop = -1;
			value = -1;
		}
		
		private void tryInit() {
			if (start < 0) {
				String[] tokens = string.split(" ");
				if (tokens.length != 3) {
					throw new IllegalArgumentException("Wrong number of tokens. String format is \"start stop value\".");
				}
				
				start = Integer.parseInt(tokens[0]);
				stop = Integer.parseInt(tokens[1]);
				value = Double.parseDouble(tokens[2]);
			}
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public int compareTo(@NotNull Unit unit) {
			tryInit();
			return Double.compare(value, unit.getValue());
		}

		/**
		 * Gets the number of generations between start and stop (both inclusively)
		 * @return number of generations 
		 */
		public int getRange() {
			tryInit();
			return stop - start + 1;
		}
		
		/**
		 * Gets the start generation
		 * @return the start generation
		 */
		public int getStart() {
			tryInit();
			return start;
		}
		
		/**
		 * Gets the stop generation
		 * @return the stop generation
		 */
		public int getStop() {
			tryInit();
			return stop;
		}
		
		/**
		 * Gets the value that is constant from start to stop
		 * @return the value
		 */
		public double getValue() {
			tryInit();
			return value;
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return string;
		}
	}
}
