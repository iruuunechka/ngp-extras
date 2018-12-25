package ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.swing.*;

import ru.ifmo.ctd.ngp.demo.ffchooser.config.*;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.factory.*;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.utils.*;

/**
 * Panel that allows to choose parameters of {@link Configuration}
 * and create this configuration
 *   
 * @author Arina Buzdalova
 */
public class ParametersPanel extends JPanel {
	private static final long serialVersionUID = 2339570431591544285L;
	private final Properties properties;
	private final Properties labels;
	private String mode;
	private final Map<String, ConfigurationFactory<? extends Configuration>> factories;
    private final List<ParameterChooser> current;
	
	/**
	 * Shows this panel
	 * @param args args[0] path to the properties file
	 * @throws IOException if an I/O error occurs
	 */
	public static void main(String[] args) throws IOException {
		JFrame frame = new JFrame("Parameters' panel");
		
		frame.setLayout(new FlowLayout());       
        Properties properties = new Properties();
        try (FileReader in = new FileReader(args[0])) {
            properties.load(in);
        }

        Properties labels = new Properties();
        try (FileReader in = new FileReader("misc/labels.properties")) {
            labels.load(in);
        }
        frame.add(new ParametersPanel(properties, labels));
        
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}
	
	/**
	 * Constructs {@link ParametersPanel} with the specified {@link Properties}
	 * @param properties the specified properties
	 * @param labels the labels of the parameters specified in <code>properties</code>
	 */
	public ParametersPanel(Properties properties, Properties labels) {
		this.properties = properties;	
		this.labels = labels;		
		this.factories = ConfigUtils.getFactories();
		
		setLayout(new FlowLayout());		
		PropertiesArrayReader reader = new PropertiesArrayReader(properties);
		
		String[] modes = reader.getValues("mode");
        Map<String, List<ParameterChooser>> choosers = new HashMap<>();
		
		for (String mode : modes) {
			String[] keys = factories.get(mode).getKeys();
			List<ParameterChooser> list = new ArrayList<>();
			for (String key : keys) {
				list.add(new ParameterChooser(key, labels.getProperty(key), reader.getValues(key)));
			}
			choosers.put(mode, list);
		}
		
		ParameterChooser modeSwitcher = new ParameterChooser("mode", labels.getProperty("mode"), modes);	
		modeSwitcher.getBox().addActionListener(new ChangeLearnModeListener());	
		this.mode = modeSwitcher.getBox().getSelectedItem().toString();
		add(modeSwitcher);
		
		current = choosers.get(mode);
		for (ParameterChooser chooser : current) {
			add(chooser);
		}
	}
	
	/**
	 * Gets the {@link Configuration} selected on this {@link ParametersPanel}
	 * @return the {@link Configuration} selected on this {@link ParametersPanel}
	 */
	public Configuration getConfiguration() {
		Properties configProps = new Properties();
		for (ParameterChooser chooser : current) {
			configProps.setProperty(chooser.getName(), chooser.getBox().getSelectedItem().toString());
		}	
		return factories.get(mode).create(configProps);
	}
	
	private class ChangeLearnModeListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			JComboBox source = (JComboBox) e.getSource();
			mode = source.getSelectedItem().toString();
			showChoosers();
		}
		
	}
	
	private void showChoosers() {
		for (ParameterChooser learnChooser : current) {
			remove(learnChooser);
		}		
		current.clear();
		
		PropertiesArrayReader reader = new PropertiesArrayReader(properties);
		for (String key : factories.get(mode).getKeys()) {			
			current.add(new ParameterChooser(key, labels.getProperty(key), reader.getValues(key)));		
		}
		
		for (ParameterChooser chooser : current) {
			add(chooser);
		}
		
		invalidate();
		validate();
	}
}
