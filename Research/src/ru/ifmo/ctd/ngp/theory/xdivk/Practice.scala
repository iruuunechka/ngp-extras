package ru.ifmo.ctd.ngp.theory.xdivk

import ru.ifmo.ctd.ngp.util.Bits
import ru.ifmo.ctd.ngp.opt._
import ru.ifmo.ctd.ngp.opt.iteration.{Mutation, Update, Selection}
import ru.ifmo.ctd.ngp.opt.listeners.EvaluationCount
import ru.ifmo.ctd.ngp.opt.termination.CodomainThreshold

/**
 * Some practical exercises on running time of [x/k] model problem.
 *
 * @author Maxim Buzdalov
 */
object Practice {
  class Problem(n: Int, d: Int, startWithZero: Boolean) extends OptConfiguration[Long, Int] {
    require(n % d == 0, "N should be divisible by D")
    require(n <= 64, "N should not exceed 64")

    def generate() = if (startWithZero) 0L else random().nextLong() & Bits.nBitMaskLong(n)

    implicit val comparator      = CodomainComparator().byOrdering.increasing
    implicit val evaluator       = Evaluator().usingFunction(v => Bits.bitCount(v) / d)
    implicit val initialization  = Initialization().useDomainGenerator(generate(), 1)
    implicit val termination     = Termination.Pluggable()
    implicit val selection       = Selection().all
    implicit val update          = Update().best
    implicit val mutation        = Mutation().using(_ ^ (1L << random().nextInt(n)))
    implicit val iteration       = Iteration().fromSelectionMutationEvaluateUpdate
    implicit val optimizer       = Optimizer().simple
    implicit val evaluationCount = new EvaluationCount()

    CodomainThreshold().register(n / d)

    def run() = {
      val Optimizer.Result(_, CodomainThreshold) = optimizer()
      evaluationCount()
    }
  }

  def solve(n: Int, d: Int) = {
    new Problem(n, d, true).run()
  }
}
