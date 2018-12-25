package ru.ifmo.ctd.ngp.demo.util.stats

import ru.ifmo.ctd.ngp.demo.util.stats.Util._

/**
 * The Wilcoxon rank sum test.
 */
object Wilcoxon {
  private def rDTVal(x: Double, lowerTail: Boolean): Double = {
    if (lowerTail) x else 0.5 - x + 0.5
  }

  private object pwilcox {
    var wCache: Array[Array[Array[Double]]] = _

    def ensureSize(m: Int, n: Int): Unit = {
      if (m > 0 || n > 0) {
        wCache = Array.ofDim[Array[Double]](m + 1, n + 1)
      }
    }

    def cwilcox(k0: Int, m: Int, n: Int): Double = {
      val u = m * n
      if (k0 < 0 || k0 > u) 0 else {
        val c = u / 2
        val k = if (k0 > c) u - k0 else k0
        val (i, j) = if (m < n) (m, n) else (n, m)
        if (j == 0) {
          if (k == 0) 1 else 0
        } else {
          if (j > 0 && k < j) {
            cwilcox(k, i, k)
          } else {
            if (wCache(i)(j) == null) {
              wCache(i)(j) = Array.fill(c + 1)(-1.0)
            }
            if (wCache(i)(j)(k) < 0) {
              if (j == 0) {
                wCache(i)(j)(k) = if (k == 0) 1 else 0
              } else {
                wCache(i)(j)(k) = cwilcox(k - j, i - 1, j) + cwilcox(k, i, j - 1)
              }
            }
            wCache(i)(j)(k)
          }
        }
      }
    }

    def apply(q0: Double, m: Int, n: Int, lowerTail: Boolean = true): Double = {
      if (q0.isInfinity || q0.isNaN || m <= 0 || n <= 0) {
        Double.NaN
      } else {
        val q = math.floor(q0 + 1e-7)
        if (q < 0.0) {
          if (lowerTail) 0 else 1
        } else if (q >= m.toDouble * n) {
          if (lowerTail) 1 else 0
        } else synchronized {
          ensureSize(m, n)
          val c = ru.ifmo.ctd.ngp.theory.Math.choose[Double](m + n, n)
          var p = 0.0
          if (q <= (m.toDouble * n / 2)) {
            for (i <- 0 to q.toInt) {
              p += cwilcox(i, m, n) / c
            }
            rDTVal(p, lowerTail)
          } else {
            val q1 = m.toDouble * n - q
            for (i <- 0 until q1.toInt) {
              p += cwilcox(i, m, n) / c
            }
            rDTVal(p, !lowerTail)
          }
        }
      }
    }
  }

  sealed trait Alternative
  case object Less extends Alternative
  case object Greater extends Alternative
  case object TwoSided extends Alternative

  def rankSumTestExt[T: Ordering](x: Iterable[T], y: Iterable[T]): Either[Double, Double] = {
    val r = ranks(x ++ y)
    val xs = x.size.toDouble
    val ys = y.size.toDouble
    val w = r.take(x.size).sum - xs * (xs + 1) / 2
    val hasTies = r.size != r.distinct.size
    if (hasTies) {
      println(s"Warning: has ties: ${r.size} != ${r.distinct.size}")
    }

    if (w > xs * ys / 2) {
      Right(math.min(2 * pwilcox(w - 1, x.size, y.size, lowerTail = false), 1))
    } else {
      Left(math.min(2 * pwilcox(w, x.size, y.size), 1))
    }
  }

  /**
   * Wilcoxon rank sum test with two sided alternative.
   * @param x the first series to use
   * @param y the second series to use
   * @tparam T the type of the series values
   * @return the p-value
   */
  def rankSumTest[T: Ordering](x: Iterable[T], y: Iterable[T], alt: Alternative = TwoSided): Double = {
    val r = ranks(x ++ y)
    val xs = x.size.toDouble
    val ys = y.size.toDouble
    val exact = xs < 50 && ys < 50
    val w = r.take(x.size).sum - xs * (xs + 1) / 2
    val hasTies = r.size != r.distinct.size
    val rv = if (exact && !hasTies) {
      val p = if (alt == Greater || alt == TwoSided && w > xs * ys / 2) {
        pwilcox(w - 1, x.size, y.size, lowerTail = false)
      } else {
        pwilcox(w, x.size, y.size)
      }
      math.min(2 * p, 1)
    } else {
      val ties = frequencies(r).map(t => t.toDouble * t * t - t).sum
      val z0 = w - xs * ys / 2
      val sigma = math.sqrt(
        (xs * ys / 12) * ((xs + ys + 1) - ties / ((xs + ys) * (xs + ys - 1)))
      )
      val correction = math.signum(z0) * 0.5
      val z = (z0 - correction) / sigma
      2 * math.min(pnorm(z), pnorm(z, lowerTail = false))
    }
    rv
  }
}
