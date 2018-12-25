package ru.ifmo.ctd.ngp.opt.iteration

import scala.language.higherKinds
import ru.ifmo.ctd.ngp.opt.{CodomainComparator, Evaluated}
import ru.ifmo.ctd.ngp.opt.types.{CodomainType, DomainType, WorkingSetType}
import ru.ifmo.ctd.ngp.util.FastRandom

/**
 * A base class for update operators which merge the old working set and `Evaluated` objects to make a new working set.
 *
 * @author Maxim Buzdalov
 */
abstract class Update[D: DomainType, C: CodomainType, W[+_D, +_C]: WorkingSetType] {
  /**
   * Updates given the working set by the given `Evaluated` objects, returning the new working set.
   * @param workingSet the working set.
   * @param evaluated the sequence of `Evaluated` objects.
   * @return the new working set.
   */
  def apply(workingSet: W[D, C], evaluated: IndexedSeq[Evaluated[D, C]]): W[D, C]
}

object Update {
  trait BestChooser {
    def choose[T](seq: IndexedSeq[T], howMuch: Int, comparator: Ordering[T]): (IndexedSeq[T], IndexedSeq[T])
  }

  object BestChooser {
    object Greedy extends BestChooser {
      override def choose[T](seq: IndexedSeq[T], howMuch: Int, comparator: Ordering[T]): (IndexedSeq[T], IndexedSeq[T]) = {
        import scala.util.Random._
        FastRandom.threadLocal().shuffle(seq).sorted(comparator.reverse).splitAt(howMuch)
      }
    }

    object BestDifferent extends BestChooser {
      private def groupSortedByComparator[T](seq: IndexedSeq[T], comparator: Ordering[T]): IndexedSeq[IndexedSeq[T]] = {
        if (seq.isEmpty) IndexedSeq.empty else {
          val rv = IndexedSeq.newBuilder[IndexedSeq[T]]
          val current = IndexedSeq.newBuilder[T]
          var last = seq.head
          for (curr <- seq) {
            if (comparator.equiv(last, curr)) {
              current += curr
            } else {
              rv += current.result()
              current.clear()
              current += curr
              last = curr
            }
          }
          rv += current.result()
          rv.result()
        }
      }

      private def takeBest[T](groups: Seq[Seq[T]], indices: Array[Int], howMuch: Int): IndexedSeq[T] = {
        val rv = IndexedSeq.newBuilder[T]
        rv.sizeHint(howMuch)
        var rvSize = 0
        while (rvSize < howMuch) {
          for (i <- groups.indices.reverse) {
            if (indices(i) < groups(i).size && rvSize < howMuch) {
              rvSize += 1
              rv += groups(i)(indices(i))
              indices(i) += 1
            }
          }
        }
        rv.result()
      }

      override def choose[T](seq: IndexedSeq[T], howMuch: Int, comparator: Ordering[T]): (IndexedSeq[T], IndexedSeq[T]) = {
        import scala.util.Random._
        val groups = groupSortedByComparator(FastRandom.threadLocal().shuffle(seq).sorted(comparator), comparator)
        val indices = Array.ofDim[Int](groups.size)
        val first = takeBest(groups, indices, howMuch)
        val second = takeBest(groups, indices, seq.size - howMuch)
        (first, second)
      }
    }
  }

