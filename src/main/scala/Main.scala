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
import scalafx.scene.control.*
import killer.BigGrid
import scalafx.Includes.*
import scalafx.event.ActionEvent
import scalafx.scene.control.Dialog
import scalafx.scene.control.ButtonType

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

object Main extends JFXApp3 :

  var lastPos = Vector[Double]()
  var theGrid = new BigGrid(9, 9, Array.fill[Option[Int]](9, 9)(None))
/*
  def getRowNumbers(row: Int): (Seq[Int], Boolean) = {
    val rowValues = for (col <- 0 until 9) yield {
      theGrid.grid(row)(col)
    }
    val distinctValues = rowValues.distinct
    (rowValues.filter(_.nonEmpty).map(x => x.get).toSeq, distinctValues.size == rowValues.size)
  }*/

  def start(): Unit =

    //val pane = new GridPane()
    val bGrid = new GridPane()
    val subgrids = Map(
      (0,0) -> new mutable.ArrayBuffer[Label](),
      (0,3) -> new mutable.ArrayBuffer[Label](),
      (0,6) -> new mutable.ArrayBuffer[Label](),
      (3,0) -> new mutable.ArrayBuffer[Label](),
      (3,3) -> new mutable.ArrayBuffer[Label](),
      (3,6) -> new mutable.ArrayBuffer[Label](),
      (6,0) -> new mutable.ArrayBuffer[Label](),
      (6,3) -> new mutable.ArrayBuffer[Label](),
      (6,6) -> new mutable.ArrayBuffer[Label]()
    )


    for i <- 0 until 9
        j <- 0 until 9
    do
      val square = new Label("")
      square.setMinWidth(50)
      square.setMinHeight(50)
      square.style = "-fx-border-color: black; "
      square.font = Font.font(15)
      bGrid.add(square, j, i)

      val subgridRow = i / 3 * 3
      val subgridCol = j / 3 * 3
      subgrids((subgridRow, subgridCol)) += square

    subgrids.foreach { case ((row, col), labels) =>
      println(s"Sub-grid at ($row, $col): ${labels.map(_.getText).mkString(", ")}")
    }

    subgrids.foreach { case ((row, col), labels) =>
      println(s"Sub-grid at ($row, $col): ${labels.map(_.getText)}")
    }



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
      hbox.getChildren.addAll(num)
      number = number + 1

    for node <- hbox.getChildren do
      node.onMouseClicked = (e: MouseEvent) =>
        if lastPos.nonEmpty then
          println(s"Clicked on square at position $lastPos")
          val button = node.asInstanceOf[javafx.scene.control.Button]
          val text = node.asInstanceOf[javafx.scene.control.Button].text.value
          for i <- bGrid.getChildren do
            if (i.layoutXProperty().value == lastPos(0)) && (i.layoutYProperty().value == lastPos(1)) then
              val buttonsText = node.asInstanceOf[javafx.scene.control.Button].text.toString.drop(node.asInstanceOf[javafx.scene.control.Button].text.toString.length - 2).dropRight(1)
              val label = i.asInstanceOf[javafx.scene.control.Label]
              label.alignment = scalafx.geometry.Pos.Center
              label.text = buttonsText
              val subgrid = subgrids.find( (a, b) => b.contains(label))
              val perse = subgrid.get(1).filter(_ != label).map(x => x.text.value)
              //val pylly = theGrid.getRowNumbers((lastPos(0) / 50.0).toInt).toSet
              if
                !perse.contains(buttonsText)
                && !theGrid.getColNumbers((lastPos(1) / 50.0).toInt).contains(Option(buttonsText.toInt))
                && !theGrid.getRowNumbers((lastPos(0) / 50.0).toInt).contains(Some(buttonsText.toInt)) //!theGrid.getRowNumbers((lastPos(0) / 50.0).toInt).contains((buttonsText.toInt))
              then
                println(theGrid.getRowNumbers((lastPos(0) / 50.0).toInt).mkString("Array(", ", ", ")"))
                theGrid.updateElement((lastPos(0) / 50.0).toInt, (lastPos(1) / 50.0).toInt, buttonsText.toInt)
                println("Placed number " + theGrid.grid((lastPos(0) / 50.0).toInt)((lastPos(1) / 50.0).toInt))
                println("numbers in column now: " + theGrid.getColNumbers((lastPos(1) / 50.0).toInt).mkString("Array(", ", ", ")"))
                println("numbers in row now: " + theGrid.getRowNumbers((lastPos(0) / 50.0).toInt).mkString("Array(", ", ", ")"))
              else
                // Create the dialog
                val dialog = new Dialog[Unit]() {
                  title = "Error"
                  contentText = "This number cannot be placed here."
                  dialogPane().buttonTypes = Seq(ButtonType.OK) }
                // Show the dialog
                dialog.showAndWait()
          lastPos = Vector[Double]()
          subgrids.foreach { case ((row, col), labels) =>
      println(s"Sub-grid at ($row, $col): ${labels.map(_.getText)}")
    }

/*!theGrid.everyInstanceDone(buttonsText.toInt) */
    val flow = new FlowPane()

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