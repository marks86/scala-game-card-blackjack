package com.gmail.namavirs86.game.core.helpers

import akka.actor.{ActorLogging, Props}
import com.gmail.namavirs86.game.core.Definitions.Flow
import com.gmail.namavirs86.game.core.actions.{BaseAction, BaseActionMessages}

object TestAction extends BaseActionMessages {
  def props(a: Int): Props = Props(new TestAction(a))
}

class TestAction(a: Int) extends BaseAction with ActorLogging {
  val id = "testAction"

  def process(flow: Flow): Unit = {
    log.info("processing test action {}", a)
  }
}
