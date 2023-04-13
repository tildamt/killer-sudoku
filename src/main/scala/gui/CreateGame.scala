package gui

import io.circe.*
import io.circe.generic.auto.*
import io.circe.parser.*
import io.circe.syntax.*
import javafx.beans.property.SimpleStringProperty
import javafx.scene.input.MouseEvent
import javafx.scene.layout.VBox
import KillerSudoku.{BigGrid, FileHandler, SubArea}
import javafx.stage.WindowEvent
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
import scala.io.Source
import scala.language.postfixOps



class CreateGame(app: JFXApp3, val cages: List[SubArea], arr: Array[Array[Option[Int]]]):

  var lastPos = Vector[Double]()   // this is to keep track of the last cell that was clicked on
  var theGrid = new BigGrid(9, 9, arr) // I create a new grid to use its methods
  val filehandler = new FileHandler // filehandling for saving and beginning a new game

  def create: Scene =

    val gridhandler = SudokuGrid(cages, arr) // this variable holds everything needed to create the grid
    val bGrid = gridhandler.createGrid       // this gives us the actual grid
    val subgrids = gridhandler.subgrids      // these are the subgrids

    // here i create hbox to hold the candidate numbers and add those numbers to the hbox
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

    // this label is created for showing the possible combinations. it is added to the flowpane, which is later added to the borderPane,
    // which holds all the elements to make the killer sudoku.
    val label = new Label
      label.alignment = scalafx.geometry.Pos.TopLeft
      label.setMinWidth(400)
      label.setMinHeight(300)
      label.style = "-fx-border-color: black; -fx-background-color: #e6e6fa; "
      label.font = Font.font("Comic Sans MS", 20)
      label.text = "Possible combinations:"

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
    // found in the same row, column or subgrid. I then filter the candidate numbers based on that method.
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

        // this shows the current possible combinations for the cage the user is hovering over, with the help of subarea's method
        // possiblecombinations, and then filtering out those combinations that don't contain the already placed numbers within the cage
        val combinations =
          if labelsnumbers.nonEmpty then
            cage.get.possibleCombinations
              .filter( sek => labelsnumbers.forall(b => sek.contains(b)))
              .map( (a: IndexedSeq[Int]) => a.filter( !labelsnumbers.contains(_)))
          else
             cage.get.possibleCombinations

        // Changing the label's text to show the possible combination when the user is hovering over a cell
        label.font = Font.font("Comic Sans MS", 20)
        label.text = "Possible combinations: " + "\n" + combinations.map(_.mkString(", ")).mkString("\n")

        // when mouse exits the cell, i delete all possible combinations from showing and change the button's background colors
        // back to normal
        square.onMouseExited = (e: MouseEvent) =>
          label.font = Font.font("Comic Sans MS", 20)
          label.text = "Possible combinations: "
           hbox.getChildren
          .toVector
          .map(a => a.asInstanceOf[javafx.scene.control.Button])
          .foreach( _.style = "-fx-background-color: #f08080; ")

    // This part of code adds numbers to the grid. In addition, it sends an error message, if the user tries to add a number
    // that already is on the same row, column, or sub-grid. First, it loops over buttons in the hBox, which are the candidate
    // numbers. Then I create variables to represent the button and its text. After this, I loop over the
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
            if (i.layoutXProperty().value == row) && (i.layoutYProperty().value == column) then {
              val buttonsText = node.asInstanceOf[javafx.scene.control.Button].text.value.last.asDigit
              val label = i.asInstanceOf[javafx.scene.control.Label]
              val labelsCurrentText = label.text.value
              val subgrid = subgrids.find( (a, b) => b.contains(label))
              val labels = subgrid.get(1).filter(_ != label).filter( x => x.text.value.nonEmpty).map(x => x.text.value.last.asDigit)
              if
                !labels.contains(buttonsText)
                && !theGrid.getColNumbers((column / 50.0).toInt).contains(Some(buttonsText))
                && !theGrid.getRowNumbers((row / 50.0).toInt).contains(Some(buttonsText))
              then {
                theGrid.updateElement((row / 50.0).toInt, (column / 50.0).toInt, Some(buttonsText)) // updating the element to the grid
                label.alignment = Pos.TopLeft
                label.font = Font.font("Comic Sans MS")
                label.text =
                      "" + "\n " + buttonsText
                if theGrid.everyInstanceDone(buttonsText) then {
                  button.disable = true }       } // checking if every instance is now done
              else {
                val dialog = new Dialog[Unit]() {
                  title = "Error"
                  contentText = "This number cannot be placed here."
                  dialogPane().buttonTypes = Seq(ButtonType.OK) }  // here i create the error message, which is a dialog
                dialog.showAndWait() }
            }
          lastPos = Vector[Double]()

    // When the user is hovering over buttons, the squares with the same number are highlighted. The way this for-loop works is
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
          .foreach( (label: javafx.scene.control.Label) =>
          label.setStyle(label.getUserData.asInstanceOf[String]))

    // This is a button so the user can delete a number. Later on, I add the delete-button to the flowPane next to the sudoku grid.
    val deleteButton = new Button("Delete number")
    deleteButton.font = Font.font("Comic Sans MS")

    // When the user clicks on the deleteButton, the program checks the last square that was clicked
    // (if there are any) and deletes that number. In addition, the user gets an error message if they haven't clicked on any cell to delete the number from.
    // The way this method works
    // is that it first gets the number that was in the cell (after confirming last position clicked is not empty),
    // after which it finds the button whose number is the same. Then it updates the
    // cell's number to be empty. If the last position is empty, the user gets an error message. If the deletion changes one number's every instance
    // not to be done, I also update the disabled thing accordingly now.
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

        val usedButton = hbox.children
          .map(b => b.asInstanceOf[javafx.scene.control.Button])
          .filter( _.text.value.head.asDigit == numberHere)
          .head

        bGrid.children
          .map(b => b.asInstanceOf[javafx.scene.control.Label])
          .filter( _.layoutXProperty().value == lastPos(0))
          .filter( _.layoutYProperty().value == lastPos(1))
          .foreach( _.text = "")
        theGrid.updateElement((lastPos(0) / 50.0).toInt, (lastPos(1) / 50.0).toInt, None)

        if !theGrid.everyInstanceDone(usedButton.text.value.head.asDigit) then
          usedButton.disable = false

        lastPos = Vector[Double]()
      else
          val dialog = new Dialog[Unit]() {
             title = "Error"
             contentText = "You must click on the cell you want to delete the number from first."
             dialogPane().buttonTypes = Seq(ButtonType.OK) }
             dialog.showAndWait()

    val fileButtons = new FileButtons(this.app, theGrid.grid, cages) // from this variable i get the buttons for handling the files
    val newGameButton = fileButtons.newGameButton  // button to start the new game
    val continueGameButton = fileButtons.continueGameButton  // button to continue an old game
    val saveGameButton = fileButtons.saveGameButton  // button to save the current progress

    // here i add all the buttons to be flowpane's children, and the flowpane is on the left of the grid
    flow.children += deleteButton
    flow.children += newGameButton
    flow.children += continueGameButton
    flow.children += saveGameButton

    // This borderPane has all the elements of the Killer Sudoku now: the label containing the possible combinations, as well
    // as the grid and the candidate numbers.
    var borderPane = new BorderPane()

    borderPane.setCenter(bGrid)
    borderPane.setRight(flow)
    borderPane.setBottom(hbox)

    // I create a new group that holds both the borderPane and the linePane. This way, the linePane will be on top of the
    // borderPane, so the lines are visible.
    val group = new Group(borderPane, gridhandler.linePane)

    // when the user closes the program, their previous progress is saved, and its named after the time they closed the app
    this.app.stage.onCloseRequest = (e: WindowEvent) =>
      val time = java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("Europe/Helsinki"))
      val day = time.get(java.util.Calendar.DAY_OF_MONTH)
      val year = time.get(java.util.Calendar.YEAR)
      val month = time.get(java.util.Calendar.MONTH)
      val hour = time.get(java.util.Calendar.HOUR_OF_DAY)
      val minute = time.get(java.util.Calendar.MINUTE)
      val second = time.get(java.util.Calendar.SECOND)
      filehandler.places(s"${day}-${month}-${year} at ${hour}-${minute}-${second}", theGrid.grid, cages)

    new Scene(group)