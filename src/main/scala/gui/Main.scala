package gui

import gui.Main.stage
import javafx.stage.WindowEvent
import KillerSudoku.FileHandler
import scalafx.application.JFXApp3

object Main extends JFXApp3 :

  def start(): Unit =

    stage = new JFXApp3.PrimaryStage
    stage.height = 550
    stage.width = 850
    stage.title = "Killer Sudoku"

    // New stage is created whose scene is set to a standard, however the user may change it by
    // continuing an old game or choosing a new game.
    val gamecreator = new FileHandler
    gamecreator.newgame("game1") // standard is game1, but this changeable
    val creator = new CreateGame(this, gamecreator.areas1, Array.fill[Option[Int]](9, 9)(None))
    val creation = creator.create // creating a new scene with the class CreateGame's create method
    val scene = creation // Scene acts as a container for the scene graph
    stage.scene = scene // Assigning the new scene as the current scene for the stage


