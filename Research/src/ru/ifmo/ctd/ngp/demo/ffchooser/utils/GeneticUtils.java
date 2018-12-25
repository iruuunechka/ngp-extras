package ru.ifmo.ctd.ngp.demo.ffchooser.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.jetbrains.annotations.NotNull;

import ru.ifmo.ctd.ngp.demo.legacy_ds.string.GString;
import ru.ifmo.ctd.ngp.demo.legacy_ds.string.impl.BitString;
import ru.ifmo.ctd.ngp.demo.legacy_ds.string.impl.BitStringBuilder;
import ru.ifmo.ctd.ngp.util.FastRandom;

/**
 * Some useful methods for genetics based on {@link GString}<code>&lt;Boolean&gt;</code>
 * with {@link Boolean} elements.
 * 
 * @author Arina Buzdalova
 */
public class GeneticUtils {

    private GeneticUtils() {}

    /**
	 * Converts {@link ru.ifmo.ctd.ngp.demo.legacy_ds.string.impl.BitString} to integer value.
	 * @param string the string to be converted
	 * @return the integer value of <code>string</code>
	 */
	public static double toInt(@NotNull BitString string) {
        long z = string.bitsAt(0, string.length());
        return Long.reverse(z) >>> (64 - string.length());
    }
	
	/**
	 * Counts number of true-values in the {@link GString}<code>&lt;Boolean&gt;</code>.
	 * @param string the string to be converted
	 * @return number of true-values in the <code>string</code>
	 */
	public static double countBits(@NotNull BitString string) {
        return string.bitCount(0, string.length());
	}

    /**
	 * Returns population, which consists of equal false-strings (false, false, ... false).
	 * @param length the length of the strings
	 * @param populationSize the number of the strings in the population
     * @return population which consists of equal false-strings
	 */
	public static List<BitString> zeroPopulation(int length, int populationSize) {
		List<BitString> population = new ArrayList<>();
		BitString zeroStr = BitString.of(new boolean[length]);
		for (int i = 0; i < populationSize; i++) {
			population.add(zeroStr);
		}
		return population;
	}
	
    /**
	 * Returns population, which consists of random strings.
	 * @param length the length of the strings
	 * @param populationSize the number of the strings in the population
     * @return population which consists of random strings
	 */
	public static List<BitString> randomPopulation(int length, int populationSize) {
		List<BitString> population = new ArrayList<>();	
		Random r = FastRandom.threadLocal();
		for (int i = 0; i < populationSize; i++) {
			BitStringBuilder b = new BitStringBuilder();
			for (int j = 0; j < length; j++) {
				b.append(r.nextBoolean());
			}
			population.add(b.toGString());
		}
		return population;
	}
}
