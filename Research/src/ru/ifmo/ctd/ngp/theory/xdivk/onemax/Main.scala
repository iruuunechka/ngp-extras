package ru.ifmo.ctd.ngp.theory.xdivk.onemax

import ru.ifmo.ctd.ngp.learning.reinforce.q.{DelayedAgent, EGreedyAgent}
import ru.ifmo.ctd.ngp.learning.reinforce.{WaitingAgent, RandomAgent}
import scala.collection.parallel.ParSeq

/**
 * Main for XdivK+OneMax controlled by (epsilon)-greedy RL algorithm.
 *
 * @author Maxim Buzdalov
 */
object Main extends App {
  val runs = 101
  for (onePlusOne <- Seq(false, true)) {
    println(if (onePlusOne) "(1+1)-ES" else "RMHC")
    for (k <- 1 to 5; n <- 2 to 10 map (k * _)) {
      def averageOf(f: => Double) = ParSeq.fill(runs)(f).toIndexedSeq.sum / runs

      val startTime = System.currentTimeMillis()
      val resLO  = averageOf(new Config(n, k, onePlusOne, 0, None).run())
      val resOM  = averageOf(new Config(n, k, onePlusOne, 1, None).run())
      val resRL  = averageOf(new Config(n, k, onePlusOne, 0, Some(new EGreedyAgent(0.0, 1.0, 0.5, 0.5))).run())
      val resRN  = averageOf(new Config(n, k, onePlusOne, 0, Some(new RandomAgent())).run())
      val resWA  = averageOf(new Config(n, k, onePlusOne, 0, Some(new WaitingAgent())).run())
      val resD1  = averageOf(new Config(n, k, onePlusOne, 0, Some(new DelayedAgent(0, 0.01, 100, 0.2))).run())
      val resD2  = averageOf(new Config(n, k, onePlusOne, 0, Some(new DelayedAgent(0, 0.1, 5, 0.001))).run())
      val totalTime = System.currentTimeMillis() - startTime
      val theory = if (onePlusOne) "" else s", Th(RN) = ${Theory.solve(n, k)}"
      println(s"$n, $k: XDK = $resLO, OM = $resOM, D1 = $resD1, D2 = $resD2, WA = $resWA, RL = $resRL, RN = $resRN$theory in $totalTime ms")
    }
  }
}
