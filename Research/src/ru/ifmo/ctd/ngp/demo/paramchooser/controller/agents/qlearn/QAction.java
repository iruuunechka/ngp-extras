package ru.ifmo.ctd.ngp.demo.paramchooser.controller.agents.qlearn;

import ru.ifmo.ctd.ngp.demo.paramchooser.controller.agents.BaseAction;

import java.util.Arrays;

/**
 * @author Arkadii Rost
 */
public class QAction extends BaseAction {
    private final int actionId;

    public QAction(int actionId, double[] parameterValues) {
        super(parameterValues);
        this.actionId = actionId;
    }

    public int getActionId() {
        return actionId;
    }

	@Override
	public String toString() {
		return "Action: " + actionId + " values: " + Arrays.toString(getParameterValues());
	}
}