package ru.ifmo.ctd.ngp.demo.util.stats

/**
 * Utilities for statistic tests
 */
object Util {
  val dblEps = 2.2204460492503131e-16
  val M_1_SQRT_2PI = 0.398942280401432677939946059934
  val M_LN_SQRT_2PI = 0.918938533204672741780329736406
  val M_LN_SQRT_PId2 = 0.225791352644727432363097614947

  def ranks[T: Ordering](seq: Iterable[T]): IndexedSeq[Double] = {
    val sortedWithIndex: IndexedSeq[(T, Iterable[(T, Int)])] = seq.zipWithIndex.groupBy(_._1).toIndexedSeq.sortBy(_._1)
    val rankOfIndex = IndexedSeq.newBuilder[(Int, Double)]

    def process(idx: Int, soFar: Int): Unit = {
      if (idx < sortedWithIndex.size) {
        val (_, itr) = sortedWithIndex(idx)
        val rank = soFar + (1.0 + itr.size) / 2
        for ((_, i) <- itr) {
          rankOfIndex += i -> rank
        }
        process(idx + 1, soFar + itr.size)
      }
    }
    process(0, 0)
    rankOfIndex.result().sortBy(_._1).map(_._2)
  }

  def frequencies[T: Numeric](seq: Iterable[T]): IndexedSeq[Int] = seq.groupBy(identity).map(_._2.size).toIndexedSeq

  def dnorm(x0: Double, mu: Double, sigma: Double): Double = {
    if ((x0 + mu + sigma).isNaN || x0.isInfinity && mu == x0) {
      Double.NaN
    } else if (sigma.isInfinity) {
      0
    } else if (sigma <= 0) {
      if (sigma < 0) Double.NaN else {
        if (x0 == mu) Double.PositiveInfinity else 0
      }
    } else {
      val x = (x0 - mu) / sigma
      if (x.isNaN || x.isInfinity) 0 else M_1_SQRT_2PI * math.exp(-0.5 * x * x) / sigma
    }
  }

  private val algmcs = Array(
    +.1666389480451863247205729650822e+0,
    -.1384948176067563840732986059135e-4,
    +.9810825646924729426157171547487e-8,
    -.1809129475572494194263306266719e-10,
    +.6221098041892605227126015543416e-13,
    -.3399615005417721944303330599666e-15,
    +.2683181998482698748957538846666e-17,
    -.2868042435334643284144622399999e-19,
    +.3962837061046434803679306666666e-21,
    -.6831888753985766870111999999999e-23,
    +.1429227355942498147573333333333e-24,
    -.3547598158101070547199999999999e-26,
    +.1025680058010470912000000000000e-27,
    -.3401102254316748799999999999999e-29,
    +.1276642195630062933333333333333e-30
  )

  def lgammacor(x: Double): Double = {
    if (x < 10) {
      Double.NaN
    } else if (x >= 94906265.62425156) {
      val tmp = 10 / x
      chebyshevEval(tmp * tmp *2 - 1, algmcs, 5) / x
    } else {
      1 / (x * 12)
    }
  }

  def lgammafn(x: Double): Double = {
    val dxrel = 1.490116119384765696e-8
    val xmax = 2.5327372760800758e+305
    if (x.isNaN) {
      x
    } else if (x <= 0 && x == x.toInt) {
      Double.PositiveInfinity
    } else {
      val y = math.abs(x)
      if (y < 1e-306) {
        -math.log(x)
      } else if (y <= 10) {
        math.log(math.abs(gammafn(x)))
      } else if (y > xmax) {
        Double.PositiveInfinity
      } else if (x > 0) {
        if (x > 1e17) {
          x * (math.log(x) - 1)
        } else if (x > 4934720) {
          M_LN_SQRT_2PI + (x - 0.5) * math.log(x) - x
        } else {
          M_LN_SQRT_2PI + (x - 0.5) * math.log(x) - x + lgammacor(x)
        }
      } else {
        val sinpiy = math.abs(math.sin(math.Pi * y))
        if (sinpiy == 0) {
          Double.NaN
        } else {
          val ans = M_LN_SQRT_PId2 + (x - 0.5) * math.log(y) - x - math.log(sinpiy) - lgammacor(x)
          if (math.abs((x - (x - 0.5).toInt) * ans / x) < dxrel) {
            throw new InternalError("Precision error")
          }
          ans
        }
      }
    }
  }

