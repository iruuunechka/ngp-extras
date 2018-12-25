package ru.ifmo.ctd.ngp.theory.unrelated.onemax

import scala.annotation.tailrec
import scala.collection.BitSet

import ru.ifmo.ctd.ngp.learning.reinforce.Agent
import ru.ifmo.ctd.ngp.learning.reinforce.q.EGreedyAgent
import ru.ifmo.ctd.ngp.theory.onemax.argmax.{Theory => OneMaxZeroMax}
import ru.ifmo.ctd.ngp.util._

/**
 * Evaluation of OneMax+OneMax'
 */
object Main extends App {
  val n = 200
  val generationSize = 1
  val runCount = 10001
  val oneMaxExpected = (1 to n).mapSum(n.toDouble / _) + 1
  val oneMaxZeroMax = OneMaxZeroMax.solve(n)

  @tailrec
  def addOne(s: BitSet): BitSet = {
    val nv = s + FastRandom.threadLocal().nextInt(n)
    if (nv.size > s.size) nv else addOne(s)
  }

  @tailrec
  def work(diff: Int, second: BitSet, newAgent: () => Option[Agent[Int, Int]]): Unit = {
    val runs = IndexedSeq.fill(runCount)(1).par.map(_ => new Config(
      n, generationSize, onePlusOne = false, nStates = true, newAgent(), second
    ).run()).seq.sorted
    val min = runs.head
    val max = runs.last
    val med = runs(runs.size / 2)
    val mean = runs.sum / runs.size
    val infs = runs.count(_.isInfinity)
    println(s"n = $n, diff = $diff: min = $min, max = $max, med = $med, mean = $mean, infs = $infs")
    if (diff < n) work(diff + 1, addOne(second), newAgent)
  }

  println(s"OneMax => $oneMaxExpected, OneMax+ZeroMax => $oneMaxZeroMax")
  work(0, BitSet.empty, () => Some(new EGreedyAgent[Int, Int](0.0, 1.0, 0.5, 0.5)))
}
