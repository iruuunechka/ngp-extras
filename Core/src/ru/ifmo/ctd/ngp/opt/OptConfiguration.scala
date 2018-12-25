package ru.ifmo.ctd.ngp.opt

import ru.ifmo.ctd.ngp.opt.types.{WorkingSetType, DomainType, CodomainType}

/**
 * A root configuration for all convenience configurations.
 *
 * @author Maxim Buzdalov
 */
class OptConfiguration[D, C] {
  /**
   * The source of randomness.
   */
  implicit val random: RandomSource = RandomSource.fastRandom
  /**
   * Defines the domain type tag.
   */
  implicit val domain: DomainType[D] = DomainType[D]
  /**
   * Defines the codomain type tag.
   */
  implicit val codomain: CodomainType[C] = CodomainType[C]
  /**
   * Defines the working set type tag.
   */
  implicit val workingSet: WorkingSetType[WorkingSetType.IndexedSeqWorkingSet] = WorkingSetType.indexedSeqWorkingSetType
  /**
   * Returns the sequence executor.
   * @return the sequence executor.
   */
  implicit def sequenceExecutor: SequenceExecutor = SequenceExecutor.sequential
}
