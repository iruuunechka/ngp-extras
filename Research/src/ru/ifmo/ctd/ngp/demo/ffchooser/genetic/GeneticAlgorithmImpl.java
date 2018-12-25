package ru.ifmo.ctd.ngp.demo.ffchooser.genetic;

import java.util.List;

import org.uncommons.watchmaker.framework.CandidateFactory;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import org.uncommons.watchmaker.framework.FitnessEvaluator;

public class GeneticAlgorithmImpl<T> extends GeneticAlgorithm<T> {

	public GeneticAlgorithmImpl(CandidateFactory<T> factory,
			int targetCriterion, int curEvaluator,
			List<? extends FitnessEvaluator<? super T>> evaluators,
			List<? extends EvolutionaryOperator<T>> operators) {
		super(factory, targetCriterion, curEvaluator, evaluators, operators);
	}

	@Override
	protected T emptyCandidate() {
		return null;
	}

	@Override
	public void setLength(int length) {
		throw new UnsupportedOperationException();
	}
}
