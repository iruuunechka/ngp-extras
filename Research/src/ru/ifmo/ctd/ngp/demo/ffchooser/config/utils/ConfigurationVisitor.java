package ru.ifmo.ctd.ngp.demo.ffchooser.config.utils;

import org.jetbrains.annotations.NotNull;

import ru.ifmo.ctd.ngp.demo.ffchooser.config.Configuration;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.DelayedConfiguration;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.DynaConfiguration;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.GreedyConfiguration;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.IdealConfiguration;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.NoLearnConfiguration;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.RConfiguration;
import ru.ifmo.ctd.ngp.demo.ffchooser.nonStationary.DynaWithStrategyConfiguration;
import ru.ifmo.ctd.ngp.demo.ffchooser.nonStationary.EpsQRLCDConfiguration;
import ru.ifmo.ctd.ngp.demo.ffchooser.nonStationary.PrioritizedSweepingConfiguration;
import ru.ifmo.ctd.ngp.demo.ffchooser.nonStationary.RLCDConfiguration;

/**
 * The visitor for configurations. This visitor is for those who do not want to use instanceof on
 * {@link Configuration} instances.
 *
 * <em>Warning!</em> For the subclasses of existing configurations that do not override their
 * superclass' {@link Configuration#accept(ConfigurationVisitor, Object)} method, the visitor method
 * for the superclass will be called. This behavior is to be refactored.
 *
 * @author Maxim Buzdalov
 */
public interface ConfigurationVisitor<A, R> {
    @NotNull R visitGreedyConfiguration(@NotNull GreedyConfiguration config, @NotNull A argument);
    @NotNull R visitDelayedConfiguration(@NotNull DelayedConfiguration config, @NotNull A argument);
    @NotNull R visitRConfiguration(@NotNull RConfiguration config, @NotNull A argument);
    @NotNull R visitDynaConfiguration(@NotNull DynaConfiguration config, @NotNull A argument);
    @NotNull R visitNoLearnConfiguration(@NotNull NoLearnConfiguration config, @NotNull A argument);
    @NotNull R visitIdealConfiguration(@NotNull IdealConfiguration config, @NotNull A argument);
    @NotNull R visitDynaWithStrategyConfiguration(@NotNull DynaWithStrategyConfiguration config, @NotNull A argument);
    @NotNull R visitPrioritizedSweepingConfiguration(@NotNull PrioritizedSweepingConfiguration config, @NotNull A argument);
    @NotNull R visitRLCDConfiguration(@NotNull RLCDConfiguration config, @NotNull A argument);
    @NotNull R visitEpsQRLCDConfiguration(@NotNull EpsQRLCDConfiguration config, @NotNull A argument);
}
