package killer

class Square(row: Int, col: Int, subGrid: SubGrid, bigGrid: BigGrid):

  var number: Option[Int] = None

  // Adds a number to the square.
  def addNumber(n: Int)() =
    number = Option(n)

  // Deletes number from a square.
  def deleteNumber() =
    number = None

  // Gives the position of the square as (row, column).
  def getPosition: (Int, Int) =
    (row, col)
 


