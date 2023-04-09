import javafx.event.EventHandler
import killer.{BigGrid, CreateGrid, filehandling}
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.control.{ButtonType, Dialog}
import scalafx.scene.layout.Pane
import javafx.stage.WindowEvent

import java.text.DateFormat
import java.time.{ZoneOffset, ZonedDateTime}

object Main extends JFXApp3 :

  def start(): Unit =

    stage = new JFXApp3.PrimaryStage
    stage.height = 550
    stage.width = 850
    stage.title = "Killer Sudoku"

    val gamecreator = new filehandling
    gamecreator.newgame("game1")
    val creator = new CreateGrid(this, gamecreator.areas1, Array.fill[Option[Int]](9, 9)(None))
    val creation = creator.create
    val scene = creation // Scene acts as a container for the scene graph
    stage.scene = scene // Assigning the new scene as the current scene for the stage


    stage.onCloseRequest = (e: WindowEvent) =>
      val aika = java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("Europe/Helsinki"))
      val päivä = aika.get(java.util.Calendar.DAY_OF_MONTH)
      val vuosi = aika.get(java.util.Calendar.YEAR)
      val kuukausi = aika.get(java.util.Calendar.MONTH)
      val tunti = aika.get(java.util.Calendar.HOUR_OF_DAY)
      val minuutti = aika.get(java.util.Calendar.MINUTE)
      val sekunti = aika.get(java.util.Calendar.SECOND)
      gamecreator.paikat(s"${päivä}-${kuukausi}-${vuosi} at ${tunti}-${minuutti}-${sekunti}", creator.theGrid.grid, creator.cages)