  private def chebyshevEval(x: Double, gamcs: Array[Double], ngam: Int): Double = {
    if (x < -1.1 || x > 1.1) Double.NaN else {
      val twox = x * 2
      var b2, b1, b0 = 0.0
      for (i <- 1 to ngam) {
        b2 = b1
        b1 = b0
        b0 = twox * b1 - b2 + gamcs(ngam - i)
      }
      (b0 - b2) * 0.5
    }
  }

  object stirlerr {
    private val sferr_halves = Array(
      0.0, /* n=0 - wrong, place holder only */
      0.1534264097200273452913848,  /* 0.5 */
      0.0810614667953272582196702,  /* 1.0 */
      0.0548141210519176538961390,  /* 1.5 */
      0.0413406959554092940938221,  /* 2.0 */
      0.03316287351993628748511048, /* 2.5 */
      0.02767792568499833914878929, /* 3.0 */
      0.02374616365629749597132920, /* 3.5 */
      0.02079067210376509311152277, /* 4.0 */
      0.01848845053267318523077934, /* 4.5 */
      0.01664469118982119216319487, /* 5.0 */
      0.01513497322191737887351255, /* 5.5 */
      0.01387612882307074799874573, /* 6.0 */
      0.01281046524292022692424986, /* 6.5 */
      0.01189670994589177009505572, /* 7.0 */
      0.01110455975820691732662991, /* 7.5 */
      0.010411265261972096497478567, /* 8.0 */
      0.009799416126158803298389475, /* 8.5 */
      0.009255462182712732917728637, /* 9.0 */
      0.008768700134139385462952823, /* 9.5 */
      0.008330563433362871256469318, /* 10.0 */
      0.007934114564314020547248100, /* 10.5 */
      0.007573675487951840794972024, /* 11.0 */
      0.007244554301320383179543912, /* 11.5 */
      0.006942840107209529865664152, /* 12.0 */
      0.006665247032707682442354394, /* 12.5 */
      0.006408994188004207068439631, /* 13.0 */
      0.006171712263039457647532867, /* 13.5 */
      0.005951370112758847735624416, /* 14.0 */
      0.005746216513010115682023589, /* 14.5 */
      0.005554733551962801371038690  /* 15.0 */
    )

    private val S0 = 0.083333333333333333333       /* 1/12 */
    private val S1 = 0.00277777777777777777778     /* 1/360 */
    private val S2 = 0.00079365079365079365079365  /* 1/1260 */
    private val S3 = 0.000595238095238095238095238 /* 1/1680 */
    private val S4 = 0.0008417508417508417508417508/* 1/1188 */

    def apply(n: Double): Double = {
      if (n <= 15) {
        val nn = n * 2
        if (nn == nn.toInt) sferr_halves(nn.toInt) else gammafn(n + 1) - (n + 0.5) * math.log(n) + n - M_LN_SQRT_2PI
      } else {
        val nn = n * n
        if (n > 500) {
          (S0 - S1 / nn) / n
        } else if (n > 80) {
          (S0 - (S1 - S2 / nn) / nn) / n
        } else if (n > 35) {
          (S0 - (S1 - (S2 - S3 / nn) / nn) / nn) / n
        } else {
          (S0 - (S1 - (S2 - (S3 - S4 / nn) / nn) / nn) / nn) / n
        }
      }
    }
  }

