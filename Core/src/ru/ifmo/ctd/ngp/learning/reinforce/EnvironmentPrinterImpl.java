package ru.ifmo.ctd.ngp.learning.reinforce;

import java.io.IOException;
import java.io.Writer;

public class EnvironmentPrinterImpl<S, A> implements EnvironmentPrinter<S, A> {
	private final Writer w;
	
	public EnvironmentPrinterImpl(Writer w) {
		this.w = w;
	}

	@Override
	public void print(Environment<S, A> env) throws IOException {
		w.append(String.format("action:\t%s\treward:\t%f\tnew state:%s\ttarget value:%f\n", 
				env.getLastAction(), env.getLastReward(), env.getCurrentState(), env.getBestTargetValue()));
		w.flush();
	}
}
