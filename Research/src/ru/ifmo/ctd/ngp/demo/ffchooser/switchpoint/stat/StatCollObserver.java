package ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.stat;

import ru.ifmo.ctd.ngp.demo.ffchooser.config.Configuration;

import java.io.Serializable;

/**
 * Observer for the {@link StatisticsCollector}.
 * 
 * @author Arina Buzdalova
 */
public interface StatCollObserver extends Serializable {
	
	/**
     * Updates the percentage of evaluated generations for the specified configuration
     * @param config the specified configuration
     * @param completed the percentage of evaluated generations for the {@code config}
     */
    void update(Configuration config, double completed);
}
