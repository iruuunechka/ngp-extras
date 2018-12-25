package ru.ifmo.ctd.ngp.demo.ffchooser.royal;
import java.io.Writer;

import ru.ifmo.ctd.ngp.demo.legacy_ds.string.GStringBuilderX;
import ru.ifmo.ctd.ngp.demo.legacy_ds.string.GStringX;

/**
 * Container of the parameters used in {@link RunVisitor}.
 * 
 * @author Arina Buzdalova
 *
 * @param <S> type of the string that represents an individual
 * @param <B> type of the builder corresponding to the string
 */
public class RunArgument<S extends GStringX<Boolean, S, B>, B extends GStringBuilderX<Boolean, B, S>> {
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
    	 * the sample string used to derive type of individuals
    	 */    	
    	public final S sampleString;

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
    	 * @param sampleString the sample string used to derive type of individuals
    	 * @param times the number of runs
    	 * @param es {@code true} if evolution strategy should be used instead of genetic algorithm
    	 */
		public RunArgument(Writer writer, int generationSize,
				int eliteCount, S sampleString, int times, boolean es) {
			this.writer = writer;
			this.generationSize = generationSize;
			this.eliteCount = eliteCount;
			this.sampleString = sampleString;
			this.times = times;
			this.es = es;
		}
}
		