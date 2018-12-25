package ru.ifmo.ctd.ngp.learning.reinforce.q;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import ru.ifmo.ctd.ngp.learning.reinforce.AgentPrinter;
import ru.ifmo.ctd.ngp.learning.reinforce.Environment;

/**
 * Printer that prints information about {@link ClassicQAgent}
 * to the specified file
 * 
 * @author Arina Buzdalova
 * 
 * @param <S> the type of a state
 * @param <A> the type of an action
 */
@SuppressWarnings("UnusedDeclaration")
public class ClassicFilePrinter<S, A> implements AgentPrinter<S, A, ClassicQAgent<S, A>> {
	private PrintWriter printer;
	
	/**
     * Constructs {@link ClassicFilePrinter} with the specified
     * file name. Newly created file will be named
     * {@code fileName-i}, where {@code i}
     * is the number of files with the same file name.
     * @param fileName the specified file name
     */
	private ClassicFilePrinter(String fileName) {
		for (int i = 0; ; i++) {
			File f = new File(String.format("%s-%02d", fileName, i));
			if (!f.exists()) {
				try {
					printer = new PrintWriter(f);
				} catch (FileNotFoundException e) {
					throw new RuntimeException(e);
				}
				break;
			}
		}
	}
	
	/**
	 * Creates the printer with the specified log-name prefix and adds it to the specified agent.
	 * @param agent the specified agent
	 * @param fileName the prefix for log-names, log-names will be {@code fileName-i}
	 */
	public static <S, A> void addTo(ClassicQAgent<S, A> agent, String fileName) {
		agent.addPrinter(new ClassicFilePrinter<>(fileName));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void print(ClassicQAgent<S, A> agent, Environment<? extends S, ? extends A> environment) {
		int step = agent.getSteps();
		if (step == 0) {
			printer.println("------------Start of learning-------------");
		}
		printer.println(step + ")" + " action taken: " + agent.getLastAction());
		printer.println("Q:");
		printer.println(agent.getQ());
		printer.flush();
	}

}
