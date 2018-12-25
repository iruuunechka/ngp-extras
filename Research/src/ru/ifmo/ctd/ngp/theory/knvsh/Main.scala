package ru.ifmo.ctd.ngp.theory.knvsh

import ru.ifmo.ctd.ngp.learning.reinforce.q.EGreedyAgent
import ru.ifmo.ctd.ngp.learning.reinforce.{Agent, Environment}
import ru.ifmo.ctd.ngp.opt.RandomSource
import ru.ifmo.ctd.ngp.theory.knvsh.Config.FunctionSetup

import scala.collection.immutable.BitSet

/**
  * A main file for various Arina's researches on agents.
  */
object Main {
  object Functions {
    def oneMax(v: BitSet) = v.size
    def leadingOnes(v: BitSet) = (0 to v.size).find(i => !v(i)).get

    class OneMaxZeroMax(val size: Int) extends FunctionSetup {
      override def targetMaximum = size
      override def function(v: BitSet) = {
        val om = oneMax(v)
        IndexedSeq(om, om, size - om)
      }
    }

    class LeadingOnesOneMax(val size: Int) extends FunctionSetup {
      override def targetMaximum = size
      override def function(v: BitSet) = {
        val lo = leadingOnes(v)
        val om = oneMax(v)
        IndexedSeq(lo, lo, om)
      }
    }

    class SwitchPointPlain(val size: Int, k: Int, swp: Int) extends FunctionSetup{
      require(size % k != 0)
      override def targetMaximum = size / k
      override def function(v: BitSet) = {
        val om = oneMax(v)
        IndexedSeq(om / k, om / k, if (om >= swp) swp else om, if (om >= swp) om else swp)
      }
    }

    class SwitchPointStrong(val size: Int, k: Int, swp: Int) extends FunctionSetup {
      require(size % k != 0)
      override def targetMaximum = size / k
      override def function(v: BitSet) = {
        val om = oneMax(v)
        IndexedSeq(om / k, om / k, if (om >= swp) size - om else om, if (om >= swp) om else size - om)
      }
    }
  }

  object Strategies {
    import Config.{Individuals, Strategy}

    class BestByHelper(populationSize: Int) extends Strategy {
      override def apply(prev: Individuals, next: Individuals, helper: Int): Individuals = {
        (next ++ prev).sortBy(-_.output(helper + 1)).take(populationSize)
      }
    }

    object NewStrategy extends Strategy {
      override def apply(prev: Individuals, next: Individuals, helper: Int): Individuals = {
        val total = next ++ prev
        val bestByTarget = total.maxBy(_.output.head)
        val bestByHelper = total.filter(_ != bestByTarget).maxBy(_.output(helper + 1))
        IndexedSeq(bestByTarget, bestByHelper)
      }
    }
  }

  object Agents {
    object SaRA extends Agent[Int, Int] {
      override def refresh(): Unit = ()
      override def makeClone(): Agent[Int, Int] = this
      override def learn(environment: Environment[Int, Int]): Int = {
        val actions = environment.actionsCount()
        while (true) {
          for (size <- 0 until 31) {
            for (a <- 0 until actions) {
              for (_ <- 0 until 1 << size) {
                environment.applyAction(a)
              }
            }
          }
        }
        throw new Error("Will never get there anyway")
      }
    }

    def eGreedy(eps: Double) = new EGreedyAgent[Int, Int](eps, 0.5, 0.5, 0.5)
  }

  object StatesRewards {
    import Config.Individuals

    private def bestTarget(g: Individuals) = g.map(_.output.head).max
    private def sumTarget(g: Individuals) = g.map(_.output.head).sum

    val dummyState = (_: Individuals) => 0
    val dummyReward = (_: Individuals, _: Individuals) => 0.0

    val bestTargetState = (individuals: Individuals) => bestTarget(individuals)
    val bestTargetDiffReward = (prev: Individuals, next: Individuals) => bestTarget(next) - bestTarget(prev).toDouble

    val sumTargetDiffReward = (prev: Individuals, next: Individuals) => sumTarget(next) - sumTarget(prev).toDouble
  }

  object Initializations {
    val zeroIndividual   = (_: Int) => BitSet.empty
    val randomIndividual = (n: Int) => BitSet((0 until n).filter(_ => RandomSource.fastRandom().nextBoolean()) :_*)
  }

  def evaluate(runs: Int)(config: => Config): String = {
    val results = (0 until runs).par.map(_ => config.run()).seq
    s"{ min = ${results.min}    avg = ${results.sum / runs}    max = ${results.max} }"
  }

  def main(args: Array[String]): Unit = {
    import Agents._
    import Functions._
    import Initializations._
    import StatesRewards._
    import Strategies._

    val runs = 100
    val size = 300
    val budget = 100000

    for ((strategy, name, reward) <- Seq(
      (new BestByHelper(1), "EARL[reward = best diff]", bestTargetDiffReward),
      (NewStrategy, "NEW [reward = sum diff] ", sumTargetDiffReward)
    )) {
      def sara = Some((SaRA, dummyState, dummyReward))
      def greedyN = Some((eGreedy(0.0), bestTargetState, reward))
      def greedy1 = Some((eGreedy(0.0), dummyState, reward))
      def eGreedyN = Some((eGreedy(0.1), bestTargetState, reward))
      def eGreedy1 = Some((eGreedy(0.1), dummyState, reward))

      println(s"$name[SARA]     @ LOOM: " + evaluate(runs) {
        new Config(new LeadingOnesOneMax(size), randomIndividual, 0, sara, strategy, budget)
      })

      println(s"$name[GreedyN]  @ OMZM: " + evaluate(runs) {
        new Config(new OneMaxZeroMax(size), randomIndividual, 1, greedyN, strategy, budget)
      })

      println(s"$name[GreedyN]  @ LOOM: " + evaluate(runs) {
        new Config(new LeadingOnesOneMax(size), randomIndividual, 0, greedyN, strategy, budget)
      })

      println(s"$name[Greedy1]  @ LOOM: " + evaluate(runs) {
        new Config(new LeadingOnesOneMax(size), randomIndividual, 0, greedy1, strategy, budget)
      })

      println(s"$name[eGreedyN] @ OMZM: " + evaluate(runs) {
        new Config(new OneMaxZeroMax(size), randomIndividual, 1, eGreedyN, strategy, budget)
      })

      println(s"$name[eGreedyN] @ LOOM: " + evaluate(runs) {
        new Config(new LeadingOnesOneMax(size), randomIndividual, 0, eGreedyN, strategy, budget)
      })

      println(s"$name[eGreedy1] @ LOOM: " + evaluate(runs) {
        new Config(new LeadingOnesOneMax(size), randomIndividual, 0, eGreedy1, strategy, budget)
      })
    }
  }
}
