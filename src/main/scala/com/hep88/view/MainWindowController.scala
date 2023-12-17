package com.hep88.view
import akka.actor.typed.ActorRef
import scalafxml.core.macros.sfxml
import scalafx.event.ActionEvent
import scalafx.scene.control.{Label, ListView, TextField, Button}
import com.hep88.ChatClient
import com.hep88.User
import scala.collection.mutable
import com.hep88.Client
import scalafx.collections.ObservableBuffer
import scalafx.Includes._

@sfxml
class MainWindowController(private val txtName: TextField,
                           private val lblStatus: Label,
                           private val listUser: ListView[String],
                           private val listMessage: ListView[String],
                           private val txtMessage: TextField,
                           private val btnSend: Button,
                           private val btnSendToAll: Button,
                           private val btnJoin: Button) {

  var chatClientRef: Option[ActorRef[ChatClient.Command]] = None

  val receivedText: ObservableBuffer[String] =  new ObservableBuffer[String]()

  // Map to hold the User objects
  private val userMap = mutable.Map[String, ActorRef[ChatClient.Command]]()

  listMessage.items = receivedText

  // Disable the Send buttons initially
  btnSend.disable = true
  btnSendToAll.disable = true

  // Enable Send buttons when a message is entered
  txtMessage.text.onChange { (_, _, newValue) =>
    val isMessageNotEmpty = newValue.nonEmpty
    btnSend.disable = !isMessageNotEmpty
    btnSendToAll.disable = !isMessageNotEmpty
  }

  // Enable Join button only if name is entered
  txtName.text.onChange { (_, _, newValue) =>
    btnJoin.disable = newValue.trim.isEmpty
  }


  def handleJoin(action: ActionEvent): Unit = {
    if (txtName.text.value.trim.nonEmpty) {
      chatClientRef foreach (_ ! ChatClient.StartJoin(txtName.text()))
    btnSend.disable = false
    btnSendToAll.disable = false
    btnJoin.disable = true
    } else {
      displayStatus("Please enter your name to join.")
    }
  }

  def displayStatus(text: String): Unit = {
    lblStatus.text = text
  }

  // Update the updateList method to populate the map
  def updateList(x: Iterable[User]): Unit = {
    listUser.items = new ObservableBuffer[String]() ++= x.map(_.name)
    userMap.clear()
    x.foreach(user => userMap(user.name) = user.ref)
  }

  // Update the handleSend method
  def handleSend(actionEvent: ActionEvent): Unit = {
    val selectedUsername = listUser.selectionModel().selectedItem.value
    if (selectedUsername.nonEmpty && userMap.contains(selectedUsername)) {
      Client.greeterMain ! ChatClient.SendMessageL(userMap(selectedUsername), txtMessage.text())
    }
  }

  // New method for handling sending message to all users
  def handleSendToAll(actionEvent: ActionEvent): Unit = {
    chatClientRef foreach (_ ! ChatClient.SendMessageToAll(txtMessage.text()))
  }

  def addText(senderName: String, text: String): Unit = {
    receivedText += s"$senderName: $text"
  }
}
