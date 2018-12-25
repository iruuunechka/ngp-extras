package ru.ifmo.ctd.ngp.demo.ffchooser.config.factory;

import java.util.Properties;

import ru.ifmo.ctd.ngp.demo.ffchooser.config.IdealConfiguration;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.utils.PropertiesUtils;

/**
 * {@link ConfigurationFactory} for {IdealConfiguration}.
 * 
 * @author Arina Buzdalova
 */
public class IdealFactory implements ConfigurationFactory<IdealConfiguration> {	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IdealConfiguration create(Properties properties) {
		return new IdealConfiguration(properties);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] getKeys() {
		return PropertiesUtils.getKeysFor(IdealConfiguration.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return "ideal";
	}

}
