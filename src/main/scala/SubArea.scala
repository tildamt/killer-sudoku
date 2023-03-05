import scala.collection.mutable.Buffer

class SubArea(val summa: Int, squares: Buffer[Int]):

  // At the moment, this method returns every possible combination. First, we make a sequence which consists of
  // numbers 1,2,3,4,5,6,7,8 and 9. Then, we use Seq's method combinations, which now gives us all the possible
  // combinations for the cage's (or sub-area's) size. Then, out of these combinations, we use the method filter
  // to get only the combinations whose sum is equal to the sum of the sub-area. We'll still have to filter the
  // combinations of numbers which have a number that's in the row or column then.
  def possibleCombinations: Seq[IndexedSeq[Int]] =
    (1 to 9).toSeq.combinations(squares.size).toSeq.filter( _.sum == summa)

  // I'm representing the squares with only integers now, so this method isn't
  // truly in its final form. For the real version, maybe mapping the numbers would be great.
  def getReadyNumbers: Buffer[Int] =
    squares

  // The sub-area is finished when its placed numbers sum equals the sub-area's sum.
  def isFinished: Boolean =
    getReadyNumbers.sum == summa