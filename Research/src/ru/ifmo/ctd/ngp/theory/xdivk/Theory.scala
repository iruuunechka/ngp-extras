package ru.ifmo.ctd.ngp.theory.xdivk

import ru.ifmo.ctd.ngp.theory.Math.choose
import ru.ifmo.ctd.ngp.util._

/**
 * Some theoretical exercises on running time of [x/k] model problem.
 *
 * @author Maxim Buzdalov
 */
object Theory {
  /*
   * z(n, x, d) for strings of length "n"
   * is the expectation of number of steps to leave "x ones"
   * if standing at "x ones" and there is a floor "d" levels below.
   * The probability of going up is "(n - x) / n"
   *
   * z(n, x, 0) = n / (n - x)
   * z(n, x, 1) = (n * (n + 1)) / ((n - x) * (n - x + 1))
   *
   * The sum-version is z(n, x, d) = sum_{k=0}^{d}{C_{n}^{x-k}} / C_{n-1}{x}
   */
  def z(n: Int, x: Int, d: Int): Double = {
    if (d == 0) {
      n.toDouble / (n - x)
    } else {
      n.toDouble / (n - x) + z(n, x - 1, d - 1) * x / (n - x)
    }
  }

  def z2(n: Int, x: Int, d: Int) = {
    (0 to d).map(i => choose[Double](n, x - i)).sum / choose[Double](n - 1, x)
  }

  def solve(n: Int, k: Int) = {
    require(n % k == 0)
    1 + (0 until n).map(i => z(n, i, i % k)).sum
  }

  def solve2(n: Int, k: Int) = {
    require(n % k == 0)

    1 + {
      (0 until k) mapSum { j =>
        (0 until n / k) mapSum { m =>
          (j until k) mapSum { i =>
            choose[Double](n, m * k + i - j) / choose[Double](n - 1, m * k + i)
          }
        }
      }
    }
  }
}
