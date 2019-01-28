package com.gmail.namavirs86.game.blackjack.actions

import akka.actor.Props
import com.gmail.namavirs86.game.card.core.ShoeManager
import com.gmail.namavirs86.game.card.core.actions.{BaseAction, BaseActionMessages}
import com.gmail.namavirs86.game.card.core.Definitions.{Card, Flow, ShoeManagerSettings}
import com.gmail.namavirs86.game.card.core.Exceptions.NoGameContextException

object DealAction extends BaseActionMessages {
  def props(shoeSettings: ShoeManagerSettings): Props = Props(new DealAction(shoeSettings))
}

final class DealAction(shoeSettings: ShoeManagerSettings) extends BaseAction {
  val id = "dealAction"

  private val shoeManager = new ShoeManager(shoeSettings)

  def process(flow: Flow): Unit = {
    val gameContext = flow.gameContext.getOrElse(throw NoGameContextException())
    val dealer = gameContext.dealer
    val player = gameContext.player

    dealer.hand += drawCard(flow, isNewRound = true)
    player.hand += drawCard(flow)
    dealer.holeCard = Some(drawCard(flow))
    player.hand += drawCard(flow)

    gameContext.bet = flow.requestContext.bet
  }

  def validateRequest(flow: Flow): Unit = {}

  private def drawCard(flow: Flow, isNewRound: Boolean = false): Card = {
    val rng = flow.rng
    val gameContext = flow.gameContext.getOrElse(throw NoGameContextException())
    val (card, shoe) = shoeManager.draw(rng, gameContext.shoe, isNewRound)
    gameContext.shoe = shoe
    card
  }
}
