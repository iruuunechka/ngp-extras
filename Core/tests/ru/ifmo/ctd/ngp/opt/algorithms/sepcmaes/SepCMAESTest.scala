package ru.ifmo.ctd.ngp.opt.algorithms.sepcmaes

import org.junit.{Assert, Test}

import ru.ifmo.ctd.ngp.opt.Optimizer
import ru.ifmo.ctd.ngp.opt.termination.CodomainThreshold
import ru.ifmo.ctd.ngp.util._

/**
 * A simple test that ensures the DiagonalCMAES works for Rosenbrock function
 * @author Maxim Buzdalov
 */
class SepCMAESTest {
  def test(config: SepCMAESConfig, depth: Int = 0) {
    config.run() match {
      case Optimizer.Result(_, CodomainThreshold) =>
      case _ =>
        Assert.assertTrue("Diagonal CMA-ES works too long for Rosenbrock function", depth < 5)
        test(config, depth + 1)
    }
  }

  def sq(x: Double): Double = x * x

  @Test
  def rosenbrock() {
    test(new SepCMAESConfig(
      y => (0 until y.size - 1).mapSum(i => 100 * sq(sq(y(i)) - y(i + 1)) + sq(y(i) - 1)),
      IndexedSeq.fill(10)(FastRandom.threadLocal().nextDouble() * 100 - 20),
      100.0 / 3,
      1e-9
    ))
  }
}
