package ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.stat.meynster;

import java.io.*;
import java.util.Properties;
import java.util.Scanner;

public class MeynsterStat {
    private static String readFile(String path) throws IOException {
        return new Scanner(new File(path)).useDelimiter("\\A").next();
    }

    private static void runVaryingNs(String propsFilename, int[] Ns) throws IOException {
        String rawProps = readFile(propsFilename);
        int n = Ns.length;
        for (int i = 0; i < n; i++) {
            System.out.println((i + 1) + "/" + n);
            String specProps = rawProps.replace("length=400", "length=" + Ns[i]);
            Properties p = new Properties();
            p.load(new StringReader(specProps));
            MeynsterRunner.runConfigurations(p);
        }
    }

    static void runVaryingNKs(String propsFilename, int[] Ns, int[] Ks) throws IOException {
        String rawProps = readFile(propsFilename);
        int n = Ns.length;
        for (int i = 0; i < n; i++) {
            System.out.println((i + 1) + "/" + n);
            String specProps = rawProps.replace("length=400", "length=" + Ns[i]);
            specProps = specProps.replace("divider=20", "divider=" + Ks[i]);
            specProps = specProps.replace("divisor=20", "divisor=" + Ks[i]);
            specProps = specProps.replace("point=360", "point=" + (int) (0.9 * Ns[i]));
            specProps = specProps.replace("switchPoint=360", "switchPoint=" + (int) (0.9 * Ns[i]));
            Properties p = new Properties();
            p.load(new StringReader(specProps));
            MeynsterRunner.runConfigurations(p);
        }
    }

    public static void main(String[] args) throws IOException {
        int[] Ns = {300};

        int[] sNs = {4, 8, 12, 16, 20, 6, 12, 18, 24, 30, 8, 16, 24, 32, 40, 10, 20, 30, 40, 50};
        int[] sKs = {2, 2,  2,  2,  2, 3,  3,  3,  3,  3, 4,  4,  4,  4,  4,  5,  5,  5,  5,  5};

        runVaryingNs("misc/config/coursework-onemax-state.properties", Ns);
        //runVaryingNs("misc/config/coursework-leadingones-state.properties", Ns);
        //runVaryingNKs("misc/config/coursework-switchpoint-state.properties", sNs, sKs);

        //runVaryingNs("misc/config/coursework-onemax-reward.properties", Ns);
        //runVaryingNs("misc/config/coursework-leadingones-reward.properties", Ns);
        //runVaryingNKs("misc/config/coursework-switchpoint-reward.properties", sNs, sKs);

        System.out.println();
    }
}
