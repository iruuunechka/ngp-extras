package ru.ifmo.ctd.ngp.theory.unrelated.onemax

import ru.ifmo.ctd.ngp.learning.reinforce.Agent
import ru.ifmo.ctd.ngp.opt._
import ru.ifmo.ctd.ngp.opt.iteration.{Mutation, Selection, Update}
import ru.ifmo.ctd.ngp.opt.listeners.EvaluationCount
import ru.ifmo.ctd.ngp.opt.misc.earl.{EARLCodomainComparator, EARLConfiguration}
import ru.ifmo.ctd.ngp.opt.multicriteria.MultipleCriteria
import ru.ifmo.ctd.ngp.opt.termination.{CodomainThreshold, EvaluationLimit}

import scala.collection.BitSet

/**
 * Configuration for OneMax + OneMax' controlled by a greedy RL algorithm.
 * @param n the problem size.
 * @param onePlusOne true if independent bit mutation should be used, false if single-bit mutation should be used.
 * @param nStates true if RL should define the state from LeadingOnes fitness, false if a single state should be used.
 * @param rlAlgorithm the RL agent that should be used, None if no algorithm should be used.
 */
class Config(
  n: Int,
  generationSize: Int,
  onePlusOne: Boolean,
  nStates: Boolean,
  rlAlgorithm: Option[Agent[Int, Int]],
  secondRef: BitSet
) extends OptConfiguration[BitSet, IndexedSeq[Int]] {
  private def randomInit(): BitSet = {
    BitSet(IndexedSeq.fill(n)(random().nextBoolean()).zipWithIndex.filter(_._1).map(_._2) :_*)
  }

  private implicit val multiple        = MultipleCriteria.fromIndexedSeqWithElementOrdering("OneMax", "OneMax'")
  private implicit val comparator      = EARLCodomainComparator().fromMultipleCriteria(random().nextInt(2))
  private implicit val evaluator       = Evaluator().usingFunction { v: BitSet =>
    IndexedSeq(v.size, (v ^ secondRef).size)
  }
  private implicit val initialization  = Initialization().fromDomains(IndexedSeq.fill(generationSize)(randomInit()))
  private implicit val termination     = Termination.Pluggable()
  private implicit val selection       = Selection().random(1)
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
        if (nStates) v => v.map(_.output(0)).max else _ => 0,
        (p, n) => n.map(_.output(0)).max - p.map(_.output(0)).max
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
