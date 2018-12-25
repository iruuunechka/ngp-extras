package ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.stat;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

import ru.ifmo.ctd.ngp.demo.ffchooser.config.Configuration;
import ru.ifmo.ctd.ngp.demo.ffchooser.genetic.Printer;
import ru.ifmo.ctd.ngp.demo.ffchooser.vfs.OldPathGenerator;
import ru.ifmo.ctd.ngp.demo.legacy_ds.string.impl.BitString;

/**
 * A class that collects statistics about learning performance.
 * The task with switch point is used.
 * 
 * @author Arina Buzdalova
 */
public class StatisticsCollector {
    private StatisticsCollector() {}
	
	/**
	 * <p>
	 * Runs GA with learning and collects statistics about its performance.
	 * The task with the switch point and the two piecewise functions is used.
	 * </p><p>
	 * Hierarchy that stores logs and log names will be generated automatically. 
	 * Newer logs with the same names will be numbered.
	 * </p>
	 * @param conf the run configuration
	 * @param times the number of runs
	 * @param path the location of the hierarchy, in which log files are stored
     * @param printers list of additional printers
	 * @throws IOException if an I/O error occurs 
	 */
	public static void run(Configuration conf, int times, String path,
    		List<? extends Printer<? super BitString>> printers) throws IOException {
        File file = new File(path, OldPathGenerator.instance().path(conf));
        //noinspection ResultOfMethodCallIgnored
        file.getParentFile().mkdirs();
        try (FileWriter out = new FileWriter(file)) {
            run(conf, times, out, printers);
		}
	}
	
	/**
	 * <p>
	 * Runs GA with learning and collects statistics about its performance.
	 * The task with the switch point and the two piecewise functions is used.
	 * </p><p>
	 * This method doesn't generate log files, but uses specified writer.
	 * It also returns number of used evaluators (fitness functions).
	 * It is convenient for testing.
	 * </p>
	 * @param conf the run configuration
	 * @param times the number of runs
	 * @param writer the writer that is used to log the run
     * @param printers list of additional printers
	 * @return number of used evaluators (fitness functions)
	 */
	public static int run(Configuration conf, int times, Writer writer,
                          List<? extends Printer<? super BitString>> printers) {
		return run(conf, times, writer, null, printers);
	}
			
	/**
	 * <p>
	 * Runs GA with learning and collects statistics about its performance.
	 * The task with the switch point and the two piecewise functions is used.
	 * </p><p>
	 * This method doesn't generate log files, but uses specified writer.
	 * It also returns number of used evaluators (fitness functions).
	 * It is convenient for testing.
	 * </p>
	 * @param conf the run configuration
	 * @param times the number of runs
	 * @param writer the writer that is used to log the run
	 * @param observer observer that counts percentage of completed tasks
     * @param printers list of additional printers
	 * @return number of used evaluators (fitness functions)
	 */
	public static int run(Configuration conf, int times, Writer writer, StatCollObserver observer,
                          List<? extends Printer<? super BitString>> printers) {
		return run(conf, times, writer, observer, false, printers);
	}
	
	/**
	 * <p>
	 * Runs GA with learning and collects statistics about its performance.
	 * The task with the switch point and the two piecewise functions is used.
	 * </p><p>
	 * This method doesn't generate log files, but uses specified writer.
	 * It also returns number of used evaluators (fitness functions).
	 * It is convenient for testing.
	 * </p>
	 * @param conf the run configuration
	 * @param times the number of runs
	 * @param writer the writer that is used to log the run
	 * @param observer observer that counts percentage of completed tasks
	 * @param es {@code true} if evolution strategy should be use instead of genetic algorithm
     * @param printers list of additional printers
	 * @return number of used evaluators (fitness functions)
	 */
	public static int run(Configuration conf, int times, Writer writer, StatCollObserver observer,
                          boolean es, List<? extends Printer<? super BitString>> printers) {
		int updatePeriod = 200;
		return StatisticsRunVisitor.instance().run(conf, new StatisticsRunArgument(
                observer, updatePeriod, writer, conf.getGenerationCount(),
                conf.getEliteCount(), printers, times, es
        ));
	}
}
