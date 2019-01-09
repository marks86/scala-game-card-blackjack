package com.gmail.namavirs86.game.blackjack

import akka.actor.Props
import com.gmail.namavirs86.game.core.{Behavior, BehaviorMessages}
import com.gmail.namavirs86.game.core.Definitions.Flow

object BlackjackBehavior extends BehaviorMessages {
  def props: Props = Props(new BlackjackBehavior())
}

// 1. if dealer has any ten or ace, he checks second card and
//    if its BJ, then card is being revealed
//    if player has also BJ, then push
//    otherwise - immediately looses

// 2. if player has BJ, the dealers hole card is being revealed

class BlackjackBehavior extends Behavior {
  val id = "blackjackBehavior"

  def process(flow: Flow): Unit = {

  }
}
