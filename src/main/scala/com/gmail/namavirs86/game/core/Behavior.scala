package com.gmail.namavirs86.game.core

import akka.actor.{Actor, ActorContext, ActorLogging, ActorRef}
import com.gmail.namavirs86.game.core.Definitions.Flow
import Behavior.{ResponseBehaviorProcess, RequestBehaviorProcess}

trait BehaviorMessages {

  final case class RequestBehaviorProcess(playerRef: ActorRef, flow: Flow)

  final case class ResponseBehaviorProcess(playerRef: ActorRef, flow: Flow)

}

object Behavior extends BehaviorMessages

// 1. if dealer has any ten or ace, he checks second card and
//    if its BJ, then card is being revealed
//    if player has also BJ, then push
//    otherwise - immediately looses

// 2. if player has BJ, the dealers hole card is being revealed
abstract class Behavior extends Actor with ActorLogging {
  val id: String

  override def receive: Receive = {
    case RequestBehaviorProcess(playerRef: ActorRef, flow: Flow) ⇒
      process(flow)
      sender ! ResponseBehaviorProcess(playerRef, flow)

    case _ => println("that was unexpected")
  }

  def process(flow: Flow): Unit
}
