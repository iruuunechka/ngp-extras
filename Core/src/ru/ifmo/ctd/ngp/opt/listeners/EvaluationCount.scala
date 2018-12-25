package ru.ifmo.ctd.ngp.opt.listeners

import scala.language.higherKinds

import ru.ifmo.ctd.ngp.opt.event.{InitializationStartedEvent, EvaluationFinishedEvent}

/**
 * A thing that listens to `EvaluationFinishedEvent`s and counts the number of fitness call count.
 *
 * @author Maxim Buzdalov
 */
class EvaluationCount[D, C](resetOnInitialization: Boolean = true)(
  implicit evaluationFinished: EvaluationFinishedEvent[D, C],
           initializationStarted: InitializationStartedEvent
) {
  private var count = 0L
  def apply(): Long = count

  if (resetOnInitialization) {
    InitializationStartedEvent() addListener { _ => count = 0 }
  }
  EvaluationFinishedEvent() addListener { count += _.size }
}
