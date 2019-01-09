package com.gmail.namavirs86.game.core.actions

import akka.actor.{Actor, ActorLogging, ActorRef}
import com.gmail.namavirs86.game.core.Definitions.Flow
import com.gmail.namavirs86.game.core.actions.BaseAction.{RequestActionProcess, ResponseActionProcess}

trait BaseActionMessages {

  final case class RequestActionProcess(playerRef: ActorRef, flow: Flow)

  final case class ResponseActionProcess(playerRef: ActorRef, flow: Flow)

}

object BaseAction extends BaseActionMessages

trait Action {
  val id: String

  def process(flow: Flow): Unit

  def validateRequest(flow: Flow): Unit
}

abstract class BaseAction extends Actor with Action with ActorLogging {
  val id: String

  override def receive: Receive = {
    case RequestActionProcess(playerRef: ActorRef, flow: Flow) ⇒
      validateRequest(flow)
      process(flow)
      sender ! ResponseActionProcess(playerRef, flow)

    case _ => println("that was unexpected")
  }
}
