package ru.ifmo.ctd.ngp.theory.leadingones.onemax

import ru.ifmo.ctd.ngp.theory.Math._
import ru.ifmo.ctd.ngp.util._

/**
 * Theoretical calculations for LeadingOnes + OneMax problem
 *
 * @author Maxim Buzdalov
 */
object Theory {
  private def xFromNext[T: Fractional](next: IndexedSeq[IndexedSeq[T]], n: Int, i: Int) = {
    val num = implicitly[Fractional[T]]
    import num._

    IndexedSeq.tabulate(i) { j =>
      if (j < i - 1) {
        ((i - j - 1 until i).mapSum(x => next(x)(j - i + x + 1) * choose[T](x - 1, i - j - 2) / choose[T](i - 1, j)) + num.fromInt(n)) / num.fromInt(i)
      } else {
        num.fromInt(n) / num.fromInt(i)
      }
    }
  }

  def leadingOnesRMHC[T: Fractional](n: Int) = {
    val num = implicitly[Fractional[T]]
    import num._

    def computeT(i: Int): IndexedSeq[IndexedSeq[T]] = {
      if (i == 1) IndexedSeq(IndexedSeq(), IndexedSeq(num.fromInt(n))) else {
        val next = computeT(i - 1)
        val x = xFromNext(next, n, i)
        val a = IndexedSeq.tabulate(i)(j => num.fromInt(-j) / num.fromInt(i))
        val b = IndexedSeq.fill(i)(num.fromInt(1))
        val c = IndexedSeq.tabulate(i)(j => num.fromInt(-(i - j - 1)) / num.fromInt(i))
        val y = solveTridiagonal(a, b, c, x)
        next :+ y
      }
    }
    val dp = computeT(n)
    dp(n)(0) + num.fromInt(1)
  }
  def leadingOnesOneMaxNStatesRMHC[T: Fractional](n: Int) = {
    val num = implicitly[Fractional[T]]
    import num._

    def computeT(i: Int): IndexedSeq[IndexedSeq[T]] = {
      if (i == 1) IndexedSeq(IndexedSeq(), IndexedSeq(num.fromInt(n))) else {
        val next = computeT(i - 1)
        val x = xFromNext(next, n, i)
        val a = IndexedSeq.tabulate(i)(j => num.fromInt(-j) / num.fromInt(i * 2))
        val b = IndexedSeq.tabulate(i)(j => num.fromInt(i * 2 - j) / num.fromInt(i * 2))
        val c = IndexedSeq.tabulate(i)(j => num.fromInt(-(i - j - 1)) / num.fromInt(i))
        val y = solveTridiagonal(a, b, c, x)
        next :+ y
      }
    }
    val dp = computeT(n)
    dp(n)(0) + num.fromInt(1)
  }
  def leadingOnesOneMax1StateRMHC[T: Fractional](n: Int) = {
    val num = implicitly[Fractional[T]]
    import num._

    def computeT(i: Int): IndexedSeq[IndexedSeq[T]] = {
      if (i == 1) IndexedSeq(IndexedSeq(), IndexedSeq(num.fromInt(n))) else {
        val next = computeT(i - 1)
        val x = xFromNext(next, n, i)
        if (i == n) {
          val x2 = x.zipWithIndex map { case (v, k) =>
            (v + num.fromInt(1) + (k + 1 until n).mapSum(p => num.fromInt(1) / num.fromInt(n - p))) / num.fromInt(2)
          }
          val a = IndexedSeq.tabulate(i)(j => num.fromInt(-j) / num.fromInt(i * 2))
          val b = IndexedSeq.tabulate(i)(j => num.fromInt(i * 2 - j) / num.fromInt(i * 2))
          val c = IndexedSeq.tabulate(i)(j => num.fromInt(-(i - j - 1)) / num.fromInt(i))
          val y = solveTridiagonal(a, b, c, x2)
          next :+ y
        } else {
          val a = IndexedSeq.tabulate(i)(j => num.fromInt(-j) / num.fromInt(i))
          val b = IndexedSeq.fill(i)(num.fromInt(1))
          val c = IndexedSeq.tabulate(i)(j => num.fromInt(-(i - j - 1)) / num.fromInt(i))
          val y = solveTridiagonal(a, b, c, x)
          next :+ y
        }
      }
    }
    val dp = computeT(n)
    dp(n)(0) + num.fromInt(1)
  }
}
