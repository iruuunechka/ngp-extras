package ru.ifmo.ctd.ngp.theory.onemax.egreedy

import ru.ifmo.ctd.ngp.theory.onemax.Config

/**
 * Main class for OneMax under e-greedy reinforcement learning.
 *
 * @author Maxim Buzdalov
 */
object Main extends App {
  def theoryVsPractice() {
    for (eps <- List(0.0, 0.2, 0.4); n <- 5 to 10) {
      val theoretical = Theory.solve(n, eps)
      val count = (100000 / theoretical).ceil.toInt
      val ideal = (0 until count).par.map(_ => Practice.solve(n, eps)).sum / count.toDouble
      val practical = (0 until count).par.map(_ => new Config(n, eps * 2, false).run()).sum / count.toDouble
      println(s"n = $n, eps = $eps: Th = $theoretical, Id = $ideal Pr = $practical, Th/Id = ${theoretical / ideal}, Th/Pr = ${theoretical / practical}")
    }
  }

  def theories() {
    for (eps <- List(0.0, 0.01, 0.02, 0.03, 0.04, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6)) {
      for (n <- (2 to 11).map(1 << _)) {
        val theoretical = Theory.solve(n, eps)
        val th0 = Theory.solve(n, 0)
        println(s"n = $n, eps = $eps: Th = $theoretical, log(Th/Th0) = ${math.log(theoretical / th0)}")
      }
      println("---------------------")
    }
  }

  theoryVsPractice()
  println("=======================")
  theories()
}
