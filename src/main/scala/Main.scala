import javafx.beans.property.SimpleStringProperty
import scalafx.scene.layout.{Border, BorderPane, BorderStroke, BorderStrokeStyle, CornerRadii, FlowPane, GridPane, HBox, Pane, Region, StackPane}
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
import scalafx.beans.binding.Bindings
import scalafx.beans.property.StringProperty
import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle
import scalafx.scene.Group
import scalafx.scene.layout.{BorderStrokeStyle, BorderWidths}
import scalafx.scene.paint.Color

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.Buffer
import scala.language.postfixOps
import scalafx.scene.shape.Polyline
import scalafx.scene.shape.*
import scalafx.scene.layout.Region



object Main extends JFXApp3 :

  var lastPos = Vector[Double]() // This variable is used for saving the last position of the square that has been clicked.
  var theGrid = new BigGrid(9, 9, Array.fill[Option[Int]](9, 9)(None)) // I create a new grid to use its methods

  def start(): Unit =

    val bGrid = new GridPane() // I add the squares to this variable called GridPane.
    //bGrid.setStyle("-fx-border-width: 3px; ")

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
      new SubArea(List((2,1),(3,1)), 17),
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

    // i create a new list that all of the labels will be added to, and they will now be in their correct cages
    val labelsPlaces = cages.map( area => area.squares).map( lista => lista.toBuffer)
      .map( lista => lista -> new mutable.ArrayBuffer[Label]()).toMap
    // in the variable sumsPlaces, I map each cage's first element to the cage's sum to reach. i use this later
    // to add the sum to the label that is at that first position
    val sumsPlaces = cages.map( subarea => subarea.squares.head -> subarea.summa)

    // A helper method to check if two cells are adjacent. There are two cases when the cells may be adjacent to each
    // other: either their x-coordinates' difference is 0 and y-coordinates' 1, or x-coordinates' difference 1 and
    // y-coordinates' difference is 0. In the first case, the cells are next to each other horizontally, and in the
    // second case vertically.
    def isAdjacent(cell1: (Int, Int), cell2: (Int, Int)): Boolean =
      val distancex = scala.math.abs(cell1._1 - cell2._1)
      val distancey = scala.math.abs(cell1._2 - cell2._2)
      (distancex, distancey) match
        case (1, 0) => true
        case (0, 1) => true
        case _ => false

    // cages will be mapped to their adjacent cages here. the first cage is just the cage, and the set contains its adjacent cages (or will contain).
    val adjacentCages = cages.map( cage => cage -> Buffer[SubArea]()).toMap//mutable.Map[SubArea, mutable.Set[SubArea]]()

    // adjacent cages are mapped for each cage in this for loop. it iterates over every cage in the cages map
    // through its indices, and has the other for-loop go after every cage after the current cage. the logic behind it
    // is that the two cages are adjacent, if any of the cells in the cages are adjacent to each other. that's why it uses
    // the if-condition; it checks if any of the cages after the cage at index i has adjacent cells to the cage at index i.
    // if that's true, it creates two variables containing the cages at indices i and j. it adds the cage at index i to be
    // adjacent to the cage at index j, and vice versa.

    for
      i <- cages.indices
      j <- i + 1 until cages.length
    do
      if cages(i).squares.exists(c1 => cages(j).squares.exists(c2 => isAdjacent(c1, c2))) then
        val cage1 = cages(i)
        val cage2 = cages(j)
        adjacentCages(cage1).append(cage2)
        adjacentCages(cage2).append(cage1)

    // In this part, I add the labels that represent the squares on the grid. Then I add them to the subgrids created above.
    // After this, I add them to their cages defined above.
    for
      i <- 0 until 9
      j <- 0 until 9
    do
      val square1 =  new Label("")
        square1.setFont(Font.font(15))
        square1.setMinWidth(50)
        square1.setMinHeight(50)

      bGrid.add(square1, j, i)

      // here i add the label to the subgrid
      val subgridRow = i / 3 * 3
      val subgridCol = j / 3 * 3
      subgrids((subgridRow, subgridCol)) += square1

      // here i add the sum as an icon for each cage's first cell
      sumsPlaces.find( kakka => kakka._1 == (j, i)) match
        case Some(value) =>
          val nicetext = new scalafx.scene.text.Text(value._2.toString)
            nicetext.font = Font.font("Comic Sans MS")
          square1.alignment = Pos.TopLeft
          square1.graphic = nicetext
        case None =>

      // here i add each cell to its cage
      val sijainti = (j, i)
      val location = labelsPlaces.keys.find(_.contains(sijainti)).get
      labelsPlaces(location) += square1


    // here i create hbox to hold the candidate numbers
    val hbox = new HBox()
    hbox.setPadding(Insets(15, 12, 15, 12))
    hbox.setSpacing(10)

    var number = 1
    while number <= 9 do
      var num = new scalafx.scene.control.Button(s"$number")
      num.setMinHeight(40)
      num.setMinWidth(40)
      num.font = Font.font(20)
      num.style = "-fx-background-color: #f08080; "
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

    // colors is a variable which contains each cage mapped to its style
    var colors = cages.map(area => (area.squares, Buffer[String]()))
    // adjacentslist contains all the adjacent cages for each cage
    var adjacentslist = adjacentCages.map( subarea => (subarea._1.squares, subarea._2.map(area => area.squares)))

    // These are the styles ( I will move them later on). This works if at most one cage has ten adjacent cages, so it
    // isn't usable for every killer sudoku and thus must be modified.
    val styles = Vector(
        "-fx-border-color: black; -fx-background-color: #f9a875;",
        "-fx-border-color: black; -fx-background-color: #a3e7d8;",
        "-fx-border-color: black; -fx-background-color: #fdd369;",
        "-fx-border-color: black; -fx-background-color: #d46a6a;",
        "-fx-border-color: black; -fx-background-color: #b983e9;",
        "-fx-border-color: black; -fx-background-color: #8bc995;",
        "-fx-border-color: black; -fx-background-color: #f49cc3;",
        "-fx-border-color: black; -fx-background-color: #77c9d4;",
        "-fx-border-color: black; -fx-background-color: #f1b8b8;",
        "-fx-border-color: black; -fx-background-color: #a5c5e9;"
    )

    // Here i choose colors based on the adjacent cages colors. I loop over the colors buffer, which contains each cage
    // mapped to its color (buffer). At first, each buffer is empty. The basic idea is that I find the current cage's neighboring
    // cages from the adjacentsList, which has each cage mapped to its neighbors. Then I loop over the colors buffer and find out
    // which colors have been assigned to each adjacent cage, and append those colors to the buffer that holds all the colors that
    // have been used in the adjacent cages. Then I filter those used colors from all possible colors (currently in the vector "styles")
    // and always take the first one out of those possible colors. This way, I think the least number of colors will be used. Then,
    // I append that color to its cage in the colors buffer above. So, basically, for each cage, I assign a color that isn't used for its
    // adjacent cage.
    var index = 0
    while index < colors.size do
      val current = colors(index)._1
      val currentNeighbors = adjacentslist.filter(adjacents => adjacents._1 == current).values
      val currentColorsUsed = Buffer[String]()
      for neighbor <- currentNeighbors do
        for one <- neighbor do
          val usedcolor = colors.filter(cage => cage._1 == one).map( cage => cage._2)
          usedcolor.foreach( used => used.foreach(currentColorsUsed.append(_)))
      val possibles = styles.filter(!currentColorsUsed.contains(_))
      colors(index)._2.append(possibles.head)
      index += 1

    // In this part, I set each label's (=cell's) style to be the one I assigned to it earlier with the algorithm.
    // It loops over the labels places that holds the labels in their correct cages. Then, from the variable colors I find
    // the color assigned to the cage based on the cage's cells positions. Then, I change each cell's style to match that color.
    labelsPlaces.foreach( cageWithLabels =>
      cageWithLabels._2.foreach( cells =>
        colors.find( cageWithColors => cageWithColors._1 == cageWithLabels._1.toList).get._2.foreach(cells.style = _)))

    // After I've set the styles, I store the styles into the userData, so when the user is hovering over the candidate buttons
    // and the cell's color must be highlighted accordingly, I can get the previous style from userData.
    labelsPlaces.foreach( cageWithLabels => cageWithLabels._2
      .foreach( cell => cell.setUserData((cell.getStyle))))

    // this label is created for showing the possible combinations. it is added to the flowpane, which is later added to the borderPane.
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


    // In this part, I add the functionality that the numbers that can be placed to the cell that the user is hovering over, are highlighted.
    // In addition, I add the functionality that the possible combinations can be shown. The first part works in a way that I first get
    // the current label's position, so I can find out what number is placed in it. Then, with the helper methods of the grid getColNumber
    // and getRowNumber, I check which numbers have already been placed in the same row or column. In addition, I check the numbers in the
    // same subgrid (I grouped the labels earlier). I then create a helper method "canAdd" which says the number can be added if it is not
    // found in the same row, column or subgrid. I then filter the candidate numbers based on that method. Currently, the possible combinations
    // is still under work, but for now, it filters those numbers that cannot be added based on the numbers on the same row, column, subgrid and cage.
    for square <- bGrid.children do

      square.onMouseEntered = (e: MouseEvent) =>

        val xLabel = (square.layoutXProperty().value / 50.0).toInt
        val yLabel = (square.layoutYProperty().value / 50.0).toInt
        val rowNumbers = theGrid.getRowNumbers(xLabel)
        val colNumbers = theGrid.getColNumbers(yLabel)
        val subgridNumbers = subgrids.find( (_, b) => b.contains(square)).get._2.filter(_.text.value.nonEmpty).map(_.text.value.last.toInt).toSet
        def canAdd(c: Int): Boolean =
          !rowNumbers.contains(Option(c)) &&
            !colNumbers.contains(Option(c)) &&
            !subgridNumbers.contains(c)
        hbox.getChildren
          .toVector
          .map(a => a.asInstanceOf[javafx.scene.control.Button])
          .filter(b => canAdd(b.text.value.toInt))
          .foreach( _.style = "-fx-background-color: #8fbc8f; ")

        val cage = cages.find(area => area.squares.contains((xLabel, yLabel)))

        // with the variable labels i get the labels numbers
        // first i map bgrid's children to be instances of labels
        // then i map the labels to their positions
        // then i filter labels which don't have any text attached
        // then i map the labels to their values
        val labelsnumbers: Vector[Int] =
          bGrid.children
            .map(node => node.asInstanceOf[javafx.scene.control.Label])
            .map(a => a -> ((a.layoutXProperty().value / 50.0).toInt, (a.layoutYProperty().value / 50.0).toInt))
            .filter( (lappu, sijainti: (Int, Int)) => cage.get.squares.contains(sijainti))
            .filter( (lappu: javafx.scene.control.Label, sijainti) => lappu.text.value != "")
            .map( (lappu: javafx.scene.control.Label, sijainti) => lappu.text.value.last.toInt).toVector


        // all the labels in a cage
        val labelsInCage: scalafx.collections.ObservableBuffer[(javafx.scene.control.Label)] =
          bGrid.children
            .map(node => node.asInstanceOf[javafx.scene.control.Label])
            .map(a => a -> ((a.layoutXProperty().value / 50.0).toInt, (a.layoutYProperty().value / 50.0).toInt))
            .filter( (lappu, sijainti: (Int, Int)) => cage.get.squares.contains(sijainti))
            .map( (lappu, sijainti) => lappu)


        //println("pylly " + cage.map( area => area.squares).get.map( first => theGrid.getRowNumbers(first._1)))
        for i <- cage do
          for cell <- i.squares do
            val rows = theGrid.getRowNumbers(cell._1)
            val cols = theGrid.getColNumbers(cell._2)



        // this variable gets us all the numbers in the subgrids the cage belongs to
        val subs: Map[(Int, Int), scala.collection.mutable.ArrayBuffer[Int]] =
          subgrids
            .filter( (numero, laput) => laput.toSeq.intersect(labelsInCage.toSeq).nonEmpty)
            .map( (numero, laput) => (numero, laput.filter(_.text.value != "")))
            .map( (numero, laput) => (numero, laput.map(_.text.value.last.asDigit)))


        for i <- cage do
          for cell <- i.squares do
            val rows = theGrid.getRowNumbers(cell._1)
            val cols = theGrid.getColNumbers(cell._2)
            //val subsnumbers =
        //println("subien arvot: " + subs.values.flatten)
        def possible(n: Int): Boolean =
          // yhdistelmä on mahdollinen, jos numero esiintyy maksimissaan subgridien määrä - 1 kertaa subgrideissä

          subs.values.flatten.toVector.count( _ == n) <= subs.keys.size - 1

        //println("mahis: " + cage.get.possibleCombinations.filter(sek => sek.forall(a => possible(a))))

        val subgridsnumbers = subs.values.flatten.toVector
        val possiblenumbers = (1 to 9).filter( n => !labelsnumbers.contains(n)).filter(n => !subgridsnumbers.contains(n))
        //println("possiblee " + possiblenumbers)
        //println("perse: " +  cage.get.possibleCombinations.filter { combination =>
          //combination.forall(n => possiblenumbers.contains(n))
        //})

        // i get the possible combinations here, and then filter already placed numbers from those

        val cell = square.asInstanceOf[javafx.scene.control.Label]
        val cellsText: Option[Int] =
            cell.text.value.toIntOption

        val combinations =
          cage.get.possibleCombinations

            .map( (a: IndexedSeq[Int]) => a.filter( !labelsnumbers.contains(_)))
            .map( a => a.filter(canAdd(_)))

        // Changing the labels text
        label.font = Font.font("Comic Sans MS")
        label.text = "Possible combinations: " + "\n" + combinations.map(_.mkString(", ")).mkString("\n")

        square.onMouseExited = (e: MouseEvent) =>
           hbox.getChildren
          .toVector
          .map(a => a.asInstanceOf[javafx.scene.control.Button])
          .foreach( _.style = "-fx-background-color: #f08080; ")

    // This part of code adds numbers to the grid. In addition, it sends an error message, if the user tries to add a number
    // that already is on the same row, column, or sub-grid. First, it loops over buttons in the hBox, which are the candidate
    // numbers. Then I create variables to represent the button and its text, because it's easier. After this, I loop over the
    // squares on the grid and check if their position matches the last position variable. If it does, I set the label's text to
    // be the one on the candidate number. Then I check for errors and create a new variable called "dialog", which just sends a
    // message to the user, letting them know this number cannot be placed here. In addition, if every instance is done, I now
    // set the button whose number matches the last placed number disabled value to be true.
    for node <- hbox.getChildren do
      node.onMouseClicked = (e: MouseEvent) =>
        if lastPos.nonEmpty then
          val button = node.asInstanceOf[javafx.scene.control.Button]
          val text = button.text.value
          for i <- bGrid.getChildren do
            if (i.layoutXProperty().value == lastPos(0)) && (i.layoutYProperty().value == lastPos(1)) then
              val buttonsText = node.asInstanceOf[javafx.scene.control.Button].text.value
              val label = i.asInstanceOf[javafx.scene.control.Label]
              val labelsCurrentText = label.text.value
              val subgrid = subgrids.find( (a, b) => b.contains(label))
              val labels = subgrid.get(1).filter(_ != label).filter( x => x.text.value.nonEmpty).map(x => x.text.value.last)
              if
                !labels.contains(buttonsText)
                && !theGrid.getColNumbers((lastPos(1) / 50.0).toInt).contains(Option(buttonsText.toInt))
                && !theGrid.getRowNumbers((lastPos(0) / 50.0).toInt).contains(Some(buttonsText.toInt))
              then
                theGrid.updateElement((lastPos(0) / 50.0).toInt, (lastPos(1) / 50.0).toInt, Some(buttonsText.toInt)) // updating the element to the grid

                label.alignment = Pos.TopLeft
                label.font = Font.font("Comic Sans MS")
                label.text =
                      "" + "\n " + buttonsText
                if theGrid.everyInstanceDone(buttonsText.toInt) then {
                  button.disable = true }        // checking if every instance is now done
              else
                val dialog = new Dialog[Unit]() {
                  title = "Error"
                  contentText = "This number cannot be placed here."
                  dialogPane().buttonTypes = Seq(ButtonType.OK) }  // here i create the error message, which is a dialog
                dialog.showAndWait()
          lastPos = Vector[Double]()


    // When the user is hovering over buttons, the squares with the same number are highlighted. The way this function works is
    // that it first checks the button's text, and saves it to a variable. After this, it filters out the grid's cells whose
    // numbers match that number and changes those cells' colors to be lighter than the other colors. When the mouse exits, the
    // previous style is from the labels' userData which I set earlier in the code.
    for nappi <- hbox.children do
      nappi.onMouseEntered = (e: MouseEvent) =>
        val butt = nappi.asInstanceOf[javafx.scene.control.Button]
        val number = butt.text.value
        bGrid.children
          .map( b => b.asInstanceOf[javafx.scene.control.Label])
          .filter( b => b.text.value.nonEmpty)
          .filter( b => b.text.value.last.toString == number)
          .foreach( (b: javafx.scene.control.Label) =>
            b.style = "-fx-border-color: black; -fx-background-color: #f0f8ff; ")
      nappi.onMouseExited = (e: MouseEvent) =>
        bGrid.children
        .map( b => b.asInstanceOf[javafx.scene.control.Label])
          .foreach( (lappu: javafx.scene.control.Label) =>
          lappu.setStyle(lappu.getUserData.asInstanceOf[String]))

    // This is a button so the user can delete a number. Later on, I add the delete-button to the flowPane next to the sudoku grid.
    val deleteButton = new Button("Delete number")
    deleteButton.font = Font.font("Comic Sans MS")

    // When the user clicks on the deleteButton, the program checks the last square that was clicked
    // (if there are any) and deletes that number. I'll probably update it to be prettier later but for
    // now what works, works... In addition, the user gets an error message if they haven't clicked on any cell to delete the number from.
    // For now, it has a bug in which if the user deletes a number for which every instance was previously done. The way this method works
    // is that it first gets the number that was in the cell (after confirming last position clicked is not empty),
    // after which it finds the button whose number is the same. Then it updates the
    // cell's number to be empty. If the last position is empty, the user gets an error message.
    deleteButton.onMouseClicked = (e: MouseEvent) =>
      if lastPos.nonEmpty then
        val numberHere: Int = bGrid.children
          .map(b => b.asInstanceOf[javafx.scene.control.Label])
          .filter( _.layoutXProperty().value == lastPos(0))
          .filter( _.layoutYProperty().value == lastPos(1))
          .head
          .text
          .value
          .last
          .asDigit

        val nappi = hbox.children
          .map(b => b.asInstanceOf[javafx.scene.control.Button])
          .filter( _.text.value.head.asDigit == numberHere)
          .head

        bGrid.children
          .map(b => b.asInstanceOf[javafx.scene.control.Label])
          .filter( _.layoutXProperty().value == lastPos(0))
          .filter( _.layoutYProperty().value == lastPos(1))
          .foreach( _.text = "")
        theGrid.updateElement((lastPos(0) / 50.0).toInt, (lastPos(1) / 50.0).toInt, None)

        if !theGrid.everyInstanceDone(nappi.text.value.head.asDigit) then
          nappi.disable = false

        lastPos = Vector[Double]()
      else
          val dialog = new Dialog[Unit]() {
             title = "Error"
             contentText = "You must click on the cell you want to delete the number from first!"
             dialogPane().buttonTypes = Seq(ButtonType.OK) }
             dialog.showAndWait()



    flow.children += deleteButton

    // In this part, I add the lines to separate the sub-grids from each other. Essentially the sub-grids are separated by four lines.
    // I create a new pane, to which I add the lines. Then, I must set each element's mouseTransparent to false. This way, the user's
    // actions over these elements does not matter.
    val linePane = new Pane()
    linePane.mouseTransparent = true

    val line1 = new Line {
        startX = 150
        endX = 150
        startY = 0
        endY = 450 }

    val line2 = new Line {
        startX = 300
        endX = 300
        startY = 0
        endY = 450}

    val line3 = new Line {
        startX = 0
        endX = 450
        startY = 150
        endY = 150
    }

    val line4 = new Line {
        startX = 0
        endX = 450
        startY = 300
        endY = 300 }

    val lines = Vector(line1, line2, line3, line4)

    // Here I add each line to the pane, after which I set the strokeWidth to be 3, so they're thicker than the other lines.
    lines.foreach( linePane.children.addAll(_))
    linePane.children.map(node => node.asInstanceOf[javafx.scene.shape.Line]).foreach(_.strokeWidth = 3)


    // Changed the borderPane's background color for fun as well. :-)
    // This borderPane has all the elements of the Killer Sudoku now: the label containing the possible combinations, as well
    // as the grid and the candidate numbers. Later on, I'll have to add the file-handling parts.
    val borderPane = new BorderPane {
      style = "-fx-background-color: #fff0f5 ; "
    }
    borderPane.setCenter(bGrid)
    borderPane.setRight(flow)
    borderPane.setBottom(hbox)

    // I create a new group that holds both the borderPane and the linePane. This way, the linePane will be on top of the
    // borderPane, so the lines are visible.
    val group = new Group(borderPane, linePane)

    // I add the group to the scene, which now has the Killer Sudoku -game.
    val scene = new Scene(group, 600, 900)
      stage = new JFXApp3.PrimaryStage
      stage.height = 600
      stage.width = 900
      stage.title = "Killer Sudoku"
      stage.scene = scene