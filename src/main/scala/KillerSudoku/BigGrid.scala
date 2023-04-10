package KillerSudoku

import scala.collection.mutable.Buffer
import KillerSudoku.Square

class BigGrid(rows: Int, cols: Int, squares: Array[Array[Option[Int]]]):

  // This variable holds the current elements on the grid.
  var grid = squares

  // This method is used to change an element on the grid. This means that when
  // the user clicks on a cell and adds a number, this number is added to the grid.
  // When the user deletes a number from a cell, this method is used for that as well.
  def updateElement(r: Int, c: Int, n: Option[Int]): Unit =
    grid(r)(c) = n

  // This method is used on a certain column on the grid.
  def getColNumbers(c: Int): Array[Option[Int]] =
    grid.map(_(c))

  // This method is used to get all the numbers on a certain row on the grid.
  def getRowNumbers(c: Int): Array[Option[Int]] =
    grid(c)

  // A method to check whether all instances of a number have been added to the grid.
  def everyInstanceDone(n: Int): Boolean =
    grid.flatten.count(_ == Some(n)) == 9