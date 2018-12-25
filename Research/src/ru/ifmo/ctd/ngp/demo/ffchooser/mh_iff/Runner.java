package ru.ifmo.ctd.ngp.demo.ffchooser.mh_iff;

import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.Properties;

import ru.ifmo.ctd.ngp.demo.ffchooser.config.Configuration;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.utils.ConfigGenerator;

public class Runner {
	public static void main(String[] args) throws IOException {
		Properties p = new Properties();
        try (FileReader in = new FileReader(args[0])) {
            p.load(in);
        }
		int times = Integer.parseInt(p.getProperty("times"));
		boolean es = Boolean.parseBoolean(p.getProperty("es"));

        Collection<Configuration> configs = ConfigGenerator.generate(p);

		for (Configuration conf : configs) {
			RunVisitor.instance().run(conf, new RunArgument(
					new OutputStreamWriter(System.out), conf.getGenerationCount(), conf.getEliteCount(), times, es
			));
		}
	}
}
