package ru.ifmo.ctd.ngp.theory.moearl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Irene Petrova
 */
public class TableTranspositionUtil {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(args[0]));
        String line;
        List<List<String>> transposed = new ArrayList<>();
        while ((line = br.readLine()) != null) {
            String[] parts1 = line.split("&");
            //System.out.print(parts1[0] + " " + "& ");
            line = br.readLine();
            String[] parts2 = line.split("&");
            //System.out.print(parts2[0] + " " + "& ");
            transposed.add(new ArrayList<>());
            for (int i = 1; i < parts1.length; ++i) {
                transposed.get(transposed.size() - 1).add(parts1[i]);
                transposed.get(transposed.size() - 1).add(parts2[i]);
            }
        }
        List<String> toOutput = new ArrayList<>();
        for (int i = 0; i < transposed.get(0).size(); ++i) {
            toOutput.add("");
        }
        for (List<String> strings : transposed) {
            for (int j = 1; j < strings.size(); ++j) {
                toOutput.set(j, toOutput.get(j) + " &" + " " + strings.get(j));
            }
        }
        for (String s : toOutput) {
            System.out.println(s);
        }
    }
}
