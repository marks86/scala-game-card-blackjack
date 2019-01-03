package com.gmail.namavirs86.blackjack

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.gmail.namavirs86.blackjack.Game.RequestPlay
import com.gmail.namavirs86.blackjack.RequestType.RequestType
import com.gmail.namavirs86.blackjack.actions.{RequestActionProcess, ResponseActionProcess}

object Game {
  def props(config: GameConfig): Props = Props(new Game(config))

  final case class RequestPlay(context: Context)

}

class Game(config: GameConfig) extends Actor with ActorLogging {
  private var actions = Map.empty[RequestType, ActorRef]

  override def preStart(): Unit = {
    config.actions.foreach {
      case (requestType, className) ⇒
        actions += requestType → context.actorOf(
          Props(Class.forName(className).asInstanceOf[Class[Actor]]), className
        )
    }

    log.info("Game actor started: {}", self)
  }

  override def postStop(): Unit = log.info("Game actor stopped: {}", self)

  override def receive: Receive = {
    case RequestPlay(context) ⇒
      val requestType = context.requestContext.requestType
      actions.get(requestType) match {
        case Some(ref) ⇒
          ref tell (RequestActionProcess(context), sender())
        case None ⇒
          log.info("Missing action for request type: {}", requestType)
      }
    case ResponseActionProcess(context) ⇒
      log.info("bla - bla - bla")
  }
}
