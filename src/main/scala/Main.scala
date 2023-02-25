import scalafx.application.{JFXApp, JFXApp3}
import scalafx.Includes.*
import scalafx.scene.Scene
import scalafx.scene.control.{Button, Label}
import scalafx.scene.layout.GridPane
import scalafx.scene.paint.Paint
//import scalafx.scene.Scene
import scalafx.scene.paint.Color
import scalafx.scene.shape.Line
//import scalafx.scene.layout.Pane

object Main extends JFXApp3:

  def start(): Unit =
    val bGrid = new GridPane()
    for i <- 0 until 9
      j <- 0 until 9
    do
      val square = new Label("")
      square.setMinWidth(50)
      square.setMinHeight(50)
      square.text <== when (square.hover) choose "1" otherwise ""
      square.style ="-fx-border-color: black;" 
      bGrid.add(square, j, i)


    val scene = new Scene
    scene.root = bGrid

    stage = new JFXApp3.PrimaryStage
      stage.title = "Killer Sudoku"
      stage.scene = scene




