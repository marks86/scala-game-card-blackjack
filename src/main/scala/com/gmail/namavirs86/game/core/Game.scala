package com.gmail.namavirs86.game.core

import akka.actor.{Actor, ActorLogging, ActorRef, Props}

import Game.RequestPlay
import actions.BaseAction.{RequestActionProcess, ResponseActionProcess}
import adapters.ResponseAdapter.{RequestCreateResponse, ResponseCreateResponse}
import Definitions.RequestType.RequestType
import Definitions.{Flow, GameConfig}

object Game {
  def props(config: GameConfig): Props = Props(new Game(config))

  final case class RequestPlay(context: Flow)

  final case class ResponsePlay()

}

class Game(config: GameConfig) extends Actor with ActorLogging {

  private var actions = Map.empty[RequestType, ActorRef]
  private var responseAdapter: ActorRef = _

  override def preStart(): Unit = {
    config.actions.foreach {
      case (requestType, props) ⇒
        actions += requestType → context.actorOf(props)
    }

    responseAdapter = context.actorOf(
      config.responseAdapter
    )

    log.info("Game actor started: {}", self)
  }

  override def postStop(): Unit = log.info("Game actor stopped: {}", self)

  override def receive: Receive = {
    case RequestPlay(flow: Flow) ⇒
      val requestType = flow.requestContext.requestType
      actions.get(requestType) match {
        case Some(ref) ⇒
          ref ! RequestActionProcess(sender, flow)
        case None ⇒
          log.info("Missing action for request type: {}", requestType)
      }

    case ResponseActionProcess(playerRef: ActorRef, flow: Flow) ⇒
      responseAdapter ! RequestCreateResponse(playerRef, flow)
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