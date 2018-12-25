package ru.ifmo.ctd.ngp.demo.ffchooser.genetic.print;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import junit.framework.Assert;

import org.junit.*;

import ru.ifmo.ctd.ngp.demo.ffchooser.Utils;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.Configuration;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.DelayedConfiguration;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.DivisorConfiguration;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.NoLearnConfiguration;
import ru.ifmo.ctd.ngp.demo.ffchooser.genetic.Printer;
import ru.ifmo.ctd.ngp.demo.ffchooser.genetic.print.Decompressor.Unit;
import ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.evaluators.SwitchPointEvaluatorsFactory;
import ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.stat.StatisticsCollector;
import ru.ifmo.ctd.ngp.demo.legacy_ds.string.impl.BitString;
import ru.ifmo.ctd.ngp.util.CollectionsEx;

/**
 * Some tests for {@link Decompressor}.
 * 
 * @author Arina Buzdalova
 */
public class DecompressorTests {
	
	/**
	 * Tests whether the {@link Decompressor} reads and saves the log correctly
	 * @throws IOException if an I/O error occurs
	 */
	@Test
	public void readData() throws IOException {
		int stepsLimit = 3;
		int times = 2;
		readDataUsingConfig(Utils.makeNoLearnConfig(stepsLimit), times);
		readDataUsingConfig(Utils.makeGreedyConfig(stepsLimit), times);
		readDataUsingConfig(Utils.makeDelayedConfig(stepsLimit), times);
	}
	
	/**
	 * Tests {@link Decompressor#getContainingInterval} method,
	 * which uses binary search
	 * @throws IOException if an I/O error occurs
	 */
	@Test
	public void getContainingInterval() throws IOException {
		int stepsLimit = 3;
		int times = 2;
		getIntervalWithConfig(Utils.makeNoLearnConfig(stepsLimit), times);
		getIntervalWithConfig(Utils.makeGreedyConfig(stepsLimit), times);
		getIntervalWithConfig(Utils.makeDelayedConfig(stepsLimit), times);
	}
	
	/**
	 * Tests the case in that run stops before the steps limit
	 * @throws IOException if an I/O error occurs
	 */
	@Test
	public void testEarlyStop() throws IOException {
		int stepsLimit = 5;
		int times = 2;
		
		double crossover = 1.0;
		double mutation = 0.1;
		
		int length = 2;
		int divider = 1;
		int switchPoint = length / divider;
		
		NoLearnConfiguration noLearnConfig = new NoLearnConfiguration(
				stepsLimit,
				crossover, mutation, 
				length, divider, 2000, 5,
				new SwitchPointEvaluatorsFactory(divider, length, switchPoint));
		
		checkEarlyStop(noLearnConfig, times);
		
		DelayedConfiguration delayedConfig = new DelayedConfiguration(
				stepsLimit + 5,
				crossover, mutation, 
				length, switchPoint, divider, 1, 0.2, 0.001,
				2000, 5);
		
		checkEarlyStop(delayedConfig, times);		
	}
	
	private void checkEarlyStop(DivisorConfiguration config, int times) throws IOException {		
		StringWriter writer = new StringWriter();
		int evaluators = StatisticsCollector.run(config, times, writer, CollectionsEx.<Printer<BitString>>listOf());
		
		String log = writer.toString();		
		Decompressor decomp = new Decompressor(new StringReader(log));
		
		for (int time = 0; time < times; time++) {
			for (int eval = 0; eval <= evaluators; eval++) {
				List<Unit> descr = decomp.getCompressedDescription(time, eval);
				int size = descr.size();
				Unit last = descr.get(size - 1);
				Assert.assertEquals(config.getSteps(), last.getStop());

				// TODO: this logic appears to be highly obsolete
				// Assert.assertEquals((double) config.getLength() / config.getDivider(), last.getValue());
				// Assert.assertTrue(size == 1 || last.getValue() > descr.get(size - 2).getValue());
			}
		}
	}
	
	private void readDataUsingConfig(Configuration config, int times) throws IOException {
		StringWriter writer = new StringWriter();
		int evaluators = StatisticsCollector.run(config, times, writer, CollectionsEx.<Printer<BitString>>listOf());
		
		String log = writer.toString();
		Decompressor decomp = new Decompressor(new StringReader(log));

        try (BufferedReader buffReader = new BufferedReader(new StringReader(log))) {

            buffReader.readLine();
            buffReader.readLine();

            String start = "1 ";

            for (int i = 0; i < times; i++) {
                for (int j = 0; j <= evaluators; j++) {
                    for (Unit unit : decomp.getCompressedDescription(i, j)) {
                        String line = buffReader.readLine();
                        Assert.assertEquals(start + line, unit.toString());
                        start = (Integer.parseInt(line.split(" ")[0]) + 1) + " ";
                    }
                    buffReader.readLine();
                    start = "1 ";
                }
                buffReader.readLine();
            }

            Assert.assertNull(buffReader.readLine());
        }
	}
	
	private void getIntervalWithConfig(Configuration config, int times) throws IOException {
		int stepsLimit = config.getSteps();
		
		StringWriter writer = new StringWriter();
		int evaluators = StatisticsCollector.run(config, times, writer, CollectionsEx.<Printer<BitString>>listOf());
		
		String log = writer.toString();
		
		Decompressor decomp = new Decompressor(new StringReader(log));
		
		for (int i = 0; i < times; i++) {
			for (int j = 0; j <= evaluators; j++) {
				for (int k = 1; k <= stepsLimit; k++) {
					Unit interval = decomp.getContainingInterval(i, j, k);
					Assert.assertTrue(interval.getStart() <= k);
					Assert.assertTrue(interval.getStop() >= k);
				}
			}
		}
	}
}
