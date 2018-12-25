package ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.stat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.uncommons.maths.statistics.EmptyDataSetException;

import ru.ifmo.ctd.ngp.demo.ffchooser.StatisticsData;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.utils.ConfigGenerator;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.Configuration;
import ru.ifmo.ctd.ngp.demo.ffchooser.genetic.print.Decompressor;
import ru.ifmo.ctd.ngp.demo.ffchooser.genetic.print.Decompressor.Unit;
import ru.ifmo.ctd.ngp.demo.ffchooser.utils.ObjectWithValue;

/**
 * Some useful methods for analyzing and viewing the statistics collected by the {@link StatisticsCollector}.
 * 
 * @author Arina Buzdalova
 */
public class StatisticsUtils {
	
	/**
	 * Creates files with plot data at the specified path
	 * @param args : 	args[0] -- path to the run logs, 
	 * 					args[1] -- path to the plot data to be created
	 * 					args[2] -- properties with collection of configurations
	 * @throws IOException if an I/O error occurs
	 */
	public static void main(String[] args) throws IOException {
		
		ArrayList<Configuration> configs = new ArrayList<>(ConfigGenerator.generate(args[2]));
		int size = configs.size();
		for (int i = 0; i < size; i++) {
			Configuration c = configs.get(i);
			
			System.out.println(i + " of " + size + " is printed. Currently printing: ");
			System.out.println(c);

            String configurationName = c.generateFullName();
            File deviationPlot = new File(args[1] + "/deviation/", configurationName);
            File categoryPlot = new File(args[1] + "/category/", configurationName);
            //noinspection ResultOfMethodCallIgnored
            deviationPlot.getParentFile().mkdirs();
            //noinspection ResultOfMethodCallIgnored
            categoryPlot.getParentFile().mkdirs();
            printDeviationPlotData(new FileWriter(deviationPlot), c, args[0]);
			printCategoryPlotData(new FileWriter(categoryPlot), c, args[0]);
		}
	}
	
	/**
	 * Prints data for generating deviation plots to the specified writer
	 * @param writer the specified writer
	 * @param config the configuration
	 * @param logsPath the path to the compressed logs
	 * @throws IOException if an I/O error occurs
	 */
	private static void printDeviationPlotData(Writer writer, Configuration config, String logsPath)
		throws IOException {
		int steps = config.getSteps();
		List<StatisticsData> stat = StatisticsUtils.getGenerationsStatistics(config, logsPath);
		
		for (int i = 1; i <= steps; i++) {
			try {
                writer.append(String.valueOf(stat.get(i).getAverage()))
                        .append(" ")
                        .append(String.valueOf(stat.get(i).getMin()))
                        .append(" ")
                        .append(String.valueOf(stat.get(i).getMax()))
                        .append("\n");
			} catch (EmptyDataSetException ex) {
				//All runs ended before steps limit was reached
				break;
			}
		}
		writer.close();
	}
	
	/**
	 * Prints data for generating category plots to the specified writer
	 * @param writer the specified writer
	 * @param config the configuration
	 * @param logsPath the path to the compressed logs
	 * @throws IOException if an I/O error occurs
	 */
	private static void printCategoryPlotData(Writer writer, Configuration config, String logsPath)
		throws IOException {
        Decompressor decomp = new Decompressor(new FileReader(logsPath + "/" + config.generateFullName()));
		int times = decomp.getActualTimes();
		int evalNum = Integer.parseInt(decomp.getParameterValue("evaluators"));
		
		double[][] data = new double[evalNum][config.getSteps()];
		for (int i = 0; i < evalNum; i++) {
			Arrays.fill(data[i], 0);
		}
			
		for (int time = 0; time < times; time++) {
			List<Unit> units = decomp.getCompressedDescription(time, evalNum);
			for (Unit unit : units) {
				for (int gen = unit.getStart(); gen <= unit.getStop(); gen++) {
					data[(int)unit.getValue()][gen - 1]++;
				}
			}
		}

        writer.append(String.valueOf(data[0].length)).append("\n");
        for (double[] d : data) {
            for (double dd : d) {
                writer.append(String.valueOf(dd)).append(" ");
            }
            writer.append("\n");
        }
		writer.close();
	}
	
	/**
	 * Generates list of {@link StatisticsData} about values of the target evaluator 
	 * in each generation of the runs specified by {@link Configuration}
	 * @param config the specified configuration
	 * @param logsPath the path to the compressed log files
	 * @return list of {@link StatisticsData} about each generation of the <code>config</code>
	 * @throws IOException if an I/O error occurs
	 */
	public static List<StatisticsData> getGenerationsStatistics(Configuration config, String logsPath) throws IOException {
        Decompressor decomp = new Decompressor(new FileReader(logsPath + "/" + config.generateFullName()));

		int times = decomp.getActualTimes();

		List<StatisticsData> genStat = new ArrayList<>();
		for (int i = 0; i < config.getSteps() + 1; i++) {
			genStat.add(new StatisticsData(times));
		}
		
		for (int time = 0; time < times; time++) {
			List<Unit> units = decomp.getCompressedDescription(time, Integer.parseInt(decomp.getParameterValue("target")));
			for (Unit unit : units) {
				for (int gen = unit.getStart(); gen <= unit.getStop(); gen++) {
					genStat.get(gen).addValue(unit.getValue());
				}
			}
		}	
		
		return genStat;
	}
	
	/**
	 * Gets the number of run in which the maximum value was reached
	 * @param statPath the path to the statistics
	 * @param compressedPath the path to the compressed logs
	 * @param conf the specified configuration
	 * @return the number of run in which the maximum value was reached
	 * @throws IOException if an I/O error occurs
	 */
	public static int getMaxTime(String statPath, String compressedPath, Configuration conf) throws IOException {
        Decompressor decomp = new Decompressor(new FileReader(compressedPath + conf.generateFullName()));
		int times = decomp.getActualTimes();
		
		double max = readLog(statPath + conf.generateFullName())[0];
		for (int time = 0; time < times; time++) {
			for (Unit unit : decomp.getCompressedDescription(time, 1)) {
				if (unit.getValue() == max) {
					return time;
				}
			}
		}
		return -1;
	}
	
	/**
	 * Sorts the specified configurations by the average best fitness
	 * @param configs the specified configurations
	 * @param statPath path to the statistics about the <code>configs</code>
	 * @return list of configurations sorted by the average best fitness
	 * @throws IOException if an I/O error occurs
	 */
	public static List<Configuration> sortByBest(Collection<Configuration> configs, String statPath) throws IOException {
		List<ObjectWithValue<Configuration>> pairs = new ArrayList<>();
		for (Configuration config : configs) {
			pairs.add(new ObjectWithValue<>(readLog(statPath + "/" + config.generateFullName())[2], config));
		}
		Collections.sort(pairs);
		return pairs.stream().map(ObjectWithValue::getObject).collect(Collectors.toList());
	}
	
	/**
	 * Reads the specified statistics log file into the array {max, min, average, median}
	 * @param log the specified log file
	 * @return {max, min, average, median}
	 * @throws NumberFormatException if there are some errors in the log format
	 * @throws IOException if an I/O error occurs
	 */
	private static double[] readLog(String log) throws NumberFormatException, IOException {
		try (BufferedReader reader = new BufferedReader(new FileReader(log))) {
            double[] rv = new double[4];
            rv[0] = Double.parseDouble(reader.readLine());
            rv[1] = Double.parseDouble(reader.readLine());
            rv[2] = Double.parseDouble(reader.readLine());
            rv[3] = Double.parseDouble(reader.readLine());
            return rv;
        }
	}
}
