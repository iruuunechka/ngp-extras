package ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.stat.meynster;

import java.io.*;
import java.util.Collection;
import java.util.Properties;

import ru.ifmo.ctd.ngp.demo.ffchooser.config.utils.ConfigGenerator;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.Configuration;
import ru.ifmo.ctd.ngp.demo.ffchooser.genetic.Printer;
import ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.stat.StatisticsCollector;
import ru.ifmo.ctd.ngp.demo.ffchooser.utils.TimesUtils;
import ru.ifmo.ctd.ngp.demo.ffchooser.vfs.OldPathGenerator;
import ru.ifmo.ctd.ngp.demo.legacy_ds.string.impl.BitString;
import ru.ifmo.ctd.ngp.util.CollectionsEx;

/**
 * Set of simple static methods that gather some statistics with
 * {@link StatisticsCollector}
 * 
 * @author Arina Buzdalova
 */
public class MeynsterRunner {
	private MeynsterRunner(){}
	
	/**
	 * Generates run logs for the configurations described in the specified properties file
	 * @param args args[0] path to the properties file with collection of configurations and the number of runs; 
	 * @throws IOException is an I/O error occurs
	 */
	public static void main(String[] args) throws IOException {
		Properties p = new Properties();

		p.load(new FileReader(new File(args[0])));
		runConfigurations(p);
	}
	
	/**
	 * Runs the {@link StatisticsCollector#run} on the specified collection of configurations
	 * for each configuration that isn't already run
	 * @param p the properties with collection of configurations
	 * @throws IOException if an I/O error occurs
	 */
	static void runConfigurations(Properties p) throws IOException {
		Collection<Configuration> configs = ConfigGenerator.generate(p);
		String path = p.getProperty("path");
		int times = TimesUtils.getTimes(p);
		boolean es = p.getProperty("es").equals("true");
		
		int i = 0;
		for (Configuration config : configs) {
			run(config, times, path, i++, es);
		}
	}
	
	private static void run(Configuration config, int times, String path, int number, boolean es) throws IOException {
        System.out.println(number + " " + config);
		File file = new File(path, OldPathGenerator.instance().path(config));
		//noinspection ResultOfMethodCallIgnored
		file.getParentFile().mkdirs();
        FileWriter out = new FileWriter(file);
		StatisticsCollector.run(config, times, out, null, es, CollectionsEx.<Printer<BitString>>listOf());
	}
}
