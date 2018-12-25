package ru.ifmo.ctd.ngp.theory.onemax.egreedy

/**
 * Some theoretical exercises on running time of OneMax model problem with e-greedy reinforcement learning.
 *
 * @author Maxim Buzdalov
 */
object Theory {
  def solve(n: Int, eps: Double) = {
    val z = Array.ofDim[Double](n)
    z(0) = 1 / (1 - eps)
    for (i <- 1 until n) {
      z(i) = (n + z(i - 1) * eps * i) / (1 - eps) / (n - i)
    }
    z.sum + 1
  }
}
