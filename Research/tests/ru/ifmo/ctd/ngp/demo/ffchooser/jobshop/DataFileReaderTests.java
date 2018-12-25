package ru.ifmo.ctd.ngp.demo.ffchooser.jobshop;

import org.junit.*;

import java.io.*;
import java.util.*;

public class DataFileReaderTests {
	@Test
	public void testReading() throws Exception {
		DataFileReader reader = new DataFileReader(new File("./misc/jobshop.txt"));
		DataFileReader.InputDataSet sample = reader.get("la12");
		int[][] times = sample.getTimes();
		int[][] machines = sample.getMachines();
		Assert.assertEquals(3, machines[2][1]);
		Assert.assertEquals(54, times[2][1]);
	}

	@Test
	public void testFullDataset() throws Exception {
		Properties p = new Properties();
        try (FileReader in = new FileReader("./misc/jobshop.properties")) {
            p.load(in);
        }
		DataFileReader reader = new DataFileReader(new File("./misc/jobshop.txt"));
		StringTokenizer sets = new StringTokenizer(p.getProperty("datasets.full"), ", ");
		while (sets.hasMoreTokens()) {
			Assert.assertNotNull(reader.get(sets.nextToken()));
            sets.nextToken();
		}
	}
}
