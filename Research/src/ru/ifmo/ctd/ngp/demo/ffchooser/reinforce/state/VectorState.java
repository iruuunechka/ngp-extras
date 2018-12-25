package ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.state;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.OptAlgEnvironment;
import ru.ifmo.ctd.ngp.demo.ffchooser.utils.ObjectWithValue;
import ru.ifmo.ctd.ngp.demo.util.textconstructor.ParamDef;

/**
 * Calculates state as a vector of FF's sorted by 
 * <code>(x<sub>current</sub> - x<sub>previous</sub>) / x<sub>current</sub></code>.
 * Allows to specify the vector's length. It should be less or equal to the number of fitness evaluators.
 * 
 * @author Arina Buzdalova
 */
public class VectorState implements StateCalculator<String, Integer> {	

	private static final long serialVersionUID = 2536535932002352573L;
	private final int length;
	
	/**
	 * Constructs {@link VectorState} with the specified length of the vector.
	 * The length should be equal or less than the number of fitness evaluators.
	 * If it is less, first <code>length</code> FFs will be taken after sorting by
	 * <code>(x<sub>current</sub> - x<sub>previous</sub>) / x<sub>current</sub></code>.
	 * @param length the specified length of the vector
	 */
	public VectorState(@ParamDef(name = "length") int length) {
		this.length = length;
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String calculate(OptAlgEnvironment<String, Integer> environment) {
		List<Double> lastValues = environment.getAlgorithm().getCurrentBest();
		List<Integer> actions = environment.getActions();
		
		if (length > lastValues.size()) {
			throw new IllegalArgumentException("Length of the vector doesn't fit the number of fitness evaluators.");
		}
		
		StringBuilder rv = new StringBuilder();
		ArrayList<ObjectWithValue<Integer>> list = new ArrayList<>();
		for (int i : actions) {			
			list.add(new ObjectWithValue<>((lastValues.get(i) - environment.getPrevValues().get(i)) / lastValues.get(i), i));
		}
		Collections.sort(list);
		
		for (int i = 0; i < length; i++) {
			rv.append(list.get(i).getObject());
		}
		return rv.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return String.format("vector%d", length);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + length;
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VectorState other = (VectorState) obj;
        return length == other.length;
    }
}
