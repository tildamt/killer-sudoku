import javafx.beans.property.SimpleStringProperty
import scalafx.scene.layout.{BorderPane, BorderStrokeStyle, FlowPane, GridPane, HBox, Pane, Region, StackPane}
import scalafx.application.{JFXApp, JFXApp3}
import scalafx.Includes.*
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.control.{Button, Label}
import scalafx.scene.image.ImageView
import scalafx.scene.paint.Paint
import scalafx.scene.text.Font
import scalafx.scene.paint.Color
import scalafx.scene.shape.Line
import scalafx.scene.shape.Rectangle
import scalafx.scene.canvas.Canvas
import scalafx.scene.image.Image
import scalafx.scene.input.MouseEvent
import scalafx.scene.control.*
import killer.BigGrid
import scalafx.Includes.*
import scalafx.event.ActionEvent
import scalafx.scene.control.Dialog
import scalafx.scene.control.ButtonType
import killer.SubArea
import scalafx.beans.property.StringProperty
import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.Buffer
import scala.language.postfixOps
import scalafx.scene.shape.Polyline
import scalafx.scene.shape.*



object Main extends JFXApp3 :

  var lastPos = Vector[Double]() // This variable is used for saving the last position of the square that has been clicked.
  var theGrid = new BigGrid(9, 9, Array.fill[Option[Int]](9, 9)(None)) // I create a new grid to use its methods





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

    val cages = List(
      new SubArea(List((0,0), (1,0)), 3),
      new SubArea(List((2,0), (3,0), (4, 0)), 15),
      new SubArea(List((5,0),(5,1),(4,1),(4,2)), 22),
      new SubArea(List((6,0),(6,1)), 4),
      new SubArea(List((7,0),(7,1)), 16),
      new SubArea(List((8,0),(8,1),(8,2),(8,3)), 15),
      new SubArea(List((0,1),(1,1),(0,2),(1,2)),25),
      new SubArea(List((2,2),(3,2)), 17),
      new SubArea(List((2,2),(3,2),(3,3)), 9),
      new SubArea(List((5,2),(5,3),(5,4)), 8),
      new SubArea(List((6,2),(7,2),(6,3)), 20),
      new SubArea(List((0,3),(0,4)), 6),
      new SubArea(List((1,3),(2,3)), 14),
      new SubArea(List((4,3),(4,4),(4,5)), 17),

      new SubArea(List((1,4),(2,4),(1,5)),13),
      new SubArea(List((3,4),(3,5),(3,6)),20),
      new SubArea(List((8,4),(8,5)), 12),

      new SubArea(List((0,5),(0,6),(0,7),(0,8)), 27),
      new SubArea(List((2,5),(2,6),(1,6)), 6),

      new SubArea(List((5,5),(5,6),(6,6)), 20),
      new SubArea(List((6,5),(7,5)), 6),

      new SubArea(List((4,6),(4,7),(3,7),(3,8)),10),
      new SubArea(List((7,6),(7,7),(8,6),(8,7)),14),

      new SubArea(List((1,7),(1,8)),8),
      new SubArea(List((2,7),(2,8)),16),

      new SubArea(List((5,7),(6,7)),15),
      new SubArea(List((4,8),(5,8),(6,8)),13),
      new SubArea(List((7,8),(8,8)),17),
      new SubArea(List((7,3),(7,4),(6,4)),17),
      new SubArea(List((2,1),(3,1)),17))

    // In this part, I add the labels that represent the squares on the grid. Then I add them to the subgrids created above.
    for
      i <- 0 until 9
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

      square.style = "-fx-border-color: black; -fx-background-color: #ffa07a; "

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
      node.onMouseClicked = (e: MouseEvent) =>
        lastPos = Vector(node.layoutXProperty().value, node.layoutYProperty().value)
        println("clicked: " + (node.layoutXProperty().value, node.layoutYProperty().value))


   // This is progress for adding the functionality of where the candidate numbers are highlighted while the user is hovering
    // over a square... It's not working quite right just yet, but I'd like to make a commit anyway right now

    val label = new Label
      label.alignment = scalafx.geometry.Pos.TopLeft
      label.setMinWidth(400)
      label.setMinHeight(300)
      label.style = "-fx-border-color: black; -fx-background-color: #e6e6fa; "
      label.font = Font.font(15)
      label.textFill = javafx.scene.paint.Color.BLACK
      label.text = ""

    val flow = new FlowPane()
    flow.children += label


    for square <- bGrid.children do
      square.onMouseEntered = (e: MouseEvent) =>

        val xLabel = (square.layoutXProperty().value / 50.0).toInt
        val yLabel = (square.layoutYProperty().value / 50.0).toInt
        val rowNumbers = theGrid.getRowNumbers(xLabel)
        val colNumbers = theGrid.getColNumbers(yLabel)
        val subgridNumbers = subgrids.find( (_, b) => b.contains(square)).get._2.filter(_.text.value.nonEmpty).map(_.text.value.toInt).toSet
        def canAdd(c: Int): Boolean =
          !rowNumbers.contains(Option(c)) &&
            !colNumbers.contains(Option(c)) &&
            !subgridNumbers.contains(c)
        hbox.getChildren
          .toVector
          .map(a => a.asInstanceOf[javafx.scene.control.Button])
          .filter(b => canAdd(b.getText.replaceAll("[^0-9]", "").toInt))
          .foreach( _.style = "-fx-background-color: #add8e6; ")

        val cage = cages.find(area => area.squares.contains((xLabel, yLabel)))

        // with the variable labels i get the labels numbers
        // first i map bgrid's children to be instances of labels
        // then i map the labels to their positions
        // then i filter labels which don't have any text attached
        // then i map the labels to their values
        val labelsnumbers =
          bGrid.children
            .map(node => node.asInstanceOf[javafx.scene.control.Label])
            .map(a => a -> ((a.layoutXProperty().value / 50.0).toInt, (a.layoutYProperty().value / 50.0).toInt))
            .filter( (lappu, sijainti: (Int, Int)) => cage.get.squares.contains(sijainti))
            .filter( (lappu: javafx.scene.control.Label, sijainti) => lappu.text.value != "")
            .map( (lappu: javafx.scene.control.Label, sijainti) => lappu.text.value.toInt)

        // all the labels in a cage
        val labelsInCage: scalafx.collections.ObservableBuffer[(javafx.scene.control.Label)] =
          bGrid.children
            .map(node => node.asInstanceOf[javafx.scene.control.Label])
            .map(a => a -> ((a.layoutXProperty().value / 50.0).toInt, (a.layoutYProperty().value / 50.0).toInt))
            .filter( (lappu, sijainti: (Int, Int)) => cage.get.squares.contains(sijainti))
            .map( (lappu, sijainti) => lappu)


        // this variable gets us all the numbers in the subgrids the cage belongs to
        val subs: Map[(Int, Int), scala.collection.mutable.ArrayBuffer[Int]] =
          subgrids
            .filter( (numero, laput) => laput.toSeq.intersect(labelsInCage.toSeq).nonEmpty)
            .map( (numero, laput) => (numero, laput.filter(_.text.value != "")))
            .map( (numero, laput) => (numero, laput.map(_.text.value.toInt)))



        def possible(n: Int): Boolean =
          // yhdistelmä on mahdollinen, jos numero esiintyy maksimissaan subgridien määrä - 1 kertaa subgrideissä
          subs.values.flatten.toVector.count( _ == n) <= subs.keys.size - 1

        // i get the possible combinations here, and then filter already placed numbers from those

        val combinations =
          cage.get.possibleCombinations
            .map( (a: IndexedSeq[Int]) => a.filter( !labelsnumbers.contains(_)))
            .map( a => a.filter(canAdd(_)))

        // Changing the labels text
        label.text = "Possible combinations: " + "\n" + combinations.map(_.mkString(", ")).mkString("\n")


        square.onMouseExited = (e: MouseEvent) =>
           hbox.getChildren
          .toVector
          .map(a => a.asInstanceOf[javafx.scene.control.Button])
          .foreach( _.style = "-fx-background-color: #ffa07a; ")


    // This part of code adds numbers to the grid. In addition, it sends an error message, if the user tries to add a number
    // that already is on the same row, column, or sub-grid. First, it loops over buttons in the hBox, which are the candidate
    // numbers. Then I create variables to represent the button and its text, because it's easier. After this, I loop over the
    // squares on the grid and check if their position matches the last position variable. If it does, I set the label's text to
    // be the one on the candidate number. Then I check for errors and create a new variable called "dialog", which just sends a
    // message to the user, letting them know this number cannot be placed here.
    for node <- hbox.getChildren do
      node.onMouseClicked = (e: MouseEvent) =>
        if lastPos.nonEmpty then
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
                theGrid.updateElement((lastPos(0) / 50.0).toInt, (lastPos(1) / 50.0).toInt, Some(buttonsText.toInt))
              else
                // Create the dialog
                val dialog = new Dialog[Unit]() {
                  title = "Error"
                  contentText = "This number cannot be placed here."
                  dialogPane().buttonTypes = Seq(ButtonType.OK) }
                // Show the dialog
                dialog.showAndWait()
          lastPos = Vector[Double]()

    // when the user is hovering over buttons, the squares with the same number are highlighted
    for nappi <- hbox.children do
      nappi.onMouseEntered = (e: MouseEvent) =>
        val butt = nappi.asInstanceOf[javafx.scene.control.Button]
        val number = butt.text.value
        bGrid.children
          .map( b => b.asInstanceOf[javafx.scene.control.Label])
          .filter( _.text.value == number)
          .foreach( _.style = "-fx-border-color: black; -fx-background-color: #add8e6; ")
      nappi.onMouseExited = (e: MouseEvent) =>
        bGrid.children
        .map( b => b.asInstanceOf[javafx.scene.control.Label])
          .foreach(_.style = "-fx-border-color: black; -fx-background-color: #ffa07a; ")



    // I added a label for the possible combinations. This is still in progress, but just to have something for now!
    /*val label = new Label
      label.alignment = scalafx.geometry.Pos.TopLeft
      label.setMinWidth(400)
      label.setMinHeight(300)
      label.style = "-fx-border-color: black; -fx-background-color: #e6e6fa; "
      label.font = Font.font(15)
      label.text = "Possible combinations: "*/

    // This is a button so the user can delete a number
    val deleteButton = new Button("Delete number")

    // When the user clicks on the deleteButton, the program checks the last square that was clicked
    // (if there are any) and deletes that number. I'll probably update it to be prettier later but for
    // now what works, works...
    deleteButton.onMouseClicked = (e: MouseEvent) =>
      if lastPos.nonEmpty then
        bGrid.children
          .map(b => b.asInstanceOf[javafx.scene.control.Label])
          .filter( _.layoutXProperty().value == lastPos(0))
          .filter( _.layoutYProperty().value == lastPos(1))
          .foreach( _.text = "")
        theGrid.updateElement((lastPos(0) / 50.0).toInt, (lastPos(1) / 50.0).toInt, None)
        lastPos = Vector[Double]()


    flow.children += deleteButton

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