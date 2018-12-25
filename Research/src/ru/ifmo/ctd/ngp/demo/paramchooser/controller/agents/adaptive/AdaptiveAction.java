package ru.ifmo.ctd.ngp.demo.paramchooser.controller.agents.adaptive;

import ru.ifmo.ctd.ngp.demo.paramchooser.controller.Bounded;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.agents.BaseAction;

import java.util.Arrays;

/**
 * @author Arkadii Rost
 */
public class AdaptiveAction extends BaseAction {
    private final Bounded[] bounds;

    public AdaptiveAction(Bounded[] bounds, double[] parameterValues) {
        super(parameterValues);
        this.bounds = bounds;
    }

    public Bounded[] getBounds() {
        return bounds;
    }

	@Override
	public String toString() {
		return "Bounds :" + Arrays.toString(bounds) + " values: " + Arrays.toString(getParameterValues());
	}
}
