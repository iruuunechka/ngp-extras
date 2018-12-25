package ru.ifmo.ctd.ngp.demo.ffchooser.mh_iff;

import java.util.List;

import ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.FunctionalFitness;
import ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.f.NullFunction;
import ru.ifmo.ctd.ngp.demo.legacy_ds.string.impl.BitString;

public class Ffk extends FunctionalFitness {
	private final Fk fk;

	public Ffk(boolean k) {
		super(new NullFunction());
		fk = new Fk(k);
	}	  

	@Override
	public double deltaRatio(BitString individual, double delta) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getFitness(BitString candidate, List<? extends BitString> population) {
		return fk.getFitness(candidate, population);
	}
}
