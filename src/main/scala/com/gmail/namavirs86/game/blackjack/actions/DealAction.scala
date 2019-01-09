package com.gmail.namavirs86.game.blackjack.actions

import akka.actor.Props
import com.gmail.namavirs86.game.core.Definitions.{Card, Flow, GameContext}
import com.gmail.namavirs86.game.core.Shoe
import com.gmail.namavirs86.game.core.actions.BaseAction
import com.gmail.namavirs86.game.core.actions.BaseActionMessages

import scala.collection.mutable.ListBuffer

object DealAction extends BaseActionMessages {
  def props(deckCount: Int): Props = Props(new DealAction(deckCount))
}

final class DealAction(deckCount: Int) extends BaseAction {
  val id = "dealAction"

  private val shoe = new Shoe(deckCount)

  def process(flow: Flow): Unit = {
    val GameContext(dealerHand, playerHand, _) = flow.gameContext
    val rng = flow.rng

    dealerHand += shoe.draw(rng)
    playerHand += shoe.draw(rng)
    playerHand += shoe.draw(rng)
  }

  def validateRequest(flow: Flow): Unit = {}
}
