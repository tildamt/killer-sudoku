package KillerSudoku

import KillerSudoku.BigGrid

class Square(row: Int, col: Int, subGrid: SubGrid, bigGrid: BigGrid):

  var number: Option[Int] = None

  // Adds a number to the square. Returns a boolean value indicating whether the adding number worked.
  def addNumber(n: Int): Boolean =
    var bool = false
    if !bigGrid.getRowNumbers(row).contains(Option(n)) && !bigGrid.getColNumbers(col).contains(Option(n)) then
      number = Option(n)
      bool = true
    bool

  // Deletes number from a square. Returns a boolean value to indicate whether the deletion worked.
  def deleteNumber: Boolean =
    var bool = false

    if number.nonEmpty then
      number = None
      bool = true

    bool

  // Gives the position of the square as (row, column).
  def getPosition: (Int, Int) =
    (row, col)



