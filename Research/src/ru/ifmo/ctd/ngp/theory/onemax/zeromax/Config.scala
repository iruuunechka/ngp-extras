package ru.ifmo.ctd.ngp.theory.onemax.zeromax

import scala.collection.BitSet

import ru.ifmo.ctd.ngp.learning.reinforce.Agent
import ru.ifmo.ctd.ngp.opt._
import ru.ifmo.ctd.ngp.opt.misc.earl.{EARLConfiguration, EARLCodomainComparator}
import ru.ifmo.ctd.ngp.opt.iteration.{Mutation, Update, Selection}
import ru.ifmo.ctd.ngp.opt.listeners.EvaluationCount
import ru.ifmo.ctd.ngp.opt.termination.{EvaluationLimit, CodomainThreshold}
import ru.ifmo.ctd.ngp.opt.multicriteria.MultipleCriteria

/**
 * Configuration for OneMax + ZeroMax controlled by (epsilon)-greedy RL algorithm.
 * @param n the problem size.
 * @param onePlusOne true if independent bit mutation should be used, false if single-bit mutation should be used.
 * @param nStates true if RL should define the state from LeadingOnes fitness, false if a single state should be used.
 * @param init the (initial) guess for optimization.
 * @param rlAlgorithm the RL agent that should be used, None if no algorithm should be used.
 */
class Config(n: Int, onePlusOne: Boolean, nStates: Boolean, init: Int, rlAlgorithm: Option[Agent[Int, Int]])
  extends OptConfiguration[BitSet, IndexedSeq[Int]]
{
  private implicit val multiple        = MultipleCriteria.fromIndexedSeqWithElementOrdering("OneMax", "ZeroMax")
  private implicit val comparator      = EARLCodomainComparator().fromMultipleCriteria(init)
  private implicit val evaluator       = Evaluator().usingFunction { v: BitSet =>
    val oneMax = v.size
    val zeroMax = n - oneMax
    IndexedSeq(oneMax, zeroMax)
  }
  private implicit val initialization  = Initialization().fromDomains(Some(BitSet.empty))
  private implicit val termination     = Termination.Pluggable()
  private implicit val selection       = Selection().all
  private implicit val update          = Update().best
  private implicit val mutation        = Mutation().using(
    if (onePlusOne) {
      Mutation.Standard.BitSet.independentPointMutation(n)
    } else {
      Mutation.Standard.BitSet.singlePointMutation(n)
    }
  )
  private implicit val iteration       = Iteration().fromSelectionMutationEvaluateUpdate
  private implicit val optimizer       = Optimizer().simple
  private implicit val evaluationCount = new EvaluationCount()

  CodomainThreshold().register(_(0), n)
  EvaluationLimit().register(100000)

  rlAlgorithm match {
    case Some(algo) =>
      EARLConfiguration().registerOldWay(
        algo,
        if (nStates) v => v(0).output(0) else _ => 0,
        (p, n) => n(0).output(0) - p(0).output(0)
      )
    case None =>
  }

  def run(): Double = {
    optimizer() match {
      case Optimizer.Result(_, CodomainThreshold) => evaluationCount().toDouble
      case Optimizer.Result(_, EvaluationLimit) => Double.PositiveInfinity
      case Optimizer.Result(_, _) => throw new AssertionError("Should not happen")
    }
  }
}
