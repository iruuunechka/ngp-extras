package ru.ifmo.ctd.ngp.theory.xdivk

import ru.ifmo.ctd.ngp.theory.Math._

/**
 * Main class for [x/k].
 *
 * @author Maxim Buzdalov
 */
object Main extends App {
  def guessAsymptotic() {
    for (d <- 1 to 5) {
      for (m <- 1 to 1000) {
        val exact = Theory.solve(d * m, d)
        val estimation = math.pow(m, d) * math.log(m)
        println(s"n = ${d * m}, d = $d => $exact; $estimation; ${estimation / exact}")
      }
    }
  }

  def theoryVsPractice() {
    for (n <- List(4, 8, 16, 32, 64); d <- List(1, 2, 4) if d <= n) {
      val theoretical = Theory.solve(n, d)
      val count = (10000000 / theoretical).ceil.toInt
      val practical = (0 until count).par.map(_ => Practice.solve(n, d)).sum / count.toDouble
      println(s"n = $n, d = $d: Th = $theoretical, Pr = $practical, Th/Pr = ${theoretical / practical}")
    }
  }

  def compareFormulae() {
    for (n <- 3 to 20; m <- divisors(n) if m != n; mul <- 0 until n / m; d <- 0 until m) {
      val x = m * mul + d
      val vz = Theory.z(n, x, d)
      val vz2 = Theory.z2(n, x, d)
      if (math.abs(vz - vz2) > 1e-9) {
        println(s"n = $n, x = $x, d = $d: z = $vz, z2 = $vz2")
      }
    }
    for (n <- 3 to 20; m <- divisors(n) if m != n) {
      val vz = Theory.solve(n, m)
      val vz2 = Theory.solve2(n, m)
      if (math.abs(vz - vz2) > 1e-9) {
        println(s"n = $n, m = $m: z = $vz, z2 = $vz2")
      }
    }
  }

  if (args.length == 0) {
    compareFormulae()
  } else args(0) match {
    case "guessAsymptotic" => guessAsymptotic()
    case "theoryVsPractice" => theoryVsPractice()
    case "compareFormulae" => compareFormulae()
    case _ => println(s"Unknown key: ${args(0)}")
  }
}
