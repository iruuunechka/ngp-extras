package ru.ifmo.ctd.ngp.theory.onemax.argmax

import ru.ifmo.ctd.ngp.theory.Math._

/**
 * Some theoretical exercises on running time of OneMax model problem with argmax of reinforcement learning.
 *
 * @author Maxim Buzdalov
 */
object Theory {
  def solve(n: Int) = {
    (0 until n).view.map(x => 2 + x * (1.0 / (n - x + 1) + 1.0 / (n - x))).sum + 1
  }
  def solveES(n: Int) = {
    val dp = Array.ofDim[Double](1 << n, n)
    val prob = 1.0 / n
    for (mask <- (dp.length - 1) to 0 by -1) {
      for (point <- (n - 1) to 0 by -1) {
        val ones = point
        val zeros = n - ones
        if ((mask & (1 << point)) == 0) {
          var sumP = 0.0
          var sumE = 0.0
          for (t0 <- 0 to zeros; t1 <- 0 to ones) {
            val p = 0.5 * math.pow(prob, t0 + t1) * math.pow(1 - prob, n - t0 - t1) * choose[Double](ones, t1) * choose[Double](zeros, t0)
            if (t0 != t1) {
              sumP += p
              sumE += p * (if (point + t0 - t1 == n) 0 else dp(mask | (1 << point))(point + t0 - t1))
            }
          }
          dp(mask)(point) = (1 + sumE) / sumP
        } else {
          var sumP = 0.0
          var sumE = 0.0
          for (t1 <- 0 to ones; t0 <- t1 + 1 to zeros) {
            val p = math.pow(prob, t0 + t1) * math.pow(1 - prob, n - t0 - t1) * choose[Double](ones, t1) * choose[Double](zeros, t0)
            sumP += p
            sumE += p * (if (point + t0 - t1 == n) 0 else dp(mask)(point + t0 - t1))
          }
          dp(mask)(point) = (1 + sumE) / sumP
        }
      }
    }
    for (mask <- dp.indices) {
      for (point <- (n - 1) to 0 by -1) {
        if ((mask & (1 << point)) != 0) {
          val prevMask = mask ^ (1 << point)
          for (z <- 0 until n) {
            assert(dp(prevMask)(z) >= dp(mask)(z))
          }
        }
      }
    }
    if (n >= 4) {
      for (mask <- dp.indices) {
        for (point <- (n - 2) to 0 by -1) {
          assert(dp(mask)(point) > dp(mask)(point + 1))
        }
      }
    }
    1 + dp(0)(0)
  }
}
