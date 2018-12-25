package ru.ifmo.ctd.ngp.demo.ffchooser.config.utils;

import org.junit.Assert;
import org.junit.Test;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.DelayedConfiguration;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.GreedyConfiguration;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.IdealConfiguration;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.NoLearnConfiguration;

import java.util.Arrays;

/**
 * Tests for existing configurations.
 *
 * @author Maxim Buzdalov
 */
public class KeysGeneratorTest {
    private final static String[] DELAYED_KEYS = {
            "steps", "length", "crossover", "mutation", "point", "divider", "period",
            "bonus", "factor", "evaluators", "reward", "state", "gensize", "elite"
    };
    private final static String[] GREEDY_KEYS = {
            "steps", "length", "crossover", "mutation", "point", "divider", "epsilon",
            "alpha", "gamma", "evaluators", "reward", "state", "gensize", "elite"
    };
    //!!!! [MaxBuzz] "evaluators" added to two arrays below. I'm not sure this is a correct fix.
    private final static String[] IDEAL_KEYS = {
            "steps", "evaluators", "length", "crossover", "mutation", "gensize", "elite"
    };
    private final static String[] NO_LEARN_KEYS = {
            "steps", "evaluators", "length", "crossover", "mutation", "divider", "gensize", "elite"
    };

    private void compare(String[] a, String[] b) {
        a = a.clone();
        b = b.clone();
        Arrays.sort(a);
        Arrays.sort(b);
        Assert.assertArrayEquals("found: " + Arrays.toString(b), a, b);
    }

    @Test
    public void testDelayed() {
        compare(DELAYED_KEYS, PropertiesUtils.getKeysFor(DelayedConfiguration.class));
    }

    @Test
    public void testGreedy() {
        compare(GREEDY_KEYS, PropertiesUtils.getKeysFor(GreedyConfiguration.class));
    }

    @Test
    public void testIdeal() {
        compare(IDEAL_KEYS, PropertiesUtils.getKeysFor(IdealConfiguration.class));
    }

    @Test
    public void testNoLearn() {
        compare(NO_LEARN_KEYS, PropertiesUtils.getKeysFor(NoLearnConfiguration.class));
    }
}
