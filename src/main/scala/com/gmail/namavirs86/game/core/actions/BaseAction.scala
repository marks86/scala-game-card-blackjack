package com.gmail.namavirs86.game.core.actions

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.gmail.namavirs86.game.core.Definitions.Flow
import com.gmail.namavirs86.game.core.actions.BaseAction.{RequestActionProcess, ResponseActionProcess}

trait BaseActionMessages {

  final case class RequestActionProcess(playerRef: ActorRef, context: Flow)

  final case class ResponseActionProcess(playerRef: ActorRef, context: Flow)

}

object BaseAction extends BaseActionMessages

abstract class BaseAction extends Actor with ActorLogging {
  val id: String

  override def receive: Receive = {
    case RequestActionProcess(playerRef: ActorRef, flow: Flow) ⇒
      process(flow)
      sender ! ResponseActionProcess(playerRef, flow)

    case _ => println("that was unexpected")
  }

  def process(flow: Flow): Unit
}
