import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.*
import scala.language.adhocExtensions
import KillerSudoku.SubArea

// In this class, I test that my SubArea-class' method possibleCombinations works correctly. I get the possible
// combinations by first getting all possible combinations with numbers from 1 to 9 based on the size of the
// cage, and then I filter out the combinations whose sum doesn't match the sum to reach.
// The correct answers are from this site: https://godoku.com/play/killer/combinations/ [read 10.4.2023]
// And I got information on how to write unit tests from the course material of Programming 2 (Spring 2023).
// Not sure I can link those, because the link doesn't work properly..

class CageTest extends AnyFlatSpec with Matchers:

  "Cage1" should "have only one possible combination" in {
    val cage_1 = SubArea(List((0,0),(1,0)), 3)
    val possibleCombinations = cage_1.possibleCombinations
    val expected: Seq[IndexedSeq[Int]] = Seq(IndexedSeq(1, 2))
    withClue("The combinations did not match the expected value") {
      possibleCombinations should { be (expected) }
    }
  }

  "Cage2" should "have multiple possible combinations" in {
    val cage_2 = SubArea(List((0,0),(1,0),(2,0),(3,0),(4,0),(5,0)), 29)
    val possibleCombinations_2 = cage_2.possibleCombinations
    val expected = Seq(IndexedSeq(1,2,3,6,8,9), IndexedSeq(1,2,4,5,8,9), IndexedSeq(1,2,4,6,7,9), IndexedSeq(1,2,5,6,7,8), IndexedSeq(1,3,4,5,7,9), IndexedSeq(1,3,4,6,7,8), IndexedSeq(2,3,4,5,6,9), IndexedSeq(2,3,4,5,7,8))
     withClue("The combinations did not match the expected value") {
      possibleCombinations_2 should { be (expected) }
    }
  }

  "Cage3" should "have multiple combinations" in {
    val cage_3 = SubArea(List((0,0),(1,0),(2,0),(3,0)), 13)
    val possibleCombinations_3 = cage_3.possibleCombinations
    val expected = Seq(IndexedSeq(1,2,3,7), IndexedSeq(1,2,4,6), IndexedSeq(1,3,4,5))
    withClue("The combinations did not match the expected value") {
      possibleCombinations_3 should { be (expected) }
    }
  }



