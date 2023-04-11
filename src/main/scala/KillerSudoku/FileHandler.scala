package KillerSudoku

import io.circe._, io.circe.generic.auto._, io.circe.parser._, io.circe.syntax._
import KillerSudoku.SubArea
import java.nio.file.{Paths, Files}

class FileHandler:

  // As stated in the course material, I first create three case classes. The first case class
  // called "GameState" represents the current grid. The second case class, "CurrentCages" represents
  // the cages on the grid. Finally, the third case class represents the game as a whole, and combines
  // both the GameState and subareas (which are the same as cages).
  case class GameState(game: Array[Array[Option[Int]]])
  case class CurrentCages(cells: List[(Int, Int)], sum: Int)
  case class WholeGame(state: GameState, subareas: List[CurrentCages])

  var errorText = ""

  // This method is used when the user clicks on the Save Game -button. This method
  // takes three parameters: n representing the name of the new file being created,
  // v representing the current elements on the grid, and cages representing the cages
  // on the grid. Then it writes all this information to a new file. This method can
  // also be used when the user exits the app.
  def places(n: String, v: Array[Array[Option[Int]]], cages: List[SubArea]): Unit =
    val eventualCages = cages.map(a => CurrentCages(a.squares, a.summa)).toList
    val gameState = GameState(v)
    val bothStateAndCages = WholeGame(gameState, eventualCages)
    val json = bothStateAndCages.asJson
    val file = new java.io.FileWriter(s"savedgames/${n}.json")
    file.write(json.spaces2)
    file.close()

  // When the user wants to continue their previous games, these variables are changes to hold
  // the previous subareas (in the variable areas) and the previous grid (in the variable ngrid).
  var areas = List[SubArea]()
  var ngrid: Array[Array[Option[Int]]] = Array.fill[Option[Int]](9, 9)(None)

  // In this method, I read the previous progress from the file and store it into a variable
  // called contents. Then, I decode the elements as taught on this page: https://circe.github.io/circe/codec.html
  // [read 8.4.2023]. I then use that result to change the areas and ngrid variables, so I can retrieve them and use
  // them to create the same grid as the one from the previous progress.
  def continue(s: String): Unit =
    val contents = new String(Files.readAllBytes(Paths.get(s"savedgames/${s}.json")))
    val result = decode[WholeGame](contents)
    // now the result creates a new variable
    result match
        case Left(error) => println(s"Invalid JSON, error: $error")
          errorText = s"$error"
        case Right(wholeGame) => val newcages = wholeGame.subareas.map( currentCages =>
          new SubArea(currentCages.cells, currentCages.sum))
          areas = newcages
          val currentstate = wholeGame.state.game
          ngrid = currentstate

  // This variable is used to store the new game.
  var areas1 = List[SubArea]()

  // In this method, I create a new game by reading from the file, somewhat similarly as in the previous
  // part. The reason that these methods are separated is that it is easier for me to use them. In both
  // methods, I read from files as taught on this page: https://attacomsian.com/blog/java-files-readallbytes-example?utm_content=cmp-true
  // [read 8.4.2023]. Then I create a string for the decode-method.
  def newgame(s: String): Unit =
    val contents = new String(Files.readAllBytes(Paths.get(s"games/${s}.json")))
    val result = decode[WholeGame](contents)

    // now the result creates a new variable
    result match
        case Left(error) => println(s"Invalid JSON, error: $error")
          errorText = s"$error"
        case Right(wholeGame) =>
          val newcages = wholeGame.subareas.map( currentCages =>
          new SubArea(currentCages.cells, currentCages.sum))
          areas1 = newcages
