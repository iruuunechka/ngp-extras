package ru.ifmo.ctd.ngp.demo.ffchooser.config.factory;

import java.util.Properties;

import ru.ifmo.ctd.ngp.demo.ffchooser.config.GreedyConfiguration;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.utils.PropertiesUtils;

/**
 * {@link ConfigurationFactory} for {GreedyConfiguration}.
 * 
 * @author Arina Buzdalova
 */
public class GreedyFactory implements ConfigurationFactory<GreedyConfiguration> {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public GreedyConfiguration create(Properties properties) {
		return new GreedyConfiguration(properties);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] getKeys() {
		return PropertiesUtils.getKeysFor(GreedyConfiguration.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return "greedy";
	}

}
