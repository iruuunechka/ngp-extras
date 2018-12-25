package ru.ifmo.ctd.ngp.demo.paramchooser.controller.agents.qlearn;

/**
 * @author Arkadii Rost
 */
public class QLogState {
	protected final QAction action;

	public QLogState(QAction action) {
		this.action = action;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(action.getActionId()).append("\t");

		for (double v : action.getParameterValues())
			sb.append(v).append("\t");

		return sb.toString();
	}
}
