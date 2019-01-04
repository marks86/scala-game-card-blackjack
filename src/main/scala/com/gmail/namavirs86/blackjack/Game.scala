package com.gmail.namavirs86.blackjack

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.util.Timeout
import akka.pattern.ask

import scala.concurrent.Await
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import com.gmail.namavirs86.blackjack.Game.RequestPlay
import com.gmail.namavirs86.blackjack.RequestType.RequestType
import com.gmail.namavirs86.blackjack.actions.{RequestActionProcess, ResponseActionProcess}
import com.gmail.namavirs86.blackjack.adapters.{RequestCreateResponse, ResponseCreateResponse}

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
    case RequestPlay(context) ⇒
      val requestType = context.requestContext.requestType
      actions.get(requestType) match {
        case Some(ref) ⇒
          ref ! RequestActionProcess(sender(), context)
        case None ⇒
          log.info("Missing action for request type: {}", requestType)
      }

    case ResponseActionProcess(playerRef, context) ⇒
      responseAdapter ! RequestCreateResponse(playerRef, context)
      log.info("Received ResponseActionProcess")

    case ResponseCreateResponse(playerRef) ⇒
      log.info("Response created")
  }
}


//implicit val timeout = Timeout(1 seconds)
//val future: Future[Context] = ask(ref, RequestActionProcess(context)).mapTo[Context]
//future.onComplete {
//  case Success(context: Context) ⇒
//}