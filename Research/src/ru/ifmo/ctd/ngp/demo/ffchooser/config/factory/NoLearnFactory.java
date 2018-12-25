package ru.ifmo.ctd.ngp.demo.ffchooser.config.factory;

import java.util.Properties;

import ru.ifmo.ctd.ngp.demo.ffchooser.config.NoLearnConfiguration;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.utils.PropertiesUtils;

/**
 * {@link ConfigurationFactory} for {IdealConfiguration}.
 * 
 * @author Arina Buzdalova
 */
public class NoLearnFactory implements ConfigurationFactory<NoLearnConfiguration> {
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public NoLearnConfiguration create(Properties properties) {
		return new NoLearnConfiguration(properties);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] getKeys() {
		return PropertiesUtils.getKeysFor(NoLearnConfiguration.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return "none";
	}

}
