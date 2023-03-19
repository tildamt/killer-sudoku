import scalafx.scene.layout.BorderStrokeStyle
import scalafx.application.{JFXApp, JFXApp3}
import scalafx.Includes.*
import scalafx.geometry.{Insets, Pos}
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
import killer.SubArea

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

object Main extends JFXApp3 :

  var lastPos = Vector[Double]() // This variable is used for saving the last position of the square that has been clicked.
  var theGrid = new BigGrid(9, 9, Array.fill[Option[Int]](9, 9)(None)) // I create a new grid to use its methods.

  def start(): Unit =

    val parentPane = new Pane()
    val bGrid = new GridPane() // I add the squares to this variable called GridPane.

    // I made this map to represent the sub-grids on the bigger grid. This makes the sub-grid unnecessary, though...
    // I can already check from this the sub-grid's placed numbers.
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



    // In this part, I add the labels that represent the squares on the grid. Then I add them to the subgrids created above.
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



    // In this part, I loop over bGrid's children (essentially the squares or cells), and when the user clicks on
    // the square, its position on the grid is saved into a variable called lastPos.
    for (node <- bGrid.children) do
      node.onMouseClicked = (e: MouseEvent) =>
        lastPos = Vector(node.layoutXProperty().value, node.layoutYProperty().value)
        println(lastPos)

    // In the next part, I create a new hBox and add the candidate numbers to it.
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


    // This is progress for adding the functionality of where the candidate numbers are highlighted while the user is hovering
    // over a square... It's not working quite right just yet, but I'd like to make a commit anyway right now.
    for label <- bGrid.getChildren do
      val square = label.asInstanceOf[javafx.scene.control.Label]
      val xPos = (square.layoutXProperty().value / 50.0).toInt
      val yPos = (square.layoutYProperty().value / 50.0).toInt
      val rowNumbers = theGrid.getRowNumbers(xPos)
      val colNumbers = theGrid.getColNumbers(yPos)
      val subgridNumbers =  subgrids.find( (a, b) => b.contains(label)).get(1).filter(_ != label).map(x => x.text.value)
      label.onMouseEntered = (e: MouseEvent) =>
        for button <- hbox.getChildren do
          val candidate = button.asInstanceOf[javafx.scene.control.Button]
          val candidateNumber = candidate.text.toString.drop(candidate.text.toString.length - 2).dropRight(1).toInt
          if !rowNumbers.contains(Option(candidateNumber))
            && !colNumbers.contains(Option(candidateNumber))
            && !subgridNumbers.contains(candidateNumber) then
            candidate.style = "-fx-background-color: red; "
          else
            candidate.style = "-fx-background-color: blue; "
      label.onMouseExited = (e: MouseEvent) =>
        for button <- hbox.getChildren do
          val candidate = button.asInstanceOf[javafx.scene.control.Button]
          candidate.style = "-fx-background-color: yellow; "


    // This part of code adds numbers to the grid. In addition, it sends an error message, if the user tries to add a number
    // that already is on the same row, column, or sub-grid. First, it loops over buttons in the hBox, which are the candidate
    // numbers. Then I create variables to represent the button and its text, because it's easier. After this, I loop over the
    // squares on the grid and check if their position matches the last position variable. If it does, I set the label's text to
    // be the one on the candidate number. Then I check for errors and create a new variable called "dialog", which just sends a
    // message to the user, letting them know this number cannot be placed here.
    for node <- hbox.getChildren do
      node.onMouseClicked = (e: MouseEvent) =>
        if lastPos.nonEmpty then
          println(s"Clicked on square at position $lastPos")
          val button = node.asInstanceOf[javafx.scene.control.Button]
          val text = button.text.value
          for i <- bGrid.getChildren do
            if (i.layoutXProperty().value == lastPos(0)) && (i.layoutYProperty().value == lastPos(1)) then
              val buttonsText = node.asInstanceOf[javafx.scene.control.Button].text.toString.drop(node.asInstanceOf[javafx.scene.control.Button].text.toString.length - 2).dropRight(1)
              val label = i.asInstanceOf[javafx.scene.control.Label]
              label.alignment = scalafx.geometry.Pos.Center
              label.text = buttonsText
              val subgrid = subgrids.find( (a, b) => b.contains(label))
              val labels = subgrid.get(1).filter(_ != label).map(x => x.text.value)
              if
                !labels.contains(buttonsText)
                && !theGrid.getColNumbers((lastPos(1) / 50.0).toInt).contains(Option(buttonsText.toInt))
                && !theGrid.getRowNumbers((lastPos(0) / 50.0).toInt).contains(Some(buttonsText.toInt))
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

    val flow = new FlowPane()

    // I added a label for the possible combinations. This is still in progress, but just to have something for now!
    val label = new Label
      label.alignment = scalafx.geometry.Pos.TopLeft
      label.setMinWidth(400)
      label.setMinHeight(300)
      label.style = "-fx-border-color: black; -fx-background-color: #e6e6fa; "
      label.font = Font.font(15)
      label.text = "Possible combinations: "

    flow.children += label

    // Changed the borderPane's background color for fun as well. :-)
    val borderPane = new BorderPane {
      style = "-fx-background-color: #fff0f5 ; "
    }
    borderPane.setCenter(bGrid)
    borderPane.setRight(flow)
    borderPane.setBottom(hbox)

    val scene = new Scene(borderPane, 600, 600)

      stage = new JFXApp3.PrimaryStage
      stage.height = 600
      stage.width = 900
      stage.title = "Killer Sudoku"
      stage.scene = scene