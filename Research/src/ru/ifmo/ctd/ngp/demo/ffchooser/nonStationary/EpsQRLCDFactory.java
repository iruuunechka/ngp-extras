package ru.ifmo.ctd.ngp.demo.ffchooser.nonStationary;

import ru.ifmo.ctd.ngp.demo.ffchooser.config.factory.ConfigurationFactory;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.utils.PropertiesUtils;

import java.util.Properties;

/**
 * @author Irene Petrova
 */
public class EpsQRLCDFactory implements ConfigurationFactory<EpsQRLCDConfiguration> {
    /**
     * {@inheritDoc}
     */
    @Override
    public EpsQRLCDConfiguration create(Properties properties) {
        return new EpsQRLCDConfiguration(properties);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] getKeys() {
        return PropertiesUtils.getKeysFor(EpsQRLCDConfiguration.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "eqrlcd";
    }
}
