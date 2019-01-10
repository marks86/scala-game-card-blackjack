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
        requestType = RequestType.DEAL,
        bet = 1f),
      GameContext(
        dealer = DealerContext(
          hand = ListBuffer[Card](),
          value = 0,
          holeCard = None,
          hasBJ = false,
        ),
        player = PlayerContext(
          hand = ListBuffer[Card](),
          value = 0,
          hasBJ = false,
        ),
        bet = 0,
        totalWin = 0f,
        outcome = None,
        roundEnded = true,
      ),
      rng = new RandomCheating(cheat)
    )
  }

}

