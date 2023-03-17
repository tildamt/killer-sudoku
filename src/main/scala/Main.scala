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
import killer.BigGrid
import scalafx.Includes._
import scalafx.scene.control.Dialog
import scalafx.scene.control.ButtonType

object Main extends JFXApp3 :

  var lastPos = Vector[Double]()
  var theGrid = new BigGrid(9, 9, Array.fill[Option[Int]](9, 9)(None))

  def start(): Unit =
    val pane = new GridPane()
    val bGrid = new GridPane()
    val squares = Vector(Vector(0.0, 0.0))

    for i <- 0 until 9
        j <- 0 until 9
    do
      val square = new Label("")
      square.setMinWidth(50)
      square.setMinHeight(50)
      square.style = "-fx-border-color: black; "
      square.font = Font.font(15)
      if (i == 0 && j == 0) then
        square.style = "-fx-border-color: black; -fx-background-color: #8fbc8f "
      bGrid.add(square, j, i)

    for (node <- bGrid.children) do
      node.onMouseClicked = (e: MouseEvent) =>
        lastPos = Vector(node.layoutXProperty().value, node.layoutYProperty().value)
        println(lastPos)

    val hbox = new HBox()
    hbox.setPadding(Insets(15, 12, 15, 12))
    hbox.setSpacing(10)

    var number = 1
    while number <= 9 do
      var num = new scalafx.scene.control.Button(s"$number")
      num.setMinHeight(40)
      num.setMinWidth(40)
      num.font = Font.font(20)
      num.style <== when(num.pressed) choose "-fx-background-color: #faebd7;" otherwise "-fx-background-color: white; "
      hbox.getChildren.addAll(num)
      number = number + 1

    for node <- hbox.getChildren do
      node.onMouseClicked = (e: MouseEvent) =>
        if lastPos.nonEmpty then
          println(s"Clicked on square at position $lastPos")
          var text = node.asInstanceOf[javafx.scene.control.Button].text.value
          for i <- bGrid.getChildren do
            if (i.layoutXProperty().value == lastPos(0)) && (i.layoutYProperty().value == lastPos(1)) then
              val buttonsText = node.asInstanceOf[javafx.scene.control.Button].text.toString.drop(node.asInstanceOf[javafx.scene.control.Button].text.toString.length - 2).dropRight(1)
              val label = i.asInstanceOf[javafx.scene.control.Label]
              label.alignment = scalafx.geometry.Pos.Center
              label.text = buttonsText
              label.style = "-fx-border-color: black; -fx-background-color: #00ff00; "
              //println(node.asInstanceOf[javafx.scene.control.Button].text.toString.drop(node.asInstanceOf[javafx.scene.control.Button].text.toString.length - 2).dropRight(1)))
              if !theGrid.everyInstanceDone(buttonsText.toInt) then
                theGrid.updateElement((lastPos(0) / 50.0).toInt, (lastPos(1) / 50.0).toInt, buttonsText.toInt)
              else
                // Create the dialog
                val dialog = new Dialog[Unit]() {
                  title = "Error"
                  //headerText = "Invalid Action"
                  contentText = "All instances of this number have already been placed."
                  dialogPane().buttonTypes = Seq(ButtonType.OK) }
                // Show the dialog
                dialog.showAndWait()
                //button.visible = true
              println("the column numbers: " + theGrid.getColNumbers((lastPos(0) / 50.0).toInt))
              println("the row numbers: " + theGrid.getRowNumbers((lastPos(1) / 50.0).toInt))
              println("every instance done: " + theGrid.everyInstanceDone(1))
          lastPos = Vector[Double]()


    val flow = new FlowPane()
     // flow.children += button

    val borderPane = new BorderPane()
    borderPane.setCenter(bGrid)
    borderPane.setRight(flow)
    borderPane.setBottom(hbox)



    val scene = new Scene(borderPane, 600, 600)

      stage = new JFXApp3.PrimaryStage
      stage.height = 600
      stage.width = 600
      stage.title = "Killer Sudoku"
      stage.scene = scene