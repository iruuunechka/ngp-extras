package ru.ifmo.ctd.ngp.opt.event

/**
 * An event which is fired when the iteration is started by an `Iteration` object.
 *
 * The event's argument is the working set after the iteration.
 *
 * @author Maxim Buzdalov
 */
trait IterationStartedEvent[W] {
  protected val iterationStartedAccessor = new EventBackend.Accessor[W]
  val iterationStartedEvent: EventBackend[W] = iterationStartedAccessor.event
}

object IterationStartedEvent {
  def apply[W]()(implicit event: IterationStartedEvent[W]): EventBackend[W] = {
    event.iterationStartedEvent
  }
}
