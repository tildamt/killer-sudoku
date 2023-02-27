import scalafx.scene.layout.BorderStrokeStyle
import scalafx.application.{JFXApp, JFXApp3}
import scalafx.Includes.*
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.control.{Button, Label}
import scalafx.scene.image.ImageView
import scalafx.scene.layout.GridPane
import scalafx.scene.paint.Paint
import scalafx.scene.text.Font
import scalafx.scene.paint.Color
import scalafx.scene.shape.Line
import scalafx.scene.shape.Rectangle
import scalafx.scene.layout.Pane
import scalafx.scene.canvas.Canvas
import scalafx.scene.image.Image
import scalafx.scene.layout.BorderPane
import scalafx.scene.layout.FlowPane
import scalafx.scene.layout.HBox


object Main extends JFXApp3:

  def start(): Unit =
    val pane = new GridPane()
    val bGrid = new GridPane()
    for i <- 0 until 9
      j <- 0 until 9
    do
      val square = new Label("")
      square.setMinWidth(50)
      square.setMinHeight(50)
      square.text <== when (square.hover) choose "1\n   2" otherwise ""
      square.style <== when (square.hover) choose "-fx-border-color: black; -fx-background-color: #00ff00;" otherwise "-fx-border-color: black; -fx-background-color: #ffff00; "
      square.font = Font.font(15)
      bGrid.add(square, j, i)

    val hbox = new HBox()
    hbox.setPadding(Insets(15, 12, 15, 12))
    hbox.setSpacing(10)

    var number = 1
    while number <= 9 do
      var num = new Button(s"$number")
      num.setMinHeight(40)
      num.setMinWidth(40)
      num.font = Font.font(20)
      num.style <== when (num.pressed) choose "-fx-background-color: #faebd7;" otherwise "-fx-background-color: white; "
      hbox.getChildren.addAll(num)
      number = number + 1

    val borderPane = new BorderPane()
    borderPane.setCenter(bGrid)
    borderPane.setRight(new FlowPane())
    borderPane.setBottom(hbox)

    val scene = new Scene(borderPane, 600, 600)
    scene.root = borderPane

    //scene.root = bGrid

    stage = new JFXApp3.PrimaryStage
      stage.height = 600
      stage.width = 600
      stage.title = "Killer Sudoku"
      stage.scene = scene



