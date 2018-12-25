package ru.ifmo.ctd.ngp.theory.royalroads

import scala.collection.BitSet
import ru.ifmo.ctd.ngp.learning.reinforce.Agent
import ru.ifmo.ctd.ngp.opt._
import ru.ifmo.ctd.ngp.opt.misc.earl.{EARLConfiguration, EARLCodomainComparator}
import ru.ifmo.ctd.ngp.opt.iteration.{Mutation, Update, Selection}
import ru.ifmo.ctd.ngp.opt.listeners.EvaluationCount
import ru.ifmo.ctd.ngp.opt.termination.{EvaluationLimit, CodomainThreshold}
import ru.ifmo.ctd.ngp.opt.multicriteria.MultipleCriteria
import ru.ifmo.ctd.ngp.opt.listeners.BestEvaluated

/**
 * Configuration for MH-IFF controlled by (epsilon)-greedy RL algorithm.
 * @param n the problem size.
 * @param onePlusOne true if independent bit mutation should be used, false if single-bit mutation should be used.
 * @param nStates true if RL should define the state from LeadingOnes fitness, false if a single state should be used.
 * @param init the (initial) guess for optimization.
 * @param rlAlgorithm the RL agent that should be used, None if no algorithm should be used.
 */
class Config(n: Int, onePlusOne: Boolean, nStates: Boolean, init: Int, 
             rlAlgorithm: Option[Agent[Int, Int]])
  extends OptConfiguration[BitSet, IndexedSeq[Int]]
{
  private val sqrt = math.sqrt(n).toInt
  require(n == sqrt * sqrt, "n is not a square")
  private implicit val multiple        = MultipleCriteria.fromIndexedSeqWithElementOrdering(
      "RoyalRoads", "ZeroMax"
  )
  private implicit val comparator      = EARLCodomainComparator().fromMultipleCriteria(init)
  private implicit val evaluator       = Evaluator().usingFunction { v: BitSet =>
    var royal = 0
    for (_ <- 0 until n by sqrt) {
      var allOnes = true
      for (j <- 0 until sqrt) {
        if (!v(j)) allOnes = false
      }
      if (allOnes) royal += sqrt
    }    
    IndexedSeq(royal, n - v.size)
  }
  private implicit val initialization  = Initialization().fromDomains(Some {
    val mbs = BitSet.newBuilder
    val rng = random()
    for (i <- 0 until n) {
      if (rng.nextBoolean()) {
        mbs += i
      }
    }
    mbs.result()
  })
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
  private implicit val bestEvaluated   = new BestEvaluated()

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

  def run(): Int = {
    optimizer() match {
      case Optimizer.Result(_, _) => bestEvaluated().get.output(0)
    }
  }
}
