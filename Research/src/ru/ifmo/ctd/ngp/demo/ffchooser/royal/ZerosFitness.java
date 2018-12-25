package ru.ifmo.ctd.ngp.demo.ffchooser.royal;

import java.util.List;

import ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.FunctionalFitness;
import ru.ifmo.ctd.ngp.demo.ffchooser.utils.GeneticUtils;
import ru.ifmo.ctd.ngp.demo.legacy_ds.string.impl.BitString;

public class ZerosFitness extends FunctionalFitness {

	public ZerosFitness() {
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
		return candidate.length() - GeneticUtils.countBits(candidate);
	}
}
