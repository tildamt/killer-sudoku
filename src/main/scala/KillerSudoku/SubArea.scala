package KillerSudoku

import scala.collection.mutable.Buffer

class SubArea(val squares: List[(Int, Int)], val summa: Int):

  // This method first creates all possible combinations with the numbers from 1-9 according
  // to the cage's size, after which it filters out those whose sum doesn't add up to the sum
  // to reach. I tried to re-name the variable summa to be in English, but it kept failing for
  // some reason...
  def possibleCombinations: Seq[IndexedSeq[Int]] =
    (1 to 9).toSeq.combinations(squares.size).toSeq.filter( _.sum == summa)

  override def toString: String = "Squares: " + squares + " sum: " + summa