package gui

import KillerSudoku.SubArea
import scalafx.geometry.Pos
import scalafx.scene.Scene
import scalafx.scene.control.{ButtonType, Dialog, Label}
import scalafx.scene.layout.{GridPane, Pane}
import scalafx.scene.shape.Line
import scalafx.scene.text.{Font, FontWeight}
import scalafx.Includes.jfxDialogPane2sfx
import scalafx.scene.SceneIncludes.jfxDialogPane2sfx
import scalafx.scene.control.ControlIncludes.jfxDialogPane2sfx
import scala.collection.mutable
import scala.collection.mutable.Buffer
import scalafx.Includes.jfxLine2sfx

class SudokuGrid(cages: List[SubArea], arr: Array[Array[Option[Int]]]):

    val bGrid = new GridPane()        // creates a new gridpane to add the cells to

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

    def createGrid: GridPane =

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
        val adjacentCages = cages.map( cage => cage -> Buffer[SubArea]()).toMap

        // adjacent cages are mapped for each cage in this for loop. it iterates over every cage in the cages map
        // through its indices, and has the other for-loop go after every cage after the current cage. the logic behind it
        // is that the two cages are adjacent, if any of the cells in the cages are adjacent to each other. that's why it uses
        // the if-condition; it checks if any of the cages after the cage at index i has adjacent cells to the cage at index j.
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

          // here i add the label to the subgrid
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

          // here i add each cell to its cage. in case a cell doesn't belong to any cage, the user gets an error message
          val location1 = (j, i)
          val location = labelsPlaces.keys.find(_.contains(location1))
          location match
            case Some(value) =>
              labelsPlaces(value) += square1
            case None =>
              val dialog = new Dialog[Unit]() {
                 title = "Error"
                 contentText = s"A label without a cage was found at ${location1}. There must be an error in the file somewhere."
                 dialogPane().buttonTypes = Seq(ButtonType.OK) }
                 dialog.showAndWait()
              throw new ErrorMessage(s"A label without a cage was found at ${location1}.")

        // colors is a variable which contains each cage mapped to its style
        var colors = cages.map(area => (area.squares, Buffer[String]()))
        // adjacentslist contains all the adjacent cages for each cage
        var adjacentslist = adjacentCages.map( subarea => (subarea._1.squares, subarea._2.map(area => area.squares)))

        // These are the colors used for cages later on.
        val styles = Vector(
            "#f9a875", // soft orange
            "#a3e7d8", // very soft cyan
            "#fdd369", // soft orange, different from the first one though
            "#d46a6a", // moderate red
            "#b983e9", // very soft violet
            "#8bc995", // slightly desaturated lime green
            "#f49cc3", // very soft pink
            "#77c9d4", // slightly desaturated cyan
            "#f1b8b8", // very soft red
            "#a5c5e9"  // very soft blue
        )

        // Here i choose colors based on the adjacent cages colors. I loop over the colors buffer, which contains each cage
        // mapped to its color. At first, each buffer is empty. The basic idea is that I find the current cage's neighboring
        // cages from the adjacentsList, which has each cage mapped to its neighbors. Then I loop over the colors buffer and find out
        // which colors have been assigned to each adjacent cage, and append those colors to the buffer that holds all the colors that
        // have been used in the adjacent cages. Then I filter those used colors from all possible colors (currently in the vector "styles")
        // and always take the first one out of those possible colors. This way, I think the least number of colors will be used. Then,
        // I append that color to its cage in the colors buffer above. So, basically, for each cage, I assign a color that isn't used for its
        // adjacent cage. Since the list is limited, in case there's a cage which has more adjacent cages than the size of the styles list,
        // the user gets an error message that lets them know at which cage the error occurred.
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
          try
            colors(index)._2.append(possibles.head)
          catch
            case e: Exception =>
              val dialog = new Dialog[Unit]() {
                title = "Error"
                contentText = s"Too many adjacent cages for one cage. The cage includes cells at the following positions: ${current}."
                dialogPane().buttonTypes = Seq(ButtonType.OK)
              }
              dialog.showAndWait()
              throw ErrorMessage(s"Too many adjacent cages for one cage. The cage includes cells at the following positions: ${current}.")
          index += 1

      // In this part, I set each label's (=cell's) style to be the one I assigned to it earlier with the algorithm.
      // It loops over the labels places that holds the labels in their correct cages. Then, from the variable colors I find
      // the color assigned to the cage based on the cage's cells positions. Then, I change each cell's style to match that color.
      // The method get leaves space for error cases, but in this case, there should always be one that matches. Since I already
      // added the error handling above.
      labelsPlaces.foreach( cageWithLabels =>
         cageWithLabels._2.foreach( cells =>
           colors.find( cageWithColors => cageWithColors._1 == cageWithLabels._1.toList).get._2.foreach( a => cells.style = s"-fx-border-color: black; -fx-background-color: ${a}; ")))

      // After I've set the styles, I store the styles into the userData, so when the user is hovering over the candidate buttons
      // and the cell's color must be highlighted accordingly, I can get the previous style from userData.
      labelsPlaces.foreach( cageWithLabels => cageWithLabels._2
          .foreach( cell => cell.setUserData((cell.getStyle))))

      bGrid

  // In this part, I add the lines to separate the sub-grids from each other. Essentially the sub-grids are separated by four lines.
  // I createGrid a new pane, to which I add the lines. Then, I must set each element's mouseTransparent to false. This way, the user's
  // actions over these elements do not matter.
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

    // Here I add each line to the pane, after which I set the strokeWidth to be 3.5, so they're thicker than the other lines.
    lines.foreach( linePane.children.addAll(_))
    linePane.children.map(node => node.asInstanceOf[javafx.scene.shape.Line]).foreach(_.strokeWidth = 3.5)