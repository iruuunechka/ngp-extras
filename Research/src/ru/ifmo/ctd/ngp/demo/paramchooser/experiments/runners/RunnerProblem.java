package ru.ifmo.ctd.ngp.demo.paramchooser.experiments.runners;

import org.uncommons.watchmaker.framework.TerminationCondition;
import ru.ifmo.ctd.ngp.demo.paramchooser.experiments.NumericProblem;

import java.util.Random;

/**
 * @author Arkadii Rost
 */
public interface RunnerProblem extends NumericProblem {
	ProblemInstance getProblemInstance(Random rand);
	TerminationCondition getTerminationCondition();
}
