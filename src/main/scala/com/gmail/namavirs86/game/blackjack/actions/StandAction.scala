package com.gmail.namavirs86.game.blackjack.actions

import akka.actor.Props
import com.gmail.namavirs86.game.core.Definitions.{Flow, ShoeManagerSettings}
import com.gmail.namavirs86.game.core.ShoeManager
import com.gmail.namavirs86.game.core.actions.{BaseAction, BaseActionMessages}

object StandAction extends BaseActionMessages {
  def props(shoeSettings: ShoeManagerSettings): Props = Props(new StandAction(shoeSettings))
}

final class StandAction(shoeSettings: ShoeManagerSettings) extends BaseAction {
  val id = "standAction"

  private val shoeManager = new ShoeManager(shoeSettings)

  def process(flow: Flow): Unit = {

  }


  def validateRequest(flow: Flow): Unit = {}
}
