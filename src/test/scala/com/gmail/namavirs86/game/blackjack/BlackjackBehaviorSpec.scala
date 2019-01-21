package com.gmail.namavirs86.game.blackjack

import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{TestKit, TestProbe}
import org.scalatest.{BeforeAndAfterAll, Matchers, OptionValues, WordSpecLike, FlatSpec}
import pprint._
import com.gmail.namavirs86.game.blackjack.Definitions.BehaviorSettings
import com.gmail.namavirs86.game.core.Definitions._
import com.gmail.namavirs86.game.core.helpers.Helpers

import scala.collection.mutable.ListBuffer


class BlackjackBehaviorSpec(_system: ActorSystem)
  extends TestKit(_system)
    with Matchers
    with OptionValues
    with WordSpecLike
    with BeforeAndAfterAll {

  def this() = this(ActorSystem(classOf[BlackjackBehaviorSpec].getSimpleName))

  val settings = BehaviorSettings(
    Helpers.cardValues,
    bjValue = 21,
    dealerStandValue = 17,
    softValue = 17,
    bjPayoutMultiplier = 3f / 2f,
    payoutMultiplier = 2,
  )

  val probe = TestProbe()
  val bjBehavior: ActorRef = system.actorOf(BlackjackBehavior.props(settings))

  override def afterAll: Unit = {
    shutdown(system)
  }

  "Blackjack Behavior" should {
    "calculate player and dealer hand values" in {
      val flow = Helpers.createFlow()
      flow.gameContext.player.hand += (Card(Rank.TWO, Suit.CLUBS), Card(Rank.THREE, Suit.CLUBS))
      flow.gameContext.dealer.hand += Card(Rank.ACE, Suit.CLUBS)

      bjBehavior.tell(BlackjackBehavior.RequestBehaviorProcess(probe.ref, flow), probe.ref)

      val response = probe.expectMsgType[BlackjackBehavior.ResponseBehaviorProcess]
      val gameContext = response.flow.gameContext

      gameContext.player.value shouldBe 5
      gameContext.dealer.value shouldBe 11
      gameContext.roundEnded shouldBe false
    }


    "reveal hole card because of dealers BJ" in {
      val flow = Helpers.createFlow()

      flow.gameContext.bet = 5
      flow.gameContext.player.hand += (Card(Rank.TWO, Suit.CLUBS), Card(Rank.THREE, Suit.CLUBS))
      flow.gameContext.dealer.hand += Card(Rank.ACE, Suit.CLUBS)
      flow.gameContext.dealer.holeCard = Some(Card(Rank.TEN, Suit.CLUBS))

      bjBehavior.tell(BlackjackBehavior.RequestBehaviorProcess(probe.ref, flow), probe.ref)

      val response = probe.expectMsgType[BlackjackBehavior.ResponseBehaviorProcess]
      val gameContext = response.flow.gameContext

      gameContext.dealer shouldBe DealerContext(
        hand = ListBuffer(Card(Rank.ACE, Suit.CLUBS), Card(Rank.TEN, Suit.CLUBS)),
        value = 21,
        holeCard = None,
        hasBJ = true)

      gameContext.outcome shouldBe Some(Outcome.DEALER)
      gameContext.bet shouldBe 5
      gameContext.roundEnded shouldBe true
    }

    "reveal hole card because of players BJ" in {
      val flow = Helpers.createFlow()

      flow.gameContext.bet = 5
      flow.gameContext.player.hand += (Card(Rank.ACE, Suit.CLUBS), Card(Rank.TEN, Suit.CLUBS))
      flow.gameContext.dealer.hand += Card(Rank.TWO, Suit.CLUBS)
      flow.gameContext.dealer.holeCard = Some(Card(Rank.TEN, Suit.CLUBS))

      bjBehavior.tell(BlackjackBehavior.RequestBehaviorProcess(probe.ref, flow), probe.ref)

      val response = probe.expectMsgType[BlackjackBehavior.ResponseBehaviorProcess]
      val gameContext = response.flow.gameContext

      gameContext.player shouldBe PlayerContext(
        hand = ListBuffer(Card(Rank.ACE, Suit.CLUBS), Card(Rank.TEN, Suit.CLUBS)),
        value = 21,
        hasBJ = true)

      gameContext.dealer shouldBe DealerContext(
        hand = ListBuffer(Card(Rank.TWO, Suit.CLUBS), Card(Rank.TEN, Suit.CLUBS)),
        value = 12,
        holeCard = None,
        hasBJ = false)

      gameContext.outcome shouldBe Some(Outcome.PLAYER)
      gameContext.totalWin shouldBe 7.5f
      gameContext.roundEnded shouldBe true
    }

    "consider Ace as 1 point valued" in {
      val flow = Helpers.createFlow()

      flow.gameContext.bet = 5
      flow.gameContext.player.hand += (
        Card(Rank.SIX, Suit.CLUBS),
        Card(Rank.ACE, Suit.CLUBS),
        Card(Rank.ACE, Suit.CLUBS)
      )

      bjBehavior.tell(BlackjackBehavior.RequestBehaviorProcess(probe.ref, flow), probe.ref)

      val response = probe.expectMsgType[BlackjackBehavior.ResponseBehaviorProcess]
      val gameContext = response.flow.gameContext

      gameContext.player shouldBe PlayerContext(
        hand = ListBuffer(Card(Rank.SIX, Suit.CLUBS), Card(Rank.ACE, Suit.CLUBS), Card(Rank.ACE, Suit.CLUBS)),
        value = 18,
        hasBJ = false
      )

      gameContext.outcome shouldBe None
      gameContext.bet shouldBe 5
      gameContext.totalWin shouldBe 0
    }

    "determine round outcome as TIE" in {
      val flow = Helpers.createFlow()

      flow.requestContext.requestType = RequestType.STAND
      flow.gameContext.bet = 5
      flow.gameContext.player.hand += (
        Card(Rank.EIGHT, Suit.CLUBS),
        Card(Rank.TEN, Suit.CLUBS),
      )
      flow.gameContext.dealer.hand += (
        Card(Rank.EIGHT, Suit.CLUBS),
        Card(Rank.TEN, Suit.CLUBS),
      )

      bjBehavior.tell(BlackjackBehavior.RequestBehaviorProcess(probe.ref, flow), probe.ref)

      val response = probe.expectMsgType[BlackjackBehavior.ResponseBehaviorProcess]
      val gameContext = response.flow.gameContext

      gameContext.dealer shouldBe DealerContext(
        hand = ListBuffer(Card(Rank.EIGHT, Suit.CLUBS), Card(Rank.TEN, Suit.CLUBS)),
        value = 18,
        holeCard = None,
        hasBJ = false
      )

      gameContext.player shouldBe PlayerContext(
        hand = ListBuffer(Card(Rank.EIGHT, Suit.CLUBS), Card(Rank.TEN, Suit.CLUBS)),
        value = 18,
        hasBJ = false
      )

      gameContext.outcome shouldBe Some(Outcome.TIE)
      gameContext.bet shouldBe 5
      gameContext.totalWin shouldBe 5
      gameContext.roundEnded shouldBe true
    }

    "determine round outcome as TIE cause both have BJ" in {
      val flow = Helpers.createFlow()

      flow.gameContext.bet = 5
      flow.gameContext.player.hand += (
        Card(Rank.ACE, Suit.CLUBS),
        Card(Rank.TEN, Suit.CLUBS),
      )
      flow.gameContext.dealer.hand += Card(Rank.ACE, Suit.CLUBS)
      flow.gameContext.dealer.holeCard = Some(Card(Rank.TEN, Suit.CLUBS))

      bjBehavior.tell(BlackjackBehavior.RequestBehaviorProcess(probe.ref, flow), probe.ref)

      val response = probe.expectMsgType[BlackjackBehavior.ResponseBehaviorProcess]
      val gameContext = response.flow.gameContext

      gameContext.dealer shouldBe DealerContext(
        hand = ListBuffer(Card(Rank.ACE, Suit.CLUBS), Card(Rank.TEN, Suit.CLUBS)),
        value = 21,
        holeCard = None,
        hasBJ = true
      )

      gameContext.player shouldBe PlayerContext(
        hand = ListBuffer(Card(Rank.ACE, Suit.CLUBS), Card(Rank.TEN, Suit.CLUBS)),
        value = 21,
        hasBJ = true
      )

      gameContext.outcome shouldBe Some(Outcome.TIE)
      gameContext.bet shouldBe 5
      gameContext.totalWin shouldBe 5
      gameContext.roundEnded shouldBe true
    }

    "determine round outcome as PLAYER win" in {
      val flow = Helpers.createFlow()

      flow.requestContext.requestType = RequestType.STAND
      flow.gameContext.bet = 5
      flow.gameContext.player.hand += (
        Card(Rank.NINE, Suit.CLUBS),
        Card(Rank.TEN, Suit.CLUBS),
      )
      flow.gameContext.dealer.hand += (
        Card(Rank.EIGHT, Suit.CLUBS),
        Card(Rank.TEN, Suit.CLUBS),
      )

      bjBehavior.tell(BlackjackBehavior.RequestBehaviorProcess(probe.ref, flow), probe.ref)

      val response = probe.expectMsgType[BlackjackBehavior.ResponseBehaviorProcess]
      val gameContext = response.flow.gameContext

      gameContext.dealer shouldBe DealerContext(
        hand = ListBuffer(Card(Rank.EIGHT, Suit.CLUBS), Card(Rank.TEN, Suit.CLUBS)),
        value = 18,
        holeCard = None,
        hasBJ = false
      )

      gameContext.player shouldBe PlayerContext(
        hand = ListBuffer(Card(Rank.NINE, Suit.CLUBS), Card(Rank.TEN, Suit.CLUBS)),
        value = 19,
        hasBJ = false
      )

      gameContext.outcome shouldBe Some(Outcome.PLAYER)
      gameContext.bet shouldBe 5f
      gameContext.totalWin shouldBe 10f
      gameContext.roundEnded shouldBe true
    }

    "end round because dealer goes bust" in {
      val flow = Helpers.createFlow()

      flow.requestContext.requestType = RequestType.STAND
      flow.gameContext.bet = 5
      flow.gameContext.player.hand += (
        Card(Rank.NINE, Suit.CLUBS),
        Card(Rank.TEN, Suit.CLUBS),
      )
      flow.gameContext.dealer.hand += (
        Card(Rank.EIGHT, Suit.CLUBS),
        Card(Rank.TEN, Suit.CLUBS),
        Card(Rank.TEN, Suit.CLUBS),
      )

      bjBehavior.tell(BlackjackBehavior.RequestBehaviorProcess(probe.ref, flow), probe.ref)

      val response = probe.expectMsgType[BlackjackBehavior.ResponseBehaviorProcess]
      val gameContext = response.flow.gameContext

      gameContext.dealer shouldBe DealerContext(
        hand = ListBuffer(Card(Rank.EIGHT, Suit.CLUBS), Card(Rank.TEN, Suit.CLUBS), Card(Rank.TEN, Suit.CLUBS)),
        value = 28,
        holeCard = None,
        hasBJ = false
      )

      gameContext.player shouldBe PlayerContext(
        hand = ListBuffer(Card(Rank.NINE, Suit.CLUBS), Card(Rank.TEN, Suit.CLUBS)),
        value = 19,
        hasBJ = false
      )

      gameContext.outcome shouldBe Some(Outcome.PLAYER)
      gameContext.bet shouldBe 5f
      gameContext.totalWin shouldBe 10f
      gameContext.roundEnded shouldBe true
    }

    "end round because player goes bust" in {
      val flow = Helpers.createFlow()

      flow.requestContext.requestType = RequestType.HIT
      flow.gameContext.bet = 5
      flow.gameContext.player.hand += (
        Card(Rank.NINE, Suit.CLUBS),
        Card(Rank.TEN, Suit.CLUBS),
        Card(Rank.TEN, Suit.CLUBS),
      )
      flow.gameContext.dealer.hand += Card(Rank.EIGHT, Suit.CLUBS)
      flow.gameContext.dealer.holeCard = Some(Card(Rank.TEN, Suit.CLUBS))

      bjBehavior.tell(BlackjackBehavior.RequestBehaviorProcess(probe.ref, flow), probe.ref)

      val response = probe.expectMsgType[BlackjackBehavior.ResponseBehaviorProcess]
      val gameContext = response.flow.gameContext

      gameContext.dealer shouldBe DealerContext(
        hand = ListBuffer(Card(Rank.EIGHT, Suit.CLUBS), Card(Rank.TEN, Suit.CLUBS)),
        value = 18,
        holeCard = None,
        hasBJ = false
      )

      gameContext.player shouldBe PlayerContext(
        hand = ListBuffer(Card(Rank.NINE, Suit.CLUBS), Card(Rank.TEN, Suit.CLUBS), Card(Rank.TEN, Suit.CLUBS)),
        value = 29,
        hasBJ = false
      )

      gameContext.outcome shouldBe Some(Outcome.DEALER)
      gameContext.bet shouldBe 5f
      gameContext.totalWin shouldBe 0f
      gameContext.roundEnded shouldBe true
    }
  }
}
