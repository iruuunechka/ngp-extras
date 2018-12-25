package ru.ifmo.ctd.ngp.theory.leadingones.onemax

import java.io.File
import java.io.PrintWriter
import java.util.Locale

import ru.ifmo.ctd.ngp.learning.reinforce.q.EGreedyAgent
import ru.ifmo.ctd.ngp.learning.reinforce.RandomAgent
import ru.ifmo.ctd.ngp.learning.reinforce.WaitingAgent
import ru.ifmo.ctd.ngp.util._

/**
 * Main for LeadingOnes+OneMax controlled by (epsilon)-greedy RL algorithm.
 *
 * @author Maxim Buzdalov
 */
object Main extends App {
  Locale.setDefault(Locale.US)

  val runs = 1000
  val rootDir = "leading-ones-logs"
  new File(rootDir).mkdir()

  object StateInfo extends Enumeration {
    type StateInfo = Value
    val OneState = Value("1")
    val NStates = Value("n")
    val NoStates = Value("a")
  }
  import StateInfo._

  class Task(namePrefix: String, val onePlusOne: Boolean, val states: StateInfo, fun: Int => Double) {
    def run(n: Int) = fun(n)
    def name = s"$namePrefix-${if (onePlusOne) "one" else "rmhc"}-$states"
    def simpleName = namePrefix
  }

  trait Avg extends Task {
    override def run(n: Int) = {
      val data = (0 until runs).par.map(_ => super.run(n)).seq.sorted
      val pw = new PrintWriter(f"$rootDir%s/$name%s-$n%03d")
      for (d <- data) {
        pw.println(d)
      }
      pw.close()
      data.sum / runs
    }
  }

  val practiceWithStates = for {
    onePlusOne <- Seq(false, true)
    states <- Seq(OneState, NStates)
    (name, start, agent) <- Seq(
      ("rg", 0, Some(new EGreedyAgent[Int, Int](0.0, 1.0, 0.5, 0.5))),
      ("wa", 0, Some(new WaitingAgent[Int, Int]()))
    ) filter (t => states == OneState || onePlusOne || t._1 != "rg")
  } yield {
    new Task(name, onePlusOne, states, new Config(_, onePlusOne, states == NStates, start, agent).run()) with Avg
  }

  val practiceWithoutStates = for {
    onePlusOne <- Seq(false, true)
    (name, start, agent) <- Seq(
      ("lo", 0, None),
      ("om", 1, None),
      ("rn", 0, Some(new RandomAgent[Int, Int]()))
    )
  } yield {
    new Task(name, onePlusOne, NoStates, new Config(_, onePlusOne, false, start, agent).run()) with Avg
  }

  val theoryTasks = Seq(
    new Task("omTh",  false, NoStates, n => 1 + (0 until n).mapSum(i => n.toDouble / (n - i))),
    new Task("loTh",  false, NoStates, Theory.leadingOnesRMHC[Double]),
    new Task("rnTh",  false, NoStates, Theory.leadingOnesOneMaxNStatesRMHC[Double]),
    new Task("loThS", false, NoStates, n => 1 + n + n * (n - 1) / 2.0),
    new Task("rnThS", false, NoStates, n => 1 + n + n * (n - 1) / 3.0),
    new Task("rgThS", false, OneState, n => 1 + n + n * (n - 1) / 4.0 + (1 until n).mapSum(i => n.toDouble / i) / 2.0),
    new Task("rgTh",  false, OneState, Theory.leadingOnesOneMax1StateRMHC[Double])
  )
  val allTasks = practiceWithoutStates ++ practiceWithStates ++ theoryTasks

  val (one, rmhc) = allTasks partition (_.onePlusOne)
  for ((tasks, title) <- Seq((rmhc, "RMHC"), (one, "(1+1)"))) {
    println(title)
    for (n <- 2 to 99) {
      def work(tasks: Seq[Task], name: String) {
        val results = tasks.sortBy(_.name).map(t => f"${t.simpleName}%s = ${t.run(n)}%.3f")
        println(s"$name: ${results mkString ", "}")
      }
      println(s"$n:")
      work(tasks filter (_.states == NoStates), "Stateless")
      work(tasks filter (_.states == OneState), "One state")
      work(tasks filter (_.states == NStates), "N states")
      println()
    }
  }
}
