package com.gmail.namavirs86.game.blackjack.adapters

import akka.actor.Props
import com.gmail.namavirs86.game.card.core.Definitions.{Flow, GamePlayResponse}
import com.gmail.namavirs86.game.card.core.adapters.{BaseResponseAdapter, BaseResponseAdapterMessages}

object ResponseAdapter extends BaseResponseAdapterMessages {
  def props: Props = Props(new ResponseAdapter())
}

class ResponseAdapter extends BaseResponseAdapter {
  val id = "responseAdapter"

  def process(flow: Flow): Unit = {
    flow.response = Some(GamePlayResponse())
  }
}
