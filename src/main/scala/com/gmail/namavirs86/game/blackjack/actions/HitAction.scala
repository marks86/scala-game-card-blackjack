package com.gmail.namavirs86.game.blackjack.actions

import akka.actor.Props
import com.gmail.namavirs86.game.core.Definitions.Flow
import com.gmail.namavirs86.game.core.ShoeManager
import com.gmail.namavirs86.game.core.actions.{BaseAction, BaseActionMessages}

object HitAction extends BaseActionMessages {
  def props: Props = Props(new HitAction())
}

final class HitAction extends BaseAction {
  val id = "hitAction"

  private val shoeManager = new ShoeManager(1)

  def process(flow: Flow): Unit = {
    val playerHand = flow.gameContext.player.hand
    val rng = flow.rng

    playerHand += shoeManager.draw(rng)
  }

  def validateRequest(flow: Flow): Unit = {}
}
