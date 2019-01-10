package com.gmail.namavirs86.game.blackjack.actions

import akka.actor.ActorSystem
import akka.testkit.{TestKit, TestProbe}
import com.gmail.namavirs86.game.core.Definitions._
import com.gmail.namavirs86.game.core.helpers.Helpers
import com.gmail.namavirs86.game.core.random.RandomCheating
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

  "A Deal action" should {
    "draw the cards" in {
      val probe = TestProbe()
      val action = system.actorOf(DealAction.props(1))
      val cheat = ListBuffer[Int](0, 1, 2, 3)
      val flow = Helpers.createFlow(cheat)

      action.tell(DealAction.RequestActionProcess(probe.ref, flow), probe.ref)

      val response = probe.expectMsgType[DealAction.ResponseActionProcess]
      val dealer = response.flow.gameContext.dealer
      val player = response.flow.gameContext.player

      dealer.hand shouldBe ListBuffer(Card(Rank.TWO,Suit.CLUBS))
      player.hand shouldBe ListBuffer(Card(Rank.THREE, Suit.CLUBS), Card(Rank.FIVE, Suit.CLUBS))
      dealer.holeCard shouldBe Some(Card(Rank.FOUR, Suit.CLUBS))
    }
  }
}