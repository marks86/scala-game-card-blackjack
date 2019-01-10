package com.gmail.namavirs86.game.blackjack

import akka.actor.ActorSystem
import akka.testkit.{TestKit, TestProbe}
import com.gmail.namavirs86.game.blackjack.Definitions.BehaviorSettings
import com.gmail.namavirs86.game.core.Definitions.{Card, CardValues, Rank, Suit}
import com.gmail.namavirs86.game.core.helpers.Helpers
import org.scalatest.{BeforeAndAfterAll, Matchers, OptionValues, WordSpecLike}

class BlackjackBehaviorSpec(_system: ActorSystem)
  extends TestKit(_system)
    with Matchers
    with OptionValues
    with WordSpecLike
    with BeforeAndAfterAll {

  def this() = this(ActorSystem(classOf[BlackjackBehaviorSpec].getSimpleName))

  override def afterAll: Unit = {
    shutdown(system)
  }

  val cardValue: CardValues = Map(
    Rank.TWO → 2,
    Rank.THREE → 3,
    Rank.FOUR → 4,
    Rank.FIVE → 5,
    Rank.SIX → 6,
    Rank.SEVEN → 7,
    Rank.EIGHT → 8,
    Rank.NINE → 9,
    Rank.TEN → 10,
    Rank.JACK → 10,
    Rank.QUEEN → 10,
    Rank.KING → 10,
    Rank.ACE → 11
  )

  val settings = BehaviorSettings(
    cardValue,
    bjValue = 21,
    dealerStandValue = 17,
    softValue = 17,
    bjPayoutMultiplier = 3 / 2,
    payoutMultiplier = 2,
  )

  "A Blackjack Behavior" should {
    "process" in {
      val probe = TestProbe()
      val behavior = system.actorOf(BlackjackBehavior.props(settings))
      val flow = Helpers.createFlow()

      flow.gameContext.player.hand += Card(Rank.TWO, Suit.CLUBS)
      flow.gameContext.player.hand += Card(Rank.THREE, Suit.CLUBS)

      behavior.tell(BlackjackBehavior.RequestBehaviorProcess(probe.ref, flow), probe.ref)

      val response = probe.expectMsgType[BlackjackBehavior.ResponseBehaviorProcess]
      val gameContext = response.flow.gameContext

      println(gameContext)

    }
  }
}
