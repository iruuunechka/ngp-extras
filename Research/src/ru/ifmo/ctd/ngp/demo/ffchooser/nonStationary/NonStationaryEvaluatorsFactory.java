package ru.ifmo.ctd.ngp.demo.ffchooser.nonStationary;

import ru.ifmo.ctd.ngp.demo.ffchooser.config.factory.EvaluatorsFactory;
import ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.FunctionalFitness;
import ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.IntegerFunctionImpl;
import ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.RealFunction;
import ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.f.Const;
import ru.ifmo.ctd.ngp.demo.ffchooser.switchpoint.f.X;
import ru.ifmo.ctd.ngp.demo.util.textconstructor.ParamDef;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Irene Petrova
 */
@SuppressWarnings("UnusedDeclaration")
public class NonStationaryEvaluatorsFactory implements EvaluatorsFactory {

    private final int divisor;
    private final int length;
    private final static int targetIndex = 2;
    private static int[] switchPoints = null;

    /**
     * Creates {@link NonStationaryEvaluatorsFactory} with the specified parameters
     * @param divisor the <code>k</code> in the <code>[x/k]</code> evaluator
     * @param length the length of an individual
     * @param switchPoint the last switch point
     * @param pCou the number of switchPoints
     */
    public NonStationaryEvaluatorsFactory(@ParamDef(name = "divisor") int divisor, @ParamDef(name = "length") int length, @ParamDef(name = "switchPoint") int switchPoint, @ParamDef(name = "pCou") int pCou) {
        this.divisor = divisor;
        this.length = length;
        if (switchPoints == null)
            switchPoints = genRandomPoints(switchPoint, pCou);
    }

    private int[] genPoints(int point, int cou) {
        int[] points = new int[cou];
        int segment = point / cou;
        for (int i = 0; i < cou - 1; ++i) {
            points[i] = segment * (i + 1);
        }
        points[cou - 1] = point;
        return points;
    }

    private int[] genRandomPoints(int point, int cou) {
        Random rand = new Random();
        int[] points = new int[cou];
        int left = 0;
        @SuppressWarnings("UnnecessaryLocalVariable")
        int right = point;
        for (int i = 0; i < cou - 1; ++i) {
            int num = rand.nextInt(right - (cou - i - 1) * 100 - left - 100) + left + 100; // left place for rest points and distance between points is greater than 100
            points[i] = num;
            left = num;
        }
        points[cou - 1] = point;
        return points;
    }
    /**
     * Generate array of functions for {@link ru.ifmo.ctd.ngp.demo.ffchooser.nonStationary.NonStationaryFunction}
     * @param order if <code>order</code> = 0 then X, Const, ... else Const, X, ...
     * @return array of functions
     */
    private RealFunction[] genFunctions(int order) {
        int funcCou = switchPoints.length + 1;
        RealFunction[] functions = new RealFunction[funcCou];
        for (int i = 0; i < funcCou; ++i) {
            if (i % 2 == order) {
                functions[i] = new X();
            } else {
                functions[i] = new Const(switchPoints[i / 2]);
            }
        }
        return functions;
    }

    /**
     * Generate array of functions for {@link ru.ifmo.ctd.ngp.demo.ffchooser.nonStationary.NonStationaryFunction}
     * @param order if <code>order</code> = 0 then X, -X, ... else -X, X, ...
     * @return array of functions
     */
    private RealFunction[] genFunctionsHard(int order) {
        int funcCou = switchPoints.length + 1;
        RealFunction[] functions = new RealFunction[funcCou];
        for (int i = 0; i < funcCou; ++i) {
            if (i % 2 == order) {
                functions[i] = new X();
            } else {
                functions[i] = new MinusX(i == 0 ? 0 : switchPoints[i - 1] * 2, i == funcCou - 1 ? length : switchPoints[i]);
            }
        }
        return functions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<FunctionalFitness> getEvaluators() {
        List<FunctionalFitness> evaluators = new ArrayList<>();
        evaluators.add(new FunctionalFitness(new NonStationaryFunction(switchPoints, genFunctionsHard(0))));
        evaluators.add(new FunctionalFitness(new NonStationaryFunction(switchPoints, genFunctionsHard(1))));
        evaluators.add(new FunctionalFitness(new IntegerFunctionImpl(new X(), 1.0 / divisor, 0, length)));
        return evaluators;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int targetIndex() {
        return targetIndex;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        StringBuilder s = new StringBuilder("nonstationary ");
        for (int point : switchPoints) {
            s.append(point).append('_');
        }
        s.setLength(s.length() - 1);
        return s.toString();
    }

    @SuppressWarnings("IntegerDivisionInFloatingPointContext") // length always divides by divisor.
    @Override
    public double bestFitness() {
        return length / divisor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + divisor;
        result = prime * result + length;
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        NonStationaryEvaluatorsFactory other = (NonStationaryEvaluatorsFactory) obj;
        return divisor == other.divisor && length == other.length;
    }
}
