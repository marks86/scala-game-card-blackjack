package com.gmail.namavirs86.game.blackjack.actions

import akka.actor.Props
import com.gmail.namavirs86.game.core.Definitions.Flow
import com.gmail.namavirs86.game.core.Shoe
import com.gmail.namavirs86.game.core.actions.BaseAction
import com.gmail.namavirs86.game.core.actions.BaseActionMessages

object DealAction extends BaseActionMessages {
  def props: Props = Props(new DealAction())
}

class DealAction extends BaseAction {
  val id = "dealAction"

  //  private shoe = new Shoe()

  def process(flow: Flow) {


    log.info("Deal action called: ")
  }
}
