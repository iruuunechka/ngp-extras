package ru.ifmo.ctd.ngp.demo.ffchooser.multicriteria.temp

import java.io.{File, FileReader}
import java.util.{Properties, StringTokenizer, ArrayDeque => AD, ArrayList => AL}

import scala.collection.JavaConverters._
import scala.util.Random.javaRandomToRandom

import ru.ifmo.ctd.ngp.demo.ffchooser.jobshop.{DataFileReader, JobShopUtils}
import ru.ifmo.ctd.ngp.opt._
import ru.ifmo.ctd.ngp.opt.algorithms.NSGA2
import ru.ifmo.ctd.ngp.opt.event.IterationFinishedEvent
import ru.ifmo.ctd.ngp.opt.iteration.Mutation
import ru.ifmo.ctd.ngp.opt.listeners.{BestEvaluated, IterationCount}
import ru.ifmo.ctd.ngp.opt.misc.earl.EARLMultipleCriteria
import ru.ifmo.ctd.ngp.opt.multicriteria.MultipleCriteria.CriterionToDouble
import ru.ifmo.ctd.ngp.opt.multicriteria.{DiversityMeasure, MultipleCriteria, NonDominatedSorting}
import ru.ifmo.ctd.ngp.opt.termination.{Crash, IterationLimit}
import ru.ifmo.ctd.ngp.util._

/**
 * Runs the algorithm from the article using NSGA-II from Scala library.
 */
object ArticleAlgoRunnerNSGA2 extends App {
  private def readDataSets() = {
    val p = new Properties()
    val fr = new FileReader("./misc/jobshop.properties")
    p.load(fr)
    fr.close()
    val tok = new StringTokenizer(p.getProperty("datasets.full"), ", ")
    IndexedSeq.fill(tok.countTokens() / 2)(tok.nextToken() -> tok.nextToken().toInt)
  }

  private val dataFiles      = new DataFileReader(new File("./misc/jobshop.txt"))
  private val sets           = readDataSets()
  private val evaluations    = 20000
  private val generationSize = 100
  private val runs           = 100

