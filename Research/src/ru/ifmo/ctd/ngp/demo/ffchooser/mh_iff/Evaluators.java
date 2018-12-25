package ru.ifmo.ctd.ngp.demo.ffchooser.mh_iff;

import java.util.Collections;
import java.util.List;

import ru.ifmo.ctd.ngp.demo.ffchooser.config.factory.EvaluatorsFactory;
import ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.FunctionalFitness;
import ru.ifmo.ctd.ngp.demo.legacy_ds.string.impl.BitString;
import ru.ifmo.ctd.ngp.util.CollectionsEx;
import ru.ifmo.ctd.ngp.demo.util.textconstructor.ParamDef;

/**
 * {@link EvaluatorsFactory} for the h-iff problems
 * @author Arina Buzdalova
 *
 */
public class Evaluators implements EvaluatorsFactory {
	private static final long serialVersionUID = -5309293138845755611L;
	private final static List<FunctionalFitness> ffs = 
			CollectionsEx.listOf(new F(), new Ffk(true), new Ffk(false));

    private final BitString allOnes;

    public Evaluators(@ParamDef(name = "length") int length) {
        this.allOnes = BitString.of(Collections.nCopies(length, true));
    }
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int targetIndex() {
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<FunctionalFitness> getEvaluators() {
		return Collections.unmodifiableList(ffs);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return "mhiff";
	}

    @Override
    public double bestFitness() {
        return ffs.get(targetIndex()).getFitness(allOnes, null);
    }

    /**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return 1;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
        return this == obj || obj != null && getClass() == obj.getClass();
    }
}
