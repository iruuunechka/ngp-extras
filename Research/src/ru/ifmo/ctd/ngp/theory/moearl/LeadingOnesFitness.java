package ru.ifmo.ctd.ngp.theory.moearl;

/**
 * @author Arkadii Rost
 */
public class LeadingOnesFitness implements BitFitness {
    @Override
    public int calculate(boolean[] individual) {
        int i = 0;
        while (individual[i]) {
            i++;
            if (i == individual.length) {
                break;
            }
        }
        return i;
    }
}
