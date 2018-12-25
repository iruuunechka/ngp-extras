package ru.ifmo.ctd.ngp.demo.ffchooser.nonStationary.TSP;

import ru.ifmo.ctd.ngp.learning.reinforce.Environment;
import ru.ifmo.ctd.ngp.learning.reinforce.EnvironmentPrinter;

import java.io.IOException;
import java.io.Writer;

/**
 * @author Irene Petrova
 */
public class TSPPrinterImpl<S, A> implements EnvironmentPrinter<S, A> {
    private final Writer w;
    private final Double max;

    public TSPPrinterImpl(Writer w, Double max) {
        this.max = max;
        this.w = w;
    }

    @Override
    public void print(Environment<S, A> env) throws IOException {
        w.append(String.format("action:\t%s\treward:\t%f\tnew state:%s\ttarget value:%f\n",
        env.getLastAction(), env.getLastReward(), env.getCurrentState(), max - env.getBestTargetValue()));
        w.flush();
    }
}
