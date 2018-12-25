package ru.ifmo.ctd.ngp.demo.ffchooser.config.factory;

import java.util.Properties;

import ru.ifmo.ctd.ngp.demo.ffchooser.config.DynaConfiguration;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.utils.PropertiesUtils;

/**
 * {@link ConfigurationFactory} for {DelayedConfiguration}.
 * 
 * @author Arina Buzdalova
 */
public class DynaFactory implements ConfigurationFactory<DynaConfiguration> {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public DynaConfiguration create(Properties properties) {
		return new DynaConfiguration(properties);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] getKeys() {
		return PropertiesUtils.getKeysFor(DynaConfiguration.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return "dyna";
	}

}
