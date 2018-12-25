package ru.ifmo.ctd.ngp.demo.ffchooser.mh_iff;

import org.uncommons.maths.random.*;
import org.uncommons.maths.statistics.*;
import org.uncommons.watchmaker.framework.*;
import org.uncommons.watchmaker.framework.factories.*;
import ru.ifmo.ctd.ngp.demo.util.strings.*;
import ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria.*;
import ru.ifmo.ctd.ngp.demo.generators.*;
import ru.ifmo.ctd.ngp.demo.legacy_ds.string.impl.*;
import ru.ifmo.ctd.ngp.util.*;

import java.io.*;
import java.util.*;

import static ru.ifmo.ctd.ngp.util.CollectionsEx.*;

public class PesaIIRunner {
	public static void main(String[] args) throws IOException {
		int len = 64;
		int ideal = 448;
		
		List<FitnessEvaluator<? super BitString>> criteria = new ArrayList<>();
		criteria.add(new Fk(true));
		criteria.add(new Fk(false));
		//criteria.add(new AlternatingMaskFitness<BitString>());
		
		EvolutionaryOperator<BitString> mutation = new GStringXMutation<>(
                SetMemberGenerator.newGen(listOf(true, false)), new Probability(2.0 / len));
		
		int step = 10;
		double[] grid = new double[]{step, step/*, step(double) len / ((double) ideal / step)*/};
		Selection<BitString> selection = new HyperBoxTournament<>(grid);
		
		AbstractCandidateFactory<BitString> factory = ObjStringFactory.create(
                len,
                SetMemberGenerator.newGen(CollectionsEx.listOf(false, true)),
                BitString.empty()
        );
				
		DataSet dataSet = new DataSet();

        int one = 0;
        int both = 0;
        int times = 30;

        try (Writer log = new FileWriter("pesa.txt")) {
            for (int i = 0; i < times; i++) {
                String run = "Run " + i + ". ";
                log.append(run).append("\n");
                System.out.print(run);
                Collection<EvaluatedIndividual<BitString>> res =
                        PesaII.run(criteria, mutation, null, 0, selection, factory, 50000, 10, 100, grid, new Random());

                double max = 0;
                boolean allOnes = false;
                boolean allZeros = false;

                for (EvaluatedIndividual<BitString> ind : res) {
                    log.append(String.format("%s %s\n", ind.par(), ind.ind()));
                    log.flush();

                    double[] values = ind.par().getCriteria();
                    max = Math.max(values[0], max);
                    max = Math.max(values[1], max);
                    if (values[0] == ideal) {
                        allOnes = true;
                    }
                    if (values[1] == ideal) {
                        allZeros = true;
                    }
                }
                dataSet.addValue(max);
                if (allOnes && allZeros) {
                    both++;
                }
                if (allOnes || allZeros) {
                    one++;
                }
                System.out.println("max: " + max + " 11...1: " + allOnes + " 00...0: " + allZeros);
            }
        }
		
		System.out.print(String.format("\nmax: %f average: %f deviation: %f ", dataSet.getMaximum(),
				dataSet.getArithmeticMean(), dataSet.getStandardDeviation()));
		
		System.out.print(String.format("%%one: %f %%both: %f\n", 100 * (double) one / times, 100 * (double) both / times));	
	}
}
