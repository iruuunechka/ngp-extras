package ru.ifmo.ctd.ngp.theory.onemax.egreedy

import ru.ifmo.ctd.ngp.util.FastRandom
import java.lang.Long.bitCount

/**
 * Some practical exercises on running time of OneMax model problem with e-greedy reinforcement learning.
 *
 * @author Maxim Buzdalov
 */
object Practice {
  def solve(n: Int, eps: Double) = {
    val rng = FastRandom.threadLocal()
    var x = 0L

    val fit1 = bitCount(_: Long)
    val fit0 = n - bitCount(_: Long)

    var fitnessCalls = 1

    while (bitCount(x) != n) {
      val y = x ^ (1L << rng.nextInt(n))
      fitnessCalls += 1
      val fun = if (rng.nextDouble() < eps) fit0 else fit1
      if (fun(y) >= fun(x)) {
        x = y
      }
    }

    fitnessCalls
  }
}
