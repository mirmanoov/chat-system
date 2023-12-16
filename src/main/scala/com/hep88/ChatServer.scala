package com.hep88
import akka.actor.typed.ActorRef
import akka.actor.typed.ActorSystem
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.receptionist.{Receptionist,ServiceKey}
import com.hep88.Upnp
import scalafx.collections.ObservableHashSet

object ChatServer {
  //server protocol
  sealed trait Command
  case class JoinChat(clientName: String, from: ActorRef[ChatClient.Command]) extends Command
  case class Leave(name: String, from: ActorRef[ChatClient.Command]) extends Command

  //service key for the chat server
  val ServerKey: ServiceKey[ChatServer.Command] = ServiceKey("chatServer")

  //chat server memberlist
  val members = new ObservableHashSet[User]()

  members.onChange((hs, x)=>{
    for(user <- hs){
      user.ref ! com.hep88.ChatClient.MemberList(members.toList)
    }
  })

  def apply(): Behavior[ChatServer.Command] =
    Behaviors.setup { context =>

      //val UpnpRef = context.spawn(Upnp(), Upnp.name)
      context.system.receptionist ! Receptionist.Register(ServerKey, context.self)

      Behaviors.receiveMessage { message =>
        message match {
          case JoinChat(name, from)=>
            members += User(name, from)
            from ! com.hep88.ChatClient.Joined(members.toList)
            Behaviors.same
          case Leave(name, from)=>
            members -= User(name, from)
            Behaviors.same
        }
      }
    }
}

object Server extends App {
  val greeterMain: ActorSystem[ChatServer.Command] = ActorSystem(ChatServer(), "ChatSystem")
}

