package killer

import io.circe._, io.circe.generic.auto._, io.circe.parser._, io.circe.syntax._
import killer.SubArea
import java.nio.file.{Paths, Files}

class filehandling:

  case class GameState(game: Array[Array[Option[Int]]])
  case class CurrentCages(cells: List[(Int, Int)], sum: Int)
  case class WholeGame(state: GameState, subareas: List[CurrentCages])

  def paikat(n: String, v: Array[Array[Option[Int]]], cages: List[SubArea]) =
    val eventualCages = cages.map(a => CurrentCages(a.squares, a.summa)).toList
    val gameState = GameState(v)
    val bothStateAndCages = WholeGame(gameState, eventualCages)
    val json = bothStateAndCages.asJson
    val file = new java.io.FileWriter(s"savedgames/${n}.json")
    file.write(json.spaces2)
    file.close()

  var areas = List[SubArea]()

  var ngrid: Array[Array[Option[Int]]] = Array.fill[Option[Int]](9, 9)(None)

  def continue(s: String) =
    val contents = new String(Files.readAllBytes(Paths.get(s"savedgames/${s}.json")))

    val result = decode[WholeGame](contents)

    // now the result creates a new variable
    result match {
        case Left(error) => println(s"Invalid JSON :( $error")
        case Right(wholeGame) => val testi = wholeGame.subareas.map( currentCages =>
          new SubArea(currentCages.cells, currentCages.sum))
          areas = testi
          val kokeilu = wholeGame.state.game
          ngrid = kokeilu
          println(kokeilu.flatten.toVector.filter(_.nonEmpty).map( b => b.get))
      }

  var areas1 = List[SubArea]()


  def newgame(s: String) =
    val contents = new String(Files.readAllBytes(Paths.get(s"games/${s}.json")))
    val result = decode[WholeGame](contents)

    // now the result creates a new variable
    result match {
        case Left(error) => println(s"Invalid JSON :( $error")
        case Right(wholeGame) =>
          val testi = wholeGame.subareas.map( currentCages =>
          new SubArea(currentCages.cells, currentCages.sum))
          areas1 = testi
      }