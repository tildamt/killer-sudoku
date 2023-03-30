import javafx.beans.property.SimpleStringProperty
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
import scalafx.beans.property.StringProperty
import scalafx.scene.layout.StackPane
import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.Buffer
import scala.language.postfixOps
import scalafx.scene.shape.Polygon
import scalafx.scene.paint.Color

object Main extends JFXApp3 :

  var lastPos = Vector[Double]() // This variable is used for saving the last position of the square that has been clicked.
  var theGrid = new BigGrid(9, 9, Array.fill[Option[Int]](9, 9)(None)) // I create a new grid to use its methods.

  val subAreaColors = Map(
  (0, 0) -> "lightgray",
  (0, 1) -> "white",
  (0, 2) -> "lightgray",
  (1, 0) -> "white",
  (1, 1) -> "lightgray",
  (1, 2) -> "white",
  (2, 0) -> "lightgray",
  (2, 1) -> "white",
  (2, 2) -> "lightgray"
  )

  def getSubArea(i: Int, j: Int): (Int, Int) = {
    val rowSubArea = i / 3
    val colSubArea = j / 3
    (rowSubArea, colSubArea)
  }


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

      val (subAreaRow, subAreaCol) = getSubArea(i, j)
      val subAreaColor = subAreaColors((subAreaRow, subAreaCol))
      val newStyle = s"${square.style.value} -fx-background-color: $subAreaColor;"
      square.style = newStyle


    val hbox = new HBox()
    hbox.setPadding(Insets(15, 12, 15, 12))
    hbox.setSpacing(10)

    var number = 1
    while number <= 9 do
      var num = new scalafx.scene.control.Button(s"$number")
      num.setMinHeight(40)
      num.setMinWidth(40)
      num.font = Font.font(20)
      num.style = "-fx-background-color: #ffa07a; "
      hbox.getChildren.addAll(num)
      number = number + 1

    // In this part, I loop over bGrid's children (essentially the squares or cells), and when the user clicks on
    // the square, its position on the grid is saved into a variable called lastPos.
    for (node <- bGrid.children) do
      val xPos = node.layoutXProperty().value
      val yPos = node.layoutYProperty().value
      //println("x, y : " + (xPos, yPos))
      node.onMouseClicked = (e: MouseEvent) =>
        lastPos = Vector(node.layoutXProperty().value, node.layoutYProperty().value)
        println("clicked: " + (node.layoutXProperty().value, node.layoutYProperty().value))

    //bGrid.children.foreach( a => a.addEventHandler)

//    hbox.children.foreach(a =>
  //    a.asInstanceOf[javafx.scene.control.Button].style <== when(bGrid.children.map(b => b.asInstanceOf[javafx.scene.control.Label].hoverProperty())) choose "-fx-background-color: #0000ff; " otherwise "-fx-background-color: #8b0000; ")



   // This is progress for adding the functionality of where the candidate numbers are highlighted while the user is hovering
    // over a square... It's not working quite right just yet, but I'd like to make a commit anyway right now

    for square <- bGrid.children do
      square.onMouseEntered = (e: MouseEvent) =>
        val xLabel = (square.layoutXProperty().value / 50.0).toInt
        val yLabel = (square.layoutYProperty().value / 50.0).toInt
        //println("x, y: " + (xLabel, yLabel))
        val rowNumbers = theGrid.getRowNumbers(xLabel)
        val colNumbers = theGrid.getColNumbers(yLabel)
        val subgridNumbers = subgrids.find((_, b) => b.contains(square)).get._2.filter(_.text.value.nonEmpty).map(_.text.value.toInt).toSet
        def canAdd(c: Int): Boolean =
          !rowNumbers.contains(Option(c)) &&
            !colNumbers.contains(Option(c)) &&
            !subgridNumbers.contains(c)
        hbox.getChildren
          .toVector
          .map(a => a.asInstanceOf[javafx.scene.control.Button])
          .filter(b => canAdd(b.getText.replaceAll("[^0-9]", "").toInt))
          .foreach( _.style = "-fx-background-color: #add8e6; ")
        square.onMouseExited = (e: MouseEvent) =>
           hbox.getChildren
          .toVector
          .map(a => a.asInstanceOf[javafx.scene.control.Button])
          .foreach( _.style = "-fx-background-color: #ffa07a; ")

          println("subgrid " + subgridNumbers.mkString("Array(", ", ", ")"))
          println("column " + rowNumbers.mkString("Array(", ", ", ")"))
          println("row " + colNumbers.mkString("Array(", ", ", ")"))
          println("x, y: " + (xLabel, yLabel))

    // This part of code adds numbers to the grid. In addition, it sends an error message, if the user tries to add a number
    // that already is on the same row, column, or sub-grid. First, it loops over buttons in the hBox, which are the candidate
    // numbers. Then I create variables to represent the button and its text, because it's easier. After this, I loop over the
    // squares on the grid and check if their position matches the last position variable. If it does, I set the label's text to
    // be the one on the candidate number. Then I check for errors and create a new variable called "dialog", which just sends a
    // message to the user, letting them know this number cannot be placed here.
    for node <- hbox.getChildren do
      node.onMouseClicked = (e: MouseEvent) =>
        if lastPos.nonEmpty then
          //println(s"Clicked on square at position $lastPos")
          val button = node.asInstanceOf[javafx.scene.control.Button]
          val text = button.text.value
          for i <- bGrid.getChildren do
           // println("i " + i.layoutXProperty().value)
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
                theGrid.updateElement((lastPos(0) / 50.0).toInt, (lastPos(1) / 50.0).toInt, buttonsText.toInt)
              else
                // Create the dialog
                val dialog = new Dialog[Unit]() {
                  title = "Error"
                  contentText = "This number cannot be placed here."
                  dialogPane().buttonTypes = Seq(ButtonType.OK) }
                // Show the dialog
                dialog.showAndWait()
          lastPos = Vector[Double]()


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