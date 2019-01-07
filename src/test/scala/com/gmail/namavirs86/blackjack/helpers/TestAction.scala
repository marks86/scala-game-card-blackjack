package com.gmail.namavirs86.blackjack.helpers

import akka.actor.ActorLogging
import com.gmail.namavirs86.blackjack.Definitions.Context
import com.gmail.namavirs86.blackjack.actions.BaseAction

class TestAction extends BaseAction with ActorLogging {

  def process(context: Context): Unit = {
    log.info("processing test action")
  }
}