  class Detected[D: DomainType, C: CodomainType, W[+_D, +_C]: WorkingSetType] {
    /**
     * Returns an `Update` object using the given function as its "apply" method.
     * @param function the function to use.
     * @return the `Update` object.
     */
    def using(function: (W[D, C], IndexedSeq[Evaluated[D, C]]) => W[D, C]): Update[D, C, W] = new Update[D, C, W] {
      override def apply(workingSet: W[D, C], evaluated: IndexedSeq[Evaluated[D, C]]): W[D, C] = {
        function(workingSet, evaluated)
      }
    }
    /**
     * Returns an update operator which discards the old working set.
     * @param indexedSeq2WorkingSet the conversion from `IndexedSeq` of `Evaluated` objects to the working set.
     * @return the `Update` object.
     */
    def allNew(implicit indexedSeq2WorkingSet: IndexedSeq[Evaluated[D, C]] => W[D, C]): Update[D, C, W] = {
      new Update[D, C, W] {
        override def apply(workingSet: W[D, C], evaluated: IndexedSeq[Evaluated[D, C]]): W[D, C] = {
          indexedSeq2WorkingSet(evaluated)
        }
      }
    }
    /**
     * Returns an update operator which saves the given ratio of best `Evaluated` objects from old working set.
     * The size of the resulting working set is defined by the size of old working set.
     * @param eliteRatio the elite ratio, must be not less than 0 and not greater than 1.
     * @param indexedSeq2WorkingSet the conversion from `IndexedSeq` of `Evaluated` objects to the working set.
     * @param workingSet2IndexedSeq the conversion from working set to the `IndexedSeq` of `Evaluated` objects.
     * @param codomainComparator the codomain comparator.
     * @return the `Update` object.
     */
    def elitist(eliteRatio: Double, bestChooser: BestChooser = BestChooser.Greedy)(
      implicit indexedSeq2WorkingSet: IndexedSeq[Evaluated[D, C]] => W[D, C],
               workingSet2IndexedSeq: W[D, C] => IndexedSeq[Evaluated[D, C]],
               codomainComparator: CodomainComparator[C]
    ): Update[D, C, W] = {
      require(eliteRatio >= 0 && eliteRatio <= 1)
      new Update[D, C, W] {
        override def apply(workingSet: W[D, C], evaluated: IndexedSeq[Evaluated[D, C]]): W[D, C] = {
          implicit val o = codomainComparator.evaluatedOrdering
          val ws = workingSet2IndexedSeq(workingSet)
          val size = ws.size
          val elite = (size * eliteRatio).ceil.toInt
          val others = math.min(evaluated.size, size - elite)
          val realElite = size - others
          indexedSeq2WorkingSet(bestChooser.choose(ws, realElite, o)._1 ++ bestChooser.choose(evaluated, others, o)._1)
        }
      }
    }
    def manyComparatorElitist(decisions: Seq[(CodomainComparator[C], Double)], bestChooser: BestChooser = BestChooser.Greedy)(
      implicit indexedSeq2WorkingSet: IndexedSeq[Evaluated[D, C]] => W[D, C],
               workingSet2IndexedSeq: W[D, C] => IndexedSeq[Evaluated[D, C]],
               primaryCodomainComparator: CodomainComparator[C]
    ): Update[D, C, W] = {
      decisions.foreach(p => require(p._2 >= 0 && p._2 <= 1))
      new Update[D, C, W] {
        private type ISE = IndexedSeq[Evaluated[D, C]]
        private def collectElites(index: Int, in: ISE, out: ISE): ISE = {
          if (index == decisions.size || in.isEmpty) out else {
            val eliteCount = (decisions(index)._2 * in.size).ceil.toInt
            val (good, bad) = bestChooser.choose(in, eliteCount, decisions(index)._1.evaluatedOrdering)
            assert(good.size == eliteCount)
            collectElites(index + 1, bad, out ++ good)
          }
        }
        override def apply(workingSet: W[D, C], evaluated: IndexedSeq[Evaluated[D, C]]): W[D, C] = {
          val ws = workingSet2IndexedSeq(workingSet)
          val elite = collectElites(0, ws, IndexedSeq.empty)
          val rest = bestChooser.choose(evaluated, ws.size - elite.size, primaryCodomainComparator.evaluatedOrdering)._1
          indexedSeq2WorkingSet(elite ++ rest)
        }
      }
    }
    /**
     * Returns an update operator which selects approximately equal number of individuals for each
     * fitness value (differing as by codomain comparator).
     * @param indexedSeq2WorkingSet the conversion from `IndexedSeq` of `Evaluated` objects to the working set.
     * @param workingSet2IndexedSeq the conversion from working set to the `IndexedSeq` of `Evaluated` objects.
     * @param codomainComparator the codomain comparator.
     * @return the `Update` object.
     */
    def equalForFitnessInstance(
      implicit indexedSeq2WorkingSet: IndexedSeq[Evaluated[D, C]] => W[D, C],
               workingSet2IndexedSeq: W[D, C] => IndexedSeq[Evaluated[D, C]],
               codomainComparator: CodomainComparator[C]
    ): Update[D, C, W] = new Update[D, C, W] {
      override def apply(workingSet: W[D, C], evaluated: IndexedSeq[Evaluated[D, C]]): W[D, C] = {
        val wsAsSeq = workingSet2IndexedSeq(workingSet)
        val sorted = (evaluated ++ wsAsSeq).sorted(codomainComparator.evaluatedOrdering)
        val byFitness = {
          val builder = IndexedSeq.newBuilder[IndexedSeq[Evaluated[D, C]]]
          val latest = IndexedSeq.newBuilder[Evaluated[D, C]]
          latest += sorted.last
          for (i <- sorted.size - 2 to 0 by -1) {
            if (codomainComparator(sorted(i + 1).output, sorted(i).output) != 0) {
              builder += latest.result()
              latest.clear()
            }
            latest += sorted(i)
          }
          builder += latest.result()
          builder.result().take(wsAsSeq.size)
        }
        val selected = IndexedSeq.newBuilder[Evaluated[D, C]]
        val indices = Array.ofDim[Int](byFitness.size)
        var curr, count = 0
        while (count < wsAsSeq.size) {
          if (indices(curr) < byFitness(curr).size) {
            selected += byFitness(curr)(indices(curr))
            indices(curr) += 1
            count += 1
          }
          curr = (curr + 1) % indices.length
        }
        indexedSeq2WorkingSet(selected.result())
      }
    }
    /**
     * Returns an update operator which saves the best `Evaluated` objects from both old working set and the new ones.
     * The size of the resulting working set is defined by the size of old working set.
     * @param indexedSeq2WorkingSet the conversion from `IndexedSeq` of `Evaluated` objects to the working set.
     * @param workingSet2IndexedSeq the conversion from working set to the `IndexedSeq` of `Evaluated` objects.
     * @param codomainComparator the codomain comparator.
     * @return the `Update` object.
     */
    def best(
      implicit indexedSeq2WorkingSet: IndexedSeq[Evaluated[D, C]] => W[D, C],
               workingSet2IndexedSeq: W[D, C] => IndexedSeq[Evaluated[D, C]],
               codomainComparator: CodomainComparator[C]
    ): Update[D, C, W] = new Update[D, C, W] {
      override def apply(workingSet: W[D, C], evaluated: IndexedSeq[Evaluated[D, C]]): W[D, C] = {
        val old = workingSet2IndexedSeq(workingSet)
        indexedSeq2WorkingSet((old ++ evaluated).sorted(codomainComparator.evaluatedOrdering).takeRight(old.size))
      }
    }
    /**
     * Returns an update operator which saves the best `Evaluated` objects from the new ones.
     * The size of the resulting working set is defined by the size of old working set.
     * @param indexedSeq2WorkingSet the conversion from `IndexedSeq` of `Evaluated` objects to the working set.
     * @param workingSet2IndexedSeq the conversion from working set to the `IndexedSeq` of `Evaluated` objects.
     * @param codomainComparator the codomain comparator.
     * @return the `Update` object.
     */
    def bestNew(
      implicit indexedSeq2WorkingSet: IndexedSeq[Evaluated[D, C]] => W[D, C],
               workingSet2IndexedSeq: W[D, C] => IndexedSeq[Evaluated[D, C]],
               codomainComparator: CodomainComparator[C]
    ): Update[D, C, W] = new Update[D, C, W] {
      override def apply(workingSet: W[D, C], evaluated: IndexedSeq[Evaluated[D, C]]): W[D, C] = {
        val old = workingSet2IndexedSeq(workingSet)
        indexedSeq2WorkingSet(evaluated.sorted(codomainComparator.evaluatedOrdering).takeRight(old.size))
      }
    }
  }
  /**
   * Detects the domain, codomain, `Evaluated` object type and the working set type, and allows to select more options.
   * @tparam D the domain type.
   * @tparam C the codomain type.
   * @tparam W the working set type.
   * @return the object for more options to build an `Iteration` object.
   */
  def apply[D: DomainType, C: CodomainType, W[+_D, +_C]: WorkingSetType](): Detected[D, C, W] = new Detected
}
