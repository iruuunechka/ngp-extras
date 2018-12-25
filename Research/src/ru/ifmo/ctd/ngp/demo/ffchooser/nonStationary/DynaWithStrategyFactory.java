package ru.ifmo.ctd.ngp.demo.ffchooser.nonStationary;

import ru.ifmo.ctd.ngp.demo.ffchooser.config.factory.ConfigurationFactory;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.utils.PropertiesUtils;

import java.util.Properties;

/**
 * @author Irene Petrova
 */
public class DynaWithStrategyFactory implements ConfigurationFactory<DynaWithStrategyConfiguration> {
    /**
     * {@inheritDoc}
     */
    @Override
    public DynaWithStrategyConfiguration create(Properties properties) {
        return new DynaWithStrategyConfiguration(properties);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] getKeys() {
        return PropertiesUtils.getKeysFor(DynaWithStrategyConfiguration.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "dynaWithStrategy";
    }

}
