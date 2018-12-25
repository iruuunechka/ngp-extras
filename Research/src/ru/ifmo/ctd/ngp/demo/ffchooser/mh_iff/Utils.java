package ru.ifmo.ctd.ngp.demo.ffchooser.mh_iff;

import ru.ifmo.ctd.ngp.demo.ffchooser.utils.GeneticUtils;
import ru.ifmo.ctd.ngp.demo.legacy_ds.string.impl.BitString;

public class Utils {
	public static boolean isHomogeneous(BitString string, boolean k) {
		double ones = GeneticUtils.countBits(string);
		if (k) {
			return ones == string.length();
		} else {
			return ones == 0;
		}
	}
}
