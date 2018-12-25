package ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.gui;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DatasetUtilities;

import ru.ifmo.ctd.ngp.demo.ffchooser.config.Configuration;
import ru.ifmo.ctd.ngp.demo.ffchooser.genetic.print.Decompressor;
import ru.ifmo.ctd.ngp.demo.ffchooser.genetic.print.Decompressor.Unit;

/**
 * Representer of chart information about the learning choices certain {@link Configuration}
 * 
 * @author Arina Buzdalova
 */
public class CategoryDataSetContainer {
	private final Configuration config;	
	private final String path;	
	private final String title;
	private final PlotMode mode;
	private CategoryDataset cache;
	
	/**
	 * Constructs the {@link CategoryDataSetContainer} with the specified {@link Configuration},
	 * color and path to the plots data
	 * @param configuration the specified configuration
	 * @param title the title of the contained data set
	 * @param dataPath the path to the plots data
	 * @param mode the {@link PlotMode}
	 */
	public CategoryDataSetContainer(Configuration configuration, String title, String dataPath, PlotMode mode) {
		this.config = configuration;
		this.title = title;
		this.path = dataPath;
		this.mode = mode;
	}
	
	/**
	 * Gets the title of the contained data set
	 * @return the title of the contained data set
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * Generates {@link CategoryDataset} describing learning choices
	 * @return data set derived from the log file of the <code>configuration</code>
	 * @throws IOException  if an I/O error occurs
	 */
	public CategoryDataset getDataSet() throws IOException {
		if (cache != null) {
			return cache;
		}

        try (FileReader in = new FileReader(path + "/" + config.generateFullName())) {
            Decompressor decomp = new Decompressor(in);
            int evalNum = Integer.parseInt(decomp.getParameterValue("evaluators"));
            int generations = config.getSteps();
            double[][] data = new double[evalNum][generations];
            if (mode == PlotMode.stored) {
                try (BufferedReader reader = new BufferedReader(new FileReader(path + "/category/" + config.generateFullName()))) {
                    reader.readLine();
                    for (int i = 0; i < evalNum; i++) {
                        String[] array = reader.readLine().split(" ");
                        for (int j = 0; j < generations; j++) {
                            data[i][j] = Double.parseDouble(array[j]);
                        }
                    }
                }
            }
            if (mode == PlotMode.dynamic) {
                for (int i = 0; i < evalNum; i++) {
                    Arrays.fill(data[i], 0);
                }
                for (int time = 0, size = decomp.getActualTimes(); time < size; time++) {
                    List<Unit> units = decomp.getCompressedDescription(time, evalNum);
                    for (Unit unit : units) {
                        for (int gen = unit.getStart(); gen <= unit.getStop(); gen++) {
                            data[(int)unit.getValue()][gen - 1]++;
                        }
                    }
                }
            }

            //TODO: read labels from properties?
            String[] labels = new String[evalNum];
            for (int i = 0; i < evalNum; i++) {
            	labels[i] = "helper" + i;
            }
//            switch(evalNum) {
//                case 3 : labels = new String[]{"h1", "h2", "g"}; break;
//                case 6 : labels = new String[]{"h1", "h2", "g", "c - h1", "c - h2", "c - g"}; break;
//                default: labels = new String[]{"helper1", "helper2"};
//            }
            String[] columns = new String[data[0].length];
            for (int i = 0; i < data[0].length; i++) {
                columns[i] = i + "";
            }
            CategoryDataset dataset = DatasetUtilities.createCategoryDataset(labels, columns, data);

            cache = dataset;
            return dataset;
        }
	}
}
