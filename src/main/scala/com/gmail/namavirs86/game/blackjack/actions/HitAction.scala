package com.gmail.namavirs86.game.blackjack.actions

import akka.actor.Props
import com.gmail.namavirs86.game.card.core.Definitions.{Flow, GameContext, ShoeManagerSettings}
import com.gmail.namavirs86.game.card.core.Exceptions.NoGameContextException
import com.gmail.namavirs86.game.card.core.ShoeManager
import com.gmail.namavirs86.game.card.core.actions.{BaseAction, BaseActionMessages}

object HitAction extends BaseActionMessages {
  def props(shoeSettings: ShoeManagerSettings): Props = Props(new HitAction(shoeSettings))
}

final class HitAction(shoeSettings: ShoeManagerSettings) extends BaseAction {
  val id = "hitAction"

  private val shoeManager = new ShoeManager(shoeSettings)

  def process(flow: Flow): Option[GameContext] = {
    val gameContext = flow.gameContext.getOrElse(throw NoGameContextException())
    val player = gameContext.player
    val rng = flow.rng

    val (card, shoe) = shoeManager.draw(rng, gameContext.shoe)
    val hand = player.hand :+ card

    Some(gameContext.copy(
      player = player.copy(
        hand = hand
      ),
      shoe = shoe,
    ))
  }

  // @TODO: validate hit action request
  def validateRequest(flow: Flow): Unit = {}
}
