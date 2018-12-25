package ru.ifmo.ctd.ngp.demo.ffchooser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * Class for generating a file with a TeX-table.
 * 
 * @author Arina Buzdalova
 */
public class TablePrinter {
	
	private final PrintWriter printer;
	private String comment;

	/**
     * Constructs {@link TablePrinter} with the name {@code filename<i>i</i>.inc},
     * where {@code i} is the number of files with the similar filenames
     * @param fileName the name of the file to be generated
     * @param columns number of columns in the table being generated
     */
	public TablePrinter(String fileName, int columns) {		
		for (int i = 0; ; i++) {
			File f = new File(String.format("%s-%02d.inc", fileName, i));
			if (!f.exists()) {
				try {
					printer = new PrintWriter(f);
				} catch (FileNotFoundException e) {
					throw new RuntimeException(e);
				}
				break;
			}
		}		
			
		printer.println(genDeclaration(columns));
	}
	
	private String genDeclaration(int columns) {
		StringBuilder specBuilder = new StringBuilder("\\begin{table}\n\\begin{tabular}{");
		for (int i = 0; i < columns; i++) {		
			specBuilder.append("|c");
		}
		specBuilder.append("|").append("} \\hline");
        return specBuilder.toString();
	}
	
	/**
	 * Sets the comment to the table
	 * @param comment the text of the comment
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	/**
	 * Prints line with the specified elements to the table
	 * @param strings the elements of the line to be added
	 */
	public void addLine(String... strings) {
		StringBuilder lineBuilder = new StringBuilder();
		int length = strings.length;
		for (int i = 0; i < length - 1; i++ ) {
            lineBuilder.append(strings[i]).append(" & ");
		}

        lineBuilder.append(strings[length - 1]).append(" \\\\ \\hline");
		
		printer.println(lineBuilder.toString());
		
		printer.flush();
	}
	
	public void close() {
		printer.println("\\end{tabular}");
		printer.println("\\caption{" + comment + "}\n\\end{table}");
		printer.close();
	}
	
}
