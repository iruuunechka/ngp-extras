package ru.ifmo.ctd.ngp.theory.mhiff.h1010

import java.io.File
import java.io.PrintWriter
import java.util.Locale

import ru.ifmo.ctd.ngp.learning.reinforce.q.EGreedyAgent
import ru.ifmo.ctd.ngp.learning.reinforce.RandomAgent
import ru.ifmo.ctd.ngp.learning.reinforce.WaitingAgent
import ru.ifmo.ctd.ngp.util._

/**
 * Main for MH-IFF controlled by (epsilon)-greedy RL algorithm.
 *
 * @author Maxim Buzdalov
 */
object Main extends App {
  Locale.setDefault(Locale.US)

  val runs = 30
  val rootDir = "mhiff-logs"
  new File(rootDir).mkdir()

  object StateInfo extends Enumeration {
    type StateInfo = Value
    val OneState = Value("1")
    val NStates = Value("n")
    val NoStates = Value("a")
  }
  import StateInfo._

  class Task(namePrefix: String, val onePlusOne: Boolean, val lambda: Int, val states: StateInfo, fun: Int => Double) {
    def run(n: Int) = fun(n)
    def name = s"$namePrefix-${if (onePlusOne) "one" else "rmhc"}-$lambda-$states"
    def simpleName = s"$namePrefix-$lambda"
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
    lambda <- Seq(1)
    states <- Seq(OneState, NStates)
    (name, start, agent) <- Seq(
      ("rg", 0, Some(new EGreedyAgent[Int, Int](0.0, 1.0, 0.5, 0.5))),
      ("wa", 0, Some(new WaitingAgent[Int, Int]()))
    ) filter (t => states == OneState || onePlusOne || t._1 != "rg")
  } yield {
    new Task(name, onePlusOne, lambda, states,
      new Config(_, onePlusOne, lambda, states == NStates, start, agent).run()) with Avg
  }

  val practiceWithoutStates = for {
    onePlusOne <- Seq(false, true)
    lambda <- Seq(1)
    (name, start, agent) <- Seq(
      ("hiff", 0, None),
      ("hiff0", 1, None),
      ("hiff1", 2, None),
      ("rn", 0, Some(new RandomAgent[Int, Int]()))
    )
  } yield {
    new Task(name, onePlusOne, lambda, NoStates,
      new Config(_, onePlusOne, lambda, false, start, agent).run()) with Avg
  }

  val allTasks = practiceWithoutStates ++ practiceWithStates

  println("MH-IFF with 1010")
  
  val (one, rmhc) = allTasks partition (_.onePlusOne)
  for ((tasks, title) <- Seq((rmhc, "RMHC"), (one, "(1+1)"))) {
    println(title)
    for (pow <- 1 to 10; n = 1 << pow) {
      def work(tasks: Seq[Task], name: String) {
        val results = tasks.sortBy(_.name).map(t => f"${t.simpleName}%s = ${t.run(n)}%.3f")
        println(s"$name: ${results mkString ", "}")
      }
      println(s"$n (${n * (1 + pow)}):")
      work(tasks filter (_.states == NoStates), "Stateless")
      work(tasks filter (_.states == OneState), "One state")
      work(tasks filter (_.states == NStates), "N states")
      println()
    }
  }
}
