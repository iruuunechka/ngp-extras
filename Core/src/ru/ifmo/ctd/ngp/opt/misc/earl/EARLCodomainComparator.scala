package ru.ifmo.ctd.ngp.opt.misc.earl

import ru.ifmo.ctd.ngp.opt.types.CodomainType
import ru.ifmo.ctd.ngp.opt.CodomainComparator
import ru.ifmo.ctd.ngp.opt.multicriteria.MultipleCriteria

/**
 * A codomain comparator which is controlled by the EA+RL method.
 *
 * @author Maxim Buzdalov
 */
abstract class EARLCodomainComparator[C: CodomainType] extends CodomainComparator[C] with EARLInterface

object EARLCodomainComparator {
  class Detected[C: CodomainType] {
    /**
     * Constructs the EARLCodomainComparator from the known multiple criteria descriptor
     * and the initial choice.
     * @param initialChoice the initial criterion to optimize by.
     * @return
     */
    def fromMultipleCriteria(initialChoice: Int)
                            (implicit mc: MultipleCriteria[C]): EARLCodomainComparator[C] = {
      require(0 <= initialChoice && initialChoice < mc.numberOfCriteria)

      val enclosingChoices = mc.numberOfCriteria
      val enclosingInitial = initialChoice
      new EARLCodomainComparator[C]() {
        private var choice = enclosingInitial
        val choices: Int = enclosingChoices
        val initialChoice: Int = enclosingInitial
        def apply(lhs: C, rhs: C): Int = mc.orderingForCriterion(choice).compare(lhs, rhs)
        def currentChoice: Int = choice
        def currentChoice_=(newChoice: Int): Unit = {
          require(0 <= newChoice && newChoice < choices)
          choice = newChoice
        }
      }
    }
  }

  /**
   * Defines the codomain type and allows to select options further.
   * @tparam C the codomain type.
   * @return the object with functions to select more options.
   */
  def apply[C : CodomainType]() = new Detected[C]
}
