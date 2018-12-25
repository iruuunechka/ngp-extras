package ru.ifmo.ctd.ngp.demo.ffchooser.royal;

import java.util.List;

import ru.ifmo.ctd.ngp.demo.ffchooser.mh_iff.Utils;
import ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.FunctionalFitness;
import ru.ifmo.ctd.ngp.demo.legacy_ds.string.impl.BitString;

public class RoyalRoadFitness extends FunctionalFitness {
	private final int blockSize;

	public RoyalRoadFitness(int blockSize) {
		super(null);
		this.blockSize = blockSize;
	}	

	public double deltaRatio(BitString individual, double delta) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getFitness(BitString candidate, List<? extends BitString> population) {
		int sum = 0;		
		for (int i = 0; i < candidate.length() - blockSize + 1; i += blockSize) {
			if (Utils.isHomogeneous(candidate.substring(i, i + blockSize), true)) {
				sum += blockSize;
			}
		}		
		return sum;
	}

}
