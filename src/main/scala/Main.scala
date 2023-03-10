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
import scalafx.scene.input.MouseEvent
import scalafx.scene.control._


object Main extends JFXApp3:

  var lastPos = Vector[Double]()

  def start(): Unit =
    val pane = new GridPane()
    val bGrid = new GridPane()
    for i <- 0 until 9
      j <- 0 until 9
    do
      val square = new Label("")
      square.setMinWidth(50)
      square.setMinHeight(50)
      //square.text <== when (square.hover) choose "1\n   2" otherwise ""
      square.style <== when (square.hover) choose "-fx-border-color: black; -fx-background-color: #00ff00;" otherwise "-fx-border-color: black; -fx-background-color: #ffff00; "
      square.font = Font.font(15)
      bGrid.add(square, j, i)

    for (node <- bGrid.children) do
      node.onMouseClicked = (e: MouseEvent) =>
        lastPos = Vector(node.layoutXProperty().value, node.layoutYProperty().value)
        //node.asInstanceOf[Label].text = "New"
        println(lastPos)
        //(node.layoutXProperty().value, node.layoutYProperty().value)
    //println(lastPos)

    val hbox = new HBox()
    hbox.setPadding(Insets(15, 12, 15, 12))
    hbox.setSpacing(10)

    var number = 1
    while number <= 9 do
      var num = new scalafx.scene.control.Button(s"$number")
      num.setMinHeight(40)
      num.setMinWidth(40)
      num.font = Font.font(20)
      num.style <== when (num.pressed) choose "-fx-background-color: #faebd7;" otherwise "-fx-background-color: white; "
      hbox.getChildren.addAll(num)
      number = number + 1

    for node <- hbox.getChildren do
      node.onMouseClicked = (e: MouseEvent) =>
        if lastPos.nonEmpty then
          println(s"Clicked on square at position $lastPos")
          println(s"Text before: ${node.asInstanceOf[scalafx.scene.control.Label].text.value}")
          var text = node.asInstanceOf[scalafx.scene.control.Button].text.value
          for i <- bGrid.getChildren do
            if (i.layoutXProperty().value == lastPos(0)) && (i.layoutYProperty().value == lastPos(1)) then
              i.asInstanceOf[scalafx.scene.control.Label].text = "1"
          println(s"Text after: ${node.asInstanceOf[scalafx.scene.control.Label].text.value}")

      lastPos = Vector()


    //hbox.getChildren.head.onMouseClicked = println("Click!")





    val borderPane = new BorderPane()
    borderPane.setCenter(bGrid)
    borderPane.setRight(new FlowPane())
    borderPane.setBottom(hbox)

    val scene = new Scene(borderPane, 600, 600)


    stage = new JFXApp3.PrimaryStage
      stage.height = 600
      stage.width = 600
      stage.title = "Killer Sudoku"
      stage.scene = scene



