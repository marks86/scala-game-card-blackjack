package com.gmail.namavirs86.game.blackjack.actions

import akka.actor.ActorSystem
import akka.testkit.{TestKit, TestProbe}
import org.scalatest.{BeforeAndAfterAll, Matchers, OptionValues, WordSpecLike}

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

  "A Stand action" should {
    "process" in {
      val probe = TestProbe()

    }
  }
}
