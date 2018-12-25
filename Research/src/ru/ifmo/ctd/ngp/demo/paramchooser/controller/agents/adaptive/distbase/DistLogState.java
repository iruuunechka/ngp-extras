package ru.ifmo.ctd.ngp.demo.paramchooser.controller.agents.adaptive.distbase;

import ru.ifmo.ctd.ngp.demo.paramchooser.controller.agents.qlearn.Partition;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.agents.qlearn.QAction;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.agents.qlearn.QLogState;

/**
 * @author Arkadii Rost
 */
public class DistLogState extends QLogState {
	protected final SimplePartition[] partitions;

	public DistLogState(SimplePartition[] partitions, QAction action) {
		super(action);
		this.partitions = partitions;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(action.getActionId()).append("\t");

		for (double v : action.getParameterValues())
			sb.append(v).append("\t");

		for (Partition p : partitions)
			sb.append(p.toString());

		return sb.toString();
	}
}
