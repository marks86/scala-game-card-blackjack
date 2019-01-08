package com.gmail.namavirs86.game.core

import akka.actor.ActorSystem
import akka.testkit.{TestKit, TestProbe}
import com.gmail.namavirs86.game.core.Definitions._
import com.gmail.namavirs86.game.core.adapters.ResponseAdapter
import com.gmail.namavirs86.game.core.helpers.TestAction
import org.scalatest.{BeforeAndAfterAll, Matchers, OptionValues, WordSpecLike}

import scala.collection.mutable.ListBuffer
import scala.util.Random

class GameSpec(_system: ActorSystem)
  extends TestKit(_system)
    with Matchers
    with OptionValues
    with WordSpecLike
    with BeforeAndAfterAll {

  def this() = this(ActorSystem(classOf[GameSpec].getSimpleName))

  val config = GameConfig(
    id = "bj",
    actions = Map(RequestType.DEAL -> TestAction.props(1)),
    responseAdapter = classOf[ResponseAdapter].getName
  )

  override def afterAll: Unit = {
    shutdown(system)
  }

  "A Game actor" should {
    "process requested action" in {
      val probe = TestProbe()
      val game = system.actorOf(Game.props(config), "gameActor")

      val flow = Flow(
        RequestContext(
          requestId = 0,
          requestType = RequestType.DEAL),
        GameContext(
          dealerHand = ListBuffer[Card](),
          playerHand = ListBuffer[Card](),
        ),
        rng = new Random()
      )

      game.tell(Game.RequestPlay(flow), probe.ref)
      //      val response = probe.expectMsgType[ResponseActionProcess]
      //      val requestContext = response.context.requestContext
      //      requestContext.requestId shouldBe 0
      //      requestContext.requestType shouldBe RequestType.DEAL
    }
  }
}