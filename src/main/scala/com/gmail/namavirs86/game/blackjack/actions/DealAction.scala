package com.gmail.namavirs86.game.blackjack.actions

import com.gmail.namavirs86.game.core.Definitions.Context
import com.gmail.namavirs86.game.core.actions.BaseAction

class DealAction extends BaseAction {

  def process(context: Context) {

    //    context.gameContext.a = 1

    log.info("Deal action called: ", self)

  }
}
