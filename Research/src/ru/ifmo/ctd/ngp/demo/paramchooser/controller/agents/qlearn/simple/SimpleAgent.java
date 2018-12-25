package ru.ifmo.ctd.ngp.demo.paramchooser.controller.agents.qlearn.simple;

import ru.ifmo.ctd.ngp.demo.paramchooser.controller.agents.qlearn.Partition;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.State;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.agents.BaseTransition;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.agents.qlearn.QAction;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.agents.qlearn.QState;
import ru.ifmo.ctd.ngp.demo.paramchooser.controller.agents.qlearn.QTreeAgent;

import java.util.List;
import java.util.Random;

/**
 * @author Arkadii Rost
 */
public class SimpleAgent<S extends State> extends QTreeAgent<S, BaseTransition<QAction, S>, QState<S, BaseTransition<QAction, S>>> {
    private final Partition[] splits;


    public SimpleAgent(Partition[] splits, double eps, double alpha, double gamma, double actionScale, double obsResolution, double minSplitPercent,
        int splitLimit)
    {
        super(eps, alpha, gamma, actionScale, obsResolution, minSplitPercent, splitLimit);
        this.splits = splits;
    }

    @Override
    protected BaseTransition<QAction, S> createTransition(double reward, S lastState, QAction lastAction, S state) {
        return new BaseTransition<>(reward, lastState, lastAction, state);
    }

    @Override
    protected QState<S, BaseTransition<QAction, S>> createUState(Random rand, QState<S, BaseTransition<QAction, S>> prev,
        List<BaseTransition<QAction, S>> transitions)
    {
        QState<S, BaseTransition<QAction, S>> state = new QState<>(alpha, eps, splits);
        for (BaseTransition<QAction, S> transition : transitions) {
            state.updateTransitions(rand, transition);
            double actionDelta = actionDelta(transition.getReward(), state, transition.getAction(),
                    getUState(transition.getToState()));
            state.updateQ(transition.getAction(), actionDelta);
        }
        return state;
    }

    @Override
    protected void registerState(QState<S, BaseTransition<QAction, S>> state) {
    }

    @Override
    protected void deregisterState(QState<S, BaseTransition<QAction, S>> state) {
    }
}



