package ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.evaluators;

import java.util.ArrayList;
import java.util.List;

import ru.ifmo.ctd.ngp.demo.ffchooser.config.factory.EvaluatorsFactory;
import ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.FunctionalFitness;
import ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.IntegerFunctionImpl;
import ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.f.Abs;
import ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.f.X;
import ru.ifmo.ctd.ngp.demo.util.textconstructor.ParamDef;

/**
 * Factory of <code>{|x - p|, const - |x - p|, [x / k]}</code> evaluators list.
 * The target fitness evaluator is <code>[x / k]</code>.
 * 
 * @author Arina Buzdalova
 */
public class AbsEvaluatorsFactory implements EvaluatorsFactory {
	private static final long serialVersionUID = 7511054068657162732L;
	private final int divisor;
	private final int length;
	private final static int targetIndex = 2;
    private final int switchPoint;

    /**
	 * Creates {@link AbsEvaluatorsFactory} with the specified parameters
	 * @param divisor the <code>k</code> in the <code>[x/k]</code> evaluator
	 * @param length the length of an individual
     * @param switchPoint the switch point
     */
	public AbsEvaluatorsFactory(@ParamDef(name = "divisor") int divisor, @ParamDef(name = "length") int length, @ParamDef(name = "switchPoint") int switchPoint) {
		this.divisor = divisor;
		this.length = length;
        this.switchPoint = switchPoint;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<FunctionalFitness> getEvaluators() {
		List<FunctionalFitness> evaluators = new ArrayList<>();
		double c = Math.max((length - switchPoint) * (length - switchPoint), switchPoint * switchPoint);
		evaluators.add(new FunctionalFitness(new Abs(c + 1, -1, -switchPoint)));
		evaluators.add(new FunctionalFitness(new Abs(0, 1, -switchPoint)));
		evaluators.add(new FunctionalFitness(new IntegerFunctionImpl(new X(), 1.0 / divisor, 0, length)));
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
		return "abs";
	}

    @SuppressWarnings("IntegerDivisionInFloatingPointContext") // length % divisor === 0
	@Override
    public double bestFitness() {
        return length / divisor;
    }

    /**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + divisor;
		result = prime * result + length;
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
		AbsEvaluatorsFactory other = (AbsEvaluatorsFactory) obj;
        return divisor == other.divisor && length == other.length;
    }
}
