package ru.ifmo.ctd.ngp.theory.onemax

import ru.ifmo.ctd.ngp.opt._
import ru.ifmo.ctd.ngp.opt.misc.earl.{EARLConfiguration, EARLCodomainComparator}
import ru.ifmo.ctd.ngp.util.Bits
import ru.ifmo.ctd.ngp.opt.iteration.{Mutation, Update, Selection}
import ru.ifmo.ctd.ngp.opt.listeners.EvaluationCount
import ru.ifmo.ctd.ngp.opt.termination.CodomainThreshold
import ru.ifmo.ctd.ngp.learning.reinforce.q.EGreedyAgent
import ru.ifmo.ctd.ngp.opt.multicriteria.MultipleCriteria

class Config(n: Int, eps: Double, onePlusOne: Boolean) extends OptConfiguration[Long, IndexedSeq[Int]] {
  require(n <= 64, "N should not exceed 64")

  implicit val multiple        = MultipleCriteria.fromIndexedSeqWithElementOrdering("OneMax", "ZeroMax")
  implicit val comparator      = EARLCodomainComparator().fromMultipleCriteria(0)
  implicit val evaluator       = Evaluator().usingFunction(v => IndexedSeq(Bits.bitCount(v), n - Bits.bitCount(v)))
  implicit val initialization  = Initialization().fromDomains(Some(0L))
  implicit val termination     = Termination.Pluggable()
  implicit val selection       = Selection().all
  implicit val update          = Update().best
  implicit val mutation        = if (onePlusOne) {
    Mutation().using { x =>
      var t = x
      val rng = random()
      for (i <- 0 until n if rng.nextInt(n) == 0) {
        t ^= 1L << i
      }
      t
    }
  } else {
    Mutation().using(_ ^ (1L << random().nextInt(n)))
  }
  implicit val iteration       = Iteration().fromSelectionMutationEvaluateUpdate
  implicit val optimizer       = Optimizer().simple
  implicit val evaluationCount = new EvaluationCount()

  CodomainThreshold().register(_(0), n)

  EARLConfiguration().registerOldWay(
    new EGreedyAgent(eps, 1.0, 0.5, 0.5),
    v => v(0).output(0),
    (prev, next) => next(0).output(0) - prev(0).output(0)
  )

  def run() = {
    val Optimizer.Result(_, CodomainThreshold) = optimizer()
    evaluationCount()
  }
}
