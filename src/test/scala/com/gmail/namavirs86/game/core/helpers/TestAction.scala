package com.gmail.namavirs86.game.core.helpers

import akka.actor.ActorLogging
import com.gmail.namavirs86.game.core.Definitions.Context
import com.gmail.namavirs86.game.core.actions.BaseAction

class TestAction extends BaseAction with ActorLogging {

  def process(context: Context): Unit = {
    log.info("processing test action")
  }
}
