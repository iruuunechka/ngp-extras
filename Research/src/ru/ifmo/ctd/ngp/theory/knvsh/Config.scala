package ru.ifmo.ctd.ngp.theory.knvsh

import scala.collection.immutable.BitSet
import ru.ifmo.ctd.ngp.learning.reinforce.Agent
import ru.ifmo.ctd.ngp.opt._
import ru.ifmo.ctd.ngp.opt.iteration.{Mutation, Selection, Update}
import ru.ifmo.ctd.ngp.opt.listeners.EvaluationCount
import ru.ifmo.ctd.ngp.opt.misc.earl.{EARLConfiguration, EARLInterface}
import ru.ifmo.ctd.ngp.opt.termination.{CodomainThreshold, EvaluationLimit}

import Config.{Individuals, FunctionSetup}

/**
  * A configuration for various Arina's researches on agents.
  */
class Config(
  setup: FunctionSetup,
  initialIndividual: Int => BitSet,
  initialHelperChoice: Int,
  learningSetup: Option[(
    Agent[Int, Int],
    Individuals => Int,
    (Individuals, Individuals) => Double
  )],
  strategy: Config.Strategy,
  evaluationLimit: Int
) extends OptConfiguration[BitSet, Seq[Int]] {
  private implicit val evaluator = Evaluator().usingFunction(setup.function)
  private implicit val initialization = Initialization().fromDomains(Seq(initialIndividual(setup.size)))
  private implicit val termination = Termination.Pluggable()
  private implicit val selection = Selection().all
  private implicit val mutation = Mutation().using { v =>
    val idx = random().nextInt(setup.size)
    if (v(idx)) v - idx else v + idx
  }

  private implicit val update = new Update[domain.Type, codomain.Type, workingSet.Type] with EARLInterface {
    private[this] var theChoice = initialHelperChoice

    override def currentChoice: Int = theChoice
    override def currentChoice_=(newChoice: Int): Unit = theChoice = newChoice
    override def choices: Int = setup.functionDimension - 1
    override def initialChoice: Int = initialHelperChoice
    override def apply(ws: Individuals, evaluated: Individuals): Individuals = strategy(ws, evaluated, theChoice)
  }

  private implicit val iteration = Iteration().fromSelectionMutationEvaluateUpdate
  private implicit val optimizer = Optimizer().simple
  private implicit val evaluationCount = new EvaluationCount()

  if (learningSetup.nonEmpty) {
    val (agent, stateFn, rewardFn) = learningSetup.get
    EARLConfiguration().registerOldWay(agent, stateFn, rewardFn)
  }

  CodomainThreshold().register(_.head, setup.targetMaximum)
  EvaluationLimit().register(evaluationLimit)

  def run(): Double = {
    optimizer() match {
      case Optimizer.Result(_, CodomainThreshold) => evaluationCount().toDouble
      case Optimizer.Result(_, EvaluationLimit) => Double.PositiveInfinity
      case Optimizer.Result(_, _) => throw new AssertionError("Should not happen")
    }
  }
}

object Config {
  type Individuals = IndexedSeq[Evaluated[BitSet, Seq[Int]]]

  trait FunctionSetup {
    def size: Int
    def targetMaximum: Int
    def function(v: BitSet): IndexedSeq[Int]
    val functionDimension: Int = function(BitSet.empty).size
  }

  trait Strategy {
    def apply(prev: Individuals, next: Individuals, helper: Int): Individuals
  }
}
