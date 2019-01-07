package com.gmail.namavirs86.game.core.actions

import akka.actor.{Actor, ActorLogging, ActorRef}
import com.gmail.namavirs86.game.core.Definitions.Context


final case class RequestActionProcess(playerRef: ActorRef, context: Context)

final case class ResponseActionProcess(playerRef: ActorRef, context: Context)

abstract class BaseAction extends Actor with ActorLogging {

  override def receive: Receive = {
    case RequestActionProcess(playerRef: ActorRef, context: Context) ⇒
      process(context)
      sender ! ResponseActionProcess(playerRef, context)

    case _ => println("that was unexpected")
  }

  def process(context: Context): Unit
}
