package ru.ifmo.ctd.ngp.opt.types

import scala.language.higherKinds

import ru.ifmo.ctd.ngp.opt.Evaluated

/**
 * A descriptor of the working set of the optimization algorithm.
 *
 * @author Maxim Buzdalov
 */
final class WorkingSetType[W[+D, +C]] private () {
  type Type[+D, +C] = W[D, C]
}

object WorkingSetType {
  private val instance = new WorkingSetType[Nothing]
  /**
   * Returns the working set type tag for the given codomain type.
   * @tparam W the working set type.
   * @return the working set type tag.
   */
  def apply[W[+D1, +C1]]: WorkingSetType[W] = instance.asInstanceOf[WorkingSetType[W]]

  /**
   * A convenience definition of a working set type which is just an IndexedSeq of `Evaluated`s
   */
  type IndexedSeqWorkingSet[+D, +C] = IndexedSeq[Evaluated[D, C]]

  /**
   * This is a working set type tag for `IndexedSeqWorkingSet`.
   * This is a workaround for some Scala bug (https://issues.scala-lang.org/browse/SI-7902).
   */
  val indexedSeqWorkingSetType: WorkingSetType[IndexedSeqWorkingSet] = WorkingSetType[IndexedSeqWorkingSet]
}
