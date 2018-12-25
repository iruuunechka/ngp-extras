package ru.ifmo.ctd.ngp.opt.algorithms.three_equals

import ru.ifmo.ctd.ngp.opt._
import ru.ifmo.ctd.ngp.util.Bits
import ru.ifmo.ctd.ngp.opt.termination.{CodomainThreshold, Crash, EvaluationLimit}
import ru.ifmo.ctd.ngp.opt.listeners.EvaluationCount
import ru.ifmo.ctd.ngp.opt.iteration.{Mutation, Selection, Update}
import ru.ifmo.ctd.ngp.opt.types.WorkingSetType.IndexedSeqWorkingSet

/**
 * Performance test for a specific problem that depends on strong block relation.
 *
 * @author Maxim Buzdalov
 */
object ThreeEqualsPerformance extends App {
  abstract class Base(generationSize: Int) extends OptConfiguration[Int, Int] {
    private val thirdBitSize = 4
    protected val mask: Int = Bits.nBitMask(thirdBitSize)
    protected val bitSize: Int = 3 * thirdBitSize

    protected implicit def iteration: Iteration[domain.Type, codomain.Type, workingSet.Type]

    protected implicit val evaluator: Evaluator[Int, Int] = Evaluator().usingFunction { g =>
      val p1 = g & mask
      val p2 = (g >>> thirdBitSize) & mask
      val p3 = (g >>> (2 * thirdBitSize)) & mask
      ~(p1 ^ p2) & ~(p1 ^ p3) & mask
    }
    protected implicit val comparator: CodomainComparator[Int] = CodomainComparator().byOrdering.increasing
    protected implicit val termination = Termination.Pluggable()
    protected implicit val initialization: Initialization[Int, Int, IndexedSeqWorkingSet] = {
      Initialization().useDomainGenerator(random().nextInt(), generationSize)
    }
    protected implicit val optimizer: Optimizer[Int, Int, IndexedSeqWorkingSet] = Optimizer().simple
    protected implicit val evaluationCount = new EvaluationCount()

    def mutate(src: Int): Int

    CodomainThreshold().register(mask)
    EvaluationLimit().register(1000000)

    def run(): Int = optimizer() match {
      case Optimizer.Result(_, CodomainThreshold) => evaluationCount().toInt
      case Optimizer.Result(_, EvaluationLimit)   => Int.MaxValue
      case Optimizer.Result(_, Crash(th)) => throw th
      case _ => throw new AssertionError()
    }
  }

  abstract class ES extends Base(1) {
    implicit lazy val selection: Selection[Int, Int, IndexedSeqWorkingSet] = Selection().all
    implicit lazy val mutation: Mutation[Int, Int] = Mutation().using(mutate)
    implicit lazy val update: Update[Int, Int, IndexedSeqWorkingSet] = Update().best
    override lazy val iteration: Iteration[Int, Int, IndexedSeqWorkingSet] = Iteration().fromSelectionMutationEvaluateUpdate
  }

  abstract class GA extends Base(10) {
    def crossoverTwo(p1: Int, p2: Int): (Int, Int) = {
      val size = 1 + random().nextInt(bitSize)
      val sizeMask = Bits.nBitMask(size)
      val o1 = random().nextInt(bitSize - size + 1)
      val o2 = random().nextInt(bitSize - size + 1)
      val x1 = (p1 >>> o1) & sizeMask
      val x2 = (p2 >>> o2) & sizeMask
      val q1 = (p1 & ~(sizeMask << o1)) | (x2 << o1)
      val q2 = (p2 & ~(sizeMask << o2)) | (x1 << o2)
      (q1, q2)
    }

    implicit lazy val selection: Selection[Int, Int, IndexedSeqWorkingSet] = Selection().tournamentOlympic(10, 3, 0.9)
    implicit lazy val mutation: Mutation[Int, Int] = Mutation().usingCrossoverTwoAndMutation(crossoverTwo, 1.0, mutate, 0.5)
    implicit lazy val update: Update[Int, Int, IndexedSeqWorkingSet] = Update().elitist(0.2)
    override lazy val iteration: Iteration[Int, Int, IndexedSeqWorkingSet] = Iteration().fromSelectionMutationEvaluateUpdate
  }

  def nTimes(name: String, times: Int, fun: => Int) {
    print(name + ":")
    for (_ <- 0 until times) {
      val v = fun
      if (v == Int.MaxValue) {
        print("  XX |")
      } else {
        print(f" $v%3d |")
      }
    }
    println()
  }

  val times = 40
  for ((name, mut) <- Seq[(String, (RandomSource, Int, Int) => Int)](
    ("1", (rs, bitSize, source) => source ^ (1 << rs().nextInt(bitSize))),
    ("T", (rs, bitSize, source) => source ^ Bits.nBitMask(rs().nextInt(bitSize)))
  )) {
    nTimes(s"GA[$name]", times, new GA {
      def mutate(src: Int): Int = mut(random, mask, src)
    }.run())
    nTimes(s"ES[$name]", times, new ES {
      def mutate(src: Int): Int = mut(random, mask, src)
    }.run())
  }
}
