package ru.ifmo.ctd.ngp.demo.ffchooser.nonStationary;

import ru.ifmo.ctd.ngp.demo.ffchooser.config.factory.ConfigurationFactory;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.utils.PropertiesUtils;

import java.util.Properties;

/**
 * @author Irene Petrova
 */
public class PrioritizedSweepingFactory implements ConfigurationFactory<PrioritizedSweepingConfiguration> {
    /**
     * {@inheritDoc}
     */
    @Override
    public PrioritizedSweepingConfiguration create(Properties properties) {
            return new PrioritizedSweepingConfiguration(properties);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] getKeys() {
            return PropertiesUtils.getKeysFor(PrioritizedSweepingConfiguration.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
            return "prioritizedSweeping";
    }

}

