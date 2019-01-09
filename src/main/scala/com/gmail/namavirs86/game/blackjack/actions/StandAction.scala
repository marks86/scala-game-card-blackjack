package com.gmail.namavirs86.game.blackjack.actions

import akka.actor.{Actor, Props}
import com.gmail.namavirs86.game.core.Definitions.Flow
import com.gmail.namavirs86.game.core.Shoe
import com.gmail.namavirs86.game.core.actions.{BaseAction, BaseActionMessages}

object StandAction extends BaseActionMessages {
  def props: Props = Props(new StandAction())
}

final class StandAction extends BaseAction {
  val id = "standAction"

  private val shoe = new Shoe(1)

  def process(flow: Flow): Unit = {

  }


  def validateRequest(flow: Flow): Unit = {}
}
