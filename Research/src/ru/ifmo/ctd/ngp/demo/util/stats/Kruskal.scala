package ru.ifmo.ctd.ngp.demo.util.stats

import ru.ifmo.ctd.ngp.demo.util.stats.Util._
import ru.ifmo.ctd.ngp.util._

/**
 * The Kruskal-Wallis rank sum test.
 */
object Kruskal {
  private val coefsA = Array(
    -1e99,
    2/3.0,
    -4/135.0,
    8/2835.0,
    16/8505.0,
    -8992/12629925.0,
    -334144/492567075.0,
    698752/1477701225.0
  )

  private val coefsB = Array(
    -1e99,
    1/12.0,
    1/288.0,
    -139/51840.0,
    -571/2488320.0,
    163879/209018880.0,
    5246819/75246796800.0,
    -534703531/902961561600.0
  )

  private val lGamma1pCoeffs = Array(
    0.3224670334241132182362075833230126e-0,/* = (zeta(2)-1)/2 */
    0.6735230105319809513324605383715000e-1,/* = (zeta(3)-1)/3 */
    0.2058080842778454787900092413529198e-1,
    0.7385551028673985266273097291406834e-2,
    0.2890510330741523285752988298486755e-2,
    0.1192753911703260977113935692828109e-2,
    0.5096695247430424223356548135815582e-3,
    0.2231547584535793797614188036013401e-3,
    0.9945751278180853371459589003190170e-4,
    0.4492623673813314170020750240635786e-4,
    0.2050721277567069155316650397830591e-4,
    0.9439488275268395903987425104415055e-5,
    0.4374866789907487804181793223952411e-5,
    0.2039215753801366236781900709670839e-5,
    0.9551412130407419832857179772951265e-6,
    0.4492469198764566043294290331193655e-6,
    0.2120718480555466586923135901077628e-6,
    0.1004322482396809960872083050053344e-6,
    0.4769810169363980565760193417246730e-7,
    0.2271109460894316491031998116062124e-7,
    0.1083865921489695409107491757968159e-7,
    0.5183475041970046655121248647057669e-8,
    0.2483674543802478317185008663991718e-8,
    0.1192140140586091207442548202774640e-8,
    0.5731367241678862013330194857961011e-9,
    0.2759522885124233145178149692816341e-9,
    0.1330476437424448948149715720858008e-9,
    0.6422964563838100022082448087644648e-10,
    0.3104424774732227276239215783404066e-10,
    0.1502138408075414217093301048780668e-10,
    0.7275974480239079662504549924814047e-11,
    0.3527742476575915083615072228655483e-11,
    0.1711991790559617908601084114443031e-11,
    0.8315385841420284819798357793954418e-12,
    0.4042200525289440065536008957032895e-12,
    0.1966475631096616490411045679010286e-12,
    0.9573630387838555763782200936508615e-13,
    0.4664076026428374224576492565974577e-13,
    0.2273736960065972320633279596737272e-13,
    0.1109139947083452201658320007192334e-13/* = (zeta(40+1)-1)/(40+1) */
  )

  private val scaleFactor = math.pow(2, 256)
  private val maxIterations = 200000
  private val mCutoff = 3.196577e18
  private val eulersConst = 0.5772156649015328606065120900824024
  private val lGamma1p_c = 0.2273736845824652515226821577978691e-12
  private val logcfTolerance = 1e-14

  private def logcf(x: Double, i: Double, d: Double, eps: Double): Double = {
    var c1 = 2 * d
    var c2 = i + d
    var c4 = c2 + d
    var a1 = c2
    var b1 = i * (c2 - i * x)
    var b2 = d * d * x
    var a2 = c4 * c2 - b2

    b2 = c4 * b1 - i * b2

    while (math.abs(a2 * b1 - a1 * b2) > math.abs(eps * b1 * b2)) {
      var c3 = c2 * c2 * x
      c2 += d
      c4 += d
      a1 = c4 * a2 - c3 * a1
      b1 = c4 * b2 - c3 * b1

      c3 = c1 * c1 * x
      c1 += d
      c4 += d
      a2 = c4 * a1 - c3 * a2
      b2 = c4 * b1 - c3 * b2

      if (math.abs(b2) > scaleFactor) {
          a1 /= scaleFactor
          b1 /= scaleFactor
          a2 /= scaleFactor
          b2 /= scaleFactor
      } else if (math.abs(b2) < 1 / scaleFactor) {
          a1 *= scaleFactor
          b1 *= scaleFactor
          a2 *= scaleFactor
          b2 *= scaleFactor
      }
    }
    a2 / b2
  }

  private def log1pmx(x: Double): Double = {
    if (x > 1 || x < -0.79149064) {
      math.log1p(x) - x
    } else {
      val r = x / (2 + x)
      val y = r * r
      if (math.abs(x) < 1e-2) {
        r * ((((2.0 / 9 * y + 2.0 / 7) * y + 2.0 / 5) * y + 2.0 / 3) * y - x)
      } else {
        r * (2 * y * logcf(y, 3, 2, logcfTolerance) - x)
      }
    }
  }

