package com.gmail.namavirs86.blackjack.actions

import akka.actor.{Actor, ActorLogging, Props}
import com.gmail.namavirs86.blackjack.Definitions.Context

class DealAction extends BaseAction with ActorLogging {

  def process(context: Context) {

    //    context.gameContext.a = 1

    log.info("Deal action called: ", self)

  }
}
