package com

import akka.actor.typed.ActorSystem
import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafxml.core.{FXMLLoader, NoDependencyResolver}

object Client extends JFXApp {
  // Initializing the Actor System for ChatClient
  val greeterMain: ActorSystem[ChatClient.Command] = ActorSystem(ChatClient(), "ChatSystem")
  greeterMain ! ChatClient.start

  // Loading the FXML and setting up the controller
  val loader = new FXMLLoader(null, NoDependencyResolver)
  loader.load(getClass.getResourceAsStream("/view/MainWindow.fxml"))
  val border: scalafx.scene.layout.BorderPane = loader.getRoot[javafx.scene.layout.BorderPane]()
  val control = loader.getController[com.view.MainWindowController#Controller]()
  control.chatClientRef = Option(greeterMain)

  // Optionally, load a CSS stylesheet if you have one
  // val cssResource = getClass.getResource("view/YourStyleSheet.css")

  // Setting up the primary stage
  stage = new PrimaryStage() {
    scene = new Scene() {
      root = border
      // Uncomment and modify the following line if you are using a stylesheet
      // stylesheets = List(cssResource.toExternalForm)
    }
  }

  // Handling the application close event
  stage.onCloseRequest = handle({
    greeterMain.terminate
  })
}
