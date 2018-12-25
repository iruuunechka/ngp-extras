package ru.ifmo.ctd.ngp.util

import org.junit.{Assert, Test}

import ru.ifmo.ctd.ngp.util.optimized.IntOps

/**
 * Tests for `Rep`.
 *
 * @author Maxim Buzdalov
 */
class RepTests {
  @Test
  def testSum() {
    Assert.assertEquals((-1 until 76).sum, (-1 until 76).mapSum(i => i))
    Assert.assertEquals(0, (-1 until 76).mapSum(_ => 0))
    Assert.assertEquals((-1 until 76).sum / 10.0, (-1 until 76).mapSum(_ / 10.0), 1e-9)
  }

  @Test
  def testAnd() {
    Assert.assertEquals((0 until 40).forall(_ < 50), (0 until 40).mapAnd(_ < 50))
    Assert.assertEquals((0 until 40).forall(_ < 40), (0 until 40).mapAnd(_ < 40))
    Assert.assertEquals((0 until 40).forall(_ < 30), (0 until 40).mapAnd(_ < 30))

    Assert.assertEquals((0 until 40).inclusive.forall(_ < 50), (0 until 40).inclusive.mapAnd(_ < 50))
    Assert.assertEquals((0 until 40).inclusive.forall(_ < 40), (0 until 40).inclusive.mapAnd(_ < 40))
    Assert.assertEquals((0 until 40).inclusive.forall(_ < 30), (0 until 40).inclusive.mapAnd(_ < 30))

    Assert.assertEquals((0 until 40 by 3).forall(_ < 50), (0 until 40 by 3).mapAnd(_ < 50))
    Assert.assertEquals((0 until 40 by 3).forall(_ < 40), (0 until 40 by 3).mapAnd(_ < 40))
    Assert.assertEquals((0 until 40 by 3).forall(_ < 30), (0 until 40 by 3).mapAnd(_ < 30))

    Assert.assertEquals((39 to 0 by -1).forall(_ < 50), (39 to 0 by -1).mapAnd(_ < 50))
    Assert.assertEquals((39 to 0 by -1).forall(_ < 40), (39 to 0 by -1).mapAnd(_ < 40))
    Assert.assertEquals((39 to 0 by -1).forall(_ < 30), (39 to 0 by -1).mapAnd(_ < 30))

    Assert.assertEquals((39 to 0).forall(_ < 50), (39 to 0).mapAnd(_ < 50))
    Assert.assertEquals((39 to 0).forall(_ < 40), (39 to 0).mapAnd(_ < 40))
    Assert.assertEquals((39 to 0).forall(_ < 30), (39 to 0).mapAnd(_ < 30))
  }

  @Test
  def testOr() {
    Assert.assertEquals((0 until 40).exists(_ >= 50), (0 until 40).mapOr(_ >= 50))
    Assert.assertEquals((0 until 40).exists(_ >= 40), (0 until 40).mapOr(_ >= 40))
    Assert.assertEquals((0 until 40).exists(_ >= 30), (0 until 40).mapOr(_ >= 30))

    Assert.assertEquals((0 until 40).inclusive.exists(_ >= 50), (0 until 40).inclusive.mapOr(_ >= 50))
    Assert.assertEquals((0 until 40).inclusive.exists(_ >= 40), (0 until 40).inclusive.mapOr(_ >= 40))
    Assert.assertEquals((0 until 40).inclusive.exists(_ >= 30), (0 until 40).inclusive.mapOr(_ >= 30))

    Assert.assertEquals((0 until 40 by 3).exists(_ >= 50), (0 until 40 by 3).mapOr(_ >= 50))
    Assert.assertEquals((0 until 40 by 3).exists(_ >= 40), (0 until 40 by 3).mapOr(_ >= 40))
    Assert.assertEquals((0 until 40 by 3).exists(_ >= 30), (0 until 40 by 3).mapOr(_ >= 30))

    Assert.assertEquals((39 to 0 by -1).exists(_ >= 50), (39 to 0 by -1).mapOr(_ >= 50))
    Assert.assertEquals((39 to 0 by -1).exists(_ >= 40), (39 to 0 by -1).mapOr(_ >= 40))
    Assert.assertEquals((39 to 0 by -1).exists(_ >= 30), (39 to 0 by -1).mapOr(_ >= 30))

    Assert.assertEquals((39 to 0).exists(_ >= 50), (39 to 0).mapOr(_ >= 50))
    Assert.assertEquals((39 to 0).exists(_ >= 40), (39 to 0).mapOr(_ >= 40))
    Assert.assertEquals((39 to 0).exists(_ >= 30), (39 to 0).mapOr(_ >= 30))
  }
}

