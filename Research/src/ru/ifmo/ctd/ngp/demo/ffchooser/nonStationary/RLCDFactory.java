package ru.ifmo.ctd.ngp.demo.ffchooser.nonStationary;

import ru.ifmo.ctd.ngp.demo.ffchooser.config.factory.ConfigurationFactory;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.utils.PropertiesUtils;

import java.util.Properties;

/**
 * @author Irene Petrova
 */
public class RLCDFactory implements ConfigurationFactory<RLCDConfiguration> {
    /**
     * {@inheritDoc}
     */
    @Override
    public RLCDConfiguration create(Properties properties) {
        return new RLCDConfiguration(properties);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] getKeys() {
        return PropertiesUtils.getKeysFor(RLCDConfiguration.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "rlcd";
    }

}
