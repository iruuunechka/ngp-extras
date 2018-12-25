package ru.ifmo.ctd.ngp.demo.util.stats

import org.junit.{Test, Assert}

/**
 * Tests if `Kruskal` works.
 */
class KruskalTests {
  //R reports only four significant digits
  def assertRelative(expected: Double, found: Double): Unit = {
    Assert.assertEquals("%.4g".format(expected), "%.4g".format(found))
  }

  @Test
  def someKnownPValues1() {
    assertRelative(
      0.118, Kruskal.rankSumTest(Seq(
        (1 to 7).map(_.toDouble),
        (1 to 7).map(_.toDouble + 1.1),
        (1 to 7).map(_.toDouble + 2.2)
      ))
    )
  }

  @Test
  def someKnownPValues2() {
    assertRelative(
      0.2757, Kruskal.rankSumTest(Seq(
        (1 to 7).map(_.toDouble),
        (1 to 7).map(_.toDouble + 1),
        (1 to 7).map(_.toDouble + 2)
      ))
    )
  }

  @Test
  def someKnownPValues3() {
    assertRelative(0.004702, Kruskal.rankSumTest(Seq(1 to 12, 1 to 12, 10 to 15)))
  }
}
