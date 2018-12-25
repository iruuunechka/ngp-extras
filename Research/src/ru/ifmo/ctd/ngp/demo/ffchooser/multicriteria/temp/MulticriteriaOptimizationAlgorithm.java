package ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria.temp;

import ru.ifmo.ctd.ngp.demo.ffchooser.OptimizationAlgorithm;

import java.util.List;

/**
 */
public interface MulticriteriaOptimizationAlgorithm extends OptimizationAlgorithm {
    /**
     * Gets all the parameters values of the best (in terms of the current criterion) point
     * @return all the parameters values of the best point
     */
    List<List<Double>> getCurrentParetoFront();
    List<List<Double>> getCurrentInternalGeneration();
    List<Double> computeGenerationQuality(int generation);
    void genGeneration(int helper);
    List<List<Double>> computeParetoOfGeneration(int generation);
    void setPopulation(int population);
    void computeValuesOfGeneration(int generation);
    double getFinalBestTargetValue();
}
