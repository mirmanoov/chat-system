package com.view

import akka.actor.typed.ActorRef
import com.{ChatClient, ChatServer, Client, Group, User}
import javafx.scene.control.SelectionMode
import scalafx.application.JFXApp.PrimaryStage
import scalafx.collections.ObservableBuffer
import scalafx.event.ActionEvent
import scalafx.scene.control.{Button, Label, ListView, TextField}
import scalafx.scene.text.Text
import scalafx.scene.input.MouseEvent
import scalafxml.core.{FXMLLoader, NoDependencyResolver}
import scalafxml.core.macros.sfxml
import scalafx.Includes.handle
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafxml.core.{FXMLLoader, NoDependencyResolver}

import scala.collection.mutable.ArrayBuffer


@sfxml
class MainWindowController(private val txtName: TextField,
                           private val lblStatus: Label,
                           private val listUser: ListView[String],
                           private val listGroup: ListView[String],
                           private val listMessage: ListView[String],
                           private val groupUsers: Text,
                           private val txtMessage: TextField,
                           private val btnSend: Button,
                           private val btnCreateGroup: Button,
                           private val groupName: TextField,
                           private val btnJoin: Button) {

  var chatClientRef: Option[ActorRef[ChatClient.Command]] = None
  val users: ObservableBuffer[User] = new ObservableBuffer[User]()
  val groups: ObservableBuffer[Group] = new ObservableBuffer[Group]()

  private var userChatMap = Map[User, ObservableBuffer[String]]()
  private var groupChatMap = Map[Group, ObservableBuffer[String]]()
  private var thisUser: User = _

  listUser.getSelectionModel.setSelectionMode(SelectionMode.MULTIPLE)

  // Disable the Send buttons initially
  btnSend.disable = true
  btnCreateGroup.disable = true
  btnJoin.disable = true
  groupName.disable = true
  txtMessage.disable = true

  // Enable Send buttons when a message is entered
  txtMessage.text.onChange { (_, _, newValue) =>
    btnSend.disable = newValue.trim.isEmpty
  }

  groupName.text.onChange { (_, _, newValue) =>
    btnCreateGroup.disable = newValue.trim.isEmpty
  }

  // Enable Join button only if name is entered
  txtName.text.onChange { (_, _, newValue) =>
    btnJoin.disable = newValue.trim.isEmpty
  }

  groups.onChange { (grps, _) =>
    val names: ObservableBuffer[String] = new ObservableBuffer[String]()
    for (grp <- grps) {
      names += grp.name
    }
    listGroup.items = names
  }

  def handleJoin(action: ActionEvent): Unit = {
    if (txtName.text.value.trim.nonEmpty) {
      chatClientRef foreach (_ ! ChatClient.StartJoin(txtName.text()))
      btnSend.disable = false
      btnJoin.disable = true
    }
    else {
      displayStatus("Please enter your name to join.")
    }
  }

  def setUser(user: User): Unit = {
    thisUser = user
  }

  def displayStatus(text: String): Unit = {
    lblStatus.text = text
  }

  // Update the updateList method to populate the map
  def updateList(x: Iterable[User]): Unit = {
    users.clear()
    users ++= x
    val names: ObservableBuffer[String] = new ObservableBuffer[String]()
    for (user <- users) {
      names += user.name
    }
    listUser.items = names
    for (user <- x) {
      if (!userChatMap.contains(user)) {
        userChatMap += (user -> new ObservableBuffer[String]())
      }
    }
  }

  def handleSelectedUsers(mouseEvent: MouseEvent): Unit = {
    val indices = listUser.selectionModel().getSelectedIndices

    if (indices.size() == 1 && indices.get(0) >= 0) {
      val index = indices.get(0)
      val user: User = users(index)
      listMessage.items = userChatMap(user)
      txtMessage.disable = false
      listGroup.getSelectionModel.clearSelection()
      groupUsers.setText("")
    }

    else if (indices.size() > 1) {
      listMessage.items = null
      groupName.disable = false
      txtMessage.disable = true
      listGroup.getSelectionModel.clearSelection()
      groupUsers.setText("")
    }

    else if (listGroup.getSelectionModel.getSelectedIndex < 0) {
      txtMessage.disable = true
      groupUsers.setText("")
    }
  }

  def handleSelectedGroup(mouseEvent: MouseEvent): Unit = {
    val index = listGroup.getSelectionModel.getSelectedIndex
    if (index >= 0) {
      val group: Group = groups(index)
      listMessage.items = groupChatMap(group)
      txtMessage.disable = false
      listUser.getSelectionModel.clearSelection()
      var groupNames: String = "("

      group.users.foreach(x => groupNames += s"${x.name}, ")
      groupNames = groupNames.dropRight(2)
      groupNames += ")"
      groupUsers.setText(groupNames)
    }

    else if (listUser.getSelectionModel.getSelectedIndices.size() == 0) {
      txtMessage.disable = true
      groupUsers.setText("")
    }
  }

  // Update the handleSend method
  def handleSend(actionEvent: ActionEvent): Unit = {
    val indexUser = listUser.getSelectionModel.getSelectedIndex
    val indexGroup = listGroup.getSelectionModel.getSelectedIndex

    if (indexUser >= 0) {
      val user: User = users(indexUser)
      Client.greeterMain ! ChatClient.SendMessage(user.ref, txtMessage.text())
      addTextToUser(user, thisUser.name, txtMessage.text())
    }
    else if (indexGroup >= 0) {
      val group: Group = groups(indexGroup)
      Client.greeterMain ! ChatClient.SendMessageToGroup(group, group.users.filter(x => x.ref.path != thisUser.ref.path), txtMessage.text())
      addTextToGroup(group, thisUser.name, txtMessage.text())
    }
    txtMessage.clear()
  }

  def handleCreateGroup(actionEvent: ActionEvent): Unit = {
    if (groupName.text.value.trim.nonEmpty) {
      val indices = listUser.getSelectionModel.getSelectedIndices
      val usersGroup = ArrayBuffer[User]()
      usersGroup += thisUser
      indices.forEach { index =>
        usersGroup += users.get(index)
      }

      val group = Group(groupName.text(), usersGroup.toList)
      usersGroup.foreach(_.ref ! ChatClient.CreateGroup(group: Group))
      groupName.clear()
      groupName.disable = true
    }
  }
  def addGroup (group: Group): Unit = {
    groups += group
    groupChatMap += (group -> new ObservableBuffer[String]())
  }

  def addTextToUser(user: User, sender: String, text: String): Unit = {
    userChatMap(user) += s"$sender: $text"
  }

  def addTextToGroup(group: Group, sender: String, text: String): Unit = {
    groupChatMap(group) += s"$sender: $text"
  }
}
