package ru.ifmo.ctd.ngp.demo.ffchooser.mh_iff;

import java.util.List;

import ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.FunctionalFitness;
import ru.ifmo.ctd.ngp.demo.legacy_ds.string.impl.BitString;

import static ru.ifmo.ctd.ngp.demo.ffchooser.mh_iff.Utils.isHomogeneous;

public class F extends FunctionalFitness {

	public F() {
		super(null);
	}	

	public double deltaRatio(BitString individual, double delta) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getFitness(BitString candidate, List<? extends BitString> population) {
		int len = candidate.length();
		//System.out.println(len);
		
		if (len == 0) {
			return 0;
		}
		
		if (len == 1) {
			return 1;
		}
		
		return 	(isHomogeneous(candidate, true) || isHomogeneous(candidate, false) ? len : 0) + 
				getFitness(candidate.substring(0, len / 2 ), population) + 
				getFitness(candidate.substring(len / 2, len), population);
	}
}
