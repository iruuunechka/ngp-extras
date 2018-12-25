package ru.ifmo.ctd.ngp.demo.ffchooser.config.factory;

import java.util.Properties;

import ru.ifmo.ctd.ngp.demo.ffchooser.config.Configuration;

/**
 * Interface for "factories" that construct <code>{@link Configuration}s</code>
 * of different types and provide with their keys and names.
 * 
 * @author Arina Buzdalova
 * 
 * @param <C> type of configuration constructed by this factory
 * @see IdealFactory
 * @see NoLearnFactory
 * @see GreedyFactory
 * @see DelayedFactory
 */
public interface ConfigurationFactory<C extends Configuration> {
	/**
	 * Creates configuration from the specified properties
	 * @param properties the specified properties
	 * @return configuration described in <code>properties</code>
	 */
	C create(Properties properties);
	
	/**
	 * Returns the name of this factory
	 * @return the name of this factory
	 */
	String getName();
	
	/**
	 * Returns the keys corresponding to the parameters of
	 * the configurations created by this factory. 
	 * These keys are used in properties to describe configurations.
	 * @return the keys corresponding to the parameters of the configuration
	 */
	String[] getKeys();
}
