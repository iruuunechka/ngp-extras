package ru.ifmo.ctd.ngp.theory.xdivk.onemax

import java.util.Locale

import ru.ifmo.ctd.ngp.theory.Math._
import ru.ifmo.ctd.ngp.theory.xdivk.{Theory => XTheory}
import ru.ifmo.ctd.ngp.util._

/**
 * Theoretical investigations about XdivK+OneMax controlled by a RL agent.
 * @author Maxim Buzdalov
 */
object Theory {
  /*
   * z(n, x, d) for strings of length "n"
   * is the expectation of number of steps to leave "x ones"
   * if standing at "x ones" and there is a floor "d" levels below.
   */
  def z(n: Int, x: Int, d: Int): Double = {
    if (d == 0) {
      n.toDouble / (n - x)
    } else {
      n.toDouble / (n - x) + z(n, x - 1, d - 1) * x / (n - x) / 2
    }
  }

  def z2(n: Int, x: Int, d: Int) = {
    (0 to d).map(i => Fraction(choose[BigInt](n, x - i), 1 << i)).sum / choose[BigInt](n - 1, x)
  }

  def solve(n: Int, k: Int) = {
    require(n % k == 0)
    1 + (0 until n).map(i => z(n, i, i % k)).sum
  }

  def solve2(n: Int, k: Int) = {
    require(n % k == 0)

    1 + {
      (0 until k) mapSum { j =>
        ((0 until n / k) mapSum { m =>
          (j until k) mapSum { i =>
            Fraction(choose[BigInt](n, m * k + i - j), choose[BigInt](n - 1, m * k + i))
          }
        }) / (1 << j)
      }
    }
  }

  protected def tabulate() {
    Locale.setDefault(Locale.US)

    def sci2sci(value: Double) = {
      val str = f"$value%.2e"
      "$" + str.replace("e+0", "\\cdot 10^") + "$"
    }

    for (k <- 2 to 5) {
      for (nk <- 20 to 40 by 4) {
        val n = nk * k
        val thX = XTheory.solve(n, k)
        val thR = solve(n, k)
        println(f"$n%d & $k%d & ${sci2sci(thX)}%s & ${sci2sci(thR)}%s & ${thX / thR}%.2f\\\\")
      }
      if (k != 5) {
        println("\\hline")
      }
    }
    println()
    for (k <- 2 to 5) {
      for (nk <- 20 to 40 by 4) {
        val n = nk * k
        val thX = XTheory.solve(n, k)
        val thX2 = XTheory.solve(n / 2, k)
        println(f"$n%d & $k%d & ${sci2sci(thX)}%s & ${sci2sci(thX2)} & ${thX / thX2}%.2f\\\\")
      }
      if (k != 5) {
        println("\\hline")
      }
    }
  }
}
