package ru.ifmo.ctd.ngp.demo.ffchooser.gradient;

import java.util.Properties;

import ru.ifmo.ctd.ngp.demo.ffchooser.config.LearnConfiguration;

/**
 * Interface for {@link LearnConfiguration} factories
 * used in {@link LearningFunction}
 * 
 * @author Arina Buzdalova
 */
public interface GradientConfigFactory {
	/**
	 * Creates configuration with the parameters specified by the given
	 * basic configuration and the learning parameters specified separately
	 * @param basicConfig the basic configuration that determines all parameters but learning ones
	 * @param learnParameters the specified learning parameters
	 * @return configuration with the <code>learnParameters</code>
	 */
	LearnConfiguration create(Properties basicConfig, Vector learnParameters);
}
