package ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.gui;

import java.awt.*;

import org.jfree.data.xy.YIntervalSeries;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.LearnConfiguration;

/**
 * Generates points for a horizontal line that corresponds to the switch point.
 * 
 * @author Arina Buzdalova
 *
 */
public class PointSeriesGenerator extends IntervalSeriesContainer {
	private final LearnConfiguration config;
	
	/**
	 * Constructs {@link PointSeriesGenerator} with the specified parameters
	 * 
	 * @param configuration the {@link LearnConfiguration}
	 * @param color the line color
	 * @param label the line label
	 * @param dataPath the path to the logs
	 * @param mode the graph mode
	 */
	public PointSeriesGenerator(LearnConfiguration configuration, Color color,
			String label, String dataPath, PlotMode mode) {
		super(configuration, color, label, dataPath, mode);
		this.config = configuration;
	}
	
	/**
	 * Generates points of the horizontal line that corresponds to the switch point
	 */
	@Override
	public YIntervalSeries generateSeries() {
		YIntervalSeries series = new YIntervalSeries(label);
		int steps = config.getSteps();
		float value = (float) config.getPoint() / config.getDivider();
                      
        for (int i = 1; i <= steps; i++) {
        	series.add(i - 1, value, value, value);                 
         } 
		return series;
	}

}
