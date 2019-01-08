package com.gmail.namavirs86.game.core.helpers

import com.gmail.namavirs86.game.core.Definitions._

import scala.collection.mutable.ListBuffer
import scala.util.Random

object Helpers {

  def createFlow: Flow = {
    Flow(
      RequestContext(
        requestId = 0,
        requestType = RequestType.DEAL),
      GameContext(
        dealerHand = ListBuffer[Card](),
        playerHand = ListBuffer[Card](),
      ),
      rng = new Random()
    )
  }

}

