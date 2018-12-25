package ru.ifmo.ctd.ngp.theory.moearl;

/**
 * @author Irene Petrova
 */
public class SwitchPointFitness implements BitFitness {

    private final int n;
    private final int p;
    private final boolean firstInterval;

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
        if (firstInterval) {
            return (countOnes < p) ? countOnes : n - countOnes;
        } else {
            return (countOnes < p) ? n - countOnes : countOnes;
        }
    }

    public SwitchPointFitness(int n, int p, boolean firstInterval) {
        this.n = n;
        this.p = p;
        this.firstInterval = firstInterval;
    }

}
