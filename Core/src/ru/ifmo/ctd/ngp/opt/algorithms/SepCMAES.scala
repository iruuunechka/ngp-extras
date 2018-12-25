package ru.ifmo.ctd.ngp.opt.algorithms

import ru.ifmo.ctd.ngp.opt._
import ru.ifmo.ctd.ngp.opt.types.{WorkingSetType, CodomainType, DomainType}
import ru.ifmo.ctd.ngp.util._

/**
 * A collection of definitions needed for the diagonal CMA-ES.
 * @author Maxim Buzdalov
 */
object SepCMAES {
  case class WorkingSet(
    solution: IndexedSeq[Double],
    cov: IndexedSeq[Double],
    bd: IndexedSeq[Double],
    pc: IndexedSeq[Double],
    ps: IndexedSeq[Double],
    stepSize: Double,
    generationCount: Int
  )
  type WorkingSetType[+D, +C] = WorkingSet

  private class Impl[D: DomainType, C: CodomainType : CodomainComparator] (
    makeD: IndexedSeq[Double] => D,
    initialVector: IndexedSeq[Double],
    initialStepSize: Double,
    minimumStepSize: Double,
    weights: IndexedSeq[Double],
    sequenceExecutor: SequenceExecutor,
    random: RandomSource,
    evaluator: Evaluator[D, C],
    initialCovarianceMatrix: IndexedSeq[Double]
  ) {
    require(initialCovarianceMatrix.size == initialVector.size)

    private val size = initialVector.size

    private object params {
      val sizeD: Double = size.toDouble
      val sqrt2: Double = math.sqrt(2)
      val muW: Double = 1 / weights.view.map(i => i * i).sum

      val cS: Double = (muW + 2) / (sizeD + muW + 3)
      val dS: Double = 1 + cS + {
        val tmp = (muW - 1) / (sizeD + 1) - 1
        if (tmp >= 0) 2 * math.sqrt(tmp) else 0
      }
      val cC: Double = 4 / (4.0 + sizeD)
      val d: Double = 1 + 1 / cS

      val muCov: Double = muW
      val cdCov1: Double = 2 / muCov / (sizeD + sqrt2) / (sizeD + sqrt2)
      val cdCov2: Double = (1 - 1 / muCov) * math.min(1, (2 * muCov - 1) / ((sizeD + 2) * (sizeD + 2) + muCov))
      val cCov: Double = (sizeD + 2) / 3.0 * (cdCov1 + cdCov2)

      assert(cCov < 1.0, "Too big generation size, this algorithm will produce NaNs sometimes")

      val aCov: Double = 1.0 / muCov

      val pC_ZCoeff: Double = math.sqrt(cC * (2 - cC) * muW)
      val pS_ZCoeff: Double = math.sqrt(cS * (2 - cS) * muW)

      val ksiN: Double = math.sqrt(sizeD) * (1 - 1.0 / (4 * sizeD) + 1.0 / (21 * sizeD * sizeD))
    }

    private def sumw(vectors: IndexedSeq[IndexedSeq[Double]]) = {
      val tmp = Array.ofDim[Double](vectors(0).size)
      var j = 0
      while (j < vectors.size) {
        val v = vectors(j)
        val w = weights(j)
        var i = 0
        val l = v.size
        while (i < l) {
          tmp(i) += v(i) * w
          i += 1
        }
        j += 1
      }
      tmp.toIndexedSeq
    }

    private def zip3[A1, A2, A3](a1: IndexedSeq[A1], a2: IndexedSeq[A2], a3: IndexedSeq[A3]): IndexedSeq[(A1, A2, A3)] = {
      val builder = IndexedSeq.newBuilder[(A1, A2, A3)]
      val minIndex = a1.size min a2.size min a3.size
      builder.sizeHint(minIndex)
      for (i <- 0 until minIndex) {
        builder += ((a1(i), a2(i), a3(i)))
      }
      builder.result()
    }

    private[SepCMAES] implicit val workingSetType = WorkingSetType[SepCMAES.WorkingSetType]
    private[SepCMAES] implicit val initialization = Initialization().use {
      val rv = WorkingSet(
        solution = initialVector,
        cov = initialCovarianceMatrix,
        bd = initialCovarianceMatrix.map(math.sqrt),
        pc = IndexedSeq.fill(size)(0.0),
        ps = IndexedSeq.fill(size)(0.0),
        stepSize = initialStepSize,
        generationCount = 0
      )
      rv
    }
    private[SepCMAES] implicit val iteration = Iteration() use { ws =>
      import params._
      import ws._

      val z = sequenceExecutor.mapN(
        IndexedSeq(0),
        (_: Int) => { val rng = random(); IndexedSeq.fill(size)(rng.nextGaussian())},
        weights.size
      )
      val y = sequenceExecutor.map(
        z,
        (zz: IndexedSeq[Double]) => IndexedSeq.tabulate(size)(i => solution(i) + stepSize * bd(i) * zz(i))
      )
      val x = sequenceExecutor.map(y, makeD)
      val e = evaluator(x)
      val (sz, sy, _) = zip3(z, y, e).sortBy(_._3)(implicitly[CodomainComparator[C]].evaluatedOrdering).unzip3
      val szMean = sumw(sz)
      val newPS = IndexedSeq.tabulate(size)(i => ps(i) * (1 - cS) + szMean(i) * pS_ZCoeff)
      val psLen = math.sqrt((0 until size).mapSum(i => newPS(i) * newPS(i)))
      val h = if (psLen / math.sqrt(1 - math.pow(1 - cS, 2 * (generationCount + 1))) < (1.4 + 2 / (size + 1.0)) * ksiN) 1 else 0
      val newPC = IndexedSeq.tabulate(size)(i => pc(i) * (1 - cC) + bd(i) * szMean(i) * pC_ZCoeff * h)
      val Z = {
        val zArr = Array.ofDim[Double](size)
        var j = 0
        while (j < weights.size) {
          val w = weights(j)
          val zz = sz(j)
          var i = 0
          while (i < size) {
            val t = bd(i) * zz(i)
            zArr(i) += t * t * w
            i += 1
          }
          j += 1
        }
        zArr
      }
      val newC = IndexedSeq.tabulate(size) { i =>
        (1 - cCov) * cov(i) + newPC(i) * newPC(i) * cCov * aCov + Z(i) * cCov * (1 - aCov)
      }
      val newBD = newC.map(math.sqrt)
      val newStepSize = math.max(
        minimumStepSize,
        stepSize * math.exp((cS / dS) * (psLen / ksiN - 1))
      )
      WorkingSet(sumw(sy), newC, newBD, newPC, newPS, newStepSize, 1 + generationCount)
    }
  }

