package com.gmail.namavirs86.game.blackjack.actions

import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{TestKit, TestProbe}
import com.gmail.namavirs86.game.card.core.helpers.Helpers
import com.gmail.namavirs86.game.card.core.Definitions._
import com.gmail.namavirs86.game.card.core.Exceptions.NoGameContextException
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

  val probe = TestProbe()
  val action: ActorRef = system.actorOf(HitAction.props(Helpers.shoeManagerSettings))

  "Hit action" should {
    "draw a new card for player" in {
      val initGameContext = Helpers.createGameContext().copy(
        shoe = List(
          Card(Rank.TWO, Suit.CLUBS)
        )
      )

      val flow = Helpers.createFlow().copy(
        gameContext = Some(initGameContext),
      )

      action.tell(HitAction.RequestActionProcess(probe.ref, flow), probe.ref)

      val response = probe.expectMsgType[HitAction.ResponseActionProcess]
      val gameContext = response.flow.gameContext.getOrElse(throw NoGameContextException())
      val dealerHand = gameContext.dealer.hand
      val playerHand = gameContext.player.hand

      dealerHand shouldBe ListBuffer()
      playerHand shouldBe ListBuffer(Card(Rank.TWO, Suit.CLUBS))
    }
  }

}
