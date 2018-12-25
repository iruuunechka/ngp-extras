package ru.ifmo.ctd.ngp.opt.multicriteria

/**
 * A test suite for Jensen non-dominated sorting.
 *
 * @author Maxim Buzdalov
 */
class NonDominatedSortingJensenFortinTest extends NonDominatedSortingTestBase {
  def getSorter(criteria: Int): NonDominatedSorter = NonDominatedSorter.createJensenFortin(criteria)
}
