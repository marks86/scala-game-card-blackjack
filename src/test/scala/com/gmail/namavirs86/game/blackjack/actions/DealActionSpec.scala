package com.gmail.namavirs86.game.blackjack.actions

import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{TestKit, TestProbe}
import com.gmail.namavirs86.game.card.core.Definitions.{Card, Rank, ShoeManagerSettings, Suit}
import com.gmail.namavirs86.game.card.core.Exceptions.NoGameContextException
import com.gmail.namavirs86.game.card.core.helpers.Helpers
import org.scalatest.{BeforeAndAfterAll, Matchers, OptionValues, WordSpecLike}

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

  val probe = TestProbe()
  val settings = ShoeManagerSettings(
    deckCount = 1,
    cutCardPosition = 52,
  )
  val action: ActorRef = system.actorOf(DealAction.props(settings))

  "Deal action" should {
    "draw the cards" in {
      val initGameContext = Helpers.createGameContext().copy(
        shoe = List(
          Card(Rank.TWO, Suit.CLUBS),
          Card(Rank.THREE, Suit.CLUBS),
          Card(Rank.FIVE, Suit.CLUBS),
          Card(Rank.FOUR, Suit.CLUBS),
        )
      )

      val flow = Helpers.createFlow().copy(
        gameContext = Some(initGameContext),
      )

      action.tell(DealAction.RequestActionProcess(probe.ref, flow), probe.ref)

      val response = probe.expectMsgType[DealAction.ResponseActionProcess]
      val gameContext = response.flow.gameContext.getOrElse(throw NoGameContextException())
      val dealer = gameContext.dealer
      val player = gameContext.player

      dealer.hand shouldBe List(Card(Rank.TWO, Suit.CLUBS))
      player.hand shouldBe List(Card(Rank.THREE, Suit.CLUBS), Card(Rank.FIVE, Suit.CLUBS))
      dealer.holeCard shouldBe Some(Card(Rank.FOUR, Suit.CLUBS))
      gameContext.bet shouldBe Some(1f)
    }
  }
}