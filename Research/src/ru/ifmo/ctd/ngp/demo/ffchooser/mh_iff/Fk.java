package ru.ifmo.ctd.ngp.demo.ffchooser.mh_iff;

import java.util.List;

import org.uncommons.watchmaker.framework.FitnessEvaluator;

import ru.ifmo.ctd.ngp.demo.legacy_ds.string.impl.BitString;

import static ru.ifmo.ctd.ngp.demo.ffchooser.mh_iff.Utils.isHomogeneous;

public class Fk implements FitnessEvaluator<BitString> {
	private final boolean k;

	public Fk(boolean k) {
		this.k = k;
	}

    private double getFitnessImpl(BitString candidate) {
        int len = candidate.length();
        if (len == 0) {
            return 0;
        }
        if (len == 1) {
            if (candidate.charAt(0) == k) {
                return 1;
            } else {
                return 0;
            }
        }
        return (isHomogeneous(candidate, k) ? len : 0) +
               getFitnessImpl(candidate.substring(0, len / 2)) +
               getFitnessImpl(candidate.substring(len / 2, len));
    }

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getFitness(BitString candidate, List<? extends BitString> population) {
        return getFitnessImpl(candidate);
	}

	@Override
	public boolean isNatural() {
		return true;
	}
}
