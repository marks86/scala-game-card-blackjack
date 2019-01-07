package com.gmail.namavirs86.game.core.adapters

import akka.actor.{Actor, ActorRef}
import com.gmail.namavirs86.game.core.Definitions.Context

final case class RequestCreateResponse(playerRef: ActorRef, context: Context)

final case class ResponseCreateResponse(playerRef: ActorRef)

class ResponseAdapter extends Actor {

  override def receive: Receive = {
    case RequestCreateResponse(playerRef: ActorRef, context: Context) ⇒
      sender ! ResponseCreateResponse(playerRef)

    case _ => println("that was unexpected")
  }
}
