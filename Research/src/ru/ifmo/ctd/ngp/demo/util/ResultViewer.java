package ru.ifmo.ctd.ngp.demo.util;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.DefaultXYDataset;

/**
 * A thing that collects a list of integers and then displays the density graphic.
 *
 * @author Maxim Buzdalov
 */
public final class ResultViewer {
    private final Map<String, List<Double>> resultStore = new TreeMap<>();

    public synchronized void add(String index, double value) {
        if (!resultStore.containsKey(index)) {
            resultStore.put(index, new ArrayList<>());
        }
        resultStore.get(index).add(value);
    }

    public void showViewer(String caption, String xCaption, String yCaption) {
        int nFrames = 100;
        final double[][][] results = new double[resultStore.size()][2][nFrames];

        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;

        for (List<Double> row : resultStore.values()) {
            min = Math.min(min, Collections.min(row));
            max = Math.max(max, Collections.max(row));
        }

        double frameWidth = (max - min) / nFrames;

        DefaultXYDataset dataSet = new DefaultXYDataset();

        int idx = 0;
        for (Map.Entry<String, List<Double>> e : resultStore.entrySet()) {
            for (int j = 0; j < nFrames; ++j) {
                results[idx][0][j] = min + frameWidth / 2 + j * frameWidth;
            }
            for (double d : e.getValue()) {
                double diff = d - min;
                int frame = (int) (diff / frameWidth - 1e-6);
                results[idx][1][frame] += 1;
            }
            dataSet.addSeries(e.getKey(), results[idx]);
            ++idx;
        }

        JFreeChart chart = ChartFactory.createXYLineChart(
                caption, xCaption, yCaption, dataSet,
                PlotOrientation.VERTICAL, true, true, false
        );

        ChartPanel panel = new ChartPanel(chart, true);
        JFrame frame = new JFrame();
        frame.setLayout(new BorderLayout());
        frame.add(panel, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }
}
