package com.gmail.namavirs86.game.core

import akka.actor.{Actor, ActorLogging, ActorRef, Props}

import Game.RequestPlay
import actions.{RequestActionProcess, ResponseActionProcess}
import adapters.{RequestCreateResponse, ResponseCreateResponse}
import Definitions.RequestType.RequestType
import Definitions.{Context, GameConfig}

object Game {
  def props(config: GameConfig): Props = Props(new Game(config))

  final case class RequestPlay(context: Context)

  final case class ResponsePlay()

}

class Game(config: GameConfig) extends Actor with ActorLogging {

  private var actions = Map.empty[RequestType, ActorRef]
  private var responseAdapter: ActorRef = ActorRef.noSender

  override def preStart(): Unit = {
    config.actions.foreach {
      case (requestType, className) ⇒
        actions += requestType → context.actorOf(
          Props(Class.forName(className).asInstanceOf[Class[Actor]]), className
        )
    }

    responseAdapter = context.actorOf(
      Props(Class.forName(config.responseAdapter).asInstanceOf[Class[Actor]]), config.responseAdapter
    )

    log.info("Game actor started: {}", self)
  }

  override def postStop(): Unit = log.info("Game actor stopped: {}", self)

  override def receive: Receive = {
    case RequestPlay(context: Context) ⇒
      val requestType = context.requestContext.requestType
      actions.get(requestType) match {
        case Some(ref) ⇒
          ref ! RequestActionProcess(sender(), context)
        case None ⇒
          log.info("Missing action for request type: {}", requestType)
      }

    case ResponseActionProcess(playerRef: ActorRef, context: Context) ⇒
      responseAdapter ! RequestCreateResponse(playerRef, context)
      log.info("Received ResponseActionProcess")

    case ResponseCreateResponse(playerRef: ActorRef) ⇒
      log.info("Response created")
  }
}


//import akka.util.Timeout
//import akka.pattern.ask
//import scala.concurrent.Await
//import scala.concurrent.Future
//import scala.concurrent.ExecutionContext.Implicits.global
//import scala.concurrent.duration._

//implicit val timeout = Timeout(1 seconds)
//val future: Future[Context] = ask(ref, RequestActionProcess(context)).mapTo[Context]
//future.onComplete {
//  case Success(context: Context) ⇒
//}