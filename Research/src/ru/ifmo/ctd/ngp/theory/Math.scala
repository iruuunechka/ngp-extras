package ru.ifmo.ctd.ngp.theory

import scala.language.implicitConversions

/**
 * Mathematical utility functions.
 *
 * @author Maxim Buzdalov
 */
object Math {
  /**
   * A fraction of big integers.
   * @param num the numerator of the fraction
   * @param den the denominator of the fraction
   */
  class Fraction(num: BigInt, den: BigInt) {
    require(den.signum != 0)
    private val gcd = num.gcd(den)
    val numerator   = if (den.signum > 0) num / gcd else -num / gcd
    val denominator = if (den.signum > 0) den / gcd else -den / gcd

    def * (that: Fraction) = new Fraction(numerator * that.numerator, denominator * that.denominator)
    def / (that: Fraction) = new Fraction(numerator * that.denominator, denominator * that.numerator)
    def + (that: Fraction) = new Fraction(numerator * that.denominator + denominator * that.numerator, denominator * that.denominator)
    def - (that: Fraction) = new Fraction(numerator * that.denominator - denominator * that.numerator, denominator * that.denominator)
    def unary_- = new Fraction(-numerator, denominator)
    def pow(power: Int) = if (power == 0) Fraction.one else if (power > 0) {
      new Fraction(numerator.pow(power), denominator.pow(power))
    } else {
      new Fraction(denominator.pow(-power), numerator.pow(-power))
    }

    override def equals(o: Any) = o match {
      case that: Fraction => numerator == that.numerator && denominator == that.denominator
      case _ => false
    }
    override def hashCode() = numerator.hashCode() + 31 * denominator.hashCode()
    override def toString = s"$numerator/$denominator"

  }
  implicit object Fraction extends Fractional[Fraction] {
    private val big1E18: BigInt = 1000000000000000000L

    def apply(a: BigInt, b: BigInt) = new Fraction(a, b)
    def plus(x: Fraction, y: Fraction) = x + y
    def minus(x: Fraction, y: Fraction) = x - y
    def times(x: Fraction, y: Fraction) = x * y
    def div(x: Fraction, y: Fraction) = x / y
    def negate(x: Fraction) = -x
    def fromInt(x: Int) = new Fraction(x, 1)
    def toInt(x: Fraction) = (x.numerator / x.denominator).intValue()
    def toLong(x: Fraction) = (x.numerator / x.denominator).longValue()
    def toFloat(x: Fraction) = toDouble(x).toFloat
    def toDouble(x: Fraction) = (x.numerator * big1E18 / x.denominator).doubleValue() / 1e18
    def compare(x: Fraction, y: Fraction) = x.numerator * y.denominator compare y.numerator * x.denominator
  }
  implicit def int2fraction(that: Int): Fraction = new Fraction(that, 1)
  implicit def long2fraction(that: Long): Fraction = new Fraction(that, 1)
  implicit def bigInt2fraction(that: BigInt): Fraction = new Fraction(that, 1)

  implicit object BigIntFractional extends Fractional[BigInt] {
    def div(x: BigInt, y: BigInt) = x / y
    def plus(x: BigInt, y: BigInt) = x + y
    def minus(x: BigInt, y: BigInt) = x - y
    def times(x: BigInt, y: BigInt) = x * y
    def negate(x: BigInt) = -x
    def fromInt(x: Int) = x
    def toInt(x: BigInt) = x.toInt
    def toLong(x: BigInt) = x.toLong
    def toFloat(x: BigInt) = x.toFloat
    def toDouble(x: BigInt) = x.toDouble
    def compare(x: BigInt, y: BigInt) = x compare y
  }

  /**
   * Returns the number of ways to choose k indistinguishable items from n items.
   * @param n the total number of items.
   * @param k the number of items to choose.
   * @return the number of combinations.
   */
  def choose[T: Fractional](n: Int, k: Int) = {
    val num = implicitly[Fractional[T]]
    var res = num.fromInt(1)
    var i = 0
    while (i < k) {
      res = num.times(res, num.fromInt(n - i))
      res = num.div(res, num.fromInt(i + 1))
      i += 1
    }
    res
  }

  def divisors(n: Int): Iterator[Int] = new Iterator[Int] {
    var current = 1
    def next() = {
      if (!hasNext) throw new IllegalStateException("No more divisors")
      val rv = current
      do {
        current += 1
      } while (current <= n && n % current != 0)
      rv
    }
    def hasNext = current <= n
  }

  def solveTridiagonal[T: Fractional](a: IndexedSeq[T], b: IndexedSeq[T], c: IndexedSeq[T], x: IndexedSeq[T]) = {
    val num = implicitly[Fractional[T]]

    val n = a.size
    require(n == b.size)
    require(n == c.size)
    require(n == x.size)

    val cc = c.toBuffer
    val xx = x.toBuffer
    
    cc(0) = num.div(cc.head, b(0))
    xx(0) = num.div(xx.head, b(0))

    for (i <- 1 until n) {
      val m = num.div(num.fromInt(1), num.minus(b(i), num.times(a(i), cc(i - 1))))
      cc(i) = num.times(cc(i), m)
      xx(i) = num.times(num.minus(xx(i), num.times(a(i), xx(i - 1))), m)
    }
    for (i <- (n - 2) to 0 by -1) {
      xx(i) = num.minus(xx(i), num.times(cc(i), xx(i + 1)))
    }
    xx.toIndexedSeq
  }
}
