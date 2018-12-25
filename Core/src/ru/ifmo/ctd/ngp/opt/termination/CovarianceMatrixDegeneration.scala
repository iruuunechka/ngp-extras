package ru.ifmo.ctd.ngp.opt.termination

import scala.language.higherKinds

import ru.ifmo.ctd.ngp.opt.Termination
import ru.ifmo.ctd.ngp.opt.types.{WorkingSetType, CodomainType, DomainType}
import ru.ifmo.ctd.ngp.opt.algorithms.SepCMAES

/**
 * A termination criterion based on covariance matrix degeneration.
 *
 * @author Maxim Buzdalov
 */
case object CovarianceMatrixDegeneration extends Termination.Reason {
  def reasonText = "covariance matrix degenerated to nearly zeros"

  class Detected[D: DomainType, C: CodomainType, W[+_D, +_C]: WorkingSetType] {
    def forDiagonalCMAES(epsilon: Double)(
      implicit workingSetTyper: W[D, C] => SepCMAES.WorkingSet,
               pluggable: Termination.Pluggable[D, C, W]
    ) {
      pluggable += { w =>
        val maxValue = workingSetTyper(w).cov.max
        if (maxValue < epsilon) {
          Some(CovarianceMatrixDegeneration)
        } else {
          None
        }
      }
    }
  }

  def apply[D: DomainType, C: CodomainType, W[+_D, +_C]: WorkingSetType](): Detected[D, C, W] = new Detected()
}
