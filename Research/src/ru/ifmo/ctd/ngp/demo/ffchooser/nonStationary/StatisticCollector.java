package ru.ifmo.ctd.ngp.demo.ffchooser.nonStationary;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * @author Irene Petrova
 */
public class StatisticCollector {
    private static final int count = 30;

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(args[0]))));
        PrintWriter pw = new PrintWriter(args[1]);
        Random rand = new Random();
        String s;
        while ((s = br.readLine()) != null) {
            if (s.contains("(")) {
                List<String> str = new ArrayList<>(Arrays.asList(s.split("[(,)]")));
                pw.write(str.get(0) + "(");
                for (int i = 0; i < count; i++) {
                    int index = rand.nextInt(str.size() - 1) + 1;
                    pw.write(str.get(index) + (i != count - 1 ? ", " : ""));
                    str.remove(index);
                }
                pw.write(")\n");
            } else {
                pw.write(s + '\n');
            }
        }
        pw.close();
    }
}
