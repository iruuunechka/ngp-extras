package ru.ifmo.ctd.ngp.demo.ffchooser.vfs;

import org.junit.Assert;
import org.junit.Test;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.Configuration;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.DelayedConfiguration;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.GreedyConfiguration;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.IdealConfiguration;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.NoLearnConfiguration;

import java.util.Random;

/**
 * Tests for compatibility of {@link OldPathGenerator} with
 * {@link ru.ifmo.ctd.ngp.demo.ffchooser.config.Configuration#generateFullName()}.
 *
 * @author Maxim Buzdalov
 */
public class OldPathGeneratorTests {
    
	@Test
    public void testIdealConfiguration() {
        Random r = new Random();
        for (int times = 0; times < 100; ++times) {
            Configuration cfg = new IdealConfiguration(
                    r.nextInt(10), r.nextDouble(), r.nextDouble(),
                    r.nextInt(10), r.nextInt(100), r.nextInt(20), null
            );
            Assert.assertEquals(cfg.generateFullName(), OldPathGenerator.instance().path(cfg));
        }
    }
	
    @Test
    public void testNoLearnConfiguration() {
        Random r = new Random();
        for (int times = 0; times < 100; ++times) {
            Configuration cfg = new NoLearnConfiguration(
                    r.nextInt(10), r.nextDouble(), r.nextDouble(),
                    r.nextInt(10), r.nextInt(10), r.nextInt(100), r.nextInt(20),
                    null);
            Assert.assertEquals(cfg.generateFullName(), OldPathGenerator.instance().path(cfg));
        }
    }

    @Test
    public void testGreedyConfiguration() {
        Random r = new Random();
        for (int times = 0; times < 100; ++times) {
            Configuration cfg = new GreedyConfiguration(
                    r.nextInt(10), r.nextDouble(), r.nextDouble(),
                    r.nextInt(10), r.nextInt(5), r.nextInt(10),
                    r.nextDouble(), r.nextDouble(), r.nextDouble(),
                    r.nextInt(100), r.nextInt(20)
            );
            Assert.assertEquals(cfg.generateFullName(), OldPathGenerator.instance().path(cfg));
        }
    }

    @Test
    public void testDelayedConfiguration() {
        Random r = new Random();
        for (int times = 0; times < 100; ++times) {
            Configuration cfg = new DelayedConfiguration(
                    r.nextInt(10), r.nextDouble(), r.nextDouble(),
                    r.nextInt(10), r.nextInt(5), r.nextInt(10),
                    r.nextDouble(), r.nextDouble(), r.nextDouble(),
                    r.nextInt(100), r.nextInt(20)
            );
            Assert.assertEquals(cfg.generateFullName(), OldPathGenerator.instance().path(cfg));
        }
    }
}
