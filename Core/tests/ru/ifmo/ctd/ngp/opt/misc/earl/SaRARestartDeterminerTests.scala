package ru.ifmo.ctd.ngp.opt.misc.earl

import org.junit.{Assert, Test}


/**
  * Tests whether code which determines on which iterations SaRA would restart works OK.
  */
class SaRARestartDeterminerTests {
  private def model(iterations: Int, criteria: Int): Seq[Int] = {
    val builder = IndexedSeq.newBuilder[Int]
    var step = 1
    var remains = step
    var criterion = 0
    var curr = 0
    while (curr <= iterations) {
      remains -= 1
      curr += 1
      if (remains == 0) {
        builder += curr
        criterion += 1
        if (criterion == criteria) {
          step += step
          criterion = 0
        }
        remains = step
      }
    }
    builder.result()
  }

  @Test
  def works(): Unit = {
    for (criteria <- 1 to 17) {
      val trueAnswer = model(100000, criteria).toSet
      for (i <- 1 to 100000) {
        Assert.assertEquals(s"Heuristic fails on iteration $i with $criteria criteria",
          trueAnswer.contains(i), EARLConfiguration.saraWillSwitchAfter(i, criteria))
      }
    }
  }
}
