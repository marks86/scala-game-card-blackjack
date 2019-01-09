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
        dealer = PlayerContext(
          hand = ListBuffer[Card](),
          value = 0,
        ),
        player = PlayerContext(
          hand = ListBuffer[Card](),
          value = 0,
        ),
        roundEnded = true,
        holeCard = None,
      ),
      rng = new RandomCheating(cheat)
    )
  }

}

