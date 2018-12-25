package ru.ifmo.ctd.ngp.opt.listeners

import ru.ifmo.ctd.ngp.opt.event.{BestEvaluatedUpdatedEvent, EvaluationFinishedEvent, InitializationStartedEvent}

import scala.language.higherKinds

/**
 * A thing that listens to `EvaluationFinishedEvent`s and counts the number of iterations, including initialization,
 * since last update of the best evaluated object.
 *
 * @author Maxim Buzdalov
 */
class EvaluationCountSinceBestUpdate[D, C](
  implicit evaluationFinished: EvaluationFinishedEvent[D, C],
           bestEvaluatedUpdate: BestEvaluatedUpdatedEvent[D, C],
           initializationStarted: InitializationStartedEvent
) {
  private var count = 0L
  def apply(): Long = count

  InitializationStartedEvent() addListener { _ => count = 0  }
  EvaluationFinishedEvent()    addListener { count += _.size }
  BestEvaluatedUpdatedEvent()  addListener { _ => count = 0  }
}
