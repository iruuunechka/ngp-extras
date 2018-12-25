package ru.ifmo.ctd.ngp.opt.algorithms.sepcmaes

import ru.ifmo.ctd.ngp.opt._
import ru.ifmo.ctd.ngp.opt.algorithms.SepCMAES
import ru.ifmo.ctd.ngp.opt.algorithms.SepCMAES.WorkingSetType
import ru.ifmo.ctd.ngp.opt.listeners.{BestEvaluated, EvaluationCount}
import ru.ifmo.ctd.ngp.opt.termination.{CodomainThreshold, CovarianceMatrixDegeneration, EvaluationLimit}

/**
 * A configuration for optimization of arbitrary functions using Diagonal CMA-ES.
 * @author Maxim Buzdalov
 */
class SepCMAESConfig(
  fun: IndexedSeq[Double] => Double,
  initialPoint: IndexedSeq[Double],
  initialStepSize: Double,
  threshold: Double
) extends OptConfiguration[IndexedSeq[Double], Double] {
  private val offspringCount = 4 + (3 * math.log(initialPoint.size)).toInt

  private implicit val evaluator       = Evaluator().usingFunction(fun)
  private implicit val comparator      = CodomainComparator().byOrdering.decreasing
  private implicit val (ws, init, itr) = SepCMAES().forIndexedSeqDomain(
    initialPoint, initialStepSize, 0, SepCMAES.logarithmicWeights(offspringCount, offspringCount / 2)
  )
  private implicit val termination     = new Termination.Pluggable
  private implicit val optimizer       = Optimizer().simple
  private implicit val evalCount       = new EvaluationCount()
  private implicit val bestEvaluated   = new BestEvaluated()

  CodomainThreshold().registerUsingBestEvaluated(threshold)
  EvaluationLimit().register(10000000)
  CovarianceMatrixDegeneration().forDiagonalCMAES(1e-250)

  def evaluationCount: Long = evalCount()

  def run(): Optimizer.Result[WorkingSetType[IndexedSeq[Double], Double]] = optimizer()
}
