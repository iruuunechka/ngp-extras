package ru.ifmo.ctd.ngp.demo.ffchooser.config.factory;

import java.util.Properties;

import ru.ifmo.ctd.ngp.demo.ffchooser.config.DelayedConfiguration;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.utils.PropertiesUtils;

/**
 * {@link ConfigurationFactory} for {DelayedConfiguration}.
 * 
 * @author Arina Buzdalova
 */
public class DelayedFactory implements ConfigurationFactory<DelayedConfiguration> {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public DelayedConfiguration create(Properties properties) {
		return new DelayedConfiguration(properties);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] getKeys() {
		return PropertiesUtils.getKeysFor(DelayedConfiguration.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return "delayed";
	}

}
