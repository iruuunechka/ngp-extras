package ru.ifmo.ctd.ngp.demo.ffchooser.config.factory;

import java.util.Properties;

import ru.ifmo.ctd.ngp.demo.ffchooser.config.RConfiguration;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.utils.PropertiesUtils;

/**
 * {@link ConfigurationFactory} for {DelayedConfiguration}.
 * 
 * @author Arina Buzdalova
 */
public class RFactory implements ConfigurationFactory<RConfiguration> {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public RConfiguration create(Properties properties) {
		return new RConfiguration(properties);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] getKeys() {
		return PropertiesUtils.getKeysFor(RConfiguration.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return "average";
	}

}
