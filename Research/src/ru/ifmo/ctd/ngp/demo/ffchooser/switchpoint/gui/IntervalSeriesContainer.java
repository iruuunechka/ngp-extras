package ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jfree.chart.renderer.xy.DeviationRenderer;
import org.jfree.data.xy.YIntervalSeries;
import org.uncommons.maths.statistics.EmptyDataSetException;

import ru.ifmo.ctd.ngp.demo.ffchooser.StatisticsData;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.Configuration;
import ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.stat.StatisticsUtils;

/**
 * Representer of chart information about the runs of some certain {@link Configuration}
 * 
 * @author Arina Buzdalova
 */
public class IntervalSeriesContainer {
	protected final Configuration config;	
	private final String path;	
	private final Color color;
	protected final String label;
	private final PlotMode mode;
	private double normalizer;
	private final Map<Double, YIntervalSeries> cache;
	
	/**
	 * Constructs the {@link IntervalSeriesContainer} with the specified {@link Configuration},
	 * color and path to the plots data
	 * @param configuration the specified configuration
	 * @param color the basic color used to draw all charts of this container
	 * @param label the name of these series used in chart's legend
	 * @param dataPath the path to the plots data
	 * @param mode mode of plots data retrieving
	 */
	public IntervalSeriesContainer(Configuration configuration, Color color, String label, String dataPath, PlotMode mode) {
		this.config = configuration;
		this.path = dataPath;
		this.color = color;
		this.label = label;
		this.mode = mode;
		cache = new HashMap<>();
		normalizer = 1.0;
	}
	
	/**
	 * Gets the configuration stored in this container
	 * @return the configuration stored in this container
	 */
	public Configuration getConfiguration() {
		return config;
	}
	
	/**
	 * Sets the normalizer value that is multiplied with each y-value.
	 * Normalizer is set to 1.0 initially.
	 * @param normalizer value that is multiplied with each y-value
	 */
	public void setNormalizer(double normalizer) {
		this.normalizer = normalizer;
	}
	
	/**
	 * Tunes the specified renderer, so as it fills min-max interval 
	 * with the brighter color. The own color of this {@link IntervalSeriesContainer}
	 * is used as the base.
	 * @param renderer the specified renderer
	 * @param index the index of the renderer's element to tune. It should
	 * be the same as the index of the series corresponding to this series
	 * container.
	 */
	public void tuneRenderer(DeviationRenderer renderer, int index) {
		renderer.setSeriesStroke(index, new BasicStroke(3F, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        renderer.setSeriesPaint(index, color.darker());
        renderer.setSeriesFillPaint(index, color.brighter());
	}
	
	/**
	 * Generates xy series:
	 * x-values are generations, y-values are average, maximal and minimal fitness
	 * @return series derived from the log file of the <code>configuration</code>
	 * @throws IOException  if an I/O error occurs
	 */
	public YIntervalSeries generateSeries() throws IOException {
		if (cache.containsKey(normalizer)) {
			return cache.get(normalizer);
		}
		
		YIntervalSeries series = new YIntervalSeries(label);	
		
		if (mode == PlotMode.stored) {
            try (BufferedReader reader = new BufferedReader(new FileReader(path + "/deviation/" + config.generateFullName()))) {
                String str = reader.readLine();
                int i = 0;
                while (str != null) {
                    String[] array = str.split(" ");
                    series.add(i,
                            normalizer * Double.parseDouble(array[0]),
                            normalizer * Double.parseDouble(array[1]),
                            normalizer * Double.parseDouble(array[2]));
                    str = reader.readLine();
                    i++;
                }
            }
		}
		
		if (mode == PlotMode.dynamic) {
			 int steps = config.getSteps();
             List<StatisticsData> stat = StatisticsUtils.getGenerationsStatistics(config, path);
                          
             for (int i = 1; i <= steps; i++) {
                     try {
                             series.add(i - 1, 
                            		 normalizer * stat.get(i).getAverage(), 
                            		 normalizer * stat.get(i).getMin(), 
                            		 normalizer * stat.get(i).getMax());
                     } catch (EmptyDataSetException ex) {
                             //All runs ended before steps limit was reached
                             break;
                     }
             } 
		}
		
		cache.put(normalizer, series);
		return series;
	}
}
