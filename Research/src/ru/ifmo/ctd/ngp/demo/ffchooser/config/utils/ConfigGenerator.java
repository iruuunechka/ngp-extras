package ru.ifmo.ctd.ngp.demo.ffchooser.config.utils;

import ru.ifmo.ctd.ngp.demo.ffchooser.config.Configuration;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.factory.ConfigurationFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Set of methods that generate different collections of <code>{@link Configuration}s</code>.
 *  
 * @author Arina Buzdalova
 */
public class ConfigGenerator {
	
	/**
	 * Prints number of configurations corresponding to the specified properties file
	 * @param args args[0] -- path to the properties file
	 * @throws IOException if an I/O exception occurs
	 */
	public static void main(String[] args) throws IOException {
		System.out.println(generate(args[0]).size());
	}	
	
	/**
	 * Generates collection of configurations described in the specified properties
	 * @param collectionPath path to the properties file
	 * @return the collection of configurations that corresponds to the <code>properties</code>
	 * @throws IOException if the <code>propertiesPath</code> doesn't exist or if some other I/O error occurs
	 */
	public static Collection<Configuration> generate(String collectionPath) throws IOException {
		Properties p = new Properties();
        try (FileReader fr = new FileReader(new File(collectionPath))) {
            p.load(fr);
        }
		return generate(p);
	}
	
	/**
	 * Generates collection of configurations described in the specified properties
	 * @param collection the specified properties
	 * @return the collection of configurations that corresponds to the <code>properties</code>
	 */
	public static Collection<Configuration> generate(Properties collection) {		
		PropertiesArrayReader reader = new PropertiesArrayReader(collection);
		List<Configuration> configs = new ArrayList<>();
		
		String[] lengths = reader.getValues("length");
		String[] points = reader.getStrings("point");
		
		if (lengths.length != points.length) {
			throw new IllegalArgumentException("Points are not in accord with lengths in the property file.");
		}
		
		for (String name : reader.getValues("mode")) {			
			for (int i = 0; i < lengths.length; i++) {
				Properties p = new Properties(collection);
				p.setProperty("length", lengths[i]);
				p.setProperty("point", points[i]);
				configs.addAll(genForName(name, p));
			}
		}
		return configs;
	}
	
	private static Collection<Configuration> genForName(String name, Properties collection) {	
		PropertiesArrayReader reader = new PropertiesArrayReader(collection);
		ConfigurationFactory<?> factory = ConfigUtils.getFactories().get(name);
		List<Properties> props = new ArrayList<>();
		String[] keys = factory.getKeys();
		
		for (String value : reader.getValues(keys[0])) {
			Properties p = new Properties();
			p.setProperty(keys[0], value);
			props.add(p);
		}
		
		for (int i = 1; i < keys.length; i++) {
			String key = keys[i];
			String[] values = reader.getValues(key);
			List<Properties> addition = new ArrayList<>();
			
			for (Properties p : props) {
				p.setProperty(key, values[0]);
				for (int j = 1; j < values.length; j++) {
					Properties newP = new Properties(p);
					newP.setProperty(key, values[j]);
					addition.add(newP);
				}
			}			
			props.addAll(addition);
		}

		return props.stream().map(factory::create).collect(Collectors.toList());
	}
}