  object gammafn {
    private val gamcs = Array(
      +0.8571195590989331421920062399942e-2,
      +0.4415381324841006757191315771652e-2,
      +0.5685043681599363378632664588789e-1,
      -0.4219835396418560501012500186624e-2,
      +0.1326808181212460220584006796352e-2,
      -0.1893024529798880432523947023886e-3,
      +0.3606925327441245256578082217225e-4,
      -0.6056761904460864218485548290365e-5,
      +0.1055829546302283344731823509093e-5,
      -0.1811967365542384048291855891166e-6,
      +0.3117724964715322277790254593169e-7,
      -0.5354219639019687140874081024347e-8,
      +0.9193275519859588946887786825940e-9,
      -0.1577941280288339761767423273953e-9,
      +0.2707980622934954543266540433089e-10,
      -0.4646818653825730144081661058933e-11,
      +0.7973350192007419656460767175359e-12,
      -0.1368078209830916025799499172309e-12,
      +0.2347319486563800657233471771688e-13,
      -0.4027432614949066932766570534699e-14,
      +0.6910051747372100912138336975257e-15,
      -0.1185584500221992907052387126192e-15,
      +0.2034148542496373955201026051932e-16,
      -0.3490054341717405849274012949108e-17,
      +0.5987993856485305567135051066026e-18,
      -0.1027378057872228074490069778431e-18,
      +0.1762702816060529824942759660748e-19,
      -0.3024320653735306260958772112042e-20,
      +0.5188914660218397839717833550506e-21,
      -0.8902770842456576692449251601066e-22,
      +0.1527474068493342602274596891306e-22,
      -0.2620731256187362900257328332799e-23,
      +0.4496464047830538670331046570666e-24,
      -0.7714712731336877911703901525333e-25,
      +0.1323635453126044036486572714666e-25,
      -0.2270999412942928816702313813333e-26,
      +0.3896418998003991449320816639999e-27,
      -0.6685198115125953327792127999999e-28,
      +0.1146998663140024384347613866666e-28,
      -0.1967938586345134677295103999999e-29,
      +0.3376448816585338090334890666666e-30,
      -0.5793070335782135784625493333333e-31
    )
    private val ngam = 22

    def apply(x: Double): Double = {
      if (x.isNaN || x == 0 || x < 0 && x == x.toLong) x else {
        val xmin = -170.5674972726612
        val xmax = 171.61447887182298
        val xsml = 2.2474362225598545e-308
        val dxrel = 1.490116119384765696e-8
        var y = math.abs(x)
        if (y <= 10) {
          var n = x.toInt
          if (x < 0) n -= 1
          y = x - n
          n -= 1
          var value = chebyshevEval(y * 2 - 1, gamcs, ngam) + 0.9375
          if (n == 0) {
            value
          } else if (n < 0) {
            if (x < -0.5 && math.abs(x - (x - 0.5).toInt / x) < dxrel) {
              throw new InternalError("Precision error")
            }
            if (y < xsml) {
              if (x > 0) Double.PositiveInfinity else Double.NegativeInfinity
            } else {
              n = -n
              for (i <- 0 until n) {
                value /= x + i
              }
              value
            }
          } else {
            for (i <- 1 to n) {
              value *= y + i
            }
            value
          }
        } else {
          if (x > xmax) {
            Double.PositiveInfinity
          } else if (x < xmin) {
            0
          } else {
            var value: Double = 0.0
            if (y <= 50 && y == y.toInt) {
              value = 1.0
              for (i <- 2 until y.toInt) value *= i
            } else {
              value = math.exp(
                (y - 0.5) * math.log(y) - y + M_LN_SQRT_2PI +
                (if (2 * y == (2 * y).toInt) stirlerr(y) else lgammacor(y))
              )
            }
            if (x > 0) value else {
              if (math.abs((x - (x - 0.5).toInt) / x) < dxrel) {
                throw new InternalError("Precision error")
              }
              val sinpiy = math.sin(math.Pi * y)
              if (sinpiy == 0) {
                Double.PositiveInfinity
              } else {
                -math.Pi / (y * sinpiy * value)
              }
            }
          }
        }
      }
    }
  }