object RepTests extends App {
  def range_map_sum(range: Range, times: Int) {
    def print(time: Long, out: Int) {
      println(f"Range.map.sum:         $time%4d (checksum: $out)")
    }
    System.gc()
    System.gc()
    val start = System.currentTimeMillis()
    var out = 0
    var t = 0
    while (t != times) {
      out += range.map(i => i * i).sum
      t += 1
    }
    val time = System.currentTimeMillis() - start
    print(time, out)
  }

  def range_view_map_sum(range: Range, times: Int) {
    def print(time: Long, out: Int) {
      println(f"Range.view.map.sum:    $time%4d (checksum: $out)")
    }
    System.gc()
    System.gc()
    val start = System.currentTimeMillis()
    var out = 0
    var t = 0
    while (t != times) {
      out += range.view.map(i => i * i).sum
      t += 1
    }
    val time = System.currentTimeMillis() - start
    print(time, out)
  }

  def range_rep_mapsum(range: Range, times: Int) {
    def print(time: Long, out: Int) {
      println(f"Range.[Rep].mapSum:    $time%4d (checksum: $out)")
    }
    System.gc()
    System.gc()
    val start = System.currentTimeMillis()
    var out = 0
    var t = 0
    while (t != times) {
      out += range.mapSum(i => i * i)
      t += 1
    }
    val time = System.currentTimeMillis() - start
    print(time, out)
  }

  def range_custom_mapsum(range: Range, times: Int) {
    def print(time: Long, out: Int) {
      println(f"Range.[IntOps].mapSum: $time%4d (checksum: $out)")
    }
    System.gc()
    System.gc()
    val start = System.currentTimeMillis()
    var out = 0
    var t = 0
    while (t != times) {
      out += IntOps.mapSum(range, i => i * i)
      t += 1
    }
    val time = System.currentTimeMillis() - start
    print(time, out)
  }

  def for_loop(range: Range, times: Int) {
    def print(time: Long, out: Int) {
      println(f"For loop:              $time%4d (checksum: $out)")
    }
    System.gc()
    System.gc()
    val start = System.currentTimeMillis()
    var out = 0
    var t = 0
    while (t != times) {
      for (i <- range) {
        out += i * i
      }
      t += 1
    }
    val time = System.currentTimeMillis() - start
    print(time, out)
  }

  def java_impl(range: Range, times: Int) {
    def print(time: Long, out: Int) {
      println(f"Java impl:             $time%4d (checksum: $out)")
    }
    System.gc()
    System.gc()
    val start = System.currentTimeMillis()
    var out = 0
    var t = 0
    while (t != times) {
      val re = range.end
      val rs = range.step
      var i = range.start
      while (i < re) {
        out += i * i
        i += rs
      }
      t += 1
    }
    val time = System.currentTimeMillis() - start
    print(time, out)
  }

  for (size <- Seq(100, 1000, 10000, 100000, 1000000)) {
    val times = 100000000 / size
    val range = 0 until size
    for (it <- 0 until 5) {
      println(s"size: $size iteration: $it")
      range_map_sum(range, times)
      range_view_map_sum(range, times)
      range_rep_mapsum(range, times)
      for_loop(range, times)
      java_impl(range, times)
      range_custom_mapsum(range, times)
    }
  }
}
