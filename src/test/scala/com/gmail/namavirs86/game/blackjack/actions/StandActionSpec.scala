package com.gmail.namavirs86.game.blackjack.actions

import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{TestKit, TestProbe}
import com.gmail.namavirs86.game.blackjack.Definitions.StandActionSettings
import com.gmail.namavirs86.game.card.core.helpers.Helpers
import com.gmail.namavirs86.game.card.core.Definitions._
import com.gmail.namavirs86.game.card.core.Exceptions.NoGameContextException
import org.scalatest.{BeforeAndAfterAll, Matchers, OptionValues, WordSpecLike}

import scala.collection.mutable.ListBuffer

class StandActionSpec(_system: ActorSystem)
  extends TestKit(_system)
    with Matchers
    with OptionValues
    with WordSpecLike
    with BeforeAndAfterAll {

  def this() = this(ActorSystem(classOf[StandActionSpec].getSimpleName))

  override def afterAll: Unit = {
    shutdown(system)
  }

  val probe = TestProbe()
  val settings = StandActionSettings(
    shoeSettings = ShoeManagerSettings(
      deckCount = 1,
      cutCardPosition = 52,
    ),
    cardValues = Helpers.cardValues,
    dealerStandValue = 17,
    dealerSoftValue = 17,
    bjValue = 21,
  )
  val action: ActorRef = system.actorOf(StandAction.props(settings))

  "Stand action" should {
    "draw additional card and stand on 17" in {
      val flow = Helpers.createFlow()
      val gameContext = flow.gameContext.getOrElse(throw NoGameContextException())
      gameContext.shoe = List(
        Card(Rank.ACE, Suit.CLUBS),
      )
      gameContext.dealer.hand += Card(Rank.TEN, Suit.CLUBS)
      gameContext.dealer.holeCard = Some(Card(Rank.SIX, Suit.CLUBS))

      action.tell(StandAction.RequestActionProcess(probe.ref, flow), probe.ref)

      val response = probe.expectMsgType[HitAction.ResponseActionProcess]
      val dealer = response.flow.gameContext.getOrElse(throw NoGameContextException()).dealer

      dealer shouldBe DealerContext(
        hand = ListBuffer(Card(Rank.TEN, Suit.CLUBS), Card(Rank.SIX, Suit.CLUBS), Card(Rank.ACE, Suit.CLUBS)),
        value = 17,
        holeCard = None,
        hasBJ = false,
      )
    }

    "draw additional card on soft hand" in {
      val flow = Helpers.createFlow()
      val gameContext = flow.gameContext.getOrElse(throw NoGameContextException())
      gameContext.shoe = List(
        Card(Rank.ACE, Suit.CLUBS),
      )
      gameContext.dealer.hand += Card(Rank.ACE, Suit.CLUBS)
      gameContext.dealer.holeCard = Some(Card(Rank.SIX, Suit.CLUBS))

      action.tell(StandAction.RequestActionProcess(probe.ref, flow), probe.ref)

      val response = probe.expectMsgType[HitAction.ResponseActionProcess]
      val dealer = response.flow.gameContext.getOrElse(throw NoGameContextException()).dealer

      dealer shouldBe DealerContext(
        hand = ListBuffer(Card(Rank.ACE, Suit.CLUBS), Card(Rank.SIX, Suit.CLUBS), Card(Rank.ACE, Suit.CLUBS)),
        value = 18,
        holeCard = None,
        hasBJ = false,
      )
    }

    "not draw additional card" in {
      val flow = Helpers.createFlow()
      val gameContext = flow.gameContext.getOrElse(throw NoGameContextException())
      gameContext.shoe = List(
        Card(Rank.ACE, Suit.CLUBS),
      )
      gameContext.dealer.hand += Card(Rank.ACE, Suit.CLUBS)
      gameContext.dealer.holeCard = Some(Card(Rank.SEVEN, Suit.CLUBS))

      action.tell(StandAction.RequestActionProcess(probe.ref, flow), probe.ref)

      val response = probe.expectMsgType[HitAction.ResponseActionProcess]
      val dealer = response.flow.gameContext.getOrElse(throw NoGameContextException()).dealer

      dealer shouldBe DealerContext(
        hand = ListBuffer(Card(Rank.ACE, Suit.CLUBS), Card(Rank.SEVEN, Suit.CLUBS)),
        value = 18,
        holeCard = None,
        hasBJ = false,
      )
    }
  }
}
