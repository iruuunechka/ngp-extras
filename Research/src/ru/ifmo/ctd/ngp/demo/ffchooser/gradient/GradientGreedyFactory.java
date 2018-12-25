package ru.ifmo.ctd.ngp.demo.ffchooser.gradient;

import java.util.Properties;

import ru.ifmo.ctd.ngp.demo.ffchooser.config.GreedyConfiguration;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.LearnConfiguration;

/**
 * {@link GreedyConfiguration} factory.
 * 
 * @author Arina Buzdalova
 */
public class GradientGreedyFactory implements GradientConfigFactory {

	/**
	 * {@inheritDoc}
	 * The elements of the <code>learnParameters</code> vector
	 * should specify exploration probability (epsilon), learn speed (alpha) and discount factor (gamma) respectively
	 */
	@Override
	public LearnConfiguration create(Properties basicConfig,
			Vector learnParameters) {
		basicConfig.setProperty("epsilon", String.format("%f", learnParameters.get(0)));
		basicConfig.setProperty("alpha", String.format("%f", learnParameters.get(1)));
		basicConfig.setProperty("gamma", String.format("%f", learnParameters.get(2)));
		return new GreedyConfiguration(basicConfig);
	}
}
