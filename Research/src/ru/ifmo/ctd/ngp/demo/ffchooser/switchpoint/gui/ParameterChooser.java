package ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

/**
 * Container of a {@link JLabel} and a {@link JComboBox} for choosing
 * a named parameter from a given list
 * 
 * @author Arina Buzdalova
 */
public class ParameterChooser extends JPanel {
	private static final long serialVersionUID = 1L;
	private final String name;
	private final JComboBox<?> box;
	
	/**
	 * Shows this element
	 * @param args are not used
	 */
	public static void main(String[] args) {
		JFrame frame = new JFrame("Parameters' panel");
		
		frame.getContentPane().setLayout(new FlowLayout());       
        frame.setLayout(new BorderLayout());
       
        frame.add(new ParameterChooser("len", "length", new String[] {"400", "600"}));
        
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}
	
	/**
	 * Constructs {@link ParameterChooser} with the specified 
	 * name, label and list of elements
	 * @param name the specified name
	 * @param label the label of this chooser
	 * @param choice the list of elements
	 */
	public ParameterChooser(String name, String label, String[] choice) {
		this.name = name;
		JLabel lab = new JLabel(label);
		this.box = new JComboBox<>(choice);
		if (choice.length == 1) {
			box.setEnabled(false);
		}
		setLayout(new FlowLayout());
		add(lab);
		add(box);
	}
	
	/**
	 * Gets the inner JComboBox of this {@link ParameterChooser}
	 * @return the inner JComboBox
	 */
	public JComboBox<?> getBox() {
		return box;
	}
	
	/**
	 * Gets the name of this {@link ParameterChooser}
	 * @return the name of this parameter chooser
	 */
	public String getName() {
		return name;		
	}
}
