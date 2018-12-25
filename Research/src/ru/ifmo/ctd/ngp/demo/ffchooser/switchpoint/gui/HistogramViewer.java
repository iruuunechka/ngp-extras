package ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.gui;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.statistics.HistogramDataset;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.Configuration;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.utils.ConfigGenerator;
import ru.ifmo.ctd.ngp.demo.ffchooser.genetic.print.Decompressor;
import ru.ifmo.ctd.ngp.demo.ffchooser.genetic.print.StopCounter;
import ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.stat.Runner;
import ru.ifmo.ctd.ngp.demo.ffchooser.vfs.OldPathGenerator;
import ru.ifmo.ctd.ngp.demo.util.ChartUtils;

import javax.swing.*;
import java.awt.*;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

/**
 * Generator and viewer of histogram for the configurations listed in a specified properties file.
 * 
 * @author Arina Buzdalova
 */
public class HistogramViewer {

    private HistogramViewer() {}

    /**
	 * Shows histogram for the configurations listed in the specified properties file.
	 * Only one divisor, one length and one steps limit should be specified in properties.
	 * @param args args[0] -- property file with collection of configurations and number of runs
	 * @throws IOException if an I/O exception occurs
	 */
	public static void main(String[] args) throws IOException {
		
		Properties p = new Properties();
        try (FileReader in = new FileReader(args[0])) {
            p.load(in);
        }

		int ideal = Integer.parseInt(p.getProperty("length")) /
					Integer.parseInt(p.getProperty("divider"));
		
		Collection<Configuration> configs = ConfigGenerator.generate(p);
		
		String path = p.getProperty("path");
		Runner.runConfigurations(p);
		
		HistogramDataset dataset = new HistogramDataset();

        System.out.print("Detecting the most suitable interval width...");
        int maxStepDetected = 0;
        for (Configuration c : configs) {
            StopCounter counter = new StopCounter(ideal, Integer.parseInt(p.getProperty("steps")), 1);
            try (FileReader in = new FileReader(path + "/" + OldPathGenerator.instance().path(c))) {
                new Decompressor(in, counter);
                double[] d = counter.getData();
                for (int i = maxStepDetected; i < d.length; ++i) {
                    if (d[i] > 0) {
                        maxStepDetected = Math.max(maxStepDetected, i);
                    }
                }
            }
        }
        System.out.println(" done.");

        int interval = Math.max(1, maxStepDetected / 100);

        for (Configuration c : configs) {
			
			StopCounter counter = new StopCounter(ideal, Integer.parseInt(p.getProperty("steps")), interval);
            try (FileReader in = new FileReader(path + "/" + OldPathGenerator.instance().path(c))) {
                new Decompressor(in, counter);
            }

			double[] counted = counter.getData();
			
			List<Double> list = new ArrayList<>();
			for (int i = 0; i < counted.length; i++) {
				int num = (int)counted[i];
				for (int j = 0; j < num; j++) {
					list.add((double)i * interval);
				}
			}
			
			double[] histData = new double[list.size()];
			for (int i = 0; i < list.size(); i++) {
				histData[i] = list.get(i);
			}

            if (histData.length != 0) {
			    dataset.addSeries(c.getLabel(), histData, maxStepDetected / interval);
            }
		}		
		
		ChartUtils.changeFontSize(22, 22, 22);
		
		JFreeChart chart = ChartFactory.createHistogram(
				"", 
				"Generation number", 
				"Times best fitness reached", dataset, 
				PlotOrientation.VERTICAL, true, true, false);			
				
//		ChartUtils.strokeLines(chart.getXYPlot(), (float) 3.0, configs.size());
		ChartUtils.setWhiteBackground(chart.getXYPlot());
		
		
		ChartPanel panel = new ChartPanel(chart, true);
        JFrame frame = new JFrame();
        frame.setLayout(new BorderLayout());
        frame.add(panel, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}
}
