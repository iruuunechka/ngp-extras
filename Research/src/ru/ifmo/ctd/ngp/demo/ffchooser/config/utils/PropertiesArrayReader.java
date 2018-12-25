package ru.ifmo.ctd.ngp.demo.ffchooser.config.utils;

import java.util.Properties;

/**
 * Reader of {@link Properties} entity in which one property
 * may have several values separated by spaces or commas
 * 
 * @author Arina Buzdalova
 */
public class PropertiesArrayReader {
	private final Properties properties;
	
	/**
	 * Constructs this {@link PropertiesArrayReader} with the 
	 * specified {@link Properties} object
	 * @param properties the specified {@link Properties} object
	 */
	public PropertiesArrayReader(Properties properties) {
		this.properties = properties;
	}
	
	/**
	 * Gets the integer value associated with the specified property name
	 * @param name the specified property name
	 * @return the integer value associated with <code>name</code>
	 */
	public int getInt(String name) {
		return Integer.parseInt(properties.getProperty(name));
	}
	
	/**
	 * Gets the array of string values associated with the specified property name
	 * @param name the specified property name
	 * @return the array of string values associated with the <code>name</code>
	 */
	public String[] getValues(String name) {
		return properties.getProperty(name).split("[ |;]+");
	}
	
	/**
	 * Reads values corresponding to the specified property into an array of Strings
	 * separated in the property file by commas.
	 * @param name the specified property
	 * @return two dimensional array of <code>name</code>'s values
	 */
	public String[] getStrings(String name) {
		return properties.getProperty(name).split("; ");
	}
	
	/**
	 * Gets the array of integer values associated with the specified property name
	 * @param name the specified property name
	 * @return the array of integer values associated with the <code>name</code>
	 */
	public int[] getIntValues(String name) {
		String[] sArray = getValues(name);
		int[] iArray = new int[sArray.length];
		for (int i = 0; i < sArray.length; i++) {
			iArray[i] = Integer.parseInt(sArray[i]);
		}
		return iArray;
	}
	
	/**
	 * Gets the array of double values associated with the specified property name
	 * @param name the specified property name
	 * @return the array of double values associated with the <code>name</code>
	 */
	public double[] getDoubleValues(String name) {
		String[] sArray = getValues(name);
		double[] dArray = new double[sArray.length];
		for (int i = 0; i < sArray.length; i++) {
			dArray[i] = Double.parseDouble(sArray[i]);
		}
		return dArray;
	}
	
	/**
	 * Reads values corresponding to the specified property in a two dimensional array.
	 * The "rows" should be separated in the property file by commas.
	 * @param name the specified property
	 * @return two dimensional array of <code>name</code>'s values
	 */
	public String[][] getTwoDimensional(String name) {
		String[] rows = properties.getProperty(name).split("; ");
		String[][] array = new String[rows.length][];
		for (int i = 0; i < rows.length; i++) {
			String[] row = rows[i].split(" ");
			array[i] = new String[row.length];
            System.arraycopy(row, 0, array[i], 0, row.length);
		}
		return array;
	}
	
	/**
	 * Reads values corresponding to the specified property in a two dimensional integer array.
	 * The "rows" should be separated in the property file by commas.
	 * @param name the specified property
	 * @return two dimensional integer array of <code>name</code>'s values
	 */
	public int[][] getIntTwoDimensional(String name) {
		String[][] sArray = getTwoDimensional(name);
		int[][] iArray = new int[sArray.length][];
		for (int i = 0; i < sArray.length; i++) {
			iArray[i] = new int[sArray[i].length];
			for (int j = 0; j < sArray[i].length; j++) {
				iArray[i][j] = Integer.parseInt(sArray[i][j]);
			}
		}
		return iArray;
	}
}
