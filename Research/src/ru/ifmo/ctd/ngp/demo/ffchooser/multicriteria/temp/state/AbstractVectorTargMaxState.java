package ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria.temp.state;

import ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria.temp.MulticriteriaOptimizationAlgorithm;
import ru.ifmo.ctd.ngp.demo.util.textconstructor.ParamDef;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Irene Petrova
 */
public abstract class AbstractVectorTargMaxState extends AbstractVectorState {

    private static final long serialVersionUID = -2527167768700605134L;

    protected AbstractVectorTargMaxState(@ParamDef(name = "length") int length, MulticriteriaOptimizationAlgorithm alg) {
        super(length, alg);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<Double> getLastValues(MulticriteriaOptimizationAlgorithm alg) {
        List<Double> res = new ArrayList<>(alg.getCurrentParetoFront().get(0).size());
        double max = Double.NEGATIVE_INFINITY;
        int cou = 0;
        List<List<Double>> individualsSet = getIndividualSet(alg);
        for (List<Double> ind : individualsSet) {
            if (ind.get(alg.getTargetParameter()) > max) {
                max = ind.get(alg.getTargetParameter());
                cou = 1;
            } else if (ind.get(alg.getTargetParameter()) == max) {
                cou++;
            }
        }
        int funcSize = individualsSet.get(0).size();
        for (int i = 0; i < funcSize; ++i) {
            res.add(0.0);
        }
        for (List<Double> ind : individualsSet) {
            if (ind.get(alg.getTargetParameter()) == max) {
                for (int i = 0; i < funcSize; ++i) {
                    res.set(i, res.get(i) + ind.get(i) / cou);
                }
            }
        }
        return res;
    }
}