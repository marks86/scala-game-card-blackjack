package com.gmail.namavirs86.game.blackjack.actions

import akka.actor.Props
import com.gmail.namavirs86.game.core.Definitions.Flow
import com.gmail.namavirs86.game.core.actions.{BaseAction, BaseActionMessages}

object HitAction extends BaseActionMessages {
  def props: Props = Props(new HitAction())
}

class HitAction extends BaseAction {
  val id = "hitAction"

  def process(flow: Flow): Unit = {

  }

  def validateRequest(flow: Flow): Unit = {}
}
