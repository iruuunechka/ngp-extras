package ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.gui;

import org.jfree.chart.*;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.*;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.utils.*;
import ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.stat.*;
import ru.ifmo.ctd.ngp.demo.util.ChartUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

/**
 * Viewer of charts based on logs of runs determined by configurations
 *  
 * @author Arina Buzdalova
 */
public class ChartViewer {
	private final String dataPath;
	private final String propPath;
	private final Configuration[] allConfigs;
	private final IntervalSeriesContainer[] intervals;
	private final CategoryDataSetContainer[] categories;
	//private final Color[] colors = {Color.BLACK, new Color(80, 80, 80)};
	private final Color[] colors = {Color.RED, Color.BLUE};
	private final PlotMode mode;
	private ChartPanel fitnessPanel;
	private ChartPanel choicePanel;
	private final JPanel charts;
	private final JPanel lists;
	private final JPanel parameters;
	private ParametersPanel[] paramPanels;
	
	/**
	 * Runs frame with chart.
	 * @param args  args[0] path to the properties file that describes collection of configurations 
	 * 				and contains the number of runs
	 * @throws IOException if an I/O error occurs
	 */
	public static void main(String[] args) throws IOException {
		ChartViewer viewer = new ChartViewer(args[0], PlotMode.dynamic);
		viewer.showFrame();
	}
	
	/**
	 * Constructs {@link ChartViewer} with the specified path to the log files
	 *
     * @param propPath path to the {@link java.util.Properties} file
     * @param mode mode of drawing plots. Currently only {@link ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.gui.PlotMode#dynamic} is supported.
     * @throws IOException if an I/O error occurs
	 */
	public ChartViewer(String propPath, PlotMode mode) throws IOException {
		Properties p = new Properties();
        try (FileReader fr = new FileReader(new File(propPath))) {
            p.load(fr);
        }

		this.dataPath = p.getProperty("path");
		this.propPath = propPath;
		this.mode = mode;
		
		if (!new File(dataPath).exists()) {
			Runner.runConfigurations(p);
		}

        Collection<Configuration> all = ConfigGenerator.generate(p);
        allConfigs = all.toArray(new Configuration[0]);
        //allConfigs = StatisticsUtils.sortByBest(all, statPath).toArray(new Configuration[all.size()]);
		
        intervals = new IntervalSeriesContainer[2];
		categories = new CategoryDataSetContainer[2];
		
		parameters = createParametersPane();
		Configuration[] defaultConfigs = {paramPanels[0].getConfiguration(), paramPanels[1].getConfiguration()};
		
		for (int i = 0; i < 2; i++) {
			createDataSets(defaultConfigs[i], i);
		}
		
		ChartUtils.changeFontSize(18, 18, 18);
		charts = new JPanel();
		charts.setLayout(new BorderLayout());

        Runner.runConfigurations(p);
        placeCharts();
//		try {
//			placeCharts();
//		} catch (IOException e) {
//			Runner.runConfigurations(p);
//			placeCharts();
//		}
		
		lists = createListsPane(defaultConfigs);
	}
	
	/**
	 * Shows {@link ChartViewer} frame
	 */
	public void showFrame() {
		JFrame frame = new JFrame("Comparison of configurations");
		
        frame.setLayout(new BorderLayout());
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("parameters", parameters);
        tabbedPane.addTab("lists", lists);
        
        frame.add(charts, BorderLayout.CENTER);        
        frame.add(tabbedPane, BorderLayout.NORTH);
        
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        
        frame.invalidate();
        frame.validate();
	}
	
	private JPanel createParametersPane() throws IOException {
		JPanel parameters = new JPanel();
		parameters.setLayout(new BorderLayout());
		
		Properties properties = new Properties();
        try (FileReader fr = new FileReader(propPath)) {
            properties.load(fr);
        }

        paramPanels = new ParametersPanel[2];        
        JPanel[] paramsNButton = new JPanel[2];
        
        Properties labels = new Properties();
        try (FileReader fr = new FileReader("misc/labels.properties")) {
            labels.load(fr);
        }

        for (int i = 0; i < 2; i++) {
        	paramPanels[i] = new ParametersPanel(properties, labels);
        	paramsNButton[i] = new JPanel(new BorderLayout());
        	paramsNButton[i].add(paramPanels[i], BorderLayout.WEST);
        	JButton ok = new JButton("ok");
        	ok.addActionListener(new ParamListListener(i, paramPanels[i]));
        	paramsNButton[i].add(ok, BorderLayout.EAST);
        }		
		
		parameters.add(paramsNButton[0], BorderLayout.NORTH);
		parameters.add(paramsNButton[1], BorderLayout.SOUTH);		
		
		return parameters;
	}
	
