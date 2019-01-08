package com.gmail.namavirs86.game.core.helpers

import akka.actor.Props
import com.gmail.namavirs86.game.core.{Behavior, BehaviorMessages}
import com.gmail.namavirs86.game.core.Definitions.Flow

object TestBehavior extends BehaviorMessages {
  def props: Props = Props(new TestBehavior())
}

class TestBehavior extends Behavior {
  val id = "testBehavior"

  def process(flow: Flow): Unit = {}
}
