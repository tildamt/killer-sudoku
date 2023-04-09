import killer.{BigGrid, CreateGrid, filehandling}
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.layout.Pane

object Main extends JFXApp3 :

  def start(): Unit =

    stage = new JFXApp3.PrimaryStage
    stage.height = 550
    stage.width = 850
    stage.title = "Killer Sudoku"

    val gamecreator = new filehandling
    gamecreator.newgame("game1")
    val creation = new CreateGrid(this, gamecreator.areas1, Array.fill[Option[Int]](9, 9)(None)).create
    val scene = creation // Scene acts as a container for the scene graph
    stage.scene = scene // Assigning the new scene as the current scene for the stage