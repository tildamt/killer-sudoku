package gui

import KillerSudoku.FileHandler
import javafx.scene.input.MouseEvent
import scalafx.scene.control.{Button, ButtonType, Dialog, DialogPane, ScrollPane, TextField, TextInputDialog}
import scalafx.scene.layout.BorderPane
import scalafx.scene.text.{Font, Text}
import scalafx.Includes.jfxDialogPane2sfx
import scalafx.scene.SceneIncludes.jfxDialogPane2sfx
import scalafx.scene.control.ControlIncludes.jfxDialogPane2sfx
import scalafx.application.JFXApp3
import KillerSudoku.SubArea
import java.io.File

class FileButtons(app: JFXApp3, grid: Array[Array[Option[Int]]], cages: List[SubArea]):
  
  val filehandler = new FileHandler // a filehandler is needed for saving the files etc.
  
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
    scroll.setContent(text)
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
            filehandler.places(value, grid, cages)

        case None =>

    // With this button, the user may continue their previous progress.
    // This also handles the error case when the user tries to select a game that doesn't exist.
    // The program then lets the user know that an error has occurred. Otherwise, the previous progress shows on the screen.
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
            filehandler.continue(tfield.text.value)
            val creation = new gui.CreateGame(this.app, filehandler.areas, filehandler.ngrid).create
            this.app.stage.scene = creation
        case None =>

    // With this button, the user may start a new game by choosing from one of the options. Note
    // that they can always add new games by saving them into directory "games" in the correct format.
    // This handles the error case in which the user may type the name wrong or nothing at all. This
    // means that the program sends the user an error message letting them know what went wrong.
    val newGameButton = new Button("New Game")
    newGameButton.font = Font.font("Comic Sans MS")
    newGameButton.onMouseClicked = (e: MouseEvent) =>
      val directory1 = new File("games")
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
            val creation = new CreateGame(this.app, filehandler.areas1, Array.fill[Option[Int]](9, 9)(None)).create
            this.app.stage.scene = creation
          else
            val dialog = new Dialog[Unit]() {
               title = "Error"
               contentText = s"The game you selected doesn't exist. Press the button again and choose one of these options: ${filenames1.mkString(", ")}"
               dialogPane().buttonTypes = Seq(ButtonType.OK) }
               dialog.showAndWait()
        case None =>