  for ((name, optimum) <- sets) {
    println("----------------------")
    val dataSet       = dataFiles.get(name)
    val times         = dataSet.getTimes
    val machines      = dataSet.getMachines
    val machineCount  = times(0).length
    val jobs          = times.length
    val max           = jobs * times.map(_.sum).sum
    val flowTime      = IndexedSeq.tabulate(jobs)(i => times(i).sum)
    val sortedHelpers = (0 until jobs).sortBy(flowTime)
    val iterations     = math.max(evaluations / generationSize, 2 * jobs * machineCount)

    def checkAndReturn(a: IndexedSeq[Int]) = {
      val counts = new Array[Int](jobs)
      a foreach (t => counts(t) += 1)
      assert(counts forall (_ == machineCount))
      a
    }

    for (helperCount <- Seq(1, 2, (jobs + 1) / 2)) {
      val ranges = (0 until jobs).grouped(helperCount).map(t => t.head to t.last).toIndexedSeq

      def fitness(ind: IndexedSeq[Int]): IndexedSeq[Int] = {
        val ev = JobShopUtils.evalFlowTimes(ind.map(Integer.valueOf).asJava, JobShopUtils.createJobsList(times, machines))
        (max - ev.sum) +: ranges.map(r => max - r.mapSum(i => ev(sortedHelpers(i))))
      }

      class Config(iterations: Long, initialState: Option[IndexedSeq[Evaluated[IndexedSeq[Int], IndexedSeq[Int]]]], helper: Int = 1)
        extends OptConfiguration[IndexedSeq[Int], IndexedSeq[Int]]
      {

        private def pbm(orig: IndexedSeq[Int]) = {
          val i1, i2 = random().nextInt(orig.size)
          val builder = IndexedSeq.newBuilder[Int]
          builder.sizeHint(orig.size)
          for (i <- orig.indices) {
            if (i2 <= i1 && i == i2) {
              builder += orig(i1)
            }
            if (i != i1) {
              builder += orig(i)
            }
            if (i2 > i1 && i == i2) {
              builder += orig(i1)
            }
          }
          checkAndReturn(builder.result())
        }

        private def gox(a: IndexedSeq[Int], b: IndexedSeq[Int]) = {
          val rng = random()
          val len = a.size
          val subLen = len / 3 + rng.nextInt(len / 6)
          val pos = rng.nextInt(len)

          def indices(a: IndexedSeq[Int]) = {
            val cnt = Array.ofDim[Int](jobs)
            Array.tabulate(a.size){i => cnt(a(i)) += 1; cnt(a(i)) - 1}
          }
          def toUsedIndex(job: Int, index: Int) = job + jobs * index

          val ai = indices(a)
          val bi = indices(b)
          val used = Array.ofDim[Boolean](a.size)

          if (pos + subLen <= len) {
            checkAndReturn {
              for (i <- pos until pos + subLen) {
                used(toUsedIndex(b(i), bi(i))) = true
              }
              val builder = IndexedSeq.newBuilder[Int]
              val insertIndex = toUsedIndex(b(pos), bi(pos))
              for (i <- a.indices) {
                val currIndex = toUsedIndex(a(i), ai(i))
                if (!used(currIndex)) {
                  builder += a(i)
                }
                if (currIndex == insertIndex) {
                  for (j <- pos until pos + subLen) {
                    builder += b(j)
                  }
                }
              }
              builder.result()
            }
          } else {
            checkAndReturn {
              val builder = IndexedSeq.newBuilder[Int]
              for (i <- 0 until pos + subLen - len) {
                used(toUsedIndex(b(i), bi(i))) = true
                builder += b(i)
              }
              for (i <- pos until len) {
                used(toUsedIndex(b(i), bi(i))) = true
              }
              for (i <- a.indices) {
                if (!used(toUsedIndex(a(i), ai(i)))) {
                  builder += a(i)
                }
              }
              for (i <- pos until len) {
                builder += b(i)
              }
              builder.result()
            }
          }
        }

        private val iterationsPerHelper = (iterations + ranges.size - 1) / ranges.size

        private implicit val evaluator = Evaluator().usingFunction(fitness)
        private implicit val multiple = EARLMultipleCriteria().fromMultipleCriteria(
          Seq(0), "dynamic", helper
        )(
            MultipleCriteria.fromIndexedSeqOfNumeric("sum" +: ranges.map(_.toString()) :_*)
          )
        private implicit val criterion2double = new CriterionToDouble[IndexedSeq[Int]] {
          override def criterionToDouble(codomain: IndexedSeq[Int], criterion: Int): Double = codomain(criterion)
        }
        private implicit val sorting = NonDominatedSorting().jensenFortinSorting
        private implicit val diversity = DiversityMeasure().crowdingDistanceDeb
        private implicit val mutation = Mutation().usingCrossoverOneAndMutation(gox, 1.0, pbm, 1.0)
        private implicit val (ws, w2i, i2w, itr) = NSGA2().configuration(2 * generationSize)
        private implicit val initialization = initialState match {
          case Some(v) => Initialization().use(v)
          case None => Initialization().useDomainGenerator(
            random().shuffle(IndexedSeq.tabulate(jobs * machineCount)(i => i / machineCount)),
            generationSize
          )
        }
        private implicit val termination = Termination.Pluggable()
        private implicit val optimizer = Optimizer().simple
        private implicit val comparator = CodomainComparator().byOrdering(multiple.orderingForCriterion(0)).increasing
        private implicit val bestEvaluated = new BestEvaluated()
        private implicit val iterationCount = new IterationCount()

        IterationLimit().register(iterations)

        if (initialState.isEmpty) {
          IterationFinishedEvent().addListener { _ =>
            if (iterationCount() % iterationsPerHelper == 0) {
              multiple.currentChoice = (iterationCount() / iterationsPerHelper).toInt
            }
          }
        }
        def run(): (Int, IndexedSeq[Evaluated[IndexedSeq[Int], IndexedSeq[Int]]]) = {
          optimizer() match {
            case Optimizer.Result(set, IterationLimit) =>
              (max - bestEvaluated().get.output(0), w2i(set()))
            case Optimizer.Result(_, Crash(th)) =>
              th.printStackTrace()
              throw th
            case Optimizer.Result(_, _) =>
              throw new AssertionError("Should not happen")
          }
        }
      }

      for (algo <- Seq("[article]", "[irene]  ")) {
        def experiment() = if (algo == "[article]") new Config(iterations, None).run()._1 else {
          var currentState = IndexedSeq.fill(generationSize){
            FastRandom.threadLocal().shuffle(IndexedSeq.tabulate(jobs * machineCount)(i => i / machineCount))
          }.map(t => Evaluated(t, fitness(t)))
          var min = 1000000000
          val bigIterations = iterations / (2 * ranges.size)
          for (_ <- 0 until bigIterations) {
            val generationsForEach = {
              val arr = Array.ofDim[Int](ranges.size)
              for (_ <- 0 until 2 * ranges.size) {
                arr(FastRandom.threadLocal().nextInt(ranges.size)) += 1
              }
              arr.toIndexedSeq
            }
            val results = ranges.indices.map{ helper =>
              if (generationsForEach(helper) == 0) {
                (1000000000, currentState)
              } else {
                new Config(generationsForEach(helper), Some(currentState), helper + 1).run()
              }
            }
            val optimal = results.minBy(_._1)
            min = math.min(min, optimal._1)
            currentState = optimal._2
          }
          min
        }
        val t0 = System.currentTimeMillis()
        val results = (0 until runs).par.map(_ => experiment()).seq.map(_.toDouble)
        val time = System.currentTimeMillis() - t0
        val percentList = results.map(t => (t - optimum) / optimum * 100)
        def mean(a: Seq[Double]) = a.sum / a.size
        def dispersion(a: Seq[Double]) = {
          val m = mean(a)
          math.sqrt(mean(a.map(t => (t - m) * (t - m))))
        }
        val resMean = mean(results)
        val resDisp = dispersion(results)
        val pcMean = mean(percentList)
        val pcDisp = dispersion(percentList)
        println(s"$algo [$helperCount] $name, $optimum: ($resMean, $resDisp)($pcMean, $pcDisp) [${results.min}..${results.max}] in $time ms")
      }
    }
  }
}
