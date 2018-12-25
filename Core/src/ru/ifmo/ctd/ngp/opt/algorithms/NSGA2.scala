package ru.ifmo.ctd.ngp.opt.algorithms

import scala.annotation.tailrec
import ru.ifmo.ctd.ngp.opt._
import ru.ifmo.ctd.ngp.opt.iteration.{Mutation, Selection, Update}
import ru.ifmo.ctd.ngp.opt.multicriteria.{DiversityMeasure, DominationComparisonResult, MultipleCriteria, NonDominatedSorting}
import ru.ifmo.ctd.ngp.opt.types.{CodomainType, DomainType, WorkingSetType}

/**
 * A configuration piece for NSGA-II as by Deb, 2002.
 *
 * @author Maxim Buzdalov
 */
object NSGA2 {
  case class WorkingSet[+D, +C](
    evaluated: IndexedSeq[Evaluated[D, C]],
    frontIndex: IndexedSeq[Int],
    crowding: IndexedSeq[Double]
  )
  type WorkingSetType[+D, +C] = WorkingSet[D, C]

  def apply[D : DomainType, C : CodomainType : MultipleCriteria](): Detected[D, C] = new Detected

  class Detected[D : DomainType, C : CodomainType : MultipleCriteria] {
    def configuration(selectionSize: Int)(implicit
      evaluator: Evaluator[D, C],
      random: RandomSource,
      nonDominationSorting: NonDominatedSorting[D, C],
      diversity: DiversityMeasure[D, C],
      mutation: Mutation[D, C]
    ): (
      types.WorkingSetType[WorkingSetType],
      algorithms.NSGA2.WorkingSetType[D, C] => IndexedSeq[Evaluated[D, C]],
      IndexedSeq[Evaluated[D, C]] => algorithms.NSGA2.WorkingSetType[D, C],
      Iteration[D, C, WorkingSetType]
    ) = {
      def lexCmp(a: Evaluated[D, C], b: Evaluated[D, C]) = {
        val multi = implicitly[MultipleCriteria[C]]
        def impl(idx: Int): Int = {
          if (idx == multi.numberOfCriteria) 0 else {
            val cmp = multi.orderingForCriterion(idx).compare(a.output, b.output)
            if (cmp != 0) cmp else impl(idx + 1)
          }
        }
        impl(0)
      }
      def splitByCodomains(seq: IndexedSeq[Evaluated[D, C]]) = {
        val evs = seq.sortWith((a, b) => lexCmp(a, b) < 0)
        val cBuilder = IndexedSeq.newBuilder[C]
        val dBuilder = IndexedSeq.newBuilder[IndexedSeq[Evaluated[D, C]]]
        val ddBuilder = IndexedSeq.newBuilder[Evaluated[D, C]]
        cBuilder += evs(0).output
        ddBuilder += evs(0)
        for (i <- 1 until evs.size) {
          if (lexCmp(evs(i - 1), evs(i)) != 0) {
            dBuilder += ddBuilder.result()
            ddBuilder.clear()
            cBuilder += evs(i).output
          }
          ddBuilder += evs(i)
        }
        dBuilder += ddBuilder.result()
        (cBuilder.result(), dBuilder.result())
      }
      //TODO: implement fast sampling without replacement and extract to util
      def samplingWithoutReplacement[T](seq: IndexedSeq[T], k: Int) = {
        val rng = random()
        val builder = IndexedSeq.newBuilder[T]
        var m, t = 0
        while (m < k) {
          val u = rng.nextDouble()
          if ((seq.size - t) * u < k - m) {
            builder += seq(t)
            m += 1
          }
          t += 1
        }
        builder.result()
      }

      def construct(s: IndexedSeq[Evaluated[D, C]], size: Int) = {
        val sorted = nonDominationSorting(s)
        val evBuilder = IndexedSeq.newBuilder[Evaluated[D, C]]
        val frBuilder = IndexedSeq.newBuilder[Int]
        val crBuilder = IndexedSeq.newBuilder[Double]
        evBuilder.sizeHint(size)
        frBuilder.sizeHint(size)
        crBuilder.sizeHint(size)

        @tailrec
        def work(index: Int, collected: Int) {
          if (collected < size) {
            val currSize = sorted(index).size
            val (codomains, domainsBy) = splitByCodomains(sorted(index))
            val crowd = diversity(domainsBy.map(_(0)))
            if (collected + currSize <= size) {
              for (i <- codomains.indices) {
                evBuilder ++= domainsBy(i)
                crBuilder ++= Iterator.fill(domainsBy(i).size)(crowd(i))
              }
              frBuilder ++= Iterator.fill(currSize)(index)
              work(index + 1, collected + currSize)
            } else {
              val order = crowd.indices.sortBy(crowd)
              val shuffled = domainsBy.map(s => scala.util.Random.javaRandomToRandom(random()).shuffle(s))
              val indices = Array.ofDim[Int](shuffled.size)
              var cIndex = crowd.size - 1
              var remains = size - collected
              while (remains > 0) {
                val realIndex = order(cIndex)
                if (indices(realIndex) < shuffled(realIndex).size) {
                  evBuilder += shuffled(realIndex)(indices(realIndex))
                  crBuilder += crowd(realIndex)
                  frBuilder += index
                  indices(realIndex) += 1
                  remains -= 1
                }
                cIndex = (cIndex + crowd.size - 1) % crowd.size
              }
            }
          }
        }
        work(0, 0)
        WorkingSet(evBuilder.result(), frBuilder.result(), crBuilder.result())
      }

      implicit val workingSetType = WorkingSetType[NSGA2.WorkingSetType]
      implicit val w2IndexedSeq = (w: WorkingSet[D, C]) => w.evaluated
      implicit val indexedSeq2W = (s: IndexedSeq[Evaluated[D, C]]) => construct(s, s.size)
      implicit val selection = Selection() using { ws =>
        val n = selectionSize
        val (codomains, domainsBy) = splitByCodomains(ws.evaluated)
        if (codomains.size == 1) {
          domainsBy(0)
        } else {
          val multi = implicitly[MultipleCriteria[C]]
          val res = IndexedSeq.newBuilder[Evaluated[D, C]]
          res.sizeHint(n)
          val rng = random()
          var count = 0
          while (count < n) {
            val k = math.min(2 * (n - count), codomains.size)
            val sampled = samplingWithoutReplacement(codomains.indices, k)
            for (i <- 1 until k by 2) {
              val cmp = multi.domination(codomains(sampled(i - 1)), codomains(sampled(i)))
              val cd = cmp match {
                case DominationComparisonResult.Less => sampled(i)
                case DominationComparisonResult.Greater => sampled(i - 1)
                case _ => if (rng.nextBoolean()) sampled(i) else sampled(i - 1)
              }
              val domains = domainsBy(cd)
              res += domains(rng.nextInt(domains.size))
              count += 1
            }
          }
          res.result()
        }
      }
      implicit val update = Update() using { (workingSet, evaluated) =>
        construct(workingSet.evaluated ++ evaluated, workingSet.evaluated.size)
      }
      implicit val iteration = Iteration().fromSelectionMutationEvaluateUpdate
      (workingSetType, w2IndexedSeq, indexedSeq2W, iteration)
    }
  }
}
