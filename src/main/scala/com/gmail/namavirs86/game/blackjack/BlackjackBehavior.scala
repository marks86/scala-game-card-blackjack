package com.gmail.namavirs86.game.blackjack

import akka.actor.Props
import com.gmail.namavirs86.game.core.{Behavior, BehaviorMessages}
import com.gmail.namavirs86.game.core.Definitions.Flow

object BlackjackBehavior extends BehaviorMessages {
  def props: Props = Props(new BlackjackBehavior())
}

class BlackjackBehavior extends Behavior {
  val id = "blackjackBehavior"

  def process(flow: Flow): Unit = {

  }
}
