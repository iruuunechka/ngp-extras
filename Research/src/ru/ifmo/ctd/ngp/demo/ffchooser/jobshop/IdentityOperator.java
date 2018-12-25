package ru.ifmo.ctd.ngp.demo.ffchooser.jobshop;

import java.util.List;
import java.util.Random;

import org.uncommons.watchmaker.framework.EvolutionaryOperator;

public class IdentityOperator implements EvolutionaryOperator<List<Integer>> {

	@Override
	public List<List<Integer>> apply(List<List<Integer>> arg0, Random arg1) {
		return arg0;
	}

}
