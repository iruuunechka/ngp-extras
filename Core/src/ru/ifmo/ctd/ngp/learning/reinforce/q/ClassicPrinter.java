package ru.ifmo.ctd.ngp.learning.reinforce.q;

import ru.ifmo.ctd.ngp.learning.reinforce.AgentPrinter;
import ru.ifmo.ctd.ngp.learning.reinforce.Environment;
import ru.ifmo.ctd.ngp.learning.util.Maps;
import ru.ifmo.ctd.ngp.learning.util.PrintFormatter;

import java.io.IOException;
import java.io.Writer;

/**
 * Printer that prints information about {@link ClassicQAgent}.
 * 
 * @author Arina Buzdalova
 * 
 * @param <S> the type of a state
 * @param <A> the type of an action
 */
public class ClassicPrinter<S, A> implements AgentPrinter<S, A, ClassicQAgent<S, A>> {
	private final Writer writer;
	
	/**
	 * Constructs the {@link ClassicPrinter} with the specified writer
	 * @param writer the specified writer
	 */
	private ClassicPrinter(Writer writer) {
		this.writer = writer;
	}
	
	/**
	 * Creates this printer with the specified writer and adds it to the 
	 * specified agent
	 * @param agent the specified agent
	 * @param writer the specified writer
	 */
	public static<S, A> void addTo(ClassicQAgent<S, A> agent, Writer writer) {
		agent.addPrinter(new ClassicPrinter<>(writer));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void print(ClassicQAgent<S, A> agent, Environment<? extends S, ? extends A> environment) {
		try {
			writer.append(String.valueOf(agent.getSteps()));		
			writer.append(" action: ");
			writer.append(String.valueOf(agent.getLastAction()));
			writer.append(" state: ");
			writer.append(String.valueOf(environment.getCurrentState()));
			writer.append("\nQ:\n");
			writer.append(Maps.format(agent.getQ(), environment.getActions(), DOUBLE_0p3));
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

    private static final PrintFormatter<Double> DOUBLE_0p3 = value -> String.format("%.3f ", value);
}
