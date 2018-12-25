package ru.ifmo.ctd.ngp.demo.ffchooser.gradient;

import java.util.Properties;

import ru.ifmo.ctd.ngp.demo.ffchooser.config.DelayedConfiguration;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.LearnConfiguration;

/**
 * {@link DelayedConfiguration} factory.
 * 
 * @author Arina Buzdalova
 */
@SuppressWarnings("UnusedDeclaration")
public class GradientDelayedFactory implements GradientConfigFactory {

	/**
	 * {@inheritDoc}
	 * The elements of the <code>learnParameters</code> vector
	 * should specify period, bonus and discount respectively
	 */
	@Override
	public LearnConfiguration create(Properties basicConfig,
			Vector learnParameters) {
		basicConfig.setProperty("period", String.format("%f", 1000 * learnParameters.get(0)));
		basicConfig.setProperty("bonus", String.format("%f", learnParameters.get(1)));
		basicConfig.setProperty("factor", String.format("%f", learnParameters.get(2)));
		return new DelayedConfiguration(basicConfig);
	}
}
