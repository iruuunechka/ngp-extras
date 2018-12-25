package ru.ifmo.ctd.ngp.demo.paramchooser.controller.agents.adaptive.distbase;

import ru.ifmo.ctd.ngp.demo.paramchooser.controller.Bounded;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.agents.qlearn.Partition;

import java.util.Random;

/**
 * @author Arkadii Rost
 */
public class SimplePartition implements Partition {
	private final Bounded[] splits;

	public SimplePartition(Bounded... splits) {
		this.splits = splits;
	}

	@Override
	public int getSplitCount() {
		return splits.length;
	}

	public Bounded getSplit(int i) {
		return splits[i];
	}

	@Override
	public double chooseParameterValue(Random rand, int split) {
		return splits[split].getRandomValue(rand);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(splits.length).append("\t");

		for (int i = 0; i < splits.length - 1; i++)
			sb.append(splits[i].getUpperBound()).append("\t");

		return sb.toString();
	}
}
