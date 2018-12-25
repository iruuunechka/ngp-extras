package ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.evaluators;

import java.util.Collections;
import java.util.List;

import ru.ifmo.ctd.ngp.demo.ffchooser.config.factory.EvaluatorsFactory;
import ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.FunctionalFitness;
import ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.InvertedFunctionalFitness;
import ru.ifmo.ctd.ngp.demo.legacy_ds.string.impl.BitString;
import ru.ifmo.ctd.ngp.demo.util.textconstructor.ParamDef;

/**
 * Factory of fitness evaluators list with inverted analogs of each evaluator 
 * 
 * @author Arina Buzdalova
 */
@SuppressWarnings("UnusedDeclaration")
public class InvertedEvaluatorsFactory implements EvaluatorsFactory {
	private static final long serialVersionUID = 5177403930986873683L;
	private final EvaluatorsFactory factory;
	private final double constant;
    private final BitString allOnes;

    /**
	 * Creates the {@link InvertedEvaluatorsFactory} that generates list with inverted analogs 
	 * of each evaluator from the specified factory, and with the original evaluators as well. 
	 * The inverted analog of an <code>evaluator</code> is <code>constant - evaluator</code>.
	 * @param factory the specified factory
	 * @param constant the maximal fitness value
     * @param switchPoint the switch point
	 */
	public InvertedEvaluatorsFactory(@ParamDef(name = "factory") EvaluatorsFactory factory,
                                     @ParamDef(name = "constant") double constant,
                                     @ParamDef(name = "switchPoint") int switchPoint,
                                     @ParamDef(name = "length") int length) {
		this.factory = factory;
		this.constant = constant;
        this.allOnes = BitString.of(Collections.nCopies(length, true));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<FunctionalFitness> getEvaluators() {
		List<FunctionalFitness> evaluators = factory.getEvaluators();
		for (int i = 0, len = evaluators.size(); i < len; i++) {
			evaluators.add(new InvertedFunctionalFitness(evaluators.get(i), constant));
		}
		return evaluators;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int targetIndex() {
		return factory.targetIndex();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return "inverted";
	}

    @Override
    public double bestFitness() {
        return getEvaluators().get(targetIndex()).getFitness(allOnes, null);
    }

    /**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(constant);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((factory == null) ? 0 : factory.hashCode());
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		InvertedEvaluatorsFactory other = (InvertedEvaluatorsFactory) obj;
		if (Double.doubleToLongBits(constant) != Double
				.doubleToLongBits(other.constant))
			return false;
		if (factory == null) {
			return other.factory == null;
		} else {
			return factory.equals(other.factory);
		}
	}
}
