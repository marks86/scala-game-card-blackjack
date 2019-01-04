package com.gmail.namavirs86.blackjack.adapters

import akka.actor.{Actor, ActorRef}
import com.gmail.namavirs86.blackjack.Context

final case class RequestCreateResponse(playerRef: ActorRef, context: Context)

final case class ResponseCreateResponse(playerRef: ActorRef)

class ResponseAdapter extends Actor {

  override def receive: Receive = {
    case RequestCreateResponse(playerRef, context) ⇒
      sender ! ResponseCreateResponse(playerRef)

    case _ => println("that was unexpected")
  }
}
