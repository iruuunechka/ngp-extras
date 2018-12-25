package ru.ifmo.ctd.ngp.demo.ffchooser.vfs;

import org.jetbrains.annotations.NotNull;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.Configuration;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.DelayedConfiguration;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.DynaConfiguration;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.GreedyConfiguration;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.IdealConfiguration;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.NoLearnConfiguration;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.RConfiguration;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.utils.ConfigurationVisitor;
import ru.ifmo.ctd.ngp.demo.ffchooser.nonStationary.DynaWithStrategyConfiguration;
import ru.ifmo.ctd.ngp.demo.ffchooser.nonStationary.EpsQRLCDConfiguration;
import ru.ifmo.ctd.ngp.demo.ffchooser.nonStationary.PrioritizedSweepingConfiguration;
import ru.ifmo.ctd.ngp.demo.ffchooser.nonStationary.RLCDConfiguration;
import ru.ifmo.ctd.ngp.demo.util.Nil;

import java.util.Locale;

/**
 * An implementation of configuration path generator, which behaves identical to
 * the old {@link ru.ifmo.ctd.ngp.demo.ffchooser.config.Configuration#generateFullName()} way.
 *
 * @author Maxim Buzdalov
 */
public final class OldPathGenerator implements PathGenerator {
    private OldPathGenerator() {}

    private static final ConfigurationVisitor<Nil, String> visitor = new ConfigurationVisitor<Nil, String>() {
        @NotNull
        @Override
        public String visitNoLearnConfiguration(@NotNull NoLearnConfiguration config, @NotNull Nil argument) {
            return String.format(Locale.US, "len%d%s/mutation%scrossover%s/div%s",
                    config.getLength(), config.getLabel(), config.getMutation(), config.getCrossover(),
                    config.getDivider());
        }

        @NotNull
        @Override
        public String visitGreedyConfiguration(@NotNull GreedyConfiguration config, @NotNull Nil argument) {
            return String.format(Locale.US, "len%d%s/mutation%scrossover%s/div%spoint%s-%s-%s-%s/eps%salpha%sgamma%s",
            		config.getLength(), config.getLabel(), config.getMutation(), config.getCrossover(),
                    config.getDivider(), config.getPoint(), 
                    config.getEvaluators().getName(), config.getReward().getName(), config.getState().getName(),
                    config.getEpsilon(), config.getAlpha(), config.getGamma());
        }

        @NotNull
        @Override
        public String visitDelayedConfiguration(@NotNull DelayedConfiguration config, @NotNull Nil argument) {
            return String.format("len%d%s/mutation%scrossover%s/div%spoint%s-%s-%s-%s/period%sbonus%sdiscount%s",
            		config.getLength(), config.getLabel(), config.getMutation(), config.getCrossover(),
                    config.getDivider(), config.getPoint(), 
                    config.getEvaluators().getName(), config.getReward().getName(), config.getState().getName(),
                    config.getPeriod(), config.getBonus(), config.getFactor());
        }
        

        @NotNull
        @Override
        public String visitRConfiguration(@NotNull RConfiguration config, @NotNull Nil argument) {
            return String.format("len%d%s/mutation%scrossover%s/div%spoint%s-%s-%s-%s/avrate%srrate%sexpl%s",
            		config.getLength(), config.getLabel(), config.getMutation(), config.getCrossover(),
                    config.getDivider(), config.getPoint(), 
                    config.getEvaluators().getName(), config.getReward().getName(), config.getState().getName(),
                    config.getAvrate(), config.getRrate(), config.getExpl());
        }

        @NotNull
        @Override
        public String visitDynaConfiguration(@NotNull DynaConfiguration config, @NotNull Nil argument) {
            return String.format("len%d%s/mutation%scrossover%s/div%spoint%s-%s-%s-%s/probability%sdiscount%sk%d",
                    config.getLength(), config.getLabel(), config.getMutation(), config.getCrossover(),
                    config.getDivider(), config.getPoint(),
                    config.getEvaluators().getName(), config.getReward().getName(), config.getState().getName(),
                    config.getProbability(), config.getDiscount(), config.getK());
        }

        @NotNull
        @Override
        public String visitDynaWithStrategyConfiguration(@NotNull DynaWithStrategyConfiguration config, @NotNull Nil argument) {
            return String.format("len%d%s/mutation%scrossover%s/div%spoint%s-%s-%s-%s/probability%sdiscount%sk%d",
            		config.getLength(), config.getLabel(), config.getMutation(), config.getCrossover(),
                    config.getDivider(), config.getPoint(), 
                    config.getEvaluators().getName(), config.getReward().getName(), config.getState().getName(),
                    config.getProbability(), config.getDiscount(), config.getK());
        }

        @NotNull
        @Override
        public String visitPrioritizedSweepingConfiguration(@NotNull PrioritizedSweepingConfiguration config, @NotNull Nil argument) {
            return String.format("len%d%s/mutation%scrossover%s/div%spoint%s-%s-%s-%s/probability%sdiscount%sk%d",
                    config.getLength(), config.getLabel(), config.getMutation(), config.getCrossover(),
                    config.getDivider(), config.getPoint(),
                    config.getEvaluators().getName(), config.getReward().getName(), config.getState().getName(),
                    config.getProbability(), config.getDiscount(), config.getK());
        }

        @NotNull
        @Override
        public String visitRLCDConfiguration(@NotNull RLCDConfiguration config, @NotNull Nil argument) {
            return String.format("len%d%s/mutation%scrossover%s/div%spoint%s-%s-%s-%s/probability%sdiscount%sk%domega%srho%s",
                    config.getLength(), config.getLabel(), config.getMutation(), config.getCrossover(),
                    config.getDivider(), config.getPoint(),
                    config.getEvaluators().getName(), config.getReward().getName(), config.getState().getName(),
                    config.getProbability(), config.getDiscount(), config.getK(), config.getOmega(), config.getRho());
        }

        @NotNull
        @Override
        public String visitEpsQRLCDConfiguration(@NotNull EpsQRLCDConfiguration config, @NotNull Nil argument) {
            return String.format("len%d%s/mutation%scrossover%s/div%spoint%s-%s-%s-%s/alpha%sgamma%seps%smindiff%s",
                    config.getLength(), config.getLabel(), config.getMutation(), config.getCrossover(),
                    config.getDivider(), config.getPoint(),
                    config.getEvaluators().getName(), config.getReward().getName(), config.getState().getName(),
                    config.getAlpha(), config.getGamma(), config.getEpsq(), config.getMindiff());
        }

        @NotNull
        @Override
		public String visitIdealConfiguration(@NotNull IdealConfiguration config,
				@NotNull Nil argument) {
			return String.format(Locale.US, "len%d%s/mutation%scrossover%s",
                    config.getLength(), config.getLabel(), config.getMutation(), config.getCrossover());
		}
    };

    private static final OldPathGenerator instance = new OldPathGenerator();

    /**
     * Returns the instance of {@link OldPathGenerator}
     * @return the instance of {@link OldPathGenerator}
     */
    public static OldPathGenerator instance() {
        return instance;
    }

    /**
     * Generates the path for the given configuration.
     * @param config the configuration.
     * @return the path.
     */
    public String path(Configuration config) {
        return config.accept(visitor, Nil.value());
    }

}
