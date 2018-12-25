package ru.ifmo.ctd.ngp.demo.util;

import java.awt.*;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.plot.XYPlot;

/**
 * Some useful methods for creating and showing charts.
 * 
 * @author Arina Buzdalova
 */
public class ChartUtils {
	
	/**
	 * Changes the sizes of fonts in all charts created by {@link ChartFactory}.
	 * @param regularSize the font size for most inscriptions.
	 * @param largeSize the font size for large-sized inscriptions.
	 * @param extraLargeSize the font size for extra-large-sized inscriptions.
	 */
	public static void changeFontSize(int regularSize, int largeSize, int extraLargeSize) {
		StandardChartTheme theme = new StandardChartTheme("LargerFont");
		
		Font reg = theme.getRegularFont();
		theme.setRegularFont(new Font(reg.getFontName(), reg.getStyle(), regularSize));
		
		Font large = theme.getLargeFont();
		theme.setLargeFont(new Font(large.getFontName(), large.getStyle(), largeSize));
		
		Font extra = theme.getExtraLargeFont();
		theme.setExtraLargeFont(new Font(extra.getFontName(), extra.getStyle(), extraLargeSize));
		
		ChartFactory.setChartTheme(theme);
	}
	
	/**
	 * Stroke lines of the first <code>number</code> series
	 * on the <code>plot</code> with the specified width.
	 * 
	 * The plot can be retrieved by calling {@link JFreeChart#getXYPlot()} method.
	 * 
	 * @param plot the plot to be adjusted
	 * @param width the width of the lines' stroke
	 * @param number number of series to be stroked
	 */
	public static void strokeLines(XYPlot plot, float width, int number) {
		for (int i = 0; i < number; i++) {
			plot.getRenderer().setSeriesStroke(i, new BasicStroke(width));			
		}
	}
	
	/**
	 * Sets the color of the <code>plot's</code> background to white,
	 * the color of the grid to black.
	 * 
	 * The plot can be retrieved by calling {@link JFreeChart#getXYPlot()} method.
	 * 
	 * @param plot the plot to be adjusted
	 */
	public static void setWhiteBackground(XYPlot plot) {
		plot.setBackgroundPaint(Color.white);
		plot.setRangeGridlinePaint(Color.black);
		plot.setDomainGridlinePaint(Color.black);
	}
}
