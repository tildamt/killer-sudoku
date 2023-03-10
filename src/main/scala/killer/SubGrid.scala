package killer

import scala.collection.mutable.Buffer

class SubGrid(rows: Int, cols: Int, squares: Array[Int]):

  val grid = Array.fill[Option[Int]](3, 3)(None)

  // A method to check which numbers have already been placed in the sub-grid.
  def getPlacedNumbers: Buffer[Int] =
    grid.flatten.filter(_.nonEmpty).map(a => a.get).toBuffer

  // A method to check whether the sub-grid has been finished.
  def isFinished: Boolean =
    getPlacedNumbers.size == 9
