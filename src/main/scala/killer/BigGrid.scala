package killer

import scala.collection.mutable.Buffer

class BigGrid(rows: Int, cols: Int, squares: Array[Int]):

  val grid = Array.fill[Option[Int]](9, 9)(None)

  // This method is used to get all the numbers on a certain column on the grid.
  def getColNumbers(c: Int): Buffer[Int] =
    val optionNumbers = (for (row <- grid) yield row(c)).filter(_.nonEmpty)
    var numbers = Buffer[Int]()
    for number <- optionNumbers do
      if number.nonEmpty then numbers.append(number.get)  // I think get is suitable here since I've already checked twice for the element not to empty.
    numbers

  // This method is used to get all the numbers on a certain row on the grid.
  def getRowNumbers(r: Int): Buffer[Option[Int]] =
    grid(r).filter(_.nonEmpty).toBuffer

  // This method is used to check whether the grid has already been completed.
  def isCompleted: Boolean =
    grid.forall(_.nonEmpty)

  // A method to check whether all instances of a number have been added to the grid.
  def everyInstanceDone(n: Int): Boolean =
    grid.count( _.contains(Option(n))) == 9
