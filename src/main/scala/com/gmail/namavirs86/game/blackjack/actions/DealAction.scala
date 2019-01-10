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
    val gameContext = flow.gameContext
    val dealer = gameContext.dealer
    val player = gameContext.player
    val rng = flow.rng

    dealer.hand += shoe.draw(rng)
    player.hand += shoe.draw(rng)
    dealer.holeCard = Some(shoe.draw(rng))
    player.hand += shoe.draw(rng)

    gameContext.bet = flow.requestContext.bet
  }

  def validateRequest(flow: Flow): Unit = {}
}