  class Detected[D: DomainType, C: CodomainType] {
    def forIndexedSeqDomain(
      initialVector: IndexedSeq[Double],
      initialStepSize: Double,
      minimumStepSize: Double,
      weights: IndexedSeq[Double]
    )(implicit
      codomainComparator: CodomainComparator[C],
      indexedSeqDoubleToD: IndexedSeq[Double] => D,
      sequenceExecutor: SequenceExecutor,
      random: RandomSource,
      evaluator: Evaluator[D, C]
    ): (
      types.WorkingSetType[WorkingSetType],
      Initialization[D, C, WorkingSetType],
      Iteration[D, C, WorkingSetType]
    ) = {
      val impl = new Impl(
        indexedSeqDoubleToD, initialVector, initialStepSize,
        minimumStepSize, weights, sequenceExecutor, random, evaluator,
        IndexedSeq.fill(initialVector.size)(1.0)
      )
      (impl.workingSetType, impl.initialization, impl.iteration)
    }

    def forIndexedSeqDomain(
      initialVector: IndexedSeq[Double],
      initialStepSize: Double,
      minimumStepSize: Double,
      weights: IndexedSeq[Double],
      initialCovarianceMatrix: IndexedSeq[Double]
    )(implicit
      codomainComparator: CodomainComparator[C],
      indexedSeqDoubleToD: IndexedSeq[Double] => D,
      sequenceExecutor: SequenceExecutor,
      random: RandomSource,
      evaluator: Evaluator[D, C]
    ): (
      types.WorkingSetType[WorkingSetType],
      Initialization[D, C, WorkingSetType],
      Iteration[D, C, WorkingSetType]
    ) = {
      val impl = new Impl(
        indexedSeqDoubleToD, initialVector, initialStepSize,
        minimumStepSize, weights,
        sequenceExecutor, random, evaluator, initialCovarianceMatrix
      )
      (impl.workingSetType, impl.initialization, impl.iteration)
    }
  }

  def apply[D: DomainType, C: CodomainType](): Detected[D, C] = new Detected

  /**
   * Returns an `IndexedSeq` determining the weights for diagonal CMA-ES.
   * The weights are equal for all selected individuals and their sum is equal to 1.0
   *
   * @param offspringCount the number of individuals to generate.
   * @param selectedCount the number of best individuals to select.
   * @return the weight sequence.
   */
  def equalWeights(offspringCount: Int, selectedCount: Int): IndexedSeq[Double] = {
    val ratio = 1.0 / selectedCount
    IndexedSeq.tabulate(offspringCount)(i => if (i + selectedCount >= offspringCount) ratio else 0)
  }

  /**
   * Returns an `IndexedSeq` determining the weights for diagonal CMA-ES.
   * The weights are zero for unselected individuals and
   * are proportional to (log(selectedCount + 1) - log(index + 1)) for selected individuals
   * (given index is zero-based).
   *
   * @param offspringCount the number of individuals to generate.
   * @param selectedCount the number of best individuals to select.
   * @return the weight sequence.
   */
  def logarithmicWeights(offspringCount: Int, selectedCount: Int): IndexedSeq[Double] = {
    val logarithms = IndexedSeq.tabulate(selectedCount) {
      i => math.log(selectedCount + 1) - math.log(selectedCount - i)
    }
    val logSum = logarithms.sum
    IndexedSeq.tabulate(offspringCount) {
      i => if (i >= offspringCount - selectedCount) logarithms(i - offspringCount + selectedCount) / logSum else 0
    }
  }
}
