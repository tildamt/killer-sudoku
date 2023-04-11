package gui

import io.circe.*
import io.circe.generic.auto.*
import io.circe.parser.*
import io.circe.syntax.*
import javafx.beans.property.SimpleStringProperty
import javafx.scene.input.MouseEvent
import javafx.scene.layout.VBox
import KillerSudoku.{BigGrid, FileHandler, SubArea}
import scalafx.Includes.*
import scalafx.application.JFXApp3
import scalafx.beans.binding.Bindings
import scalafx.beans.property.StringProperty
import scalafx.event.ActionEvent
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.{Group, Scene}
import scalafx.scene.control.*
import scalafx.scene.layout.*
import scalafx.scene.paint.{Color, Paint}
import scalafx.scene.shape.{Line, Rectangle}
import scalafx.scene.text.{Font, FontWeight, Text}

import java.io.File
import scala.collection.mutable
import scala.collection.mutable.{ArrayBuffer, Buffer}
import scala.language.postfixOps



class CreateGrid(app: JFXApp3, val cages: List[SubArea], var arr: Array[Array[Option[Int]]]):

  val bGrid = new GridPane()
  var lastPos = Vector[Double]()   // this is to keep track of the last cell that was clicked on
  var theGrid = new BigGrid(9, 9, arr) // I create a new grid to use its methods
  val filehandler = new FileHandler // filehandling for saving and beginning a new game

  def create: Scene =

    // On the page https://stackoverflow.com/questions/4718213/dividing-a-9x9-2d-array-into-9-sub-grids-like-in-sudoku-c
    // [read 11.4.2023] it is explained how the subgrids on the sudoku can be assigned to numbers from 0-8, which is what
    // I chose to use in my program, because you only really need subgrids to check which numbers have already been placed
    // there.
    val subgrids = Map(
        0 -> new mutable.ArrayBuffer[Label](),
        1 -> new mutable.ArrayBuffer[Label](),
        2 -> new mutable.ArrayBuffer[Label](),
        3 -> new mutable.ArrayBuffer[Label](),
        4 -> new mutable.ArrayBuffer[Label](),
        5 -> new mutable.ArrayBuffer[Label](),
        6 -> new mutable.ArrayBuffer[Label](),
        7 -> new mutable.ArrayBuffer[Label](),
        8 -> new mutable.ArrayBuffer[Label]()
      )

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
      def isAdjacent(cell1: (Int, Int), cell2: (Int, Int)) =
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
        if cages(i).squares.exists(cell1 => cages(j).squares.exists(cell2 => isAdjacent(cell1, cell2))) then
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
        val square1 =  new Label()
        square1.setMinWidth(50)
        square1.setMinHeight(50)

        bGrid.add(square1, j, i)

        //int block = (row/3)*3 + (col/3);
        // here i add the label to the subgrid
        val subgridRow = i / 3 * 3
        val subgridCol = j / 3 * 3
        subgrids((i/3)*3 + (j/3)) += square1

        // here i add the sum as an icon for each cage's first cell
        sumsPlaces.find( cellsWithSum => cellsWithSum._1 == (j, i)) match
          case Some(value) =>
            val nicetext = new scalafx.scene.text.Text(value._2.toString)
              nicetext.font = Font.font("Comic Sans MS", FontWeight.Bold, 15)
            square1.alignment = Pos.TopLeft
            square1.graphic = nicetext
          case None =>

        // when the user continues an old game, I use the variable arr to check which numbers
        // they had already placed there. In the case the game is completely new and they haven't
        // started it earlier, these will always be none.
        val text = arr(j)(i)
        text match
          case Some(value) =>
            square1.font = Font.font("Comis Sans MS", 15)
            square1.text = "" + "\n " + value.toString
          case None =>

        // here i add each cell to its cage
        val sijainti = (j, i)
        val location = labelsPlaces.keys.find(_.contains(sijainti)).get
        labelsPlaces(location) += square1



      // colors is a variable which contains each cage mapped to its style
      var colors = cages.map(area => (area.squares, Buffer[String]()))
      // adjacentslist contains all the adjacent cages for each cage
      var adjacentslist = adjacentCages.map( subarea => (subarea._1.squares, subarea._2.map(area => area.squares)))

      // These are the styles ( I will move them later on). This works if at most one cage has ten adjacent cages, so it
      // isn't usable for every killer sudoku and thus must be modified.
      val styles = Vector(
          "#f9a875",
          "#a3e7d8",
          "#fdd369",
          "#d46a6a",
          "#b983e9",
          "#8bc995",
          "#f49cc3",
          "#77c9d4",
          "#f1b8b8",
          "#a5c5e9"
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
    // The method get leaves space for error cases, but in this case, there should always be one that matches.
    labelsPlaces.foreach( cageWithLabels =>
       cageWithLabels._2.foreach( cells =>
         colors.find( cageWithColors => cageWithColors._1 == cageWithLabels._1.toList).get._2.foreach( a => cells.style = s"-fx-border-color: black; -fx-background-color: ${a}; ")))

      // After I've set the styles, I store the styles into the userData, so when the user is hovering over the candidate buttons
      // and the cell's color must be highlighted accordingly, I can get the previous style from userData.
    labelsPlaces.foreach( cageWithLabels => cageWithLabels._2
        .foreach( cell => cell.setUserData((cell.getStyle))))

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
    // the square, its position on the grid is saved into a variable called lastPos. This can be used when adding a
    // number to a cell, or deleting a number.
    for (node <- bGrid.children) do
      val xPos = node.layoutXProperty().value
      val yPos = node.layoutYProperty().value
      node.onMouseClicked = (e: MouseEvent) =>
        lastPos = Vector(node.layoutXProperty().value, node.layoutYProperty().value)
        println("clicked: " + (node.layoutXProperty().value, node.layoutYProperty().value))

    // this label is created for showing the possible combinations. it is added to the flowpane, which is later added to the borderPane,
    // which holds all the elements to make the killer sudoku.
    val label = new Label
      label.alignment = scalafx.geometry.Pos.TopLeft
      label.setMinWidth(400)
      label.setMinHeight(300)
      label.style = "-fx-border-color: black; -fx-background-color: #e6e6fa; "
      label.font = Font.font(25)
      label.font = Font.font("Comic Sans MS")
      label.text = "Possible combinations"

    // this flowpane holds the elements such as possible combinations -label, and the buttons
    // the user can press to delete a number, start a new game, continue an old game etc.
    val flow = new FlowPane()
    flow.hgap = 5.0
    flow.vgap = 5.0
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
        val subgridNumbers = subgrids.find( (_, b) => b.contains(square)).get._2.filter(_.text.value.nonEmpty).map(_.text.value.last.asDigit).toSet


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
            .filter( (label, location: (Int, Int)) => cage.get.squares.contains(location))
            .filter( (label: javafx.scene.control.Label, location) => label.text.value != "")
            .map( (label: javafx.scene.control.Label, location) => label.text.value.last.asDigit).toVector

        // current sum of the numbers in the cage
        val currentSum = labelsnumbers.sum
       // println("curr
        val originalsum = cage.get.summa
        val npossibilities = originalsum - currentSum
        val ncombs =
          (1 to 9).toSeq.combinations(cage.get.squares.size).toSeq.filter( _.sum == npossibilities).filter( sek => sek.intersect(cage.get.possibleCombinations).nonEmpty)

        // all the labels in a cage
        val labelsInCage: scalafx.collections.ObservableBuffer[(javafx.scene.control.Label)] =
          bGrid.children
            .map(node => node.asInstanceOf[javafx.scene.control.Label])
            .map(a => a -> ((a.layoutXProperty().value / 50.0).toInt, (a.layoutYProperty().value / 50.0).toInt))
            .filter( (lappu, sijainti: (Int, Int)) => cage.get.squares.contains(sijainti))
            .map( (lappu, sijainti) => lappu)

        // filtteröi ne yhdistelmät, että asetettu numero sisältyy niihin
        val combinations =
          if labelsnumbers.nonEmpty then
            cage.get.possibleCombinations
              //.filter( sek => sek.intersect(labelsnumbers).nonEmpty)
              .filter( sek => labelsnumbers.forall(b => sek.contains(b)))
              .map( (a: IndexedSeq[Int]) => a.filter( !labelsnumbers.contains(_)))
              .map( a => a.filter(canAdd(_)))
          else
             cage.get.possibleCombinations


        // Changing the labels text to show the possible combination when the user is hovering over a cell
        label.font = Font.font("Comic Sans MS")
        label.text = "Possible combinations: " + "\n" + combinations.map(_.mkString(", ")).mkString("\n")

        // when mouse exits the cell, i delete all possible combinations from showing and change the button's background colors
        // back to normal
        square.onMouseExited = (e: MouseEvent) =>
          label.font = Font.font("Comic Sans MS")
          label.text = "Possible combinations: "
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
          val row = lastPos(0)
          val column = lastPos(1)
          for i <- bGrid.getChildren do
            if (i.layoutXProperty().value == row) && (i.layoutYProperty().value == column) then
              val buttonsText = node.asInstanceOf[javafx.scene.control.Button].text.value.last.asDigit
              val label = i.asInstanceOf[javafx.scene.control.Label]
              val labelsCurrentText = label.text.value
              val subgrid = subgrids.find( (a, b) => b.contains(label))
              val labels = subgrid.get(1).filter(_ != label).filter( x => x.text.value.nonEmpty).map(x => x.text.value.last.asDigit)
              if
                !labels.contains(buttonsText)
                && !theGrid.getColNumbers((column / 50.0).toInt).contains(Some(buttonsText))
                && !theGrid.getRowNumbers((row / 50.0).toInt).contains(Some(buttonsText))
              then
                theGrid.updateElement((row / 50.0).toInt, (column / 50.0).toInt, Some(buttonsText)) // updating the element to the grid
                label.alignment = Pos.TopLeft
                label.font = Font.font("Comic Sans MS")
                label.text =
                      "" + "\n " + buttonsText
                if theGrid.everyInstanceDone(buttonsText) then {
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
    for button <- hbox.children do
      button.onMouseEntered = (e: MouseEvent) =>
        val butt = button.asInstanceOf[javafx.scene.control.Button]
        val number = butt.text.value
        bGrid.children
          .map( b => b.asInstanceOf[javafx.scene.control.Label])
          .filter( b => b.text.value.nonEmpty)
          .filter( b => b.text.value.last.toString == number)
          .foreach( (b: javafx.scene.control.Label) =>
            b.style = "-fx-border-color: black; -fx-background-color: #f0f8ff; ")
      button.onMouseExited = (e: MouseEvent) =>
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

    // These are used for checking the current saved games later on with the Continue Game -button.
    // In the filenames variable, as the name suggests, I get the names of the files and drop the ends
    // .json so it's more convenient for the user to continue their game.
    val directory = new File("savedgames")
    val files = directory.listFiles()
    val filenames: Array[String] = files.map( _.getName.dropRight(5))

    // in this part, i create a scrollpane that contains the names of the saved games.
    // this can be used with the Continue Game -button, when the user enters the name
    // of the game they wish to continue.
    val scroll = new ScrollPane()
    val text = new Text(s"${filenames.mkString(", ")}")
    text.mouseTransparent = false
    scroll.setContent(text)
    scroll.mouseTransparent = false
     val pane = new DialogPane()
        pane.content = scroll
        val tfield = new TextField()
        tfield.text = ""
     val bPane = new BorderPane()
     bPane.setTop(Text("Write the name of the game you'd like to continue"))
     bPane.setCenter(scroll)
     bPane.setBottom(tfield)
     pane.content = bPane
     pane.buttonTypes = Seq(ButtonType.OK, ButtonType.Close)
     pane.prefWidth = 100
     pane.prefHeight = 100

    // With this button, the user may save the progress they currently have. They must
    // choose a name for the file to save, and it's then stored to savedgames. This
    // handles error cases where user does not enter a name, or tries to name the progress
    // the same name as a previous one. when the user saves a game, i make sure that the name will end in .json
    // no matter the input!
    val saveGameButton = new Button("Save Game")
    saveGameButton.font = Font.font("Comic Sans MS")
    saveGameButton.onMouseClicked = (e: MouseEvent) =>
      val dialog = new TextInputDialog()
      dialog.title = "Save Game"
      dialog.headerText = "Choose a name for your file:"
      dialog.contentText = "Name:"
      val result = dialog.showAndWait()
      result match
        case Some(value) =>
          if (value.isEmpty) then
            val dialog1 = new Dialog[Unit]() {
              title = "Error"
              contentText = s"You must choose a name for your file. Please, try again."
              dialogPane().buttonTypes = Seq(ButtonType.OK)
            }
            dialog1.showAndWait()
          else if filenames.contains(value) then
              val dialog2 = new Dialog[Unit]() {
              title = "Error"
              val ndialogpane = new DialogPane()
              val boPane = new BorderPane()
              boPane.setTop(new Text("You have already saved a game with the following names:"))
              boPane.setCenter(scroll)
              boPane.setBottom(new Text("Please, try again."))
              ndialogpane.buttonTypes = Seq(ButtonType.OK)
              ndialogpane.content = boPane
              ndialogpane.prefWidth = 100
              ndialogpane.prefHeight = 100
              dialogPane = ndialogpane
              resizable = true

            }
              dialog2.showAndWait()
          else
            filehandler.places(value, theGrid.grid, cages)

        case None =>

    // With this button, the user may continue their previous progress.
    // This also handles the error case when the user tries to select a game that doesn't exist.
    // The program then lets the user know that an error has occurred.
    val continueGameButton = new Button("Continue Game")
    continueGameButton.font = Font.font("Comic Sans MS")

    continueGameButton.onMouseClicked = (e: MouseEvent) =>
      val dialog = new TextInputDialog()
      dialog.dialogPane = pane
      dialog.resizable = true
      dialog.title = "Continue Game"
      dialog.dialogPane()
      val result = dialog.showAndWait()
      result match
        case Some(value) =>
          if !filenames.contains(tfield.text.value) then
            val directory = new File("savedgames")
            val files = directory.listFiles()
            val filenames = files.map( _.getName.dropRight(5))
            println("file names: " + filenames.mkString("Array(", ", ", ")"))
            val dialog1 = new Dialog[Unit]() {
              title = "Error"
              val ndialogpane = new DialogPane()
              val boPane = new BorderPane()
              boPane.setTop(new Text("Such a game doesn't exist. You have already saved these games:"))
              boPane.setCenter(scroll)
              boPane.setBottom(new Text("Please, try again."))
              ndialogpane.buttonTypes = Seq(ButtonType.OK)
              ndialogpane.content = boPane
              ndialogpane.prefWidth = 100
              ndialogpane.prefHeight = 100
              dialogPane = ndialogpane
              resizable = true
              dialogPane().buttonTypes = Seq(ButtonType.OK)
            }
            dialog1.showAndWait()
          else
            val ngame = new FileHandler
            ngame.continue(tfield.text.value)
            val creation = new CreateGrid(this.app, ngame.areas, ngame.ngrid).create
            this.app.stage.scene = creation
        case None =>

    // With this button, the user may start a new game by choosing from one of the options. Note
    // that they can always add new games by saving them into directory "games" in the correct format.
    // This handles the error case in which the user may type the name wrong or nothing at all. This
    // means that the program sends the user an error message letting them know what went wrong.
    val newGameButton = new Button("New Game")
    newGameButton.font = Font.font("Comic Sans MS")
    newGameButton.onMouseClicked = (e: MouseEvent) =>
      val directory1 = new File("games") // specify the directory path here
      val files1 = directory1.listFiles()
      val filenames1 = files1.map( _.getName.dropRight(5))
      val dialog = new TextInputDialog()
      dialog.title = "Enter Name"
      dialog.headerText = s"Choose a game from the following: \n${filenames1.mkString(", ")}"
      dialog.contentText = "Name:"
      val result = dialog.showAndWait()
      result match
        case Some(value) =>
          val fileName = s"${value}.json"
          val file = new File(directory1, fileName)
          if (file.exists()) then
            filehandler.newgame(result.get)
            val creation = new CreateGrid(this.app, filehandler.areas1, Array.fill[Option[Int]](9, 9)(None)).create
            this.app.stage.scene = creation
          else
            val dialog = new Dialog[Unit]() {
               title = "Error"
               contentText = s"The game you selected doesn't exist. Press the button again and choose one of these options: ${filenames1.mkString(", ")}"
               dialogPane().buttonTypes = Seq(ButtonType.OK) }
               dialog.showAndWait()
        case None =>

    flow.children += deleteButton
    flow.children += newGameButton
    flow.children += continueGameButton
    flow.children += saveGameButton

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
        endY = 450 }

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
    linePane.children.map(node => node.asInstanceOf[javafx.scene.shape.Line]).foreach(_.strokeWidth = 3.5)

    // Changed the borderPane's background color for fun as well. :-)
    // This borderPane has all the elements of the Killer Sudoku now: the label containing the possible combinations, as well
    // as the grid and the candidate numbers. Later on, I'll have to add the file-handling parts.
    var borderPane = new BorderPane()

    borderPane.setCenter(bGrid)
    borderPane.setRight(flow)
    borderPane.setBottom(hbox)

    // I create a new group that holds both the borderPane and the linePane. This way, the linePane will be on top of the
    // borderPane, so the lines are visible.
    val group = new Group(borderPane, linePane)

    new Scene(group)