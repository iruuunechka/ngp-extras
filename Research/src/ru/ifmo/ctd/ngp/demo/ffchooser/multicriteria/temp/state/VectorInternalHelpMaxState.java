package ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria.temp.state;

import ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria.temp.*;
import ru.ifmo.ctd.ngp.demo.util.textconstructor.*;

import java.util.*;

/**
 * @author Irene Petrova
 */
public class VectorInternalHelpMaxState extends AbstractVectorHelpMaxState {

    /**
     * Constructs {@link VectorInternalHelpMaxState} with the specified length of the vector.
     * The length should be equal or less than the number of fitness evaluators.
     * If it is less, first <code>length</code> FFs will be taken after sorting by
     * <code>(x<sub>current</sub> - x<sub>previous</sub>) / x<sub>current</sub></code>.
     * @param length the specified length of the vector
     */
    public VectorInternalHelpMaxState(@ParamDef(name = "length") int length, MulticriteriaOptimizationAlgorithm alg) {
        super(length, alg);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return String.format("vectorInternalHelpMaxState%d", length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<List<Double>> getIndividualSet(MulticriteriaOptimizationAlgorithm alg) {
        return alg.getCurrentInternalGeneration();
    }
}