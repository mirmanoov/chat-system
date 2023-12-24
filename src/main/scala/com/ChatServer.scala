package com

import akka.actor.typed.receptionist.{Receptionist, ServiceKey}
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, ActorSystem, Behavior}
import scalafx.collections.ObservableBuffer

object ChatServer {
  // Server protocol
  sealed trait Command

  case class JoinChat(clientName: String, from: ActorRef[ChatClient.Command]) extends Command

  case class Leave(name: String, from: ActorRef[ChatClient.Command]) extends Command

  // New command for broadcasting messages
  case class BroadcastMessage(content: String, from: ActorRef[ChatClient.Command]) extends Command

  // Service key for the chat server
  val ServerKey: ServiceKey[ChatServer.Command] = ServiceKey("chatServer")

  // Chat server member list
  val members = new ObservableBuffer[User]()
  var id: Int = 0;

  members.onChange { (hs, _) =>
    for (user <- hs) {
      user.ref ! ChatClient.MemberList(members.toList)
    }
  }

  def apply(): Behavior[ChatServer.Command] =
    Behaviors.setup { context =>
      context.system.receptionist ! Receptionist.Register(ServerKey, context.self)

      Behaviors.receiveMessage { message =>
        message match {
          case JoinChat(name, from) =>
            members += User(name, from)
            from ! ChatClient.Joined(User(name, from), members.toList)
            Behaviors.same
          case Leave(name, from) =>
            members -= User(name, from)
            Behaviors.same
          // 1Handling broadcast message
          case BroadcastMessage(content, from) =>
            val senderName = "Server" // or some other appropriate identifier
            members.foreach(_.ref ! ChatClient.Message(content, from))
            Behaviors.same
        }
      }
    }
}

object Server extends App {
  val greeterMain: ActorSystem[ChatServer.Command] = ActorSystem(ChatServer(), "ChatSystem")
}