package ru.ifmo.ctd.ngp.demo.ffchooser.utils;

import java.util.Properties;

/**
 * Set of utilities for working with number of configuration runs
 * 
 * @author Arina Buzdalova
 *
 */
public class TimesUtils {
	private TimesUtils() {}
	
	/**
	 * Retrieves number of configuration runs from the specified properties.
	 * The properties should contain key {@code times}.
	 * @param properties the specified properties
	 * @return number of runs
	 */
	public static int getTimes(Properties properties) {
		return Integer.parseInt(properties.getProperty("times"));
	}
}
