package com.gmail.namavirs86.game.core.helpers

import com.gmail.namavirs86.game.core.Definitions._
import com.gmail.namavirs86.game.core.random.RandomCheating

import scala.collection.mutable.ListBuffer
import scala.util.Random

object Helpers {

  def createFlow(cheat: ListBuffer[Int] = ListBuffer[Int]()): Flow = {
    Flow(
      RequestContext(
        requestId = 0,
        requestType = RequestType.DEAL),
      GameContext(
        dealerHand = ListBuffer[Card](),
        playerHand = ListBuffer[Card](),
        roundEnded = true,
        holeCard = None,
      ),
      rng = new RandomCheating(cheat)
    )
  }

}

