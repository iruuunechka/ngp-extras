package ru.ifmo.ctd.ngp.opt.multicriteria

import scala.util.Sorting

import ru.ifmo.ctd.ngp.opt.types.{CodomainType, DomainType}
import ru.ifmo.ctd.ngp.opt.Evaluated

/**
 * Evaluator of the crowding distance (used in NSGA-II and in some other algorithms).
 *
 * @author Maxim Buzdalov
 */
abstract class DiversityMeasure[D : DomainType, C : CodomainType] {
  def apply(seq: IndexedSeq[Evaluated[D, C]]): IndexedSeq[Double]
}

object DiversityMeasure {
  class Detected[D : DomainType, C : CodomainType] {
//    def crowdingDistanceFortin(implicit
//      multiple: MultipleCriteria[C],
//      criterionToDouble: MultipleCriteria.CriterionToDouble[C]
//    ) = new DiversityMeasure[D, C] {
//      def apply(seq: IndexedSeq[Evaluated[D, C]]) = {
//        val idx = Array.ofDim[Int](seq.size)
//        val dist = Array.ofDim[Double](seq.size)
//        for (criterion <- 0 until multiple.numberOfCriteria) {
//          Sorting.stableSort(idx, {
//            (l: Int, r: Int) => multiple.orderingForCriterion(criterion).compare(seq(l).output, seq(r).output)
//          })
//        }
//
//        dist.toIndexedSeq
//      }
//    }
    def crowdingDistanceDeb(implicit
      multiple: MultipleCriteria[C],
      criterionToDouble: MultipleCriteria.CriterionToDouble[C]
    ) = new DiversityMeasure[D, C] {
      def apply(seq: IndexedSeq[Evaluated[D, C]]): IndexedSeq[Double] = {
        val idx = Array.ofDim[Int](seq.size)
        val dist = Array.ofDim[Double](seq.size)
        for (criterion <- 0 until multiple.numberOfCriteria) {
          Sorting.stableSort(idx, {
            (l: Int, r: Int) => multiple.orderingForCriterion(criterion).compare(seq(l).output, seq(r).output) < 0
          })
          def c2d(i: Int) = criterionToDouble.criterionToDouble(seq(idx(i)).output, criterion)
          val min = c2d(0)
          val max = c2d(seq.size - 1)
          if (max != min) {
            val diff = 1.0 / math.abs(max - min)
            for (i <- 1 until seq.size - 1) {
              dist(i) += math.abs(c2d(i + 1) - c2d(i - 1)) * diff
            }
            dist(0) = Double.PositiveInfinity
            dist(seq.size - 1) = Double.PositiveInfinity
          }
        }
        dist.toIndexedSeq
      }
    }
  }

  def apply[D : DomainType, C : CodomainType](): Detected[D, C] = new Detected
}
