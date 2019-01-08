package com.gmail.namavirs86.game.core.actions

import akka.actor.{Actor, ActorLogging, ActorRef}
import com.gmail.namavirs86.game.core.Definitions.Flow


final case class RequestActionProcess(playerRef: ActorRef, context: Flow)

final case class ResponseActionProcess(playerRef: ActorRef, context: Flow)

abstract class BaseAction extends Actor with ActorLogging {

  override def receive: Receive = {
    case RequestActionProcess(playerRef: ActorRef, flow: Flow) ⇒
      process(flow)
      sender ! ResponseActionProcess(playerRef, flow)

    case _ => println("that was unexpected")
  }

  def process(flow: Flow): Unit
}
