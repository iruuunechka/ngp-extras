package ru.ifmo.ctd.ngp.opt.multicriteria

import scala.collection.{IndexedSeq => IS}

import ru.ifmo.ctd.ngp.opt.{OptConfiguration, Evaluated}

/**
 * A configuration for non-dominated sorters.
 *
 * @author Maxim Buzdalov
 */
abstract class NonDominatedSorter(criteria: Int) extends OptConfiguration[Null, IS[Int]] {
  protected implicit val multiple: MultipleCriteria[IS[Int]] = {
    MultipleCriteria.fromIndexedSeqWithElementOrdering((0 until criteria).map(_.toString) :_*)
  }
  private implicit val sorting = sortingImpl

  def doSorting(coll: IS[IS[Int]]): IS[IS[IS[Int]]] = {
    sorting(coll.map(e => Evaluated(null, e))).map(_.map(_.output))
  }

  protected def sortingImpl: NonDominatedSorting[domain.Type, codomain.Type]
}

object NonDominatedSorter {
  val seqOrdering = new Ordering[IS[Int]] {
    def compare(x: IS[Int], y: IS[Int]): Int = {
      def impl(index: Int): Int = {
        if (index == x.size) 0 else {
          val cmp = x(index).compareTo(y(index))
          if (cmp == 0) {
            impl(index + 1)
          } else cmp
        }
      }
      impl(0)
    }
  }
  def sortNDSResults(res: IS[IS[IS[Int]]]): IS[IS[IS[Int]]] = res.map(_.sorted(seqOrdering))

  def createDeb(criteria: Int): NonDominatedSorter = new NonDominatedSorter(criteria) {
    protected def sortingImpl: NonDominatedSorting[Null, IS[Int]] = NonDominatedSorting().debLinearMemorySorting
  }
  def createJensenFortin(criteria: Int): NonDominatedSorter = new NonDominatedSorter(criteria) {
    protected def sortingImpl: NonDominatedSorting[Null, IS[Int]] = NonDominatedSorting().jensenFortinSorting
  }
  def createJensenFortinBuzdalov(criteria: Int): NonDominatedSorter = new NonDominatedSorter(criteria) {
    protected def sortingImpl: NonDominatedSorting[Null, IS[Int]] = NonDominatedSorting().jensenFortinBuzdalovSorting
  }
}
