package ru.ifmo.ctd.ngp.demo.ffchooser.mh_iff;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.ifmo.ctd.ngp.demo.ffchooser.config.factory.EvaluatorsFactory;
import ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.FunctionalFitness;
import ru.ifmo.ctd.ngp.demo.legacy_ds.string.impl.BitString;
import ru.ifmo.ctd.ngp.demo.util.textconstructor.ParamDef;

/**
 * {@link EvaluatorsFactory} that extends the h-iff {@link Evaluators} with
 * some inefficient evaluators.
 *  
 * @author Arina Buzdalova
 */
@SuppressWarnings("UnusedDeclaration")
public class EvaluatorsEx implements EvaluatorsFactory {
	private static final long serialVersionUID = -5261570364794050248L;
	private final Evaluators evaluators;
	private final List<FunctionalFitness> ffs;
    private final BitString allOnes;

    /**
	 * Constructs {@link EvaluatorsEx} without any parameters
	 */
	public EvaluatorsEx(@ParamDef(name = "length") int length) {
        this.allOnes = BitString.of(Collections.nCopies(length, true));
		evaluators = new Evaluators(length);
		ffs = new ArrayList<>();
		ffs.add(new FAlternatingMaskFitness());
		ffs.addAll(evaluators.getEvaluators());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int targetIndex() {
		return evaluators.targetIndex() + 1;
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
		return evaluators.getName() + "-ext";
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
