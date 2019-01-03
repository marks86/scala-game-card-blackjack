package com.gmail.namavirs86.blackjack.actions

import akka.actor.Actor
import com.gmail.namavirs86.blackjack.Context

final case class RequestActionProcess(context: Context)

final case class ResponseActionProcess(context: Context)

abstract class BaseAction extends Actor {

  override def receive: Receive = {
    case RequestActionProcess(context) ⇒ process(context)
  }

  def process(context: Context): Unit
}
