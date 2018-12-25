package ru.ifmo.ctd.ngp.demo.ffchooser.gradient;

/**
 * Imprecise {@link MultiFunction}
 * 
 * @author Arina Buzdalova
 */
public interface ImpreciseFunction extends MultiFunction {

	/**
	 * Calculates error of this function at the specified point
	 * @param v the specified point
	 * @return error of this function at the point <code>v</code>
	 */
	double error(Vector v);
}
