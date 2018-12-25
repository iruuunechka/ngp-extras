package ru.ifmo.ctd.ngp.demo.ffchooser.vfs;

import ru.ifmo.ctd.ngp.demo.ffchooser.config.Configuration;

/**
 * An interface for path generators.
 *
 * @author Maxim Buzdalov
 */
public interface PathGenerator {
    /**
     * Generates path for the given configuration.
     * @param configuration the configuration to generate path for.
     * @return the path.
     */
    String path(Configuration configuration);
}
