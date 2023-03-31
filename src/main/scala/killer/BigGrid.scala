package killer

import scala.collection.mutable.Buffer
import killer.Square

class BigGrid(rows: Int, cols: Int, squares: Array[Array[Option[Int]]]):

  var grid = Array.fill[Option[Int]](9, 9)(None)

  def updateElement(r: Int, c: Int, n: Option[Int]) =
    grid(r)(c) = n

  /*def getRowNumbers(r: Int): Buffer[Int] =
    val optionNumbers = grid(r).filter(_.nonEmpty)
    optionNumbers.map(_.get).toBuffer*/

  def getColNumbers(c: Int) /*Buffer[Option[Int]] */=
    grid.map(_(c))//.toBuffer


  // This method is used to get all the numbers on a certain column on the grid.
  def getRowNumbers(c: Int) /*Seq[Int]*/ =
    grid(c)//.filter(_.nonEmpty).map(x => x.get).toSeq

  // This method is used to get all the numbers on a certain row on the grid.
  /*def getRowNumbers(r: Int): Buffer[Option[Int]] =
    grid(r).filter(_.nonEmpty).toBuffer//.map(_.get)*/

  // This method is used to check whether the grid has already been completed.
  def isCompleted: Boolean =
    grid.forall(_.nonEmpty)

  // A method to check whether all instances of a number have been added to the grid.
  def everyInstanceDone(n: Int): Boolean =
    grid.flatten.count(_ == Some(n)) == 9

