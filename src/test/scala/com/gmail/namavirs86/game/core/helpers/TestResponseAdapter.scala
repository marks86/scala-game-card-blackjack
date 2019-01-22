package com.gmail.namavirs86.game.core.helpers

import akka.actor.Props
import com.gmail.namavirs86.game.core.Definitions.{Flow, GamePlayResponse}
import com.gmail.namavirs86.game.core.adapters.{BaseResponseAdapter, BaseResponseAdapterMessages}

object TestResponseAdapter extends BaseResponseAdapterMessages {
  def props: Props = Props(new TestResponseAdapter())
}

class TestResponseAdapter extends BaseResponseAdapter {
  val id = "testResponseAdapter"

  def process(flow: Flow): Unit = {
    flow.response = Some(GamePlayResponse())
  }
}