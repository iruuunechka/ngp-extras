package ru.ifmo.ctd.ngp.theory.moearl;

/**
 * @author Irene Petrova
 */
public class XdivKFitness implements BitFitness {
    private final int n;
    private final int k;

    @Override
    public int calculate(boolean[] individual) {
        if (individual.length != n) {
            throw new RuntimeException("Length of individual does not correspond to defined length");
        }
        int countOnes = 0;
        for (int i = 0; i < n; i++) {
            if (individual[i]) {
                countOnes++;
            }
        }
        return countOnes / k;
    }

    public XdivKFitness(int n, int k) {
        this.n = n;
        this.k = k;
    }
}
