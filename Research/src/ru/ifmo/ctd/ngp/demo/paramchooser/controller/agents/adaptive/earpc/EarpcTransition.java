package ru.ifmo.ctd.ngp.demo.paramchooser.controller.agents.adaptive.earpc;


import org.apache.commons.math3.ml.clustering.Clusterable;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.Action;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.State;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.agents.BaseTransition;

/**
 * @author Arkadii Rost
 */
public abstract class EarpcTransition<A extends Action, S extends State> extends BaseTransition<A, S>
        implements Clusterable
{
    public EarpcTransition(double reward, S fromState, A action, S toState) {
        super(reward, fromState, action, toState);
    }
}