  private def lGamma1p(a: Double): Double = {
    if (math.abs(a) >= 0.5) Util.lgammafn(a + 1) else {
      var lgam = lGamma1p_c * logcf(-a / 2, lGamma1pCoeffs.length + 2, 1, logcfTolerance)
      for (i <- lGamma1pCoeffs.indices.reverse) {
        lgam = lGamma1pCoeffs(i) - a * lgam
      }
      (a * lgam - eulersConst) * a - log1pmx(a)
    }
  }

  private def bd0(x: Double, np: Double): Double = {
    if (x.isNaN || x.isInfinity || np.isNaN || np.isInfinity || np == 0) {
      Double.NaN
    } else {
      if (math.abs(x - np) < 0.1 * (x + np)) {
        val v = (x - np) / (x + np)
        val vv = v * v

        def taylor(j: Int, s: Double, ej: Double): Double = {
          val ej1 = ej * vv
          val s1 = s + ej1 / ((j << 1) + 1)
          if (s1 == s) s1 else taylor(j + 1, s1, ej1)
        }
        taylor(1, (x - np) * v, 2 * x * v)
      } else {
        x * math.log(x / np) + np - x
      }
    }
  }

  private def dpoisRaw(x: Double, lambda: Double): Double = {
    if (lambda == 0) {
      if (x == 0) 1 else 0
    } else if (lambda.isInfinity || lambda.isNaN) {
      0
    } else if (x < 0) {
      0
    } else if (x <= lambda * 2.2250738585072014e-308) {
      math.exp(-lambda)
    } else if (lambda < x * 2.2250738585072014e-308) {
      math.exp(-lambda + x * math.log(lambda) - lgammafn(x + 1))
    } else {
      math.exp(-Util.stirlerr(x) - bd0(x, lambda)) / math.sqrt(math.Pi * 2 * x)
    }
  }

  private def dpoisWrap(xPlus1: Double, lambda: Double): Double = {
    if (lambda.isInfinity || lambda.isNaN) {
      0
    } else if (xPlus1 > 1) {
      dpoisRaw(xPlus1 - 1, lambda)
    } else if (lambda > math.abs(xPlus1 - 1) * mCutoff) {
      math.exp(-lambda - Util.lgammafn(xPlus1))
    } else {
      dpoisRaw(xPlus1, lambda) * (xPlus1 / lambda)
    }
  }

  private def pGammaSmallX(x: Double, alpha: Double, lowerTail: Boolean): Double = {
    var sum = 0.0
    var c = alpha
    var n = 0.0
    var term = 0.0

    do {
      n += 1
      c *= -x / n
      term = c / (alpha + n)
      sum += term
    } while (math.abs(term) > Util.dblEps * math.abs(sum))

    if (lowerTail) {
      val f1 = 1 + sum
      val f2 = if (alpha > 1) {
        math.exp(x) * dpoisRaw(alpha, x)
      } else {
        math.pow(x, alpha) / math.exp(lGamma1p(alpha))
      }
      f1 * f2
    } else {
      val lf2 = alpha * math.log(x) - lGamma1p(alpha)
      val f1m1 = sum
      val f2m1 = math.expm1(lf2)
      -(f1m1 + f2m1 + f1m1 * f2m1)
    }
  }

  private def pdUpperSeries(x: Double, y0: Double): Double = {
    var y = y0
    var term = x / y
    var sum = term
    do {
      y += 1
      term *= x / y
      sum += term
    } while (term > sum * Util.dblEps)
    sum
  }

  private def pdLowerCF(y: Double, d: Double): Double = {
    if (y == 0) 0 else {
      val f00 = y / d
      if (math.abs(y - 1) < math.abs(d) * Util.dblEps) f00 else {
        val f0 = math.min(1, f00)
        var c2 = y
        var c4 = d
        var a1 = 0.0
        var b1 = 1.0
        var a2 = y
        var b2 = d
        while (b2 > scaleFactor) {
          a1 /= scaleFactor
          b1 /= scaleFactor
          a2 /= scaleFactor
          b2 /= scaleFactor
        }

        def iteration(i0: Int, of: Double): Double = {
          if (i0 >= maxIterations) of else {
            var i = i0
            locally {
              i += 1
              c2 -= 1
              val c3 = i * c2
              c4 += 2
              a1 = c4 * a2 + c3 * a1
              b1 = c4 * b2 + c3 * b1
            }
            locally {
              i += 1
              c2 -= 1
              val c3 = i * c2
              a2 = c4 * a1 + c3 * a2
              b2 = c4 * b1 + c3 * b2
            }
            if (b2 > scaleFactor) {
              a1 /= scaleFactor
              b1 /= scaleFactor
              a2 /= scaleFactor
              b2 /= scaleFactor
            }
            if (b2 != 0) {
              val f = a2 / b2
              if (math.abs(f - of) <= Util.dblEps * math.max(f0, math.abs(f))) {
                f
              } else {
                iteration(i, f)
              }
            } else {
              iteration(i, of)
            }
          }
        }

        iteration(0, -1)
      }
    }
  }

