package ru.ifmo.ctd.ngp.demo.ffchooser.leadingones;

import java.util.List;

import org.uncommons.watchmaker.framework.FitnessEvaluator;

import ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.FunctionalFitness;
import ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.RealFunction;
import ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.f.NullFunction;
import ru.ifmo.ctd.ngp.demo.legacy_ds.string.impl.BitString;

/**
 * 
 * @author Arina Buzdalova
 *
 */
public class LeadingOnesFitness extends FunctionalFitness implements FitnessEvaluator<BitString> {

	public LeadingOnesFitness() {
		super(new NullFunction());
	}
	
	public LeadingOnesFitness(RealFunction function) {
		super(function);
	}

	@Override
	public double getFitness(BitString arg0, List<? extends BitString> arg1) {
		int cur = 0;
		while (cur < arg0.length()) {
			if (!arg0.charAt(cur)) {
				break;
			}
			cur++;
		}
		return cur;
	}

	@Override
	public boolean isNatural() {
		return true;
	}
	
	@Override
	public double deltaRatio(BitString individual, double delta) {
		throw new UnsupportedOperationException();
	}
}