  object pnorm {
    private val A = Array(
      2.2352520354606839287,
      161.02823106855587881,
      1067.6894854603709582,
      18154.981253343561249,
      0.065682337918207449113
    )
    private val B = Array(
      47.20258190468824187,
      976.09855173777669322,
      10260.932208618978205,
      45507.789335026729956
    )
    private val C = Array(
      0.39894151208813466764,
      8.8831497943883759412,
      93.506656132177855979,
      597.27027639480026226,
      2494.5375852903726711,
      6848.1904505362823326,
      11602.651437647350124,
      9842.7148383839780218,
      1.0765576773720192317e-8
    )
    private val D = Array(
      22.266688044328115691,
      235.38790178262499861,
      1519.377599407554805,
      6485.558298266760755,
      18615.571640885098091,
      34900.952721145977266,
      38912.003286093271411,
      19685.429676859990727
    )
    private val P = Array(
      0.21589853405795699,
      0.1274011611602473639,
      0.022235277870649807,
      0.001421619193227893466,
      2.9112874951168792e-5,
      0.02307344176494017303
    )
    private val Q = Array(
      1.28426009614491121,
      0.468238212480865118,
      0.0659881378689285515,
      0.00378239633202758244,
      7.29751555083966205e-5
    )

    def both(x: Double): (Double, Double) = {
      if (x.isNaN) (x, x) else {
        val eps = dblEps * 0.5
        val y = math.abs(x)
        if (y <= 0.67448975) {
          val (xnum, xden) = if (y > eps) {
            val xsq = x * x
            var xnum0 = A(4) * xsq
            var xden0 = xsq
            for (i <- 0 until 3) {
              xnum0 = (xnum0 + A(i)) * xsq
              xden0 = (xden0 + B(i)) * xsq
            }
            (xnum0, xden0)
          } else (0.0, 0.0)
          val temp = x * (xnum + A(3)) / (xden + B(3))
          (0.5 + temp, 0.5 - temp)
        } else if (y < math.sqrt(32)) {
          var xnum = C(8) * y
          var xden = y
          for (i <- 0 until 7) {
            xnum = (xnum + C(i)) * y
            xden = (xden + D(i)) * y
          }
          val temp = (xnum + C(7)) / (xden + D(7))
          val xsq = (y * 16).toLong / 16.0
          val del = (y - xsq) * (y + xsq)
          val cum = math.exp(-xsq * xsq * 0.5) * math.exp(-del * 0.5) * temp
          if (x > 0) (1 - cum, cum) else (cum, 1 - cum)
        } else if (-37.5193 < x && x < 37.5193) {
          val xsq = 1.0 / (x * x)
          var xnum = P(5) * xsq
          var xden = xsq
          for (i <- 0 until 4) {
            xnum = (xnum + P(i)) * xsq
            xden = (xden + Q(i)) * xsq
          }
          val temp0 = xsq * (xnum + P(4)) / (xden + Q(4))
          val temp = (M_1_SQRT_2PI - temp0) / y

          val xsq2 = (x * 16).toLong / 16.0
          val del = (x - xsq2) * (x + xsq2)
          val cum = math.exp(-xsq2 * xsq2 * 0.5) * math.exp(-del * 0.5) * temp
          if (x > 0) (1 - cum, cum) else (cum, 1 - cum)
        } else {
          if (x > 0) (1, 0) else (0, 1)
        }
      }
    }

    def apply(x: Double, mu: Double = 0.0, sigma: Double = 1.0, lowerTail: Boolean = true): Double = {
      if (x.isNaN || mu.isNaN || sigma.isNaN) {
        x + mu + sigma
      } else if (x.isInfinity && x == mu) {
        Double.NaN
      } else if (sigma <= 0) {
        if (sigma < 0) {
          Double.NaN
        } else {
          if ((x < mu) == lowerTail) 0 else 1
        }
      } else {
        val p = (x - mu) / sigma
        if (p.isInfinity) {
          if ((x < mu) == lowerTail) 0 else 1
        } else {
          val (rP, rCP) = both(p)
          if (lowerTail) rP else rCP
        }
      }
    }
  }

}
