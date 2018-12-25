package ru.ifmo.ctd.ngp.demo.util.stats

import org.junit.{Assert, Test}

/**
 * Tests for separate statistic utilities.
 */
class UtilTests {
  @Test
  def checkRanks(): Unit = {
    Assert.assertArrayEquals(Array(1.0, 2.0, 3.0), Util.ranks(Seq(3, 6, 10)).toArray, 1e-9)
    Assert.assertArrayEquals(Array(1.5, 1.5, 3.0), Util.ranks(Seq(3, 3, 10)).toArray, 1e-9)
    Assert.assertArrayEquals(Array(1.5, 3.0, 1.5), Util.ranks(Seq(3, 10, 3)).toArray, 1e-9)
    Assert.assertArrayEquals(Array(2.0, 3.0, 1.0), Util.ranks(Seq(6, 10, 3)).toArray, 1e-9)
    Assert.assertArrayEquals(Array(2.0, 2.0, 2.0), Util.ranks(Seq(3, 3.0, 3)).toArray, 1e-9)
  }
}
