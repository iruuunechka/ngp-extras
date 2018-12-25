package ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.stat;

import java.io.Writer;
import java.util.List;

import ru.ifmo.ctd.ngp.demo.ffchooser.genetic.Printer;
import ru.ifmo.ctd.ngp.demo.legacy_ds.string.impl.BitString;

/**
 * Container of the parameters used in {@link StatisticsRunVisitor}.
 * 
 * @author Arina Buzdalova
 */
public class StatisticsRunArgument {
    /**
     * the {@link StatCollObserver} used to collect information about the runs performance
     */
    public final StatCollObserver observer;
    /**
     * the period of observer updates
     */
    public final int updatePeriod;
    /**
     * the writer used to log runs
     */
    public final Writer writer;
    /**
     * the number of individuals in a generation
     */
    public final int generationSize;
    /**
     * the elite count
     */
    public final int eliteCount;
    /**
     * the additional printers used to observe runs performance
     */
    public final List<? extends Printer<? super BitString>> printers;
    /**
     * the number of runs
     */
    public final int times;
    /**
     * {@code true} is evolution strategy is used for evolving individuals,
     * {@code false} if genetic algorithm is used
     */
    public final boolean es;

    /**
     * Constructs {@link StatisticsRunArgument} with the specified parameters
     * @param observer the {@link StatCollObserver} used to collect information about the runs performance
     * @param updatePeriod the period of <code>observer</code> updates
     * @param writer the writer used to log the runs
     * @param generationSize the number of individuals in a generation
     * @param eliteCount the elite count
     * @param printers the additional printers used to observe runs performance
     * @param times the number of runs
     * @param es {@code true} if evolution strategy should be used instead of genetic algorithm
     */
    public StatisticsRunArgument(StatCollObserver observer, int updatePeriod, Writer writer, int generationSize,
            int eliteCount, List<? extends Printer<? super BitString>> printers, int times, boolean es) {
        this.observer = observer;
        this.updatePeriod = updatePeriod;
        this.writer = writer;
        this.generationSize = generationSize;
        this.eliteCount = eliteCount;
        this.printers = printers;
        this.times = times;
        this.es = es;
    }
}
		