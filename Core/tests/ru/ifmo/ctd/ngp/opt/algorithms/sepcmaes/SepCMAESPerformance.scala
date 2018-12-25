package ru.ifmo.ctd.ngp.opt.algorithms.sepcmaes

import ru.ifmo.ctd.ngp.opt.Optimizer
import ru.ifmo.ctd.ngp.opt.termination.{CovarianceMatrixDegeneration, EvaluationLimit, CodomainThreshold}
import ru.ifmo.ctd.ngp.util._

/**
 * A performance test for sep-CMA-ES.
 *
 * Is intended to check the performance both for an implementation constant factor and
 * for being efficient in terms of the number of fitness evaluations.
 *
 * @author Maxim Buzdalov
 */
object SepCMAESPerformance extends App {
  def test(name: String, config: SepCMAESConfig) {
    val runs: Seq[Option[Long]] = (0 until 20).map { _ =>
      val t0 = System.currentTimeMillis()
      val res = config.run()
      val time = System.currentTimeMillis() - t0
      val cnt = config.evaluationCount
      println(s"   ${res.terminationReason}, $cnt, time per FF call: ${time.toDouble / cnt / 1000} s")
      res match {
        case Optimizer.Result(_, CodomainThreshold) =>            Some(cnt)
        case Optimizer.Result(_, EvaluationLimit) =>              None
        case Optimizer.Result(_, CovarianceMatrixDegeneration) => None
        case _ => throw new AssertionError()
      }
    }
    val ok: Seq[Long] = runs.flatMap(_.toList)
    println(s"$name: okay: ${ok.size}, average: ${ok.sum.toDouble / ok.size}")
  }

  def sq(x: Double): Double = x * x

  for (size <- Seq(10, 100)) {
    test("Rosenbrock", new SepCMAESConfig(
      y => (0 until y.size - 1).mapSum(i => 100 * sq(sq(y(i)) - y(i + 1)) + sq(y(i) - 1)),
      IndexedSeq.fill(size)(FastRandom.threadLocal().nextDouble() * 100 - 20),
      100.0 / 3,
      1e-9
    ))
  }
}
