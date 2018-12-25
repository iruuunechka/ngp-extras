package ru.ifmo.ctd.ngp.opt.misc.earl

import ru.ifmo.ctd.ngp.opt.multicriteria.MultipleCriteria
import ru.ifmo.ctd.ngp.opt.types.CodomainType

/**
 * An multiple criteria configuration which is controlled by the EA+RL method.
 *
 * @author Maxim Buzdalov
 */
abstract class EARLMultipleCriteria[C: CodomainType] extends MultipleCriteria[C] with EARLInterface

object EARLMultipleCriteria {
  class Detected[C: CodomainType] {
    def fromMultipleCriteria(fixedCriteria: Seq[Int], dynamicName: String, initial: Int)
                            (implicit mc: MultipleCriteria[C]): EARLMultipleCriteria[C] = {
      require(fixedCriteria.forall(c => c >= 0 && c < mc.numberOfCriteria),
        "Fixed criteria contain negative or too large indices")
      require(fixedCriteria.distinct.size == fixedCriteria.size,
        "Fixed criteria contain duplicates")
      val allOther = (0 until mc.numberOfCriteria).diff(fixedCriteria)
      require(allOther.contains(initial),
        "Initial criterion is either negative, too large or from fixed criteria")
      val implNumberOfCriteria = fixedCriteria.size + 1
      val dynamicIndex = implNumberOfCriteria - 1
      new EARLMultipleCriteria[C] {
        def numberOfCriteria: Int = implNumberOfCriteria
        def nameOfCriterion(criterion: Int): String = {
          if (criterion != dynamicIndex) {
            mc.nameOfCriterion(fixedCriteria(criterion))
          } else dynamicName
        }
        def orderingForCriterion(criterion: Int): Ordering[C] = if (criterion != dynamicIndex) {
          mc.orderingForCriterion(fixedCriteria(criterion))
        } else {
          mc.orderingForCriterion(allOther(currentChoice))
        }
        private var choice = allOther.indexOf(initial)

        def currentChoice: Int = choice
        def currentChoice_=(newChoice: Int): Unit = {
          choice = newChoice
        }
        def initialChoice: Int = initial
        def choices: Int = allOther.size
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
