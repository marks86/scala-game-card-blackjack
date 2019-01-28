package com.gmail.namavirs86.game.blackjack

import scala.collection.mutable.ListBuffer
import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{TestKit, TestProbe}
import org.scalatest.{BeforeAndAfterAll, Matchers, OptionValues, WordSpecLike}
import com.gmail.namavirs86.game.blackjack.Definitions.{BehaviorSettings, BlackjackActionType}
import com.gmail.namavirs86.game.card.core.helpers.Helpers
import com.gmail.namavirs86.game.card.core.Definitions._
import com.gmail.namavirs86.game.card.core.Exceptions.NoGameContextException

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
      val initGameContext = flow.gameContext.getOrElse(throw NoGameContextException())
      initGameContext.player.hand += (Card(Rank.TWO, Suit.CLUBS), Card(Rank.THREE, Suit.CLUBS))
      initGameContext.dealer.hand += Card(Rank.ACE, Suit.CLUBS)

      bjBehavior.tell(BlackjackBehavior.RequestBehaviorProcess(probe.ref, flow), probe.ref)

      val response = probe.expectMsgType[BlackjackBehavior.ResponseBehaviorProcess]
      val gameContext = response.flow.gameContext.getOrElse(throw NoGameContextException())

      gameContext.player.value shouldBe 5
      gameContext.dealer.value shouldBe 11
      gameContext.roundEnded shouldBe false
    }


    "reveal hole card because of dealers BJ" in {
      val flow = Helpers.createFlow()
      val initGameContext = flow.gameContext.getOrElse(throw NoGameContextException())

      initGameContext.bet = Some(5f)
      initGameContext.player.hand += (Card(Rank.TWO, Suit.CLUBS), Card(Rank.THREE, Suit.CLUBS))
      initGameContext.dealer.hand += Card(Rank.ACE, Suit.CLUBS)
      initGameContext.dealer.holeCard = Some(Card(Rank.TEN, Suit.CLUBS))

      bjBehavior.tell(BlackjackBehavior.RequestBehaviorProcess(probe.ref, flow), probe.ref)

      val response = probe.expectMsgType[BlackjackBehavior.ResponseBehaviorProcess]
      val gameContext = response.flow.gameContext.getOrElse(throw NoGameContextException())

      gameContext.dealer shouldBe DealerContext(
        hand = ListBuffer(Card(Rank.ACE, Suit.CLUBS), Card(Rank.TEN, Suit.CLUBS)),
        value = 21,
        holeCard = None,
        hasBJ = true)

      gameContext.outcome shouldBe Some(Outcome.DEALER)
      gameContext.bet shouldBe Some(5)
      gameContext.roundEnded shouldBe true
    }

    "reveal hole card because of players BJ" in {
      val flow = Helpers.createFlow()
      val initGameContext = flow.gameContext.getOrElse(throw NoGameContextException())

      initGameContext.bet = Some(5)
      initGameContext.player.hand += (Card(Rank.ACE, Suit.CLUBS), Card(Rank.TEN, Suit.CLUBS))
      initGameContext.dealer.hand += Card(Rank.TWO, Suit.CLUBS)
      initGameContext.dealer.holeCard = Some(Card(Rank.TEN, Suit.CLUBS))

      bjBehavior.tell(BlackjackBehavior.RequestBehaviorProcess(probe.ref, flow), probe.ref)

      val response = probe.expectMsgType[BlackjackBehavior.ResponseBehaviorProcess]
      val gameContext = response.flow.gameContext.getOrElse(throw NoGameContextException())

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
      val initGameContext = flow.gameContext.getOrElse(throw NoGameContextException())

      initGameContext.bet = Some(5)
      initGameContext.player.hand += (
        Card(Rank.SIX, Suit.CLUBS),
        Card(Rank.ACE, Suit.CLUBS),
        Card(Rank.ACE, Suit.CLUBS)
      )

      bjBehavior.tell(BlackjackBehavior.RequestBehaviorProcess(probe.ref, flow), probe.ref)

      val response = probe.expectMsgType[BlackjackBehavior.ResponseBehaviorProcess]
      val gameContext = response.flow.gameContext.getOrElse(throw NoGameContextException())

      gameContext.player shouldBe PlayerContext(
        hand = ListBuffer(Card(Rank.SIX, Suit.CLUBS), Card(Rank.ACE, Suit.CLUBS), Card(Rank.ACE, Suit.CLUBS)),
        value = 18,
        hasBJ = false
      )

      gameContext.outcome shouldBe None
      gameContext.bet shouldBe Some(5)
      gameContext.totalWin shouldBe 0
    }

    "determine round outcome as TIE" in {
      val flow = Helpers.createFlow()
      val initGameContext = flow.gameContext.getOrElse(throw NoGameContextException())

      flow.requestContext.action = BlackjackActionType.STAND
      initGameContext.bet = Some(5)
      initGameContext.player.hand += (
        Card(Rank.EIGHT, Suit.CLUBS),
        Card(Rank.TEN, Suit.CLUBS),
      )
      initGameContext.dealer.hand += (
        Card(Rank.EIGHT, Suit.CLUBS),
        Card(Rank.TEN, Suit.CLUBS),
      )

      bjBehavior.tell(BlackjackBehavior.RequestBehaviorProcess(probe.ref, flow), probe.ref)

      val response = probe.expectMsgType[BlackjackBehavior.ResponseBehaviorProcess]
      val gameContext = response.flow.gameContext.getOrElse(throw NoGameContextException())

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
      gameContext.bet shouldBe Some(5)
      gameContext.totalWin shouldBe 5
      gameContext.roundEnded shouldBe true
    }

    "determine round outcome as TIE cause both have BJ" in {
      val flow = Helpers.createFlow()
      val initGameContext = flow.gameContext.getOrElse(throw NoGameContextException())

      initGameContext.bet = Some(5)
      initGameContext.player.hand += (
        Card(Rank.ACE, Suit.CLUBS),
        Card(Rank.TEN, Suit.CLUBS),
      )
      initGameContext.dealer.hand += Card(Rank.ACE, Suit.CLUBS)
      initGameContext.dealer.holeCard = Some(Card(Rank.TEN, Suit.CLUBS))

      bjBehavior.tell(BlackjackBehavior.RequestBehaviorProcess(probe.ref, flow), probe.ref)

      val response = probe.expectMsgType[BlackjackBehavior.ResponseBehaviorProcess]
      val gameContext = response.flow.gameContext.getOrElse(throw NoGameContextException())

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
      gameContext.bet shouldBe Some(5)
      gameContext.totalWin shouldBe 5
      gameContext.roundEnded shouldBe true
    }

    "determine round outcome as PLAYER win" in {
      val flow = Helpers.createFlow()
      val initGameContext = flow.gameContext.getOrElse(throw NoGameContextException())

      flow.requestContext.action = BlackjackActionType.STAND
      initGameContext.bet = Some(5)
      initGameContext.player.hand += (
        Card(Rank.NINE, Suit.CLUBS),
        Card(Rank.TEN, Suit.CLUBS),
      )
      initGameContext.dealer.hand += (
        Card(Rank.EIGHT, Suit.CLUBS),
        Card(Rank.TEN, Suit.CLUBS),
      )

      bjBehavior.tell(BlackjackBehavior.RequestBehaviorProcess(probe.ref, flow), probe.ref)

      val response = probe.expectMsgType[BlackjackBehavior.ResponseBehaviorProcess]
      val gameContext = response.flow.gameContext.getOrElse(throw NoGameContextException())

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
      gameContext.bet shouldBe Some(5f)
      gameContext.totalWin shouldBe 10f
      gameContext.roundEnded shouldBe true
    }

    "end round because dealer goes bust" in {
      val flow = Helpers.createFlow()
      val initGameContext = flow.gameContext.getOrElse(throw NoGameContextException())

      flow.requestContext.action = BlackjackActionType.STAND
      initGameContext.bet = Some(5)
      initGameContext.player.hand += (
        Card(Rank.NINE, Suit.CLUBS),
        Card(Rank.TEN, Suit.CLUBS),
      )
      initGameContext.dealer.hand += (
        Card(Rank.EIGHT, Suit.CLUBS),
        Card(Rank.TEN, Suit.CLUBS),
        Card(Rank.TEN, Suit.CLUBS),
      )

      bjBehavior.tell(BlackjackBehavior.RequestBehaviorProcess(probe.ref, flow), probe.ref)

      val response = probe.expectMsgType[BlackjackBehavior.ResponseBehaviorProcess]
      val gameContext = response.flow.gameContext.getOrElse(throw NoGameContextException())

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
      gameContext.bet shouldBe Some(5f)
      gameContext.totalWin shouldBe 10f
      gameContext.roundEnded shouldBe true
    }

    "end round because player goes bust" in {
      val flow = Helpers.createFlow()
      val initGameContext = flow.gameContext.getOrElse(throw NoGameContextException())

      flow.requestContext.action = BlackjackActionType.HIT
      initGameContext.bet = Some(5)
      initGameContext.player.hand += (
        Card(Rank.NINE, Suit.CLUBS),
        Card(Rank.TEN, Suit.CLUBS),
        Card(Rank.TEN, Suit.CLUBS),
      )
      initGameContext.dealer.hand += Card(Rank.EIGHT, Suit.CLUBS)
      initGameContext.dealer.holeCard = Some(Card(Rank.TEN, Suit.CLUBS))

      bjBehavior.tell(BlackjackBehavior.RequestBehaviorProcess(probe.ref, flow), probe.ref)

      val response = probe.expectMsgType[BlackjackBehavior.ResponseBehaviorProcess]
      val gameContext = response.flow.gameContext.getOrElse(throw NoGameContextException())

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
      gameContext.bet shouldBe Some(5f)
      gameContext.totalWin shouldBe 0f
      gameContext.roundEnded shouldBe true
    }
  }
}
