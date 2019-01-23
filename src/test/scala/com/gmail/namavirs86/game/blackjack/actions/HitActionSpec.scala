package com.gmail.namavirs86.game.blackjack.actions

import akka.actor.ActorSystem
import akka.testkit.{TestKit, TestProbe}
import com.gmail.namavirs86.game.card.core.helpers.Helpers
import com.gmail.namavirs86.game.card.core.Definitions.{Card, Rank, Suit}
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

  "Hit action" should {
    "draw a new card for player" in {
      val probe = TestProbe()
      val action = system.actorOf(HitAction.props(Helpers.shoeManagerSettings))
      val flow = Helpers.createFlow()
      flow.gameContext.shoe = List(
        Card(Rank.TWO, Suit.CLUBS)
      )

      action.tell(HitAction.RequestActionProcess(probe.ref, flow), probe.ref)

      val response = probe.expectMsgType[HitAction.ResponseActionProcess]
      val dealerHand = response.flow.gameContext.dealer.hand
      val playerHand = response.flow.gameContext.player.hand

      dealerHand shouldBe ListBuffer()
      playerHand shouldBe ListBuffer(Card(Rank.TWO, Suit.CLUBS))
    }
  }

}
