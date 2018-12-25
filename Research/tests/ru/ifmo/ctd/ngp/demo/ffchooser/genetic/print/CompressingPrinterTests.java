package ru.ifmo.ctd.ngp.demo.ffchooser.genetic.print;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.StringTokenizer;

import org.junit.Test;

import junit.framework.Assert;
import ru.ifmo.ctd.ngp.demo.ffchooser.Utils;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.Configuration;
import ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.stat.StatisticsCollector;
import ru.ifmo.ctd.ngp.util.CollectionsEx;

/**
 * Some tests for the {@code CompressingPrinter}.
 *
 * @author Arina Buzdalova
 */
public class CompressingPrinterTests {
	
	@Test
	public void testCorrectness() throws IOException {
		int stepsLimit = 5;
		int times = 2;
		
		testCorrectnessWithConfig(Utils.makeNoLearnConfig(stepsLimit), times);
		testCorrectnessWithConfig(Utils.makeGreedyConfig(stepsLimit), times);
		testCorrectnessWithConfig(Utils.makeDelayedConfig(stepsLimit), times);
	}
	
	private void testCorrectnessWithConfig(Configuration config, int times) throws IOException {
		int stepsLimit = config.getSteps();
		
		StringWriter cWriter = new StringWriter();
		StringWriter fullWriter = new StringWriter();
		
		int evaluators = StatisticsCollector.run(
				config,
				times,
				cWriter,
                CollectionsEx.listOf(new GeneralPrinter<>(fullWriter)));
		
		Decompressor decomp = new Decompressor(new StringReader(cWriter.toString()));

        try (BufferedReader buffReader = new BufferedReader(new StringReader(fullWriter.toString()))) {
            buffReader.readLine();

            for (int i = 0; i < times; i++) {
                for (int j = 1; j <= stepsLimit; j++) {

                    StringTokenizer tokenizer = new StringTokenizer(buffReader.readLine());
                    Assert.assertEquals(j, Integer.parseInt(tokenizer.nextToken()));

                    for (int e = 0; e < evaluators; e++) {
                        Assert.assertEquals(
                                Double.parseDouble(tokenizer.nextToken()),
                                decomp.getFitness(i, e, j));
                    }

                    Assert.assertEquals(
                            (int) Double.parseDouble(tokenizer.nextToken()),
                            decomp.getChoice(i, j));
                }
            }

            Assert.assertNull(buffReader.readLine());
		}
	}
}
