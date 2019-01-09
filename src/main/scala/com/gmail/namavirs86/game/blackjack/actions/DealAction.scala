package com.gmail.namavirs86.game.blackjack.actions

import akka.actor.Props
import com.gmail.namavirs86.game.core.Definitions.{Card, Flow, GameContext}
import com.gmail.namavirs86.game.core.Shoe
import com.gmail.namavirs86.game.core.actions.BaseAction
import com.gmail.namavirs86.game.core.actions.BaseActionMessages

object DealAction extends BaseActionMessages {
  def props(deckCount: Int): Props = Props(new DealAction(deckCount))
}

final class DealAction(deckCount: Int) extends BaseAction {
  val id = "dealAction"

  private val shoe = new Shoe(deckCount)

  def process(flow: Flow): Unit = {
    val dealerHand = flow.gameContext.dealerHand
    val playerHand = flow.gameContext.playerHand
    val rng = flow.rng

    dealerHand += shoe.draw(rng)
    playerHand += shoe.draw(rng)
    flow.gameContext.holeCard = Some(shoe.draw(rng))
    playerHand += shoe.draw(rng)
  }

  def validateRequest(flow: Flow): Unit = {}
}
