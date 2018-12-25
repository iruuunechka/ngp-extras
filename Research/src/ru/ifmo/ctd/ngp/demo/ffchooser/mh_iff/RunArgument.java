package ru.ifmo.ctd.ngp.demo.ffchooser.mh_iff;

import java.io.Writer;

/**
 * Container of the parameters used in {@link RunVisitor}.
 * 
 * @author Arina Buzdalova
 */
public class RunArgument {
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
     * the number of runs
     */
    public final int times;
    /**
     * {@code true} is evolution strategy is used for evolving individuals,
     * {@code false} if genetic algorithm is used
     */
    public final boolean es;

    /**
     * Constructs {@link RunArgument} with the specified parameters
     * @param writer the writer used to log the runs
     * @param generationSize the number of individuals in a generation
     * @param eliteCount the elite count
     * @param times the number of runs
     * @param es {@code true} if evolution strategy should be used instead of genetic algorithm
     */
    public RunArgument(Writer writer, int generationSize, int eliteCount, int times, boolean es) {
        this.writer = writer;
        this.generationSize = generationSize;
        this.eliteCount = eliteCount;
        this.times = times;
        this.es = es;
    }
}
		