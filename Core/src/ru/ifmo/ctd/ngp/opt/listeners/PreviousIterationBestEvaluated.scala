package ru.ifmo.ctd.ngp.opt.listeners

import scala.language.higherKinds

import ru.ifmo.ctd.ngp.opt.{CodomainComparator, Evaluated}
import ru.ifmo.ctd.ngp.opt.event.{InitializationStartedEvent, EvaluationFinishedEvent}

/**
 * A listener which stores the best `Evaluated` from the previous iteration.
 *
 * @author Maxim Buzdalov
 */
class PreviousIterationBestEvaluated[D, C]()(
  implicit evaluationFinished: EvaluationFinishedEvent[D, C],
           initializationStarted: InitializationStartedEvent,
           codomainComparator: CodomainComparator[C]
) {
  private var best, prevBest: Option[Evaluated[D, C]] = None
  def apply(): Option[Evaluated[D, C]] = prevBest
  def get: Evaluated[D, C] = prevBest.get

  InitializationStartedEvent() addListener { _ => best = None; prevBest = None }
  EvaluationFinishedEvent()    addListener { evaluated =>
    prevBest = best
    evaluated.foreach { t =>
      best = best match {
        case None      => Some(t)
        case o@Some(v) => if (codomainComparator(t.output, v.output) > 0) Some(t) else o
      }
    }
  }
}
