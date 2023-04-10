import javafx.collections.ObservableList
import javafx.event.EventHandler
import killer.{BigGrid, CreateGrid, filehandling}
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.control.{ButtonType, Dialog}
import scalafx.scene.layout.Pane
import javafx.stage.WindowEvent
import scalafx.Includes.string2jfxColor
import scalafx.scene.SceneIncludes.string2jfxColor
import scalafx.scene.paint.PaintIncludes.string2jfxColor

import java.text.DateFormat
import java.time.{ZoneOffset, ZonedDateTime}

object Main extends JFXApp3 :

  def start(): Unit =

    stage = new JFXApp3.PrimaryStage
    stage.height = 550
    stage.width = 850
    stage.title = "Killer Sudoku"

    // New stage is created whose scene is set to a standard, however the user may change it by 
    // continuing an old game or choosing a new game.
    val gamecreator = new filehandling
    gamecreator.newgame("game1")  // standard is game1, but this changeable
    val creator = new CreateGrid(this, gamecreator.areas1, Array.fill[Option[Int]](9, 9)(None))
    val creation = creator.create  // creating a new scene with the class CreateGrid's create method
    val scene = creation // Scene acts as a container for the scene graph
    stage.scene = scene // Assigning the new scene as the current scene for the stage


    // When the user closes the app, the current progress is saved to a file which is named 
    // in the format DD-MM-YYYY at HH-MM-SS
    stage.onCloseRequest = (e: WindowEvent) =>
      val aika = java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("Europe/Helsinki"))
      val päivä = aika.get(java.util.Calendar.DAY_OF_MONTH)
      val vuosi = aika.get(java.util.Calendar.YEAR)
      val kuukausi = aika.get(java.util.Calendar.MONTH)
      val tunti = aika.get(java.util.Calendar.HOUR_OF_DAY)
      val minuutti = aika.get(java.util.Calendar.MINUTE)
      val sekunti = aika.get(java.util.Calendar.SECOND)
      gamecreator.paikat(s"${päivä}-${kuukausi}-${vuosi} at ${tunti}-${minuutti}-${sekunti}", creator.arr, creator.cages)