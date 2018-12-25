package ru.ifmo.ctd.ngp.demo.paramchooser.experiments.kmeans;

import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.Clusterable;
import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;
import org.apache.commons.math3.ml.distance.EuclideanDistance;

import java.util.Arrays;
import java.util.List;

/**
 * @author Arkadii Rost
 */
public class Main {


    public static void main(String[] args) {
        KMeansPlusPlusClusterer<Clusterable> clusterer = new KMeansPlusPlusClusterer<>(2, 100, new EuclideanDistance());
        List<Clusterable> points = Arrays.asList(
                new DoublePoint(new double[]{1, 1}),
                new DoublePoint(new double[]{1.5, 2}),
                new DoublePoint(new double[]{3, 4}),
                new DoublePoint(new double[]{5, 7}),
                new DoublePoint(new double[]{3.5, 5}),
                new DoublePoint(new double[]{4.5, 5}),
                new DoublePoint(new double[]{3.5, 4.5})
        );
        List<CentroidCluster<Clusterable>> centers = clusterer.cluster(points);
        for (CentroidCluster<Clusterable> cluster : centers) {
            System.out.println("Cluster center: " + Arrays.toString(cluster.getCenter().getPoint()));
            for (Clusterable point : cluster.getPoints())
                System.out.println(Arrays.toString(point.getPoint()));
        }
    }
}
