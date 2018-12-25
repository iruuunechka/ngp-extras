package ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.stat;

import ru.ifmo.ctd.ngp.demo.ffchooser.config.Configuration;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.utils.ConfigGenerator;
import ru.ifmo.ctd.ngp.demo.ffchooser.genetic.Printer;
import ru.ifmo.ctd.ngp.demo.ffchooser.genetic.print.Decompressor;
import ru.ifmo.ctd.ngp.demo.ffchooser.utils.TimesUtils;
import ru.ifmo.ctd.ngp.demo.ffchooser.vfs.OldPathGenerator;
import ru.ifmo.ctd.ngp.demo.legacy_ds.string.impl.BitString;
import ru.ifmo.ctd.ngp.util.CollectionsEx;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Properties;

/**
 * Set of simple static methods that gather some statistics with
 * {@link StatisticsCollector}
 * 
 * @author Arina Buzdalova
 */
public class Runner {
	private Runner(){}
	
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
	public static void runConfigurations(Properties p) throws IOException {	
		Collection<Configuration> configs = ConfigGenerator.generate(p);
		System.out.println(configs.size() + " configurations");
		
		String path = p.getProperty("path");
		int times = TimesUtils.getTimes(p);
		boolean es = p.getProperty("es").equals("true");

        File f = new File(path + "/statistics.txt");
		int i = 0;
		for (Configuration config : configs) {
            if (!new File(path, OldPathGenerator.instance().path(config)).exists()) {
			//if (!new File(path + "/" + config.generateFullName()).exists()) {
				run(config, times, path, i++, es);
			}
		}

        //if (!f.exists()) {
            FileWriter fw = new FileWriter(f);
            for (Configuration config : configs) {
                Decompressor decomp = new Decompressor(new FileReader(path + "/" + config.generateFullName()));
                if (!config.getLabel().equals("none")) {
                    String s = config.getLabel() + config.getLength()+config.getDivider() + ' ' +
                            decomp.getCouGoodChoices(config.getLabel() + config.getLength() + config.getDivider(), new File("../forICMLA/five/wilcox.txt")) + "\n";
                    fw.write(s);
                    System.out.println(s);
                }
            }
            fw.close();
        //}
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
