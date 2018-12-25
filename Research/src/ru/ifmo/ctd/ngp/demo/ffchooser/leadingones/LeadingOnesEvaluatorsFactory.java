package ru.ifmo.ctd.ngp.demo.ffchooser.leadingones;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.ifmo.ctd.ngp.demo.ffchooser.config.factory.EvaluatorsFactory;
import ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.FunctionalFitness;
import ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.f.X;
import ru.ifmo.ctd.ngp.demo.legacy_ds.string.impl.BitString;
import ru.ifmo.ctd.ngp.demo.util.textconstructor.ParamDef;

/**
 * Factory of <code>LeadingOnes</code> and <code>OneMax</code> evaluators list.
 * The target fitness evaluator is <code>LeadingOnes</code>.
 * 
 * @author Arina Buzdalova
 */
@SuppressWarnings({"unused"})
public class LeadingOnesEvaluatorsFactory implements EvaluatorsFactory {
	private static final long serialVersionUID = -6975030689860535735L;
	private final static int targetIndex = 1;
    private final BitString allOnes;

    public LeadingOnesEvaluatorsFactory(@ParamDef(name = "length") int length){
        this.allOnes = BitString.of(Collections.nCopies(length, true));
    }
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<FunctionalFitness> getEvaluators() {
		List<FunctionalFitness> evaluators = new ArrayList<>();
		evaluators.add(new FunctionalFitness(new X()));
		evaluators.add(new LeadingOnesFitness());
		return evaluators;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int targetIndex() {
		return targetIndex;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return "minmax";
	}

    @Override
    public double bestFitness() {
        return getEvaluators().get(targetIndex).getFitness(allOnes, null);
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
