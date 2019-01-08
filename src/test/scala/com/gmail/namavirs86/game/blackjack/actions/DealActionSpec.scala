package com.gmail.namavirs86.game.blackjack.actions

import akka.actor.ActorSystem
import akka.testkit.{TestKit, TestProbe}
import com.gmail.namavirs86.game.core.Definitions._
import org.scalatest.{BeforeAndAfterAll, Matchers, OptionValues, WordSpecLike}

import scala.collection.mutable.ListBuffer
import scala.util.Random

class DealActionSpec(_system: ActorSystem)
  extends TestKit(_system)
    with Matchers
    with OptionValues
    with WordSpecLike
    with BeforeAndAfterAll {

  def this() = this(ActorSystem(classOf[DealActionSpec].getSimpleName))

  override def afterAll: Unit = {
    shutdown(system)
  }

  "A DealAction action" should {
    "process" in {
      val probe = TestProbe()
      val action = system.actorOf(DealAction.props(1))

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

      action.tell(DealAction.RequestActionProcess(probe.ref, flow), probe.ref)
    }
  }
}