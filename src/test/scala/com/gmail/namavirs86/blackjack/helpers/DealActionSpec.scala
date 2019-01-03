package com.gmail.namavirs86.blackjack.helpers

import akka.actor.ActorSystem
import akka.testkit.{TestKit, TestProbe}
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

  "A DealAction action" should {
    "process" in {
      val probe = TestProbe()
    }
  }
}