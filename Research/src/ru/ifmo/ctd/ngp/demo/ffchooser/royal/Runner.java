package ru.ifmo.ctd.ngp.demo.ffchooser.royal;

import java.io.*;
import java.util.Collection;
import java.util.Properties;

import ru.ifmo.ctd.ngp.demo.ffchooser.config.Configuration;
import ru.ifmo.ctd.ngp.demo.ffchooser.config.utils.ConfigGenerator;
import ru.ifmo.ctd.ngp.demo.legacy_ds.string.impl.BitString;

public class Runner {
	public static void main(String[] args) throws IOException, InterruptedException {
		
		Properties p = new Properties();
        try (FileReader in = new FileReader(args[0])) {
            p.load(in);
        }
		final int times = Integer.parseInt(p.getProperty("times"));
		final boolean es = Boolean.parseBoolean(p.getProperty("es"));

        Collection<Configuration> configs = ConfigGenerator.generate(p);
		System.out.println("configurations: " + configs.size());

        final String[] result = new String[configs.size()];
        final Thread[] threads = new Thread[configs.size()];
        int size = 0;
		for (final Configuration conf : configs) {
            final int index = size++;
            threads[index] = new Thread(() -> {
                Writer writer = new StringWriter();
                RunVisitor.instance().run(
                        conf,
                        new RunArgument<>(
                                writer, conf.getGenerationCount(),
                                conf.getEliteCount(), BitString.empty(), times, es)
                );
                result[index] = writer.toString();
            });
            threads[index].start();
		}
        for (Thread th : threads) {
            th.join();
        }
        try (Writer writer = new FileWriter("royal-roads")) {
            for (String s : result) {
                writer.write(s);
                writer.append("\n\n");
            }
        }
	}
}