	private JPanel createListsPane(Configuration[] defConfig) {
		JPanel lists = new JPanel();
        lists.setLayout(new BorderLayout());
        
        int listsNum = 2;
        
        JLabel[] labels = new JLabel[listsNum];
        JComboBox<?>[] boxes = new JComboBox<?>[listsNum];
        JPanel[] listNLabel = new JPanel[listsNum];
        
        for (int i = 0; i < listsNum; i++) {
        	labels[i] = new JLabel("configuration-" + i);
        	boxes[i] = new JComboBox<>(allConfigs);
        	boxes[i].setSelectedItem(defConfig[i]);        	
        	ActionListener listener = new ConfigListListener(i);
        	boxes[i].addActionListener(listener);
        	listNLabel[i] = new JPanel(new FlowLayout());
        	listNLabel[i].add(labels[i]);
        	listNLabel[i].add(boxes[i]);
        }

        lists.add(listNLabel[0], BorderLayout.NORTH); 
        lists.add(listNLabel[1], BorderLayout.SOUTH);
        
        return lists;
	}
	
	private void createCharts() throws IOException {
		normalizeFitnessSeries();
		DeviationChartCreator fitnessCreator = new DeviationChartCreator();
		CategoryChartCreator choiceCreator = new CategoryChartCreator();		
		for (int i = 0; i < intervals.length; i++) {						
			fitnessCreator.addSeriesContainer(intervals[i]);			
			choiceCreator.addCategoryContainer(categories[i]);
		}

		fitnessPanel = new ChartPanel(fitnessCreator.generateChart("Fitness values"), true);	
		choicePanel = new ChartPanel(choiceCreator.generateCombinedChart("Learning choices"), true);
	}
	
	private void placeCharts() throws IOException {
		createCharts();
		charts.removeAll();
		charts.add(fitnessPanel, BorderLayout.NORTH);
		charts.add(choicePanel, BorderLayout.CENTER);
		charts.invalidate();
		charts.validate();
	}
	
	private class ParamListListener implements ActionListener {
		private final int index;
		private final ParametersPanel panel;
		
		public ParamListListener(int index, ParametersPanel panel) {
			this.index = index;
			this.panel = panel;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			Configuration config = panel.getConfiguration();
			applyConfig(index, config);	
		}		
	}
	
	private class ConfigListListener implements ActionListener {
		private final int index;
		
		public ConfigListListener(int index) {
			this.index = index;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			JComboBox<?> source = (JComboBox<?>) e.getSource();
			Configuration config = (Configuration) source.getSelectedItem();
			applyConfig(index, config);
		}		
	}

	private void applyConfig(int index, Configuration config) {
		try {
			createDataSets(config, index);
			placeCharts();
//		} catch (FileNotFoundException fex) {
//            JOptionPane.showMessageDialog(parameters, "Such configuration wasn't calculated.");
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	private static final String[] configLabels = {"switches", "Number of"};

	private void createDataSets(Configuration config, int index) {
		intervals[index] = new IntervalSeriesContainer(config, colors[index], config.getLabel(), dataPath, mode);		
		categories[index] = new CategoryDataSetContainer(config, configLabels[index], dataPath, mode);
	}
	
	private void normalizeFitnessSeries() {
		int divider0 = intervals[0].getConfiguration().getDivider();
		int divider1 = intervals[1].getConfiguration().getDivider();
		
		if (divider0 > divider1) {
			intervals[1].setNormalizer((double) divider1 / divider0);
		}
		
		if (divider0 < divider1) {
			intervals[0].setNormalizer((double) divider0 / divider1);
		}
		
		if (divider0 == divider1) {
			intervals[0].setNormalizer(1);
			intervals[1].setNormalizer(1);
		}
	}
}
