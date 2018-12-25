package ru.ifmo.ctd.ngp.opt.misc.earl

/**
 * An interface to EARL algorithm family.
 *
 * @author Maxim Buzdalov
 */
trait EARLInterface {
  def initialChoice: Int
  def currentChoice: Int
  def currentChoice_=(newChoice: Int)
  def choices: Int
}
