package com.gmail.namavirs86.game.blackjack.actions

import akka.actor.Props
import com.gmail.namavirs86.game.card.core.Definitions.{Flow, ShoeManagerSettings}
import com.gmail.namavirs86.game.card.core.Exceptions.NoGameContextException
import com.gmail.namavirs86.game.card.core.ShoeManager
import com.gmail.namavirs86.game.card.core.actions.{BaseAction, BaseActionMessages}

object HitAction extends BaseActionMessages {
  def props(shoeSettings: ShoeManagerSettings): Props = Props(new HitAction(shoeSettings))
}

final class HitAction(shoeSettings: ShoeManagerSettings) extends BaseAction {
  val id = "hitAction"

  private val shoeManager = new ShoeManager(shoeSettings)

  def process(flow: Flow): Unit = {
    val gameContext = flow.gameContext.getOrElse(throw NoGameContextException())
    val playerHand = gameContext.player.hand
    val rng = flow.rng

    val (card, shoe) = shoeManager.draw(rng, gameContext.shoe)
    playerHand += card

    gameContext.shoe = shoe
  }

  def validateRequest(flow: Flow): Unit = {}
}
