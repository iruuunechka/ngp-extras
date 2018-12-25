package ru.ifmo.ctd.ngp.demo.paramchooser.experiments;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Arkadii Rost
 */
public class Extractor {
	private static final String DIST = "dist_base";
	private static final String SIMPLE_Q = "simple_single";
	private static final String KARAFOTIAS = "simple";
	private static final String EARPC = "earpc_single";
	private static final String UEARPC = "earpc";

	private enum Problems {
		DIST("dist", "dist_base"),
		SIMPLE_Q("q-learn","simple_single"),
		GECCO("gecco", "simple"),
		EARPC("earpc", "earpc_single"),
		UEARPC("uearpc", "earpc")
		;

		final String name;
		final String folder;

		Problems(String name, String folder) {
			this.name = name;
			this.folder = folder;
		}

		public String getName() {
			return name;
		}

		public String getFolder() {
			return folder;
		}
	}

	public static int countLines(File file) {
		try (LineNumberReader reader  = new LineNumberReader(new FileReader(file))) {
			//noinspection StatementWithEmptyBody
			while (reader.readLine() != null) {}
			return reader.getLineNumber();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static int getAverageLength(Problems problem, Path problemPath, int k, int mu, int lambda) {
		Path resPath = problemPath.resolve(String.format("%d_%d/%s_%d/", mu, lambda, problem.getFolder(), k));
		File[] results = resPath.toFile().listFiles();
		if (results == null)
			return 0;
		long sum = 0;
		for (File r : results)
			sum += countLines(r);
		return (int)(sum / results.length);
	}

	public static void main(String[] args) {
		int[] ranges = {1, 2, 3};
		int[] mus = {1, 5, 10};
		int[] lambdas = {1, 3, 7};

		Problems[] problems;
		switch (args[0]) {
			case "dist": problems = new Problems[]{Problems.DIST}; break;
			case "simple": problems = new Problems[] {Problems.SIMPLE_Q, Problems.GECCO}; break;
			case "earpc": problems = new Problems[] {Problems.EARPC, Problems.UEARPC}; break;
			default: throw new IllegalArgumentException("Unknown value for args[0]: " + args[0]);
		}
		int size = problems.length * mus.length;
		Path problemDir = Paths.get(args[1]);
		System.out.println(String.format("\\begin{tabular}{|*%d{c|}}", size + 1));
		for (int k : ranges) {
			System.out.println("\\hline");
			System.out.println(String.format("\\multicolumn{%d}{|l|}{k = %d} \\\\", size + 1,  k));
			System.out.println("\\hline");
			String diagBox = "\\diagbox{$\\mu$}{$\\lambda$}";
			System.out.print(problems.length > 1 ? String.format("\\multirow{2}{*}{%s}", diagBox) : diagBox);

			for (int lambda : lambdas)
				System.out.print(String.format(" & \\multicolumn{%d}{c|}{%d}", problems.length, lambda));
			System.out.println(" \\\\");
			if (problems.length > 1) {
				System.out.println(String.format("\\cline{2-%d}", size + 1));
				for (int ignored : lambdas) {
					for (Problems problem : problems)
						System.out.print(String.format(" & %s", problem.getName()));
				}
				System.out.println(" \\\\");
			}
			for (int mu : mus) {
				System.out.println("\\hline");
				System.out.print(mu);
				for (int lambda : lambdas) {
					for (Problems problem : problems)
						System.out.print(String.format(" & %d", getAverageLength(problem, problemDir, k, mu, lambda)));
				}
				System.out.println(" \\\\");
			}
		}
		System.out.println("\\hline");
		System.out.println("\\end{tabular}");
	}
}
