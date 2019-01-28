package com.gmail.namavirs86.game.blackjack.actions

import akka.actor.ActorSystem
import akka.testkit.{TestKit, TestProbe}
import com.gmail.namavirs86.game.card.core.Definitions.{Card, Rank, ShoeManagerSettings, Suit}
import com.gmail.namavirs86.game.card.core.Exceptions.NoGameContextException
import com.gmail.namavirs86.game.card.core.helpers.Helpers
import org.scalatest.{BeforeAndAfterAll, Matchers, OptionValues, WordSpecLike}

import scala.collection.mutable.ListBuffer

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

  "Deal action" should {
    "draw the cards" in {
      val probe = TestProbe()
      val settings = ShoeManagerSettings(
        deckCount = 1,
        cutCardPosition = 52,
      )
      val action = system.actorOf(DealAction.props(settings))
      val flow = Helpers.createFlow()
      flow.gameContext.getOrElse(throw NoGameContextException()).shoe = List(
        Card(Rank.TWO, Suit.CLUBS),
        Card(Rank.THREE, Suit.CLUBS),
        Card(Rank.FOUR, Suit.CLUBS),
        Card(Rank.FIVE, Suit.CLUBS),
      )

      action.tell(DealAction.RequestActionProcess(probe.ref, flow), probe.ref)

      val response = probe.expectMsgType[DealAction.ResponseActionProcess]
      val gameContext = response.flow.gameContext.getOrElse(throw NoGameContextException())
      val dealer = gameContext.dealer
      val player = gameContext.player

      dealer.hand shouldBe ListBuffer(Card(Rank.TWO, Suit.CLUBS))
      player.hand shouldBe ListBuffer(Card(Rank.THREE, Suit.CLUBS), Card(Rank.FIVE, Suit.CLUBS))
      dealer.holeCard shouldBe Some(Card(Rank.FOUR, Suit.CLUBS))
    }
  }
}