package ru.ifmo.ctd.ngp

import java.util.stream.{Stream => JStream}
import ru.ifmo.ctd.ngp.util.optimized.{DoubleOps, IntOps}

import scala.collection.AbstractIterator

/**
 * Useful implicits for NGP util package.
 *
 * @author Maxim Buzdalov
 */
package object util {
  implicit class ConversionToLeftHandSideArgument[T](val t: T) extends AnyVal {
    def |>[U](fun: T => U): U = fun(t)
    def ifZeroThen(other: => T)(implicit num: Numeric[T]): T = if (num.zero == t) other else t
  }

  implicit class JavaStreamWrapper[+T](val underlying: JStream[_ <: T]) {
    private[this] val iterator = underlying.iterator()
    private[this] var isClosed = false

    if (!iterator.hasNext) {
      underlying.close()
      isClosed = true
    }

    def toScalaIteratorWithCloseup: Iterator[T] = new AbstractIterator[T] {
      override def hasNext: Boolean = !isClosed && iterator.hasNext
      override def next(): T = {
        if (hasNext) {
          val rv = iterator.next()
          if (!iterator.hasNext) {
            underlying.close()
            isClosed = true
          }
          rv
        } else {
          if (!isClosed) {
            underlying.close()
            isClosed = true
          }
          throw new IllegalStateException("hasNext == false, but next() called")
        }
      }
    }
  }

  implicit class RangeWrapper(r: Range) {
    @inline
    final def mapFoldLeft[A](zero: A)(fun: (A, A) => A)(body: Int => A): A = {
      if (r.isEmpty) zero else {
        var res = zero
        val step = r.step
        val end = r.end
        var curr = r.start
        while (curr != end) {
          res = fun(res, body(curr))
          curr += step
        }
        if (r.isInclusive) {
          res = fun(res, body(end))
        }
        res
      }
    }
    @inline
    private final def mapFoldLeft2[A](zero: A)(fun: (A, A) => A)(body: Int => A): A = {
      if (r.length <= 1) zero else {
        var res = zero
        val step = r.step
        val end = r.end
        var curr = r.start + step
        while (curr != end) {
          res = fun(res, body(curr))
          curr += step
        }
        if (r.isInclusive) {
          res = fun(res, body(end))
        }
        res
      }
    }

    final def mapSum[A](body: Int => A)(implicit num: Numeric[A]): A = {
      if (num eq IntOps.Numeric) {
        IntOps.mapSum(r, body.asInstanceOf[Int => Int]).asInstanceOf[A]
      } else if (num eq DoubleOps.Numeric) {
        DoubleOps.mapSum(r, body.asInstanceOf[Int => Double]).asInstanceOf[A]
      } else {
        mapFoldLeft(num.zero)(num.plus)(body)
      }
    }

    final def mapProduct[A](body: Int => A)(implicit num: Numeric[A]): A = {
      if (num eq IntOps.Numeric) {
        IntOps.mapProduct(r, body.asInstanceOf[Int => Int]).asInstanceOf[A]
      } else if (num eq DoubleOps.Numeric) {
        DoubleOps.mapProduct(r, body.asInstanceOf[Int => Double]).asInstanceOf[A]
      } else {
        mapFoldLeft(num.one)(num.times)(body)
      }
    }

    final def mapMin[A](body: Int => A)(implicit num: Ordering[A]): A = {
      require(!r.isEmpty, "mapMin called on an empty range")
      if ((num eq IntOps.Ordering) || (num eq IntOps.Numeric)) {
        IntOps.mapMin(r, body.asInstanceOf[Int => Int]).asInstanceOf[A]
      } else if ((num eq DoubleOps.Ordering) || (num eq DoubleOps.Numeric)) {
        DoubleOps.mapMin(r, body.asInstanceOf[Int => Double]).asInstanceOf[A]
      } else {
        mapFoldLeft2(body(r.start))(num.min)(body)
      }
    }

    final def mapMax[A](body: Int => A)(implicit num: Ordering[A]): A = {
      require(!r.isEmpty, "mapMax called on an empty range")
      if ((num eq IntOps.Ordering) || (num eq IntOps.Numeric)) {
        IntOps.mapMax(r, body.asInstanceOf[Int => Int]).asInstanceOf[A]
      } else if ((num eq DoubleOps.Ordering) || (num eq DoubleOps.Numeric)) {
        DoubleOps.mapMax(r, body.asInstanceOf[Int => Double]).asInstanceOf[A]
      } else {
        mapFoldLeft2(body(r.start))(num.max)(body)
      }
    }

    final def mapAnd(body: Int => Boolean): Boolean = {
      r.isEmpty || {
        var i = r.start
        val total = r.size
        val step = r.step
        var cnt = 0
        var res = true
        while (cnt < total && res) {
          res = body(i)
          i += step
          cnt += 1
        }
        res
      }
    }

    final def mapOr(body: Int => Boolean): Boolean = {
      !r.isEmpty && {
        var i = r.start
        val total = r.size
        val step = r.step
        var cnt = 0
        var res = false
        while (cnt < total && !res) {
          res = body(i)
          i += step
          cnt += 1
        }
        res
      }
    }
  }
}
