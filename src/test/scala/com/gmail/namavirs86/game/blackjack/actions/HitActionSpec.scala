package com.gmail.namavirs86.game.blackjack.actions

import akka.actor.ActorSystem
import akka.testkit.{TestKit, TestProbe}
import com.gmail.namavirs86.game.core.Definitions.{Card, GameContext, Rank, Suit}
import com.gmail.namavirs86.game.core.helpers.Helpers
import org.scalatest.{BeforeAndAfterAll, Matchers, OptionValues, WordSpecLike}

import scala.collection.mutable.ListBuffer

class HitActionSpec(_system: ActorSystem)
  extends TestKit(_system)
    with Matchers
    with OptionValues
    with WordSpecLike
    with BeforeAndAfterAll {

  def this() = this(ActorSystem(classOf[HitActionSpec].getSimpleName))

  override def afterAll: Unit = {
    shutdown(system)
  }

  "A Hit action" should {
    "draw a new card for player" in {
      val probe = TestProbe()
      val action = system.actorOf(HitAction.props)
      val cheat = ListBuffer[Int](0)
      val flow = Helpers.createFlow(cheat)

      action.tell(HitAction.RequestActionProcess(probe.ref, flow), probe.ref)

      val response = probe.expectMsgType[HitAction.ResponseActionProcess]
      val GameContext(dealerHand, playerHand, _) = response.flow.gameContext

      dealerHand shouldBe ListBuffer()
      playerHand shouldBe ListBuffer(Card(Rank.TWO,Suit.CLUBS))
    }
  }

}
