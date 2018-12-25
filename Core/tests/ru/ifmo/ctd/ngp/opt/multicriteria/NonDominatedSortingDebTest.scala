package ru.ifmo.ctd.ngp.opt.multicriteria

/**
 * A test suite for Deb non-dominated sorting.
 *
 * @author Maxim Buzdalov
 */
class NonDominatedSortingDebTest extends NonDominatedSortingTestBase {
  def getSorter(criteria: Int): NonDominatedSorter = NonDominatedSorter.createDeb(criteria)
}
