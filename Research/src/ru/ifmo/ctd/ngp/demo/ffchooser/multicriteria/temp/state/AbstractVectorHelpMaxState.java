package ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria.temp.state;

import ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria.temp.*;
import ru.ifmo.ctd.ngp.demo.util.textconstructor.*;

import java.util.*;

/**
 * @author Irene Petrova
 */
public abstract class AbstractVectorHelpMaxState extends AbstractVectorState {

    private static final long serialVersionUID = 1255506423649528157L;

    protected AbstractVectorHelpMaxState(@ParamDef(name = "length") int length, MulticriteriaOptimizationAlgorithm alg) {
        super(length, alg);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<Double> getLastValues(MulticriteriaOptimizationAlgorithm alg) {
        List<Double> res = new ArrayList<>(alg.getCurrentParetoFront().get(0).size() - 1);
        List<List<Double>> individualsSet = getIndividualSet(alg);
        int funcSize = alg.parametersCount();
        if (funcSize != individualsSet.get(0).size() - 1) {
            throw new IllegalStateException("Actions count doesn't fit length of current individual minus one (which is target)");
        }
        //System.out.println(funcSize);
        for (int i = 1; i <= funcSize; ++i) {
            res.add(-Double.MAX_VALUE);
        }
        //System.out.println(res.size());
        for (int i = 1; i <= funcSize; ++i) {
            for (List<Double> ind : individualsSet) {
                if (ind.get(i) > res.get(i - 1)) {
                    res.set(i - 1, ind.get(i));
                }
            }
        }
        return res;
    }
}
