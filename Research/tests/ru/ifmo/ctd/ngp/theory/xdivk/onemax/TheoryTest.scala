package ru.ifmo.ctd.ngp.theory.xdivk.onemax

import org.junit.Test

import ru.ifmo.ctd.ngp.theory.Math._
import ru.ifmo.ctd.ngp.theory.xdivk.onemax.Theory._

/**
 * Tests whether Theory.main throws anything.
 *
 * @author Maxim Buzdalov
 */
class TheoryTest {
  @Test
  def run() {
    for (n <- 3 to 20; m <- divisors(n) if m != n; mul <- 0 until n / m; d <- 0 until m) {
      val x = m * mul + d
      val vz = z(n, x, d)
      val vz2 = z2(n, x, d).toDouble()
      assert(math.abs(vz - vz2) < 1e-9)
    }
    for (n <- 3 to 20; m <- divisors(n) if m != n) {
      val vz = solve(n, m)
      val vz2 = solve2(n, m).toDouble()
      assert(math.abs(vz - vz2) < 1e-9)
    }
  }
}
