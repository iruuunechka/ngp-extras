package ru.ifmo.ctd.ngp.opt

import org.junit.{Assert, Test}
import java.io.{PrintWriter, StringWriter}
import ru.ifmo.ctd.ngp.opt.termination.{Crash, CodomainThreshold}
import ru.ifmo.ctd.ngp.opt.iteration.{Update, Mutation, Selection}
import ru.ifmo.ctd.ngp.opt.event.IterationFinishedEvent

/**
 * Tests that checks if the `opt` package works at all.
 *
 * @author Maxim Buzdalov
 */
class ValidityTest {
  class Config(mu: Int, lambda: Int) extends OptConfiguration[Int, Int] {
    private val sw = new StringWriter()
    private val pw = new PrintWriter(sw)

    private implicit val evaluator          = Evaluator().usingFunction(Integer.bitCount)
    private implicit val codomainComparator = CodomainComparator().byOrdering.increasing
    private implicit val evaluatedOrdering  = codomainComparator.evaluatedOrdering
    private implicit val initialization     = Initialization().useDomainGenerator(random().nextInt(), mu)
    private implicit val selection          = Selection().all
    private implicit val mutation           = Mutation().using(_ ^ (1 << random().nextInt(32)), lambda / mu)
    private implicit val update             = Update().best
    private implicit val iteration          = Iteration().fromSelectionMutationEvaluateUpdate
    private implicit val termination        = Termination.Pluggable()
    private implicit val optimizer          = Optimizer().simple

    CodomainThreshold().register(32)
    IterationFinishedEvent().addListener(w => pw.println(w.max))

    def check() {
      sw.getBuffer.setLength(0)
      val Optimizer.Result(ws, CodomainThreshold) = optimizer()
      Assert.assertTrue(ws().exists(_.output == 32))
      Assert.assertTrue(sw.toString.contains("Evaluated(-1,32)"))
    }

    def checkFails() {
      val Optimizer.Result(_, Crash(_)) = optimizer()
    }
  }

  @Test
  def worksWithListeners() {
    new Config(1, 1).check()
    new Config(5, 20).check()
  }

  @Test
  def crashWorksCorrectly() {
    new Config(-1, 1).checkFails()
  }
}
