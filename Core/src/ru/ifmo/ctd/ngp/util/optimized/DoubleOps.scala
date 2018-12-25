package ru.ifmo.ctd.ngp.util.optimized

/**
  * Optimized operations for Double.
  *
  * @author Maxim Buzdalov
  */
object DoubleOps {
  final val Numeric: Numeric[Double] = implicitly
  final val Ordering: Ordering[Double] = implicitly

  @inline
  final def mapSum(r: Range, fun: Int => Double): Double = {
    if (r.isEmpty) 0.0 else {
      val end = r.end
      val step = r.step
      var curr = r.start
      var sum = 0.0
      while (curr != end) {
        sum += fun(curr)
        curr += step
      }
      if (r.isInclusive) {
        sum += fun(end)
      }
      sum
    }
  }

  @inline
  final def mapProduct(r: Range, fun: Int => Double): Double = {
    if (r.isEmpty) 1.0 else {
      val end = r.end
      val step = r.step
      var curr = r.start
      var prod = 1.0
      while (curr != end) {
        prod *= fun(curr)
        curr += step
      }
      if (r.isInclusive) {
        prod *= fun(end)
      }
      prod
    }
  }

  @inline
  final def mapMin(range: Range, fun: Int => Double): Double = {
    require(!range.isEmpty, "mapMin called on an empty range")
    var min = 0.0
    var first = true
    for (i <- range) {
      min = if (first) fun(i) else math.min(min, fun(i))
      first = false
    }
    min
  }

  @inline
  final def mapMax(range: Range, fun: Int => Double): Double = {
    require(!range.isEmpty, "mapMax called on an empty range")
    var max = 0.0
    var first = true
    for (i <- range) {
      max = if (first) fun(i) else math.max(max, fun(i))
      first = false
    }
    max
  }
}
