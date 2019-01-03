package com.gmail.namavirs86.blackjack

import akka.actor.ActorSystem
import akka.testkit.{TestKit, TestProbe}
import com.gmail.namavirs86.blackjack.actions.ResponseActionProcess
import com.gmail.namavirs86.blackjack.helpers.TestAction
import org.scalatest.{BeforeAndAfterAll, Matchers, OptionValues, WordSpecLike}

class GameSpec(_system: ActorSystem)
  extends TestKit(_system)
    with Matchers
    with OptionValues
    with WordSpecLike
    with BeforeAndAfterAll {

  def this() = this(ActorSystem(classOf[GameSpec].getSimpleName))

  val config = GameConfig(
    id = "bj",
    actions = Map(RequestType.DEAL -> classOf[TestAction].getName)
  )

  override def afterAll: Unit = {
    shutdown(system)
  }

  "A Game actor" should {
    "process requested action" in {
      val probe = TestProbe()
      val game = system.actorOf(Game.props(config))

      val context = Context(
        RequestContext(0, RequestType.DEAL),
        GameContext()
      )

      game.tell(Game.RequestPlay(context), probe.ref)
      val response = probe.expectMsgType[ResponseActionProcess]
      val requestContext = response.context.requestContext
      requestContext.requestId shouldBe 0
      requestContext.requestType shouldBe RequestType.DEAL
    }
  }
}