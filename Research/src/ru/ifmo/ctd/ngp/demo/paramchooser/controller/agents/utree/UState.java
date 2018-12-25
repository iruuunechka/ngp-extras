package ru.ifmo.ctd.ngp.demo.paramchooser.controller.agents.utree;

import ru.ifmo.ctd.ngp.demo.paramchooser.controller.Action;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.State;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.Transition;

import java.util.Collection;
import java.util.Random;

/**
 * @author Arkadii Rost
 */
public interface UState<A extends Action, S extends State,
        T extends Transition<A, S>>
{
    double getEffectiveQ();
    Collection<T> getTransitions();
    void updateTransitions(Random rand, T newTransition);
    A get(Random rand);
}
