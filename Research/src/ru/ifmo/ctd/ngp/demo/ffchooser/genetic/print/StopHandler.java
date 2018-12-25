package ru.ifmo.ctd.ngp.demo.ffchooser.genetic.print;

/**
 * Handles the information about best FF values 
 * in the certain generation
 * 
 * @author Arina Buzdalova
 */
public interface StopHandler {
	
	/**
	 * Handles the information about best FF values 
	 * in the specified generation
	 * @param generation the specified generation
	 * @param value the FF-value of the best individual
	 */
    void handle(int generation, double value);
}
