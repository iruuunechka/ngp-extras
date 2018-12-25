package ru.ifmo.ctd.ngp.theory.mhiff.h1010

import scala.collection.BitSet

import ru.ifmo.ctd.ngp.learning.reinforce.Agent
import ru.ifmo.ctd.ngp.opt._
import ru.ifmo.ctd.ngp.opt.misc.earl.{EARLConfiguration, EARLCodomainComparator}
import ru.ifmo.ctd.ngp.opt.iteration.{Mutation, Update, Selection}
import ru.ifmo.ctd.ngp.opt.listeners.EvaluationCount
import ru.ifmo.ctd.ngp.opt.termination.{EvaluationLimit, CodomainThreshold}
import ru.ifmo.ctd.ngp.opt.multicriteria.MultipleCriteria
import ru.ifmo.ctd.ngp.opt.listeners.BestEvaluated
import ru.ifmo.ctd.ngp.util._

/**
 * Configuration for MH-IFF controlled by (epsilon)-greedy RL algorithm.
 * @param n the problem size.
 * @param homogeneous true if independent bit mutation should be used, false if single-bit mutation should be used.
 * @param lambda the number of children in (1+lambda) algorithms.
 * @param nStates true if RL should define the state from LeadingOnes fitness, false if a single state should be used.
 * @param init the (initial) guess for optimization.
 * @param rlAlgorithm the RL agent that should be used, None if no algorithm should be used.
 */
class Config(n: Int, homogeneous: Boolean, lambda: Int, nStates: Boolean, init: Int,
             rlAlgorithm: Option[Agent[Int, Int]])
  extends OptConfiguration[BitSet, IndexedSeq[Int]]
{
  require((n & (n - 1)) == 0, "n is not a power of two")
  private implicit val multiple        = MultipleCriteria.fromIndexedSeqWithElementOrdering(
      "H-IFF", "H-IFF0", "H-IFF1", "H1010"
  )
  private implicit val comparator      = EARLCodomainComparator().fromMultipleCriteria(init)
  private implicit val evaluator       = Evaluator().usingFunction { v: BitSet =>
    var hiff = 0
    var hiff0 = 0
    var hiff1 = 0
    
    def range(l: Int, r: Int): Int = {
      val rv = if (l + 1 == r) {
        if (v(l)) 1 else 0
      } else {
        val mid = (l + r) / 2
        val left = range(l, mid)
        val right = range(mid, r)
        if (left == right) left else -1
      }
      val size = r - l
      if (rv == 0) {
        hiff0 += size
        hiff += size        
      } else if (rv == 1) {
        hiff1 += size
        hiff += size
      }
      rv
    }
    range(0, n)
    
    val h1010 = (0 until n).mapSum(i => if (v(i) == (i % 2 == 0)) 1 else 0)
    
    IndexedSeq(hiff, hiff0, hiff1, h1010)
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
  private implicit val selection       = Selection().allCopied(lambda)
  private implicit val update          = Update().best
  private implicit val mutation        = Mutation().using(
    if (homogeneous) {
      Mutation.Standard.BitSet.independentPointMutation(n)
    } else {
      Mutation.Standard.BitSet.singlePointMutation(n)
    }
  )
  private implicit val iteration       = Iteration().fromSelectionMutationEvaluateUpdate
  private implicit val optimizer       = Optimizer().simple
  private implicit val evaluationCount = new EvaluationCount()
  private implicit val bestEvaluated   = new BestEvaluated()

  CodomainThreshold().register(_(0), n * (1 + Integer.numberOfTrailingZeros(n)))
  EvaluationLimit().register(500000)

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
