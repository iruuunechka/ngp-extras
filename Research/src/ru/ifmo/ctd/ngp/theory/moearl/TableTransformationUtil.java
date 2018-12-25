package ru.ifmo.ctd.ngp.theory.moearl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author Irene Petrova
 */
public class TableTransformationUtil {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(args[0]));
        String line;
        while ((line = br.readLine()) != null) {
            String[] parts= line.split("&");
            System.out.print(parts[0] + " " + "& ");
            String[] last = parts[parts.length - 1].split("\\\\");
            parts[parts.length - 1] = last[0];
            for (int i = 1; i < parts.length; ++i) {
                try {
                    double d = Double.parseDouble(parts[i]);
                    int powcou = 0;
                    while (d > 10) {
                        d = d / 10;
                        powcou++;
                    }
                    if (powcou == 0) {
                        System.out.print(d);
                    } else {
                        System.out.print("$" + String.format("%.2f", d) + "\\cdot 10^" + powcou + "$");
                    }
                    System.out.print((i == parts.length - 1 ? "" : " & "));
                } catch (NumberFormatException e) {
                    System.out.print(parts[i] + (i == parts.length - 1 ? "" : "&"));
                }
            }
            //System.out.println();
            System.out.println("\\\\");
        }
    }
}
