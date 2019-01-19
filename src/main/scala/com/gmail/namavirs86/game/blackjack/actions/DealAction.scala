package com.gmail.namavirs86.game.blackjack.actions

import akka.actor.Props
import com.gmail.namavirs86.game.core.Definitions.{Card, Flow, GameContext, ShoeManagerSettings}
import com.gmail.namavirs86.game.core.ShoeManager
import com.gmail.namavirs86.game.core.actions.BaseAction
import com.gmail.namavirs86.game.core.actions.BaseActionMessages

import scala.util.Random

object DealAction extends BaseActionMessages {
  def props(shoeSettings: ShoeManagerSettings): Props = Props(new DealAction(shoeSettings))
}

final class DealAction(shoeSettings: ShoeManagerSettings) extends BaseAction {
  val id = "dealAction"

  private val shoeManager = new ShoeManager(shoeSettings)

  def process(flow: Flow): Unit = {
    val gameContext = flow.gameContext
    val dealer = gameContext.dealer
    val player = gameContext.player
    val shoe = gameContext.shoe
    val rng = flow.rng

    dealer.hand += shoeManager.draw(rng)
    player.hand += shoeManager.draw(rng)
    dealer.holeCard = Some(shoeManager.draw(rng))
    player.hand += shoeManager.draw(rng)

    gameContext.bet = flow.requestContext.bet
  }

  def draw(flow: Flow, isNewRound: Boolean): Card = {
    val rng = flow.rng
    val gameContext = flow.gameContext
    val (card, shoe) = shoeManager.draw(rng, gameContext.shoe, isNewRound)
    gameContext.shoe = shoe
    card
  }

  def validateRequest(flow: Flow): Unit = {}
}