  private def pdLowerSeries(lambda: Double, y0: Double): Double = {
    var term = 1.0
    var sum = 0.0
    var y = y0
    while (y >= 1 && term > sum * Util.dblEps) {
      term *= y / lambda
      sum += term
      y -= 1
    }
    if (y != math.floor(y)) {
      sum += term * pdLowerCF(y, lambda + 1 - y)
    }
    sum
  }

  private def ppoisAsymp(x: Double, lambda: Double, lowerTail: Boolean): Double = {
    val dfm = lambda - x
    val pt_ = -log1pmx(dfm / x)
    val s2pt = math.sqrt(2 * x * pt_) * (if (dfm < 0) -1 else 1)

    var res12 = 0.0
    var res1_ig, res1_term = math.sqrt(x)
    var res2_ig, res2_term = s2pt
    for (i <- 1 until 8) {
      res12 += res1_ig * coefsA(i)
      res12 += res2_ig * coefsB(i)
      res1_term *= pt_ / i ;
      res2_term *= 2 * pt_ / (2 * i + 1)
      res1_ig = res1_ig / x + res1_term
      res2_ig = res2_ig / x + res2_term
    }

    var elfb = x
    var elfb_term = 1.0
    for (i <- 1 until 8) {
      elfb += elfb_term * coefsB(i)
      elfb_term /= x
    }
    if (!lowerTail) elfb = -elfb
    val f = res12 / elfb

    val np = pnorm(s2pt, 0, 1, lowerTail)
    val nd = dnorm(s2pt, 0, 1)
    np + f * nd
  }

  private def pGammaRaw(x: Double, alpha: Double, lowerTail: Boolean): Double = {
    if (x <= 0) {
      if (lowerTail) 0 else 1
    } else if (x >= Double.PositiveInfinity) {
      if (lowerTail) 1 else 0
    } else if (x < 1) {
      pGammaSmallX(x, alpha, lowerTail)
    } else if (x <= alpha - 1 && x < 0.8 * (alpha + 50)) {
      val sum = pdUpperSeries(x, alpha)
      val d = dpoisWrap (alpha, x)
      if (!lowerTail) 1 - d * sum else sum * d
    } else if (alpha - 1 < x && alpha < 0.8 * (x + 50)) {
      val d = dpoisWrap(alpha, x)
      val sum = if (alpha < 1) {
        if (x * Util.dblEps > 1 - alpha) 1 else {
          pdLowerCF(alpha, x - (alpha - 1)) * x / alpha
        }
      } else {
        pdLowerSeries (x, alpha - 1) + 1
      }
      if (!lowerTail) sum * d else 1 - sum * d
    } else ppoisAsymp(alpha - 1, x, !lowerTail)
  }

  private def pGamma(x0: Double, alpha: Double, scale: Double, lowerTail: Boolean): Double = {
    if (x0.isNaN || alpha.isNaN || scale.isNaN || alpha < 0 || scale <= 0) {
      Double.NaN
    } else {
      val x = x0 / scale
      if (x.isNaN) x else {
        if (alpha == 0) {
          if ((x <= 0) == lowerTail) 0 else 1
        } else {
          pGammaRaw(x, alpha, lowerTail)
        }
      }
    }
  }

  /**
   * Kruskal-Wallis rank-sum test.
   * @param data the data to test
   * @tparam T the type of elements in the data.
   * @return the p-value.
   */
  def rankSumTest[T: Ordering](data: Iterable[Iterable[T]]): Double = {
    if (data.size < 2) {
      Double.NaN
    } else {
      val x = data.flatten.toIndexedSeq
      val sizes = data.map(_.size).toIndexedSeq
      val n = x.size
      val r = Util.ranks(x)
      val ties = Util.frequencies(r)

      def collect(i: Int, r: IndexedSeq[Double], done: Double): Double = {
        if (i == sizes.size) done
        else {
          val (h, t) = r.splitAt(sizes(i))
          val s = h.sum
          collect(i + 1, t, done + s * s / h.size)
        }
      }

      val kNum = (12.0 / n / (n + 1)) * collect(0, r, 0) - 3 * (n + 1)
      val kDen = 1 - ties.indices.mapSum { t => val u = ties(t).toDouble; u * u * u - u} / (n.toDouble * n * n - n)
      if (kDen == 0) {
        Double.NaN
      } else {
        val k = kNum / kDen
        pGamma(k, (data.size - 1.0) / 2, 2, lowerTail = false)
      }
    }
  }
}
