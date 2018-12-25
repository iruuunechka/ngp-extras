package ru.ifmo.ctd.ngp.theory.onemax.argmax

import java.util.Locale

import ru.ifmo.ctd.ngp.util._
import ru.ifmo.ctd.ngp.theory.onemax.Config

/**
 * Main class for OneMax under argmax reinforcement learning.
 *
 * @author Maxim Buzdalov
 */
object Main extends App {
  def theoryVsPractice() {
    for (n <- 3 to 16) {
      val theoretical = Theory.solve(n)
      val theoreticalES = Theory.solveES(n)
      val count = math.max(1, (100000 / theoretical).ceil.toInt)
      val rmhc = (0 until count).par.map(_ => new Config(n, 0, false).run()).sum / count.toDouble
      val es11 = (0 until count).par.map(_ => new Config(n, 0, true).run()).sum / count.toDouble
      println(s"n = $n: Th = $theoretical, Pr = $rmhc, ThES = $theoreticalES, PrES = $es11, Th/Pr = ${theoretical / rmhc}, ThES/PrES = ${es11 / theoreticalES}")
    }
  }

  def theoryVsRMHC() {
    for (n <- (1 to 6).map(math.pow(10, _).toInt)) {
      val theoretical = Theory.solve(n)
      val rmhc = (1 to n).mapSum(n.toDouble / _) + 1
      println(s"n = $n: Th = $theoretical, RMHC = $rmhc, Th/RMHC = ${theoretical / rmhc}")
    }
  }

  def boundVsPractice() {
    for (n <- 10 to 60 by 10) {
      val es11 = (0 until 100000).par.map(_ => new Config(n, 0, true).run()).seq
      val mean = es11.sum.toDouble / es11.size
      val stddev = math.sqrt(es11.map(i => (i - mean) * (i - mean)).sum / es11.size)
      val estimate1 = 2 * math.E * n * math.log(n)
      val estimate2 = 16.0 / 7.0 * math.E * math.E * n * math.log(n)
      println("%d & %.2f & %.2f & %.2f & %.2f".formatLocal(Locale.US, n, mean, stddev, estimate1, estimate2))
    }
  }

  theoryVsPractice()
  println("===========================")
  theoryVsRMHC()
  println("===========================")
  boundVsPractice()
}
