package ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria;

/**
 * An unmodifiable pair of an individual an a result of its evaluation ("search point" or "parameter").
 * 
 * @author Arina Buzdalova
 * 
 * @param <T> the type of an individual
 */
public class EvaluatedIndividual<T> {
	private final T individual;
	private final Parameter parameter;
	private int squeezeFactor;
	
	/**
	 * Constructs {@link EvaluatedIndividual} with the specified parameters
	 * @param individual the individual
	 * @param parameter the evaluation result for the {@code individual}
	 */
	public EvaluatedIndividual(T individual, Parameter parameter) {
		this.individual = individual;
		this.parameter = parameter;
		squeezeFactor = -1;
	}
	
	/**
	 * Gets the individual
	 * @return the individual
	 */
	public T ind() {
		return individual;
	}
	
	/**
	 * Gets the result of evaluation of the stored individual
	 * @return the result of evaluation of the individual
	 */
	public Parameter par() {
		return parameter;
	}
	
	/**
	 * Returns the squeeze factor - number of individuals that share the same hyper-box
	 * with this one.
	 * @return the squeeze factor, or {@code -1} if it is not set
	 */
	public int getSqueezeFactor() {
		return squeezeFactor;
	}
	
	void setSqueezeFactor(int factor) {
		squeezeFactor = factor;
	}
	
	void incSqueezeFactor() {
		squeezeFactor++;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((individual == null) ? 0 : individual.hashCode());
		result = prime * result
				+ ((parameter == null) ? 0 : parameter.hashCode());
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		EvaluatedIndividual<?> other = (EvaluatedIndividual<?>) obj;
		if (individual == null) {
			if (other.individual != null) {
				return false;
			}
		} else if (!individual.equals(other.individual)) {
			return false;
		}
		if (parameter == null) {
			return other.parameter == null;
		} else {
			return parameter.equals(other.parameter);
		}
	}
